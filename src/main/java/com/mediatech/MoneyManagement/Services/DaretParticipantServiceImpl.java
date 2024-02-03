package com.mediatech.MoneyManagement.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mediatech.MoneyManagement.Models.DaretOperation;
import com.mediatech.MoneyManagement.Models.DaretParticipant;
import com.mediatech.MoneyManagement.Models.User;
import com.mediatech.MoneyManagement.Repositories.DaretOperationRepository;
import com.mediatech.MoneyManagement.Repositories.DaretParticipantRepository;
import com.mediatech.MoneyManagement.Repositories.UserRepository;

import jakarta.persistence.EntityNotFoundException;

import java.time.LocalDate;
import java.util.List;

@Service
public class DaretParticipantServiceImpl implements DaretParticipantService {

    @Autowired
    private DaretParticipantRepository daretParticipantRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DaretOperationRepository daretOperationRepository;

 // Ajoute un participant à une opération "Daret".
    @Override
    public void addParticipantToDaretOperation(Long daretOperationId, Long userId, String paymentType, float montantPaye) {
        try {
            // Récupère l'opération "Daret" correspondante à l'identifiant fourni, lance une exception si non trouvée.
            DaretOperation daretOperation = daretOperationRepository.findById(daretOperationId).orElseThrow();
            // Récupère la liste des participants actuels de l'opération.
            List<DaretParticipant> participants = daretOperation.getDaretParticipants();
            // Récupère l'utilisateur correspondant à l'identifiant fourni, lance une exception si non trouvé.
            User user = userRepository.findById(userId).orElseThrow();
            // Crée une nouvelle instance de DaretParticipant pour le nouvel utilisateur.
            DaretParticipant daretParticipant = new DaretParticipant();
            daretParticipant.setUser(user);
            daretParticipant.setDaretOperation(daretOperation);
            daretParticipant.setTypePayement(paymentType);

            // Détermine le facteur multiplicatif en fonction du type de paiement.
            double factor;
            switch (paymentType) {
                case "Moitier":
                    factor = 0.5;
                    break;
                case "Double":
                    factor = 2.0;
                    break;
                default:
                    factor = 1.0; // Montant normal
                    break;
            }

            // Calcule le montant réel payé par le participant en fonction du facteur.
            float montant = (float) (montantPaye * factor);
            daretParticipant.setMontantPaye(montant);

            // Met à jour le nombre de places réservées dans l'opération.
            float currentPlacesReservees = daretOperation.getPlacesReservees();
            daretOperation.setPlacesReservees((float) (currentPlacesReservees + factor));

            // Gère la logique spécifique aux participants en couple.
            handleCoupleParticipants(daretParticipant, participants);

            // Vérifie si le nombre de places réservées atteint le nombre total de participants.
            if (daretOperation.getPlacesReservees() == daretOperation.getNombreParticipant()) {
                // Marque l'opération comme "En cours" et définit la date de début si elle n'est pas déjà définie.
                daretOperation.setStatus("Progress");
                if (daretOperation.getDateDebut() == null) {
                    daretOperation.setDateDebut(LocalDate.now());
                }

                // Définit la date de fin en fonction du type et du nombre de participants.
                setEndDateBasedOnTypeAndNumberOfParticipants(daretOperation);

                // Utilise un compteur séparé pour l'index du participant.
                //int participantIndex = 1;

                // Parcours tous les participants pour mettre à jour leurs propriétés.
                for (DaretParticipant participant : participants) {
                    // Met à jour la date de paiement uniquement si la date de début est définie.
                    if (daretOperation.getDateDebut() != null) {
                        // Logique pour définir la date de paiement en fonction de la période.
                        participant.setDatePaiement(calculateNextPaymentDate(participant, daretOperation));
                    }

                    // Met à jour l'index du participant.
                    //participant.setParticipantIndex(participantIndex);

                    // Logique pour déterminer si le participant est en couple.
                    //participant.setIsCouple(isCouple(participant, participants));

                    // Incrémente le compteur de l'index du participant.
                    //participantIndex++;
                }

                // Affiche la taille de la liste des participants et leurs index.
                System.out.println("la taille de liste :" + participants.size());
                for (DaretParticipant participant : participants) {
                    System.out.println("index = " + participant.getParticipantIndex());
                }
            }

            // Enregistre le nouveau participant et met à jour l'opération "Daret".
            daretParticipantRepository.save(daretParticipant);
            daretOperationRepository.save(daretOperation);
        } catch (EntityNotFoundException ex) {
            // Gère l'exception si une entité n'est pas trouvée.
            System.out.println("ERROR: " + ex.getMessage());
        }
    }

    // Détermine si un participant est en couple en vérifiant le type de paiement.
   /*private boolean isCouple(DaretParticipant participant, List<DaretParticipant> participants) {
        return participant.getTypePayement().equalsIgnoreCase("Moitier") && participant.getIsCouple();
    }
*/

 // Gère l'ajout de participants en couple à une opération "Daret".
    private void handleCoupleParticipants(DaretParticipant newParticipant, List<DaretParticipant> participants) {
        if (newParticipant.getTypePayement().equalsIgnoreCase("Moitier")) {
            // Si le type de paiement est "Moitier", utilise la logique pour gérer les participants de type "Moitier"
            handleMoitierParticipant(newParticipant, participants);
        } else if (newParticipant.getTypePayement().equalsIgnoreCase("Double")) {
            // Si le type de paiement est "Double", utilise la logique pour gérer les participants de type "Double"
            handleDoubleParticipant(newParticipant, participants);
        } else {
            // Pour les paiements normaux, attribue un nouvel index incrémenté
            int newIndexParticipant = findNextAvailableIndex(participants);
            int newIndexCouple = findNextAvailableIndex2(participants);
            newParticipant.setParticipantIndex(newIndexParticipant);
            newParticipant.setCoupleIndex(newIndexCouple);
            
            // Attribution de l'état du tour du participant basé sur son index
            if (newParticipant.getParticipantIndex() == 1) {
                newParticipant.setEtatTour("current");
            } else {
                newParticipant.setEtatTour("not_done");
            }
        }
    }


 // Gère l'ajout d'un participant de type "Moitier" à une opération "Daret".
    private void handleMoitierParticipant(DaretParticipant newParticipant, List<DaretParticipant> participants) {
        // Recherche d'un participant existant effectuant un paiement moitié qui n'est pas en couple dans le même Daret
        DaretParticipant halfPaymentParticipant = findAvailableHalfPaymentParticipant(participants);

        if (halfPaymentParticipant != null) {
            // Un participant effectuant un paiement moitié et qui n'est pas en couple a été trouvé dans le même Daret
            // Met à jour les deux participants pour avoir le même index et les marque comme un couple
            int coupleIndex = halfPaymentParticipant.getCoupleIndex();
            newParticipant.setIsCouple(true);
            halfPaymentParticipant.setIsCouple(true);
            newParticipant.setCoupleIndex(coupleIndex);
            
            // Attribution d'un nouvel index au participant de type "Moitier"
            int newIndex = findNextAvailableIndex(participants);
            newParticipant.setParticipantIndex(newIndex);
            
            // Attribution de l'état du tour du participant de type "Moitier" basé sur l'existant
            newParticipant.setEtatTour(halfPaymentParticipant.getEtatTour());
        } else {
            // Si aucun participant existant effectuant un paiement moitié dans le même Daret n'est trouvé, conserve l'index actuel
            int newIndexParticipant = findNextAvailableIndex(participants);
            int newIndexCouple = findNextAvailableIndex2(participants);
            newParticipant.setCoupleIndex(newIndexCouple);
            newParticipant.setParticipantIndex(newIndexParticipant);
            
            // Attribution de l'état du tour du participant de type "Moitier" basé sur son index
            if (newParticipant.getParticipantIndex() == 1) {
                newParticipant.setEtatTour("current");
            } else {
                newParticipant.setEtatTour("not_done");
            }
        }
    }


 // Gère l'ajout d'un participant de type "Double" à une opération "Daret".
    private void handleDoubleParticipant(DaretParticipant newParticipant, List<DaretParticipant> participants) {
        // Pour les participants de type "Double", attribue un nouvel index incrémenté.
        int newIndexParticipant = findNextAvailableIndex(participants);
        int newIndexCouple = findNextAvailableIndex2(participants);
        
        // Affecte les nouveaux index au participant de type "Double".
        newParticipant.setParticipantIndex(newIndexParticipant);
        newParticipant.setCoupleIndex(newIndexCouple);

        // Détermine l'état du tour pour le participant de type "Double".
        if (newParticipant.getParticipantIndex() == 1) {
            newParticipant.setEtatTour("current");
        } else {
            newParticipant.setEtatTour("not_done");
        }
    }





 // Recherche et retourne un participant qui effectue un paiement moitié et qui n'est pas en couple.
    private DaretParticipant findAvailableHalfPaymentParticipant(List<DaretParticipant> participants) {
        for (DaretParticipant existingParticipant : participants) {
            if (existingParticipant.getTypePayement().equalsIgnoreCase("Moitier")
                    && !existingParticipant.getIsCouple()) {
                // Trouvé un participant qui effectue un paiement moitié et qui n'est pas en couple
                return existingParticipant;
            }
        }
        // Aucun participant correspondant n'a été trouvé
        return null;
    }

    private int findNextAvailableIndex(List<DaretParticipant> participants) {
        int maxIndex = 0;

        for (DaretParticipant participant : participants) {
            if (participant.getParticipantIndex() > maxIndex) {
                maxIndex = participant.getParticipantIndex();
            }
        }

        return maxIndex + 1;
    }

    private int findNextAvailableIndex2(List<DaretParticipant> participants) {
        int maxIndex = 0;

        for (DaretParticipant participant : participants) {
            if (participant.getCoupleIndex() > maxIndex) {
                maxIndex = participant.getCoupleIndex();
            }
        }

        return maxIndex + 1;
    }
 // Calcule la prochaine date de paiement pour un participant d'une opération "Daret" en fonction du type de période.
    private LocalDate calculateNextPaymentDate(DaretParticipant participant, DaretOperation daretOperation) {
        // Récupère le type de période de l'opération "Daret".
        String typePeriode = daretOperation.getTypePeriode();

        // Utilise la date de paiement du participant ou la date de début de la tontine si elle est nulle.
        LocalDate currentDate = participant.getDatePaiement() != null ? participant.getDatePaiement() : daretOperation.getDateDebut();

        // Calcule et retourne la prochaine date de paiement en fonction du type de période.
        switch (typePeriode.toLowerCase()) {
            case "mensuelle":
                return currentDate.plusMonths(1);
            case "quotidienne":
                return currentDate.plusDays(1);
            case "hebdomadaire":
                return currentDate.plusWeeks(1);
            default:
                // Lance une exception si le type de période n'est pas pris en charge.
                throw new IllegalArgumentException("Type de période non pris en charge : " + typePeriode);
        }
    }

 // Définit la date de fin de l'opération "Daret" en fonction du type de période (Mensuelle, Quotidienne, Hebdomadaire) et du nombre de participants.
    private void setEndDateBasedOnTypeAndNumberOfParticipants(DaretOperation daretOperation) {
        // Vérifie le type de période pour ajuster la date de fin en conséquence.
        if ("Mensuelle".equalsIgnoreCase(daretOperation.getTypePeriode())) {
            // Calcul de la nouvelle date de fin en ajoutant le nombre de mois spécifié.
            int numberOfMonths = daretOperation.getNombreParticipant();
            LocalDate newDateFin = daretOperation.getDateDebut().plusMonths(numberOfMonths);
            // Définit la nouvelle date de fin dans l'opération "Daret".
            daretOperation.setDateFin(newDateFin);
        } else if ("Quotidienne".equalsIgnoreCase(daretOperation.getTypePeriode())) {
            // Calcul de la nouvelle date de fin en ajoutant le nombre de jours spécifié.
            int numberOfDays = daretOperation.getNombreParticipant();
            LocalDate newDateFin = daretOperation.getDateDebut().plusDays(numberOfDays);
            // Définit la nouvelle date de fin dans l'opération "Daret".
            daretOperation.setDateFin(newDateFin);
        } else if ("Hebdomadaire".equalsIgnoreCase(daretOperation.getTypePeriode())) {
            // Calcul de la nouvelle date de fin en ajoutant le nombre de semaines spécifié.
            int numberOfWeeks = daretOperation.getNombreParticipant();
            LocalDate newDateFin = daretOperation.getDateDebut().plusWeeks(numberOfWeeks);
            // Définit la nouvelle date de fin dans l'opération "Daret".
            daretOperation.setDateFin(newDateFin);
        }
    }

 // Récupère tous les participants d'opérations "Daret" existants.
    @Override
    public List<DaretParticipant> getAllDaretParticipants() {
        return daretParticipantRepository.findAll();
    }

    // Récupère un participant d'opération "Daret" par son identifiant.
    @Override
    public DaretParticipant getDaretParticipantById(Long id) {
        return daretParticipantRepository.findById(id).orElse(null);
    }

    // Récupère l'opération "Daret" associée à un participant par l'identifiant de l'opération.
    @Override
    public DaretOperation getDaretOperationById(Long daretOperationId) {
        return daretOperationRepository.findById(daretOperationId).orElse(null);
    }


}
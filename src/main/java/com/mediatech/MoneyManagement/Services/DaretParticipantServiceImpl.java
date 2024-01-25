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

    @Override
    public void addParticipantToDaretOperation(Long daretOperationId, Long userId, String paymentType, float montantPaye) {
        try {
            DaretOperation daretOperation = daretOperationRepository.findById(daretOperationId).orElseThrow();
            List<DaretParticipant> participants = daretOperation.getDaretParticipants();
            User user = userRepository.findById(userId).orElseThrow();
            DaretParticipant daretParticipant = new DaretParticipant();
            daretParticipant.setUser(user);
            daretParticipant.setDaretOperation(daretOperation);
            daretParticipant.setTypePayement(paymentType);

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

            float montant = (float) (montantPaye * factor);
            daretParticipant.setMontantPaye(montant);

            float currentPlacesReservees = daretOperation.getPlacesReservees();
            daretOperation.setPlacesReservees((float) (currentPlacesReservees + factor));

            handleCoupleParticipants(daretParticipant, participants);

            if (daretOperation.getPlacesReservees() >= daretOperation.getNombreParticipant()) {
                daretOperation.setStatus("Progress");

                if (daretOperation.getDateDebut() == null) {
                    daretOperation.setDateDebut(LocalDate.now());
                }

                setEndDateBasedOnTypeAndNumberOfParticipants(daretOperation);

                // Utiliser un compteur séparé pour l'index du participant
                int participantIndex = 1;

                for (DaretParticipant participant : participants) {
                    // Ne mettez à jour la date de paiement que si la date de début est définie
                    if (daretOperation.getDateDebut() != null) {
                        // Logique pour définir la date de paiement en fonction de la période
                        participant.setDatePaiement(calculateNextPaymentDate(participant, daretOperation));
                    }

                    participant.setParticipantIndex(participantIndex);

                    // Logique pour définir si le participant est en couple
                    participant.setIsCouple(isCouple(participant, participants));

                    // Incrémenter le compteur de l'index du participant
                    participantIndex++;

                }
 
            }
            
            daretParticipantRepository.save(daretParticipant);
            daretOperationRepository.save(daretOperation);
        } catch (EntityNotFoundException ex) {
            System.out.println("ERROR: " + ex.getMessage());
        }
    }
    private boolean isCouple(DaretParticipant participant, List<DaretParticipant> participants) {
        return participant.getTypePayement().equalsIgnoreCase("Moitier") && participant.getIsCouple();
    }


    private void handleCoupleParticipants(DaretParticipant newParticipant, List<DaretParticipant> participants) {
        if (newParticipant.getTypePayement().equalsIgnoreCase("Moitier")) {
            handleMoitierParticipant(newParticipant, participants);
        } else if (newParticipant.getTypePayement().equalsIgnoreCase("Double")) {
            handleDoubleParticipant(newParticipant, participants);
        } else {
            // For normal payments, assign a new incremented index
            int newIndexParticipant = findNextAvailableIndex(participants);
            int newIndexCouple = findNextAvailableIndex2(participants);
            newParticipant.setParticipantIndex(newIndexParticipant);
            newParticipant.setCoupleIndex(newIndexCouple);
            if(newParticipant.getParticipantIndex()==1) {
            	newParticipant.setEtatTour("current");
            }else {
            	newParticipant.setEtatTour("not_done");
            }
        }
    }

    private void handleMoitierParticipant(DaretParticipant newParticipant, List<DaretParticipant> participants) {
        DaretParticipant halfPaymentParticipant = findAvailableHalfPaymentParticipant(participants);

        if (halfPaymentParticipant != null) {
            // Found a participant paying half who is not part of a couple in the same Daret
            // Update both participants to have the same index and mark them as a couple
            int coupleIndex = halfPaymentParticipant.getCoupleIndex();
            newParticipant.setIsCouple(true);
            halfPaymentParticipant.setIsCouple(true);
            newParticipant.setCoupleIndex(coupleIndex);
            int newIndex = findNextAvailableIndex(participants);
            newParticipant.setParticipantIndex(newIndex);
            newParticipant.setEtatTour(halfPaymentParticipant.getEtatTour());
        } else {
            // If no existing participant paying half in the same Daret, retain the current index
        	int newIndexParticipant = findNextAvailableIndex(participants);
            int newIndexCouple = findNextAvailableIndex2(participants);
            newParticipant.setCoupleIndex(newIndexCouple);
            newParticipant.setParticipantIndex(newIndexParticipant);
            if(newParticipant.getParticipantIndex()==1) {
            	newParticipant.setEtatTour("current");
            }else {
            	newParticipant.setEtatTour("not_done");
            }
        }
    }

    private void handleDoubleParticipant(DaretParticipant newParticipant, List<DaretParticipant> participants) {
        // For participants with type "Double", assign a new incremented index
    	int newIndexParticipant = findNextAvailableIndex(participants);
        int newIndexCouple = findNextAvailableIndex2(participants);
    	newParticipant.setParticipantIndex(newIndexParticipant);
        newParticipant.setCoupleIndex(newIndexCouple);
        if(newParticipant.getParticipantIndex()==1) {
        	newParticipant.setEtatTour("current");
        }else {
        	newParticipant.setEtatTour("not_done");
        }
    }





    private DaretParticipant findAvailableHalfPaymentParticipant(List<DaretParticipant> participants) {
        for (DaretParticipant existingParticipant : participants) {
            if (existingParticipant.getTypePayement().equalsIgnoreCase("Moitier")
                    && !existingParticipant.getIsCouple()) {
                // Found a participant paying half who is not part of a couple
                return existingParticipant;
            }
        }
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
    // Calculate next payment date based on typePeriode
    private LocalDate calculateNextPaymentDate(DaretParticipant participant, DaretOperation daretOperation) {
        String typePeriode = daretOperation.getTypePeriode();

        // Use the participant's payment date or the tontine start date if it's null
        LocalDate currentDate = participant.getDatePaiement() != null ? participant.getDatePaiement() : daretOperation.getDateDebut();

        switch (typePeriode.toLowerCase()) {
            case "mensuelle":
                return currentDate.plusMonths(1);
            case "quotidienne":
                return currentDate.plusDays(1);
            case "hebdomadaire":
                return currentDate.plusWeeks(1);
            default:
                throw new IllegalArgumentException("Unsupported type de période: " + typePeriode);
        }
    }

    // Set the end date based on typePeriode and numberOfParticipants
    private void setEndDateBasedOnTypeAndNumberOfParticipants(DaretOperation daretOperation) {
        if ("Mensuelle".equalsIgnoreCase(daretOperation.getTypePeriode())) {
            int numberOfMonths = daretOperation.getNombreParticipant();
            LocalDate newDateFin = daretOperation.getDateDebut().plusMonths(numberOfMonths);
            daretOperation.setDateFin(newDateFin);
        } else if ("Quotidienne".equalsIgnoreCase(daretOperation.getTypePeriode())) {
            int numberOfDays = daretOperation.getNombreParticipant();
            LocalDate newDateFin = daretOperation.getDateDebut().plusDays(numberOfDays);
            daretOperation.setDateFin(newDateFin);
        } else if ("Hebdomadaire".equalsIgnoreCase(daretOperation.getTypePeriode())) {
            int numberOfWeeks = daretOperation.getNombreParticipant();
            LocalDate newDateFin = daretOperation.getDateDebut().plusWeeks(numberOfWeeks);
            daretOperation.setDateFin(newDateFin);
        }
    }

    @Override
    public List<DaretParticipant> getAllDaretParticipants() {
        return daretParticipantRepository.findAll();
    }

    @Override
    public DaretParticipant getDaretParticipantById(Long id) {
        return daretParticipantRepository.findById(id).orElse(null);
    }

    @Override
    public DaretOperation getDaretOperationById(Long daretOperationId) {
        return daretOperationRepository.findById(daretOperationId).orElse(null);
    }
    


  

	

}
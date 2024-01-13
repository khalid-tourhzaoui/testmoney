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
            double montant;
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

            montant = montantPaye * factor;
            daretParticipant.setMontantPaye((float) montant);

            float currentPlacesReservees = daretOperation.getPlacesReservees();
            daretOperation.setPlacesReservees((float)(currentPlacesReservees +factor));


            if (daretOperation.getPlacesReservees() >= daretOperation.getNombreParticipant()) {
                daretOperation.setStatus("Progress");

                // Set dateDebut only if it's not already set
                if (daretOperation.getDateDebut() == null) {
                    daretOperation.setDateDebut(LocalDate.now());
                }

                // Determine dateFin based on typePeriode and nombreParticipant
                if ("mensuelle".equalsIgnoreCase(daretOperation.getTypePeriode())) {
                    int numberOfMonths = daretOperation.getNombreParticipant();
                    LocalDate newDateFin = daretOperation.getDateDebut().plusMonths(numberOfMonths);
                    daretOperation.setDateFin(newDateFin);
                    if (daretOperation.getDateDebut() != null ) {
                        for (DaretParticipant participant : participants) {
                        	participant.setDatePaiement(daretOperation.getDateDebut().plusMonths(1));
                        }
                    }
                } else if ("hebdomadaire".equalsIgnoreCase(daretOperation.getTypePeriode())) {
                    int numberOfWeeks = daretOperation.getNombreParticipant();
                    LocalDate newDateFin = daretOperation.getDateDebut().plusDays(numberOfWeeks);
                    daretOperation.setDateFin(newDateFin);
                    if (daretOperation.getDateDebut() != null) {
                        for (DaretParticipant participant : participants) {
                        	participant.setDatePaiement(daretOperation.getDateDebut().plusDays(1));
                        }
                    }
                } else if ("semaine".equalsIgnoreCase(daretOperation.getTypePeriode())) {
                    int numberOfWeeks = daretOperation.getNombreParticipant();
                    LocalDate newDateFin = daretOperation.getDateDebut().plusWeeks(numberOfWeeks);
                    daretOperation.setDateFin(newDateFin);
                    if (daretOperation.getDateDebut() != null) {
                        for (DaretParticipant participant : participants) {
                        	participant.setDatePaiement(daretOperation.getDateDebut().plusWeeks(1));
                        }
                    }
                }
            }
            
            daretOperationRepository.save(daretOperation);
            daretParticipantRepository.save(daretParticipant);
        } catch (EntityNotFoundException ex) {
            System.out.println("EROOOOOOOOOOOOOOOOOOOR");
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
        return daretOperationRepository.findById(daretOperationId)
                .orElseThrow(() -> new EntityNotFoundException("DaretOperation not found with id: " + daretOperationId));
    }



    // Add more service methods as needed

}


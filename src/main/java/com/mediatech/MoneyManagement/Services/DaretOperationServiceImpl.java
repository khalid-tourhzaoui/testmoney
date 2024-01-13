package com.mediatech.MoneyManagement.Services;

import java.util.List;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mediatech.MoneyManagement.Models.DaretOperation;
import com.mediatech.MoneyManagement.Models.User;
import com.mediatech.MoneyManagement.Repositories.DaretOperationRepository;

@Service
public class DaretOperationServiceImpl implements DaretOperationService {

    @Autowired
    private DaretOperationRepository daretOperationRepository;
    
    

    @Override
    public DaretOperation save(DaretOperation daretOperation) {
        return daretOperationRepository.save(daretOperation);
    }

    @Override
    public List<DaretOperation> getAllDaretOperations() {
        return daretOperationRepository.findAll();
    }

    @Override
    public DaretOperation getDaretOperationById(Long id) {
        return daretOperationRepository.findById(id).orElse(null);
    }

    @Override
    public List<DaretOperation> findByAdminOffre(User adminOffre) {
        return daretOperationRepository.findByAdminOffre(adminOffre);
    }
    @Override
    public long countByStatusAndAdminOffre(String status, User adminOffre) {
        return daretOperationRepository.countByStatusAndAdminOffre(status, adminOffre);
    }

    @Override
    public long countOnProgressByAdminOffre(User adminOffre) {
        return countByStatusAndAdminOffre("On Progress", adminOffre);
    }

    @Override
    public long countPendingByAdminOffre(User adminOffre) {
        return countByStatusAndAdminOffre("Pending", adminOffre);
    }

    @Override
    public long countClosedByAdminOffre(User adminOffre) {
        return countByStatusAndAdminOffre("Closed", adminOffre);
    }
    @Override
    public List<DaretOperation> findByAdminOffreAndStatus(User adminOffre, String status) {
        return daretOperationRepository.findByAdminOffreAndStatus(adminOffre, status);
    }
    @Override
    public DaretOperation findById(Long id) {
        return daretOperationRepository.findById(id).orElse(null);
    }

    @Override
    public void deleteDaretById(Long id) {
        daretOperationRepository.deleteById(id);
    }
    @Override
    public List<DaretOperation> findPendingOffers() {
        // Implement the logic to fetch offers with status "Pending"
        return daretOperationRepository.findByStatus("Pending");
    }
    @Override
    public long countParticipationsByUserAndStatus(User participant, String status) {
        return daretOperationRepository.countByDaretParticipantsUserAndStatus(participant, status);
    }
    /*@Override
    public long countDistinctUserParticipationsByStatus(User participant, String status) {
        return daretOperationRepository.countDistinctByStatusAndParticipants(status, participant);
    }*/
    
    /*@Override
    public void addParticipantToDaretOperation(Long daretOperationId, Long userId, String paymentType) {
        DaretOperation daretOperation = daretOperationRepository.findById(daretOperationId)
                .orElseThrow(null);

        User user = userRepository.findById(userId)
                .orElseThrow(null);

        double factor;
        switch (paymentType) {
            case "Moitier":
                factor = 0.5;
                break;
            case "Double":
                factor = 2.0;
                break;
            default:
                factor = 1.0; // Default to normal
                break;
        }

        float currentPlacesReservees = daretOperation.getPlacesReservees();
        daretOperation.setPlacesReservees((float) (currentPlacesReservees + factor));

        daretOperation.getDaretParticipants().addAll(user);

       
        if (daretOperation.getPlacesReservees() >= daretOperation.getNombreParticipant()) {
            daretOperation.setStatus("Progress");

           
        }

        daretOperationRepository.save(daretOperation);
    }*/

}

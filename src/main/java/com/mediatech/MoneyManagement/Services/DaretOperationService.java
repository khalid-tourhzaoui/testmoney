package com.mediatech.MoneyManagement.Services;

import java.util.List;

import com.mediatech.MoneyManagement.Models.DaretOperation;
import com.mediatech.MoneyManagement.Models.User;

public interface DaretOperationService {
	
	DaretOperation save(DaretOperation daretOperation);
	
    List<DaretOperation> getAllDaretOperations();
    
    DaretOperation getDaretOperationById(Long id);
    
    List<DaretOperation> findByAdminOffre(User adminOffre);
    
    long countByStatusAndAdminOffre(String status, User adminOffre);
    
    long countOnProgressByAdminOffre(User adminOffre);
    
    long countPendingByAdminOffre(User adminOffre);
    
    long countClosedByAdminOffre(User adminOffre);
    
    List<DaretOperation> findByAdminOffreAndStatus(User adminOffre, String status);
    
    DaretOperation findById(Long id);
    
    void deleteDaretById(Long id);
    
    List<DaretOperation> findPendingOffers();
    
    //void addParticipantToDaretOperation(Long daretOperationId, Long userId, String paymentType);
    
    /*long countDistinctUserParticipationsByStatus(User participant, String status);*/
    /*long countDistinctByStatusAndUser(String status, User user);*/
    long countParticipationsByUserAndStatus(User participant, String status);




}

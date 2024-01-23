package com.mediatech.MoneyManagement.Repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mediatech.MoneyManagement.Models.DaretOperation;
import com.mediatech.MoneyManagement.Models.User;

public interface DaretOperationRepository extends JpaRepository<DaretOperation, Long>{
	List<DaretOperation> findByAdminOffre(User adminOffre);
	
    long countByStatusAndAdminOffre(String status, User adminOffre);
    
    long countByAdminOffre(User adminOffre);
    
    List<DaretOperation> findByAdminOffreAndStatus(User adminOffre, String status);
    
    List<DaretOperation> findByStatus(String status);
    
    List<DaretOperation> findByDaretParticipantsUser(User participant);
    
    List<DaretOperation> findByDaretParticipantsUserAndStatus(User participant,String status);
    
	long countDistinctByDaretParticipantsUser(User participant);
	
    long countDistinctByDaretParticipantsUserAndStatus(User participant, String status);
    
    long countByStatus(String status);

/*-------------------------------------------------------------------*/
  


}
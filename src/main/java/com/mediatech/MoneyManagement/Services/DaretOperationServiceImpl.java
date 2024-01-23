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
        return countByStatusAndAdminOffre("Progress", adminOffre);
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
        return daretOperationRepository.countDistinctByDaretParticipantsUserAndStatus(participant, status);
    }
    /*-------------------------------------------------------------------*/

	@Override
	public List<DaretOperation> findByDaretParticipantsUser(User participant) {
		return daretOperationRepository.findByDaretParticipantsUser(participant);
	}

	@Override
	public List<DaretOperation> findByDaretParticipantsUserAndStatus(User participant, String status) {
		return daretOperationRepository.findByDaretParticipantsUserAndStatus(participant, status);

	}
	
	@Override
    public boolean isUserCreatedUnfinishedDarets(User user) {
        // Récupérer les Daret créés par l'utilisateur qui ne sont pas terminés
        List<DaretOperation> userCreatedDarets = daretOperationRepository.findByAdminOffreAndStatus(user, "Progress");
        return !userCreatedDarets.isEmpty();
    }
	 @Override
	    public boolean isUserParticipantInUnfinishedDarets(User user) {
	        // Récupérer les Daret auxquels l'utilisateur participe et qui ne sont pas terminés
	        List<DaretOperation> userParticipantDarets = daretOperationRepository.findByDaretParticipantsUserAndStatus(user, "Progress");
	        return !userParticipantDarets.isEmpty();
	    }

	@Override
	public List<DaretOperation> getAllDaretOperationsByStatus(String status) {
		return daretOperationRepository.findByStatus(status);
	}

}
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
    
    

 // Enregistre une opération "Daret" dans la base de données.
    @Override
    public DaretOperation save(DaretOperation daretOperation) {
        return daretOperationRepository.save(daretOperation);
    }

    // Récupère toutes les opérations "Daret" existantes.
    @Override
    public List<DaretOperation> getAllDaretOperations() {
        return daretOperationRepository.findAll();
    }

    // Récupère une opération "Daret" par son identifiant.
    @Override
    public DaretOperation getDaretOperationById(Long id) {
        return daretOperationRepository.findById(id).orElse(null);
    }

    // Récupère toutes les opérations "Daret" associées à un administrateur d'offre spécifique.
    @Override
    public List<DaretOperation> findByAdminOffre(User adminOffre) {
        return daretOperationRepository.findByAdminOffre(adminOffre);
    }

    // Compte le nombre d'opérations "Daret" dans un état spécifique pour un administrateur d'offre donné.
    @Override
    public long countByStatusAndAdminOffre(String status, User adminOffre) {
        return daretOperationRepository.countByStatusAndAdminOffre(status, adminOffre);
    }

    // Compte le nombre d'opérations "Daret" en cours pour un administrateur d'offre donné.
    @Override
    public long countOnProgressByAdminOffre(User adminOffre) {
        return countByStatusAndAdminOffre("Progress", adminOffre);
    }

    // Compte le nombre d'opérations "Daret" en attente pour un administrateur d'offre donné.
    @Override
    public long countPendingByAdminOffre(User adminOffre) {
        return countByStatusAndAdminOffre("Pending", adminOffre);
    }

    // Compte le nombre d'opérations "Daret" terminées pour un administrateur d'offre donné.
    @Override
    public long countClosedByAdminOffre(User adminOffre) {
        return countByStatusAndAdminOffre("Closed", adminOffre);
    }

    // Récupère toutes les opérations "Daret" associées à un administrateur d'offre et dans un état spécifique.
    @Override
    public List<DaretOperation> findByAdminOffreAndStatus(User adminOffre, String status) {
        return daretOperationRepository.findByAdminOffreAndStatus(adminOffre, status);
    }

    // Récupère une opération "Daret" par son identifiant.
    @Override
    public DaretOperation findById(Long id) {
        return daretOperationRepository.findById(id).orElse(null);
    }

    // Supprime une opération "Daret" par son identifiant.
    @Override
    public void deleteDaretById(Long id) {
        daretOperationRepository.deleteById(id);
    }

    // Récupère toutes les opérations "Daret" en attente.
    @Override
    public List<DaretOperation> findPendingOffers() {
        return daretOperationRepository.findByStatus("Pending");
    }

    // Compte le nombre de participations dans un état spécifique pour un utilisateur donné.
    @Override
    public long countParticipationsByUserAndStatus(User participant, String status) {
        return daretOperationRepository.countDistinctByDaretParticipantsUserAndStatus(participant, status);
    }

    /*-------------------------------------------------------------------*/

    // Récupère toutes les opérations "Daret" associées à un participant spécifique.
    @Override
    public List<DaretOperation> findByDaretParticipantsUser(User participant) {
        return daretOperationRepository.findByDaretParticipantsUser(participant);
    }

    // Récupère toutes les opérations "Daret" associées à un participant spécifique et dans un état spécifique.
    @Override
    public List<DaretOperation> findByDaretParticipantsUserAndStatus(User participant, String status) {
        return daretOperationRepository.findByDaretParticipantsUserAndStatus(participant, status);
    }

    // Vérifie si un utilisateur a créé des opérations "Daret" non terminées.
    @Override
    public boolean isUserCreatedUnfinishedDarets(User user) {
        List<DaretOperation> userCreatedDarets = daretOperationRepository.findByAdminOffreAndStatus(user, "Progress");
        return !userCreatedDarets.isEmpty();
    }

    // Vérifie si un utilisateur participe à des opérations "Daret" non terminées.
    @Override
    public boolean isUserParticipantInUnfinishedDarets(User user) {
        List<DaretOperation> userParticipantDarets = daretOperationRepository.findByDaretParticipantsUserAndStatus(user, "Progress");
        return !userParticipantDarets.isEmpty();
    }

    // Récupère toutes les opérations "Daret" par un état spécifique.
    @Override
    public List<DaretOperation> getAllDaretOperationsByStatus(String status) {
        return daretOperationRepository.findByStatus(status);
    }

}
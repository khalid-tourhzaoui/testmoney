package com.mediatech.MoneyManagement.Services;

import java.util.List;

import com.mediatech.MoneyManagement.Models.DaretOperation;
import com.mediatech.MoneyManagement.Models.User;

public interface DaretOperationService {
	
	// Enregistre une opération Daret dans la base de données.
    DaretOperation save(DaretOperation daretOperation);

    // Récupère toutes les opérations "Daret" existantes.
    List<DaretOperation> getAllDaretOperations();

    // Récupère une opération "Daret" par son identifiant.
    DaretOperation getDaretOperationById(Long id);

    // Récupère toutes les opérations "Daret" associées à un administrateur d'offre spécifique.
    List<DaretOperation> findByAdminOffre(User adminOffre);

    // Compte le nombre d'opérations "Daret" dans un état spécifique pour un administrateur d'offre donné.
    long countByStatusAndAdminOffre(String status, User adminOffre);

    // Compte le nombre d'opérations "Daret" en cours pour un administrateur d'offre donné.
    long countOnProgressByAdminOffre(User adminOffre);

    // Compte le nombre d'opérations "Daret" en attente pour un administrateur d'offre donné.
    long countPendingByAdminOffre(User adminOffre);

    // Compte le nombre d'opérations "Daret" terminées pour un administrateur d'offre donné.
    long countClosedByAdminOffre(User adminOffre);

    // Récupère toutes les opérations "Daret" associées à un administrateur d'offre et dans un état spécifique.
    List<DaretOperation> findByAdminOffreAndStatus(User adminOffre, String status);

    // Récupère toutes les opérations "Daret" associées à un participant spécifique.
    List<DaretOperation> findByDaretParticipantsUser(User participant);

    // Récupère toutes les opérations "Daret" associées à un participant spécifique et dans un état spécifique.
    List<DaretOperation> findByDaretParticipantsUserAndStatus(User participant, String status);

    // Récupère une opération "Daret" par son identifiant.
    DaretOperation findById(Long id);

    // Supprime une opération "Daret" par son identifiant.
    void deleteDaretById(Long id);

    // Récupère toutes les opérations "Daret" en attente.
    List<DaretOperation> findPendingOffers();

    // Compte le nombre de participations dans un état spécifique pour un utilisateur donné.
    long countParticipationsByUserAndStatus(User participant, String status);

    // Vérifie si un utilisateur a créé des opérations "Daret" non terminées.
    boolean isUserCreatedUnfinishedDarets(User user);

    // Vérifie si un utilisateur participe à des opérations "Daret" non terminées.
    boolean isUserParticipantInUnfinishedDarets(User user);

    /* -------------------------------------------------------------- */

    // Récupère toutes les opérations "Daret" par un état spécifique.
    List<DaretOperation> getAllDaretOperationsByStatus(String status);

  

}
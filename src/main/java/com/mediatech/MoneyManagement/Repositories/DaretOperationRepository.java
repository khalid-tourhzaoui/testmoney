package com.mediatech.MoneyManagement.Repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mediatech.MoneyManagement.Models.DaretOperation;
import com.mediatech.MoneyManagement.Models.User;

public interface DaretOperationRepository extends JpaRepository<DaretOperation, Long> {

    // Méthode pour trouver une liste d'opérations dont l'utilisateur est l'administrateur
    List<DaretOperation> findByAdminOffre(User adminOffre);

    // Méthode pour compter le nombre d'opérations ayant un certain statut et un administrateur donné
    long countByStatusAndAdminOffre(String status, User adminOffre);

    // Méthode pour compter le nombre d'opérations dont l'utilisateur est l'administrateur
    long countByAdminOffre(User adminOffre);

    // Méthode pour trouver une liste d'opérations dont l'utilisateur est l'administrateur et ayant un certain statut
    List<DaretOperation> findByAdminOffreAndStatus(User adminOffre, String status);

    // Méthode pour trouver une liste d'opérations ayant un certain statut
    List<DaretOperation> findByStatus(String status);

    // Méthode pour trouver une liste d'opérations dont l'utilisateur est un participant
    List<DaretOperation> findByDaretParticipantsUser(User participant);

    // Méthode pour trouver une liste d'opérations dont l'utilisateur est un participant et ayant un certain statut
    List<DaretOperation> findByDaretParticipantsUserAndStatus(User participant, String status);

    // Méthode pour compter le nombre d'utilisateurs distincts participant à une opération
    long countDistinctByDaretParticipantsUser(User participant);

    // Méthode pour compter le nombre d'utilisateurs distincts participant à une opération ayant un certain statut
    long countDistinctByDaretParticipantsUserAndStatus(User participant, String status);

    // Méthode pour compter le nombre d'opérations ayant un certain statut
    long countByStatus(String status);



}

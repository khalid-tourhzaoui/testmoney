package com.mediatech.MoneyManagement.Repositories;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mediatech.MoneyManagement.Models.DaretParticipant;

@Repository
public interface DaretParticipantRepository extends JpaRepository<DaretParticipant, Long> {

    // Méthode pour trouver une liste de participants à une opération ayant un certain type de paiement et faisant partie ou non d'un couple
    //List<DaretParticipant> findByDaretOperationIdAndTypePayementAndIsCouple(Long daretOperationId, String typePayement, boolean isCouple);

    
}

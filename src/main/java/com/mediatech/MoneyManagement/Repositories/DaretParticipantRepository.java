package com.mediatech.MoneyManagement.Repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;


import org.springframework.stereotype.Repository;

import com.mediatech.MoneyManagement.Models.DaretParticipant;
@Repository
public interface DaretParticipantRepository extends JpaRepository<DaretParticipant, Long> {

    List<DaretParticipant> findByDaretOperationIdAndTypePayementAndIsCouple(Long daretOperationId, String typePayement, boolean isCouple);
    Optional<DaretParticipant> findByUser_IdAndDaretOperation_Id(Long userId, Long daretOperationId);

}

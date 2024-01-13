package com.mediatech.MoneyManagement.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;

import com.mediatech.MoneyManagement.Models.DaretParticipant;
@Repository
public interface DaretParticipantRepository extends JpaRepository<DaretParticipant, Long> {

}

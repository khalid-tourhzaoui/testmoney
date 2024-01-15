package com.mediatech.MoneyManagement.Services;
import java.util.List;

import com.mediatech.MoneyManagement.Models.DaretOperation;
import com.mediatech.MoneyManagement.Models.DaretParticipant;

public interface DaretParticipantService {
    List<DaretParticipant> getAllDaretParticipants();
    DaretParticipant getDaretParticipantById(Long id);
    DaretOperation getDaretOperationById(Long daretOperationId);
    void addParticipantToDaretOperation(Long daretOperationId, Long userId, String paymentType,float montantPaye);
	/*void makePaymentAndUpdateTourDeRole(DaretParticipant participant);*/
}

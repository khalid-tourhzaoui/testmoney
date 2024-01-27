package com.mediatech.MoneyManagement.Services;
import java.util.List;

import com.mediatech.MoneyManagement.Models.DaretOperation;
import com.mediatech.MoneyManagement.Models.DaretParticipant;

public interface DaretParticipantService {
	 // Récupère tous les participants d'opérations "Daret".
    List<DaretParticipant> getAllDaretParticipants();
    
    // Récupère un participant d'opération "Daret" par son identifiant.
    DaretParticipant getDaretParticipantById(Long id);
    
    // Récupère l'opération "Daret" associée à un participant par l'identifiant de l'opération.
    DaretOperation getDaretOperationById(Long daretOperationId);
    
    // Ajoute un participant à une opération "Daret" avec les détails spécifiés (id de l'opération, id de l'utilisateur, type de paiement, montant payé).
    void addParticipantToDaretOperation(Long daretOperationId, Long userId, String paymentType, float montantPaye);
	
}

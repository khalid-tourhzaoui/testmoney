package com.mediatech.MoneyManagement.Models;

import jakarta.persistence.*;


import java.time.LocalDate;
import java.util.Date;

@Entity
@Table(name = "daret_participants")
public class DaretParticipant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "daret_id")
    private DaretOperation daretOperation;

    @Column(name = "created_at")
    private Date createdAt;

    @Column(name = "updated_at")
    private Date updatedAt;
    
    @Column(nullable = true)
    private float montantPaye;

    @Column(nullable = true)
    private LocalDate datePaiement;
    
    @Column(nullable=true)
    private String typePayement;
    
    @Column(nullable=true)
    private int VerifyPayement;

   @Column(nullable=true)
   private boolean isCouple;
   
   @Column(nullable=true)
   private int ParticipantIndex;


	public DaretParticipant(User user, DaretOperation daretOperation, float montantPaye, LocalDate datePaiement,
			String typePayement, int verifyPayement,boolean isCouple) {
		super();
		this.user = user;
		this.daretOperation = daretOperation;
		this.montantPaye = montantPaye;
		this.datePaiement = datePaiement;
		this.typePayement = typePayement;
		this.VerifyPayement = verifyPayement;
		this.isCouple = isCouple;
	}

	public DaretParticipant() {
		super();
		//TODO Auto-generated constructor stub
	}

	 /*public DaretParticipant(DaretParticipant other) {
	        this.user = other.user;
	        this.daretOperation = other.daretOperation;
	        this.typePayement = other.typePayement;
	        this.montantPaye = other.montantPaye;
	        this.datePaiement = other.datePaiement;
	        this.VerifyPayement = other.VerifyPayement;
	        this.isCouple = other.isCouple;
	    }*/
	public DaretParticipant(DaretParticipant existingParticipant) {
	    this.user = existingParticipant.getUser();
	    this.daretOperation = existingParticipant.getDaretOperation();
	    this.createdAt = existingParticipant.getCreatedAt();
	    this.updatedAt = existingParticipant.getUpdatedAt();
	    this.montantPaye = existingParticipant.getMontantPaye();
	    this.datePaiement = existingParticipant.getDatePaiement();
	    this.typePayement = existingParticipant.getTypePayement();
	    this.VerifyPayement = existingParticipant.getVerifyPayement();
	    this.isCouple = existingParticipant.getIsCouple();
	    // Assuming participantIndex is also an attribute in DaretParticipant
	    this.ParticipantIndex = existingParticipant.getParticipantIndex();
	}


	

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public DaretOperation getDaretOperation() {
		return daretOperation;
	}

	public void setDaretOperation(DaretOperation daretOperation) {
		this.daretOperation = daretOperation;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

	public Date getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(Date updatedAt) {
		this.updatedAt = updatedAt;
	}

	public float getMontantPaye() {
		return montantPaye;
	}

	public void setMontantPaye(float montantPaye) {
		this.montantPaye = montantPaye;
	}

	public LocalDate getDatePaiement() {
		return datePaiement;
	}

	public void setDatePaiement(LocalDate datePaiement) {
		this.datePaiement = datePaiement;
	}

	public String getTypePayement() {
		return typePayement;
	}

	public void setTypePayement(String typePayement) {
		this.typePayement = typePayement;
	}

	public int getVerifyPayement() {
		return VerifyPayement;
	}

	public void setVerifyPayement(int verifyPayement) {
		VerifyPayement = verifyPayement;
	}

	public boolean getIsCouple() {
        return isCouple;
    }

    public void setIsCouple(boolean isCouple) {
        this.isCouple = isCouple;
    }

    // Getter et Setter pour tourDeRole
    public int getParticipantIndex() {
        return ParticipantIndex;
    }

    public void setParticipantIndex(int ParticipantIndex) {
        this.ParticipantIndex = ParticipantIndex;
    }

	@PrePersist
    protected void onCreate() {
        createdAt = new Date();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = new Date();
    }
}

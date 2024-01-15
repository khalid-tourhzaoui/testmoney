package com.mediatech.MoneyManagement.Models;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Entity
public class DaretOperation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Le nom d'opération est obligatoire")
    @Size(max = 50, message = "Le nom d'opération ne peut pas dépasser 50 caractères")
    @Column(nullable = false, length = 50)
    private String designation;

    @Min(value = 1, message = "Le nombre de participants doit être d'au moins 1")
    @Column(nullable = false)
    private int nombreParticipant;

    @NotNull(message = "La date de début est obligatoire")
    @FutureOrPresent(message = "La date de début doit être dans le futur ou aujourd'hui")
    @Column(nullable = true)
    private LocalDate dateDebut;

    @NotNull(message = "La date de fin est obligatoire")
    @Future(message = "La date de fin doit être dans le futur")
    @Column(nullable = true)
    private LocalDate dateFin;

    @NotBlank(message = "Le type de période est obligatoire")
    @Column(nullable = false, length = 30)
    private String typePeriode; // Mensuelle, hebdomadaire, etc.

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false, referencedColumnName = "id")
    private User adminOffre;

    @NotBlank(message = "Le statut est obligatoire")
    @Column(nullable = false, length = 50)
    private String status;

    @Positive(message = "Le montant par période doit être un nombre positif")
    @Column(nullable = false)
    private double montantParPeriode;
    @Column(nullable = true)
    private int tourDeRole;
    
    @Column(nullable = true)
    private float placesReservees=0;
    
    @OneToMany(mappedBy = "daretOperation", cascade = CascadeType.ALL)
    private List<DaretParticipant> daretParticipants;
    @Column(name = "created_at")
    private Date createdAt;

    @Column(name = "updated_at")
    private Date updatedAt;
	public DaretOperation(String designation, int nombreParticipant, LocalDate dateDebut, LocalDate dateFin,String typePeriode, User adminOffre, String status,
			double montantParPeriode,int tourDeRole) {
		
		this.designation = designation;
		this.nombreParticipant = nombreParticipant;
		this.dateDebut = dateDebut;
		this.dateFin = dateFin;
		this.typePeriode = typePeriode;
		this.adminOffre = adminOffre;
		this.status = status;
		this.montantParPeriode = montantParPeriode;
		this.tourDeRole=tourDeRole;
	}

	public DaretOperation() {
		super();
		//TODO Auto-generated constructor stub
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getDesignation() {
		return designation;
	}

	public void setDesignation(String designation) {
		this.designation = designation;
	}

	public int getNombreParticipant() {
		return nombreParticipant;
	}

	public void setNombreParticipant(int nombreParticipant) {
		this.nombreParticipant = nombreParticipant;
	}

	public LocalDate getDateDebut() {
		return dateDebut;
	}

	public void setDateDebut(LocalDate dateDebut) {
		this.dateDebut = dateDebut;
	}

	public LocalDate getDateFin() {
		return dateFin;
	}

	public void setDateFin(LocalDate dateFin) {
		this.dateFin = dateFin;
	}

	public String getTypePeriode() {
		return typePeriode;
	}

	public void setTypePeriode(String typePeriode) {
		this.typePeriode = typePeriode;
	}

	public User getAdminOffre() {
		return adminOffre;
	}

	public void setAdminOffre(User adminOffre) {
		this.adminOffre = adminOffre;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public double getMontantParPeriode() {
		return montantParPeriode;
	}

	public void setMontantParPeriode(double montantParPeriode) {
		this.montantParPeriode = montantParPeriode;
	}

	

	public List<DaretParticipant> getDaretParticipants() {
		return daretParticipants;
	}

	public void setDaretParticipants(List<DaretParticipant> daretParticipants) {
		this.daretParticipants = daretParticipants;
	}

	public float getPlacesReservees() {
		return placesReservees;
	}

	public void setPlacesReservees(float placesReservees) {
		this.placesReservees = placesReservees;
	}
	public int getTourDeRole() {
		return tourDeRole;
	}

	public void setTourDeRole(int tourDeRole) {
		this.tourDeRole = tourDeRole;
	}
	@PrePersist
    protected void onCreate() {
        createdAt = new Date();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = new Date();
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
    
    
}
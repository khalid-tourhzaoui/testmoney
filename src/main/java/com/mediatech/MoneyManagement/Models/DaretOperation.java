package com.mediatech.MoneyManagement.Models;

import jakarta.persistence.*;


import java.time.LocalDate;
import java.util.*;

@Entity
public class DaretOperation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

 // Désignation de l'opération, non nulle
    @Column(nullable = false)
    private String designation;

    // Nombre de participants à l'opération
    @Column(nullable = false)
    private int nombreParticipant;

    // Date de début de l'opération
    @Column(nullable = true)
    private LocalDate dateDebut;

    // Date de fin de l'opération
    @Column(nullable = true)
    private LocalDate dateFin;

    // Type de période de l'opération (Mensuelle, Hebdomadaire, etc.)
    @Column(nullable = false)
    private String typePeriode;

    // Admin offre lié à cette opération
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false, referencedColumnName = "id")
    private User adminOffre;

    // Statut de l'opération
    @Column(nullable = false)
    private String status;

    // Montant par période de l'opération
    @Column(nullable = false)
    private double montantParPeriode;

    // Nombre de tours de rôle dans l'opération
    @Column(nullable = true)
    private int tourDeRole;

    // Nombre de places réservées (par défaut à 0)
    @Column(nullable = true)
    private float placesReservees = 0;

    // Liste des participants liés à cette opération
    @OneToMany(mappedBy = "daretOperation")
    private List<DaretParticipant> daretParticipants;

    // Date de création de l'opération
    @Column(name = "created_at")
    private Date createdAt;

    // Date de mise à jour de l'opération
    @Column(name = "updated_at")
    private Date updatedAt;
	public DaretOperation(String designation, int nombreParticipant, LocalDate dateDebut, LocalDate dateFin,String typePeriode, 
			User adminOffre, String status, double montantParPeriode,int tourDeRole) {
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
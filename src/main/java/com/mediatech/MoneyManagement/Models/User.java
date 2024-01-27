package com.mediatech.MoneyManagement.Models;


import java.util.Date;


import java.util.List;

import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class User {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
	@Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;
    
    @Column(nullable =false)
    private String role;

    @Column(nullable = false)
    private String nom;

  
    @Column(nullable = false)
    private String prenom;


    @Column(nullable = false, unique = true)
    private String cin;

    @Column(nullable = false)
    private String gender;
    
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<DaretParticipant> participatedDarets;
    
    @Column(name = "created_at")
    private Date createdAt;

    @Column(name = "updated_at")
    private Date updatedAt;
    
	public User(String email, String password, String role, String nom, String prenom, String cin,String gender) {
		this.email = email;
		this.password = password;
		this.role = role;
		this.nom = nom;
		this.prenom = prenom;
		this.cin = cin;
		this.gender = gender;
	}

	public User() {
		
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public String getNom() {
		return nom;
	}

	public void setNom(String nom) {
		this.nom = nom;
	}

	public String getPrenom() {
		return prenom;
	}

	public void setPrenom(String prenom) {
		this.prenom = prenom;
	}

	public String getCin() {
		return cin;
	}

	public void setCin(String cin) {
		this.cin = cin;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public List<DaretParticipant> getParticipatedDarets() {
		return participatedDarets;
	}

	public void setParticipatedDarets(List<DaretParticipant> participatedDarets) {
		this.participatedDarets = participatedDarets;
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
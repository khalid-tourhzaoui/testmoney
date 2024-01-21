package com.mediatech.MoneyManagement.DTO;


public class UserDto {

    private String email;
    private String password;
    private String role;
    private String nom;
    private String prenom;
    private String cin;
    private String ConfirmPassword;
    private String gender;
    
	public UserDto(String email, String password, String role, String nom, String prenom, String cin, String imagePath,String gender) {
		this.email = email;
		this.password = password;
		this.role = role;
		this.nom = nom;
		this.prenom = prenom;
		this.cin = cin;
		this.gender = gender;
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

	public String getConfirmPassword() {
		return ConfirmPassword;
	}

	public void setConfirmPassword(String confirmPassword) {
		ConfirmPassword = confirmPassword;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}
  
}


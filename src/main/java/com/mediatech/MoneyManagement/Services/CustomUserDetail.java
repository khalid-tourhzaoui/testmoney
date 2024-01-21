package com.mediatech.MoneyManagement.Services;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.mediatech.MoneyManagement.Models.User;



public class CustomUserDetail implements UserDetails {
	
	private User user;
	
	public CustomUserDetail(User user) {
		this.user = user;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return List.of(() -> user.getRole());
	}
	
	public String getNom() {
		return user.getNom();
	}
	public String getPrenom() {
		return user.getPrenom();
	}
	public String getRole() {
		return user.getRole();
	}
	public String getEmail() {
		return user.getEmail();
	}
	public String getGender() {
		return user.getGender();
	}

	
	@Override
	public String getPassword() {
		return user.getPassword();
	}

	@Override
	public String getUsername() {
		return user.getEmail();
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

}

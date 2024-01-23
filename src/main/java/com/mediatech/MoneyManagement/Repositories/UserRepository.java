package com.mediatech.MoneyManagement.Repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mediatech.MoneyManagement.Models.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
	User findByEmail (String email);
	boolean existsByEmail(String email);
	boolean existsByCin(String cin);
	long countByRole(String role);
	List<User> findByRoleIn(List<String> asList);

}

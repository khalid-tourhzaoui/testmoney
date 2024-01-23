package com.mediatech.MoneyManagement.Services;

import java.util.List;

import com.mediatech.MoneyManagement.DTO.UserDto;


import com.mediatech.MoneyManagement.Models.User;

public interface UserService {
	User save (UserDto userDto);
	boolean existsByEmail(String email);
	boolean existsByCin(String cin);
	User findByEmail(String email);
	User save (User user);
	void updateUserInfo(User updatedUser);

    void updateUserPassword(String userEmail, String oldPassword, String newPassword);

    void deleteUser(String userEmail, String password);
    User findById(Long userId);
    
    boolean isCorrectPassword(String userEmail, String password);
    
	List<User> findAllUsers();


   

}
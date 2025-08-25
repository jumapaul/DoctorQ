package com.doctorq.userservice.user_profile.services;

import com.doctorq.userservice.response.ApiResponse;
import com.doctorq.userservice.user.entities.User;

import java.util.List;

public interface UserService {
    ApiResponse<List<User>> getAllUsers();
}

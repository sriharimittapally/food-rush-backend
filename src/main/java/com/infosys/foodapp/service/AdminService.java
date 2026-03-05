package com.infosys.foodapp.service;

import com.infosys.foodapp.dto.response.AdminDashboardResponse;
import com.infosys.foodapp.dto.response.RestaurantRevenueResponse;
import com.infosys.foodapp.dto.response.UserResponse;

import java.util.List;

public interface AdminService {
    AdminDashboardResponse getDashboard();
    List<UserResponse> getAllUsers();
    List<UserResponse> getAllCustomers();
    UserResponse blockUnblockUser(Long userId);
    List<RestaurantRevenueResponse> getRevenueReport();
}
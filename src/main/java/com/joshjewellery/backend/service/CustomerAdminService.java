package com.joshjewellery.backend.service;

import com.joshjewellery.backend.dto.CustomerDTOs;

import java.util.List;

public interface CustomerAdminService {
    List<CustomerDTOs.CustomerSummary> getAllCustomers();
}

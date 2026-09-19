package com.joshjewellery.backend.service;

import com.joshjewellery.backend.dto.DashboardDTOs;

public interface AdminDashboardService {
    DashboardDTOs.DashboardSummaryResponse getDashboardSummary();
}

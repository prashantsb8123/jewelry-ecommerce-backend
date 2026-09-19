package com.joshjewellery.backend.service.impl;

import com.joshjewellery.backend.constant.RoleType;
import com.joshjewellery.backend.dto.CustomerDTOs;
import com.joshjewellery.backend.entity.User;
import com.joshjewellery.backend.repository.OrderRepository;
import com.joshjewellery.backend.repository.UserRepository;
import com.joshjewellery.backend.service.CustomerAdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CustomerAdminServiceImpl implements CustomerAdminService {

    private final UserRepository userRepository;
    private final OrderRepository orderRepository;

    @Override
    @Transactional(readOnly = true)
    public java.util.List<CustomerDTOs.CustomerSummary> getAllCustomers() {
        Map<UUID, Object[]> spendByUser = new HashMap<>();
        for (Object[] row : orderRepository.aggregateSpendByUser()) {
            Object rawId = row[0];
            UUID userId = rawId instanceof UUID ? (UUID) rawId : UUID.fromString(rawId.toString());
            spendByUser.put(userId, row);
        }

        return userRepository.findAll().stream()
                .filter(u -> u.getRole() == null || u.getRole().getName() == RoleType.ROLE_CUSTOMER)
                .map(u -> {
                    Object[] row = spendByUser.get(u.getId());
                    BigDecimal totalSpent = row != null ? (BigDecimal) row[1] : BigDecimal.ZERO;
                    Long ordersCount = row != null ? ((Number) row[2]).longValue() : 0L;
                    return CustomerDTOs.CustomerSummary.builder()
                            .id(u.getId())
                            .name(u.getFullName())
                            .email(u.getEmail())
                            .phone(u.getPhone())
                            .rewardPoints(u.getRewardPoints())
                            .totalSpent(totalSpent)
                            .ordersCount(ordersCount)
                            .joinedDate(u.getCreatedAt())
                            .build();
                })
                .sorted((a, b) -> b.getTotalSpent().compareTo(a.getTotalSpent()))
                .toList();
    }
}

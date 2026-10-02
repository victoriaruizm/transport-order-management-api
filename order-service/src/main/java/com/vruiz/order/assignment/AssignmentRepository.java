package com.vruiz.order.assignment;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AssignmentRepository extends JpaRepository<Assignment, UUID> {

    boolean existsByOrderId(UUID orderId);
}

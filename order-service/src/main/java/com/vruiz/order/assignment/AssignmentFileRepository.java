package com.vruiz.order.assignment;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AssignmentFileRepository extends JpaRepository<AssignmentFile, UUID> {
}

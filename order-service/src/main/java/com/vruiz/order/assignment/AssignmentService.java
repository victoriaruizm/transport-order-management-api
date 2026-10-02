package com.vruiz.order.assignment;

import com.vruiz.order.order.Order;
import com.vruiz.order.order.OrderService;
import com.vruiz.order.order.OrderStatus;
import java.io.IOException;
import java.util.Set;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AssignmentService {

    private static final Logger log = LoggerFactory.getLogger(AssignmentService.class);
    private static final Set<String> PDF_TYPES = Set.of("application/pdf");
    private static final Set<String> IMAGE_TYPES = Set.of("image/png", "image/jpeg");

    private final AssignmentRepository assignments;
    private final AssignmentFileRepository files;
    private final OrderService orderService;
    private final DriverClient driverClient;

    public AssignmentService(AssignmentRepository assignments, AssignmentFileRepository files,
            OrderService orderService, DriverClient driverClient) {
        this.assignments = assignments;
        this.files = files;
        this.orderService = orderService;
        this.driverClient = driverClient;
    }

    public Assignment assign(UUID orderId, UUID driverId) {
        Order order = orderService.get(orderId);
        if (order.getStatus() != OrderStatus.CREATED) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Order must be CREATED, but is " + order.getStatus());
        }
        if (!driverClient.find(driverId).active()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Driver is not active: " + driverId);
        }
        if (assignments.existsByOrderId(orderId)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Order already assigned: " + orderId);
        }
        Assignment assignment = assignments.save(new Assignment(orderId, driverId));
        log.info("Order assigned orderId={} driverId={}", orderId, driverId);
        return assignment;
    }

    public AssignmentFile addPdf(UUID assignmentId, MultipartFile file) throws IOException {
        return store(assignmentId, file, PDF_TYPES);
    }

    public AssignmentFile addImage(UUID assignmentId, MultipartFile file) throws IOException {
        return store(assignmentId, file, IMAGE_TYPES);
    }

    private AssignmentFile store(UUID assignmentId, MultipartFile file, Set<String> allowedTypes) throws IOException {
        if (!assignments.existsById(assignmentId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Assignment not found: " + assignmentId);
        }
        if (!allowedTypes.contains(file.getContentType())) {
            throw new ResponseStatusException(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "Allowed types: " + allowedTypes);
        }
        AssignmentFile saved = files.save(new AssignmentFile(assignmentId, file.getOriginalFilename(),
                file.getContentType(), file.getBytes()));
        log.info("File stored assignmentId={} fileId={} type={}", assignmentId, saved.getId(), saved.getContentType());
        return saved;
    }
}

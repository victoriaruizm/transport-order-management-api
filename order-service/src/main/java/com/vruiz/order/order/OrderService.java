package com.vruiz.order.order;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class OrderService {

    private static final Logger log = LoggerFactory.getLogger(OrderService.class);

    private final OrderRepository repository;

    public OrderService(OrderRepository repository) {
        this.repository = repository;
    }

    public Order create(OrderRequest request) {
        Order order = repository.save(new Order(request.origin(), request.destination()));
        log.info("Order created id={}", order.getId());
        return order;
    }

    public Order get(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found: " + id));
    }

    public Order changeStatus(UUID id, OrderStatus next) {
        Order order = get(id);
        if (!order.getStatus().canMoveTo(next)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Invalid status transition: " + order.getStatus() + " -> " + next);
        }
        log.info("Order status changed id={} from={} to={}", id, order.getStatus(), next);
        order.setStatus(next);
        return repository.save(order);
    }

    /** Todos los filtros son opcionales; "from" y "to" (inclusivos) se aplican a createdAt. */
    public List<Order> search(OrderStatus status, LocalDate from, LocalDate to, String origin, String destination) {
        Specification<Order> spec = (root, query, cb) -> cb.conjunction();
        if (status != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("status"), status));
        }
        if (from != null) {
            spec = spec.and((root, query, cb) -> cb.greaterThanOrEqualTo(root.get("createdAt"), from.atStartOfDay()));
        }
        if (to != null) {
            spec = spec.and((root, query, cb) -> cb.lessThan(root.get("createdAt"), to.plusDays(1).atStartOfDay()));
        }
        if (origin != null) {
            spec = spec.and((root, query, cb) -> cb.like(cb.lower(root.get("origin")), "%" + origin.toLowerCase() + "%"));
        }
        if (destination != null) {
            spec = spec.and((root, query, cb) -> cb.like(cb.lower(root.get("destination")),
                    "%" + destination.toLowerCase() + "%"));
        }
        return repository.findAll(spec);
    }
}

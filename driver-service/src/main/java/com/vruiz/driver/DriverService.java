package com.vruiz.driver;

import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class DriverService {

    private static final Logger log = LoggerFactory.getLogger(DriverService.class);

    private final DriverRepository repository;

    public DriverService(DriverRepository repository) {
        this.repository = repository;
    }

    public Driver create(DriverRequest request) {
        boolean active = request.active() == null || request.active();
        Driver driver = repository.save(new Driver(request.name(), request.licenseNumber(), active));
        log.info("Driver created id={} active={}", driver.getId(), active);
        return driver;
    }

    public List<Driver> listActive() {
        return repository.findByActiveTrue();
    }

    public Driver get(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Driver not found: " + id));
    }
}

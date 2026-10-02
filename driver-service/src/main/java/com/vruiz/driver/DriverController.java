package com.vruiz.driver;

import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/drivers")
public class DriverController {

    private final DriverService service;

    public DriverController(DriverService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DriverResponse create(@Valid @RequestBody DriverRequest request) {
        return DriverResponse.from(service.create(request));
    }

    /** Lista solo los conductores activos. */
    @GetMapping
    public List<DriverResponse> listActive() {
        return service.listActive().stream().map(DriverResponse::from).toList();
    }

    @GetMapping("/{id}")
    public DriverResponse get(@PathVariable UUID id) {
        return DriverResponse.from(service.get(id));
    }
}

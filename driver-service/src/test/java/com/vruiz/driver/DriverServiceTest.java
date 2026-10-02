package com.vruiz.driver;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

@ExtendWith(MockitoExtension.class)
class DriverServiceTest {

    @Mock
    DriverRepository repository;

    @InjectMocks
    DriverService service;

    @Test
    void createDefaultsToActive() {
        when(repository.save(any(Driver.class))).thenAnswer(i -> i.getArgument(0));

        Driver driver = service.create(new DriverRequest("Ana", "LIC-1", null));

        assertTrue(driver.isActive());
    }

    @Test
    void createKeepsInactiveFlag() {
        when(repository.save(any(Driver.class))).thenAnswer(i -> i.getArgument(0));

        Driver driver = service.create(new DriverRequest("Ana", "LIC-1", false));

        assertFalse(driver.isActive());
    }

    @Test
    void listActiveDelegatesToRepository() {
        List<Driver> drivers = List.of(new Driver("Ana", "LIC-1", true));
        when(repository.findByActiveTrue()).thenReturn(drivers);

        assertEquals(drivers, service.listActive());
    }

    @Test
    void getUnknownDriverReturnsNotFound() {
        UUID id = UUID.randomUUID();
        when(repository.findById(id)).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> service.get(id));

        assertEquals(404, ex.getStatusCode().value());
    }
}

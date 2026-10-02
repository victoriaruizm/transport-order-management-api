package com.vruiz.order.order;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    OrderRepository repository;

    @InjectMocks
    OrderService service;

    @Test
    void createStartsInCreatedStatus() {
        when(repository.save(any(Order.class))).thenAnswer(i -> i.getArgument(0));

        Order order = service.create(new OrderRequest("Lima", "Cusco"));

        assertEquals(OrderStatus.CREATED, order.getStatus());
        assertEquals("Lima", order.getOrigin());
    }

    @Test
    void getUnknownOrderReturnsNotFound() {
        UUID id = UUID.randomUUID();
        when(repository.findById(id)).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> service.get(id));

        assertEquals(404, ex.getStatusCode().value());
    }

    @Test
    void changeStatusAppliesValidTransition() {
        UUID id = UUID.randomUUID();
        Order order = new Order("Lima", "Cusco");
        when(repository.findById(id)).thenReturn(Optional.of(order));
        when(repository.save(order)).thenReturn(order);

        Order result = service.changeStatus(id, OrderStatus.IN_TRANSIT);

        assertEquals(OrderStatus.IN_TRANSIT, result.getStatus());
    }

    @Test
    void changeStatusRejectsInvalidTransition() {
        UUID id = UUID.randomUUID();
        when(repository.findById(id)).thenReturn(Optional.of(new Order("Lima", "Cusco")));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> service.changeStatus(id, OrderStatus.DELIVERED));

        assertEquals(409, ex.getStatusCode().value());
        verify(repository, never()).save(any());
    }
}

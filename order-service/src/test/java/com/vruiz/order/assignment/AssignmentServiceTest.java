package com.vruiz.order.assignment;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.vruiz.order.order.Order;
import com.vruiz.order.order.OrderService;
import com.vruiz.order.order.OrderStatus;
import java.io.IOException;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.server.ResponseStatusException;

@ExtendWith(MockitoExtension.class)
class AssignmentServiceTest {

    @Mock
    AssignmentRepository assignments;
    @Mock
    AssignmentFileRepository files;
    @Mock
    OrderService orderService;
    @Mock
    DriverClient driverClient;

    @InjectMocks
    AssignmentService service;

    final UUID orderId = UUID.randomUUID();
    final UUID driverId = UUID.randomUUID();

    @Test
    void assignsActiveDriverToCreatedOrder() {
        when(orderService.get(orderId)).thenReturn(new Order("Lima", "Cusco"));
        when(driverClient.find(driverId)).thenReturn(new DriverClient.DriverInfo(driverId, true));
        when(assignments.save(any(Assignment.class))).thenAnswer(i -> i.getArgument(0));

        Assignment result = service.assign(orderId, driverId);

        assertEquals(orderId, result.getOrderId());
        assertEquals(driverId, result.getDriverId());
    }

    @Test
    void rejectsOrderNotInCreatedStatus() {
        Order order = new Order("Lima", "Cusco");
        order.setStatus(OrderStatus.IN_TRANSIT);
        when(orderService.get(orderId)).thenReturn(order);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> service.assign(orderId, driverId));

        assertEquals(409, ex.getStatusCode().value());
        verify(assignments, never()).save(any());
    }

    @Test
    void rejectsInactiveDriver() {
        when(orderService.get(orderId)).thenReturn(new Order("Lima", "Cusco"));
        when(driverClient.find(driverId)).thenReturn(new DriverClient.DriverInfo(driverId, false));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> service.assign(orderId, driverId));

        assertEquals(409, ex.getStatusCode().value());
        verify(assignments, never()).save(any());
    }

    @Test
    void rejectsOrderAlreadyAssigned() {
        when(orderService.get(orderId)).thenReturn(new Order("Lima", "Cusco"));
        when(driverClient.find(driverId)).thenReturn(new DriverClient.DriverInfo(driverId, true));
        when(assignments.existsByOrderId(orderId)).thenReturn(true);

        assertThrows(ResponseStatusException.class, () -> service.assign(orderId, driverId));
    }

    @Test
    void storesPdf() throws IOException {
        UUID assignmentId = UUID.randomUUID();
        when(assignments.existsById(assignmentId)).thenReturn(true);
        when(files.save(any(AssignmentFile.class))).thenAnswer(i -> i.getArgument(0));
        var pdf = new MockMultipartFile("file", "doc.pdf", "application/pdf", new byte[] {1, 2, 3});

        AssignmentFile saved = service.addPdf(assignmentId, pdf);

        assertEquals("doc.pdf", saved.getFileName());
        assertEquals(3, saved.getContent().length);
    }

    @Test
    void rejectsPdfEndpointWithImage() {
        UUID assignmentId = UUID.randomUUID();
        when(assignments.existsById(assignmentId)).thenReturn(true);
        var image = new MockMultipartFile("file", "a.png", "image/png", new byte[] {1});

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> service.addPdf(assignmentId, image));

        assertEquals(415, ex.getStatusCode().value());
    }

    @Test
    void storesJpegImage() throws IOException {
        UUID assignmentId = UUID.randomUUID();
        when(assignments.existsById(assignmentId)).thenReturn(true);
        when(files.save(any(AssignmentFile.class))).thenAnswer(i -> i.getArgument(0));
        var image = new MockMultipartFile("file", "a.jpg", "image/jpeg", new byte[] {1});

        assertEquals("image/jpeg", service.addImage(assignmentId, image).getContentType());
    }

    @Test
    void rejectsFileForUnknownAssignment() {
        UUID assignmentId = UUID.randomUUID();
        when(assignments.existsById(assignmentId)).thenReturn(false);
        var pdf = new MockMultipartFile("file", "doc.pdf", "application/pdf", new byte[] {1});

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> service.addPdf(assignmentId, pdf));

        assertEquals(404, ex.getStatusCode().value());
    }
}

package com.vruiz.order.assignment;

import jakarta.validation.Valid;
import java.io.IOException;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/assignments")
public class AssignmentController {

    private final AssignmentService service;

    public AssignmentController(AssignmentService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AssignmentResponse assign(@Valid @RequestBody AssignmentRequest request) {
        return AssignmentResponse.from(service.assign(request.orderId(), request.driverId()));
    }

    /** Adjunta un PDF a la asignacion. */
    @PostMapping(path = "/{id}/files", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public FileResponse addPdf(@PathVariable UUID id, @RequestParam MultipartFile file) throws IOException {
        return FileResponse.from(service.addPdf(id, file));
    }

    /** Adjunta una imagen (png o jpg) a la asignacion. */
    @PostMapping(path = "/{id}/images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public FileResponse addImage(@PathVariable UUID id, @RequestParam MultipartFile file) throws IOException {
        return FileResponse.from(service.addImage(id, file));
    }
}

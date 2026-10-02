package com.vruiz.order.assignment;

import java.util.UUID;

public record FileResponse(UUID id, String fileName, String contentType, int size) {

    public static FileResponse from(AssignmentFile f) {
        return new FileResponse(f.getId(), f.getFileName(), f.getContentType(), f.getContent().length);
    }
}

package com.vruiz.order.assignment;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import java.util.UUID;

@Entity
public class AssignmentFile {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false)
    private UUID assignmentId;

    private String fileName;

    @Column(nullable = false)
    private String contentType;

    @Lob
    @Column(nullable = false)
    private byte[] content;

    protected AssignmentFile() {
    }

    public AssignmentFile(UUID assignmentId, String fileName, String contentType, byte[] content) {
        this.assignmentId = assignmentId;
        this.fileName = fileName;
        this.contentType = contentType;
        this.content = content;
    }

    public UUID getId() {
        return id;
    }

    public String getFileName() {
        return fileName;
    }

    public String getContentType() {
        return contentType;
    }

    public byte[] getContent() {
        return content;
    }
}

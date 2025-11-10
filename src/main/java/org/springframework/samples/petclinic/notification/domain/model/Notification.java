package org.springframework.samples.petclinic.notification.domain.model;

import java.time.LocalDate;

/**
 * Notification domain entity representing a notification about a visit.
 * This is part of the notification module's domain layer.
 *
 * @author GitHub Copilot
 */
public class Notification {
    private Integer id;
    private Integer visitId;
    private LocalDate visitDate;
    private String visitDescription;
    private Integer vetId;
    private String vetName;
    private String message;
    private NotificationStatus status;
    private String errorMessage;

    public Notification() {
    }

    public Notification(Integer visitId, LocalDate visitDate, String visitDescription,
                       Integer vetId, String vetName) {
        this.visitId = visitId;
        this.visitDate = visitDate;
        this.visitDescription = visitDescription;
        this.vetId = vetId;
        this.vetName = vetName;
        this.status = NotificationStatus.PENDING;
        this.message = createNotificationMessage();
    }

    private String createNotificationMessage() {
        return String.format("Visit scheduled for %s (%s) has been assigned to Dr. %s", 
                visitDate, visitDescription, vetName);
    }

    // Getters and setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getVisitId() {
        return visitId;
    }

    public void setVisitId(Integer visitId) {
        this.visitId = visitId;
    }

    public LocalDate getVisitDate() {
        return visitDate;
    }

    public void setVisitDate(LocalDate visitDate) {
        this.visitDate = visitDate;
    }

    public String getVisitDescription() {
        return visitDescription;
    }

    public void setVisitDescription(String visitDescription) {
        this.visitDescription = visitDescription;
    }

    public Integer getVetId() {
        return vetId;
    }

    public void setVetId(Integer vetId) {
        this.vetId = vetId;
    }

    public String getVetName() {
        return vetName;
    }

    public void setVetName(String vetName) {
        this.vetName = vetName;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public NotificationStatus getStatus() {
        return status;
    }

    public void setStatus(NotificationStatus status) {
        this.status = status;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public void markAsSent() {
        this.status = NotificationStatus.SENT;
        this.errorMessage = null;
    }

    public void markAsFailed(String errorMessage) {
        this.status = NotificationStatus.FAILED;
        this.errorMessage = errorMessage;
    }
}
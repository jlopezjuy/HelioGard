package ar.com.heliguard.app.service.dto;

import ar.com.heliguard.app.domain.enumeration.AlertSeverity;
import ar.com.heliguard.app.domain.enumeration.WorkOrderStatus;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link ar.com.heliguard.app.domain.WorkOrder} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class WorkOrderDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(max = 80)
    private String referenceCode;

    @NotNull
    @Size(max = 160)
    private String title;

    @NotNull
    private WorkOrderStatus status;

    @NotNull
    private AlertSeverity priority;

    @NotNull
    private Instant openedAt;

    private Instant scheduledAt;

    private Instant closedAt;

    private OrganizationDTO organization;

    @NotNull
    private InstallationDTO installation;

    private IncidentDTO incident;

    private PlanSubscriptionDTO subscription;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getReferenceCode() {
        return referenceCode;
    }

    public void setReferenceCode(String referenceCode) {
        this.referenceCode = referenceCode;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public WorkOrderStatus getStatus() {
        return status;
    }

    public void setStatus(WorkOrderStatus status) {
        this.status = status;
    }

    public AlertSeverity getPriority() {
        return priority;
    }

    public void setPriority(AlertSeverity priority) {
        this.priority = priority;
    }

    public Instant getOpenedAt() {
        return openedAt;
    }

    public void setOpenedAt(Instant openedAt) {
        this.openedAt = openedAt;
    }

    public Instant getScheduledAt() {
        return scheduledAt;
    }

    public void setScheduledAt(Instant scheduledAt) {
        this.scheduledAt = scheduledAt;
    }

    public Instant getClosedAt() {
        return closedAt;
    }

    public void setClosedAt(Instant closedAt) {
        this.closedAt = closedAt;
    }

    public OrganizationDTO getOrganization() {
        return organization;
    }

    public void setOrganization(OrganizationDTO organization) {
        this.organization = organization;
    }

    public InstallationDTO getInstallation() {
        return installation;
    }

    public void setInstallation(InstallationDTO installation) {
        this.installation = installation;
    }

    public IncidentDTO getIncident() {
        return incident;
    }

    public void setIncident(IncidentDTO incident) {
        this.incident = incident;
    }

    public PlanSubscriptionDTO getSubscription() {
        return subscription;
    }

    public void setSubscription(PlanSubscriptionDTO subscription) {
        this.subscription = subscription;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof WorkOrderDTO)) {
            return false;
        }

        WorkOrderDTO workOrderDTO = (WorkOrderDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, workOrderDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "WorkOrderDTO{" +
            "id=" + getId() +
            ", referenceCode='" + getReferenceCode() + "'" +
            ", title='" + getTitle() + "'" +
            ", status='" + getStatus() + "'" +
            ", priority='" + getPriority() + "'" +
            ", openedAt='" + getOpenedAt() + "'" +
            ", scheduledAt='" + getScheduledAt() + "'" +
            ", closedAt='" + getClosedAt() + "'" +
            ", organization=" + getOrganization() +
            ", installation=" + getInstallation() +
            ", incident=" + getIncident() +
            ", subscription=" + getSubscription() +
            "}";
    }
}

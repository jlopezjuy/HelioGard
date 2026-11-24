package ar.com.heliguard.app.domain;

import ar.com.heliguard.app.domain.enumeration.AlertSeverity;
import ar.com.heliguard.app.domain.enumeration.WorkOrderStatus;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A WorkOrder.
 */
@Table("work_order")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class WorkOrder implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @NotNull
    @Size(max = 80)
    @Column("reference_code")
    private String referenceCode;

    @NotNull
    @Size(max = 160)
    @Column("title")
    private String title;

    @NotNull
    @Column("status")
    private WorkOrderStatus status;

    @NotNull
    @Column("priority")
    private AlertSeverity priority;

    @NotNull
    @Column("opened_at")
    private Instant openedAt;

    @Column("scheduled_at")
    private Instant scheduledAt;

    @Column("closed_at")
    private Instant closedAt;

    @org.springframework.data.annotation.Transient
    private Organization organization;

    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "organization", "site", "climateRiskProfile" }, allowSetters = true)
    private Installation installation;

    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "organization", "installation", "alertRuleVersion" }, allowSetters = true)
    private Incident incident;

    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "organization", "installation", "planVersion", "serviceAgreement" }, allowSetters = true)
    private PlanSubscription subscription;

    @Column("organization_id")
    private Long organizationId;

    @Column("installation_id")
    private Long installationId;

    @Column("incident_id")
    private Long incidentId;

    @Column("subscription_id")
    private Long subscriptionId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public WorkOrder id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getReferenceCode() {
        return this.referenceCode;
    }

    public WorkOrder referenceCode(String referenceCode) {
        this.setReferenceCode(referenceCode);
        return this;
    }

    public void setReferenceCode(String referenceCode) {
        this.referenceCode = referenceCode;
    }

    public String getTitle() {
        return this.title;
    }

    public WorkOrder title(String title) {
        this.setTitle(title);
        return this;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public WorkOrderStatus getStatus() {
        return this.status;
    }

    public WorkOrder status(WorkOrderStatus status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(WorkOrderStatus status) {
        this.status = status;
    }

    public AlertSeverity getPriority() {
        return this.priority;
    }

    public WorkOrder priority(AlertSeverity priority) {
        this.setPriority(priority);
        return this;
    }

    public void setPriority(AlertSeverity priority) {
        this.priority = priority;
    }

    public Instant getOpenedAt() {
        return this.openedAt;
    }

    public WorkOrder openedAt(Instant openedAt) {
        this.setOpenedAt(openedAt);
        return this;
    }

    public void setOpenedAt(Instant openedAt) {
        this.openedAt = openedAt;
    }

    public Instant getScheduledAt() {
        return this.scheduledAt;
    }

    public WorkOrder scheduledAt(Instant scheduledAt) {
        this.setScheduledAt(scheduledAt);
        return this;
    }

    public void setScheduledAt(Instant scheduledAt) {
        this.scheduledAt = scheduledAt;
    }

    public Instant getClosedAt() {
        return this.closedAt;
    }

    public WorkOrder closedAt(Instant closedAt) {
        this.setClosedAt(closedAt);
        return this;
    }

    public void setClosedAt(Instant closedAt) {
        this.closedAt = closedAt;
    }

    public Organization getOrganization() {
        return this.organization;
    }

    public void setOrganization(Organization organization) {
        this.organization = organization;
        this.organizationId = organization != null ? organization.getId() : null;
    }

    public WorkOrder organization(Organization organization) {
        this.setOrganization(organization);
        return this;
    }

    public Installation getInstallation() {
        return this.installation;
    }

    public void setInstallation(Installation installation) {
        this.installation = installation;
        this.installationId = installation != null ? installation.getId() : null;
    }

    public WorkOrder installation(Installation installation) {
        this.setInstallation(installation);
        return this;
    }

    public Incident getIncident() {
        return this.incident;
    }

    public void setIncident(Incident incident) {
        this.incident = incident;
        this.incidentId = incident != null ? incident.getId() : null;
    }

    public WorkOrder incident(Incident incident) {
        this.setIncident(incident);
        return this;
    }

    public PlanSubscription getSubscription() {
        return this.subscription;
    }

    public void setSubscription(PlanSubscription planSubscription) {
        this.subscription = planSubscription;
        this.subscriptionId = planSubscription != null ? planSubscription.getId() : null;
    }

    public WorkOrder subscription(PlanSubscription planSubscription) {
        this.setSubscription(planSubscription);
        return this;
    }

    public Long getOrganizationId() {
        return this.organizationId;
    }

    public void setOrganizationId(Long organization) {
        this.organizationId = organization;
    }

    public Long getInstallationId() {
        return this.installationId;
    }

    public void setInstallationId(Long installation) {
        this.installationId = installation;
    }

    public Long getIncidentId() {
        return this.incidentId;
    }

    public void setIncidentId(Long incident) {
        this.incidentId = incident;
    }

    public Long getSubscriptionId() {
        return this.subscriptionId;
    }

    public void setSubscriptionId(Long planSubscription) {
        this.subscriptionId = planSubscription;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof WorkOrder)) {
            return false;
        }
        return getId() != null && getId().equals(((WorkOrder) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "WorkOrder{" +
            "id=" + getId() +
            ", referenceCode='" + getReferenceCode() + "'" +
            ", title='" + getTitle() + "'" +
            ", status='" + getStatus() + "'" +
            ", priority='" + getPriority() + "'" +
            ", openedAt='" + getOpenedAt() + "'" +
            ", scheduledAt='" + getScheduledAt() + "'" +
            ", closedAt='" + getClosedAt() + "'" +
            "}";
    }
}

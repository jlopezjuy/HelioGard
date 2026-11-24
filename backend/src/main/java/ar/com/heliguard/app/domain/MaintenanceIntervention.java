package ar.com.heliguard.app.domain;

import ar.com.heliguard.app.domain.enumeration.InterventionType;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A MaintenanceIntervention.
 */
@Table("maintenance_intervention")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class MaintenanceIntervention implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @NotNull
    @Column("type")
    private InterventionType type;

    @Column("started_at")
    private Instant startedAt;

    @Column("finished_at")
    private Instant finishedAt;

    @Column("work_cost")
    private BigDecimal workCost;

    @Size(max = 3)
    @Column("currency_code")
    private String currencyCode;

    @Column("notes")
    private String notes;

    @org.springframework.data.annotation.Transient
    private Organization organization;

    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "organization", "installation", "incident", "subscription" }, allowSetters = true)
    private WorkOrder workOrder;

    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "organization", "member" }, allowSetters = true)
    private OrganizationMembership performedByMembership;

    @Column("organization_id")
    private Long organizationId;

    @Column("work_order_id")
    private Long workOrderId;

    @Column("performed_by_membership_id")
    private Long performedByMembershipId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public MaintenanceIntervention id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public InterventionType getType() {
        return this.type;
    }

    public MaintenanceIntervention type(InterventionType type) {
        this.setType(type);
        return this;
    }

    public void setType(InterventionType type) {
        this.type = type;
    }

    public Instant getStartedAt() {
        return this.startedAt;
    }

    public MaintenanceIntervention startedAt(Instant startedAt) {
        this.setStartedAt(startedAt);
        return this;
    }

    public void setStartedAt(Instant startedAt) {
        this.startedAt = startedAt;
    }

    public Instant getFinishedAt() {
        return this.finishedAt;
    }

    public MaintenanceIntervention finishedAt(Instant finishedAt) {
        this.setFinishedAt(finishedAt);
        return this;
    }

    public void setFinishedAt(Instant finishedAt) {
        this.finishedAt = finishedAt;
    }

    public BigDecimal getWorkCost() {
        return this.workCost;
    }

    public MaintenanceIntervention workCost(BigDecimal workCost) {
        this.setWorkCost(workCost);
        return this;
    }

    public void setWorkCost(BigDecimal workCost) {
        this.workCost = workCost;
    }

    public String getCurrencyCode() {
        return this.currencyCode;
    }

    public MaintenanceIntervention currencyCode(String currencyCode) {
        this.setCurrencyCode(currencyCode);
        return this;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public String getNotes() {
        return this.notes;
    }

    public MaintenanceIntervention notes(String notes) {
        this.setNotes(notes);
        return this;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Organization getOrganization() {
        return this.organization;
    }

    public void setOrganization(Organization organization) {
        this.organization = organization;
        this.organizationId = organization != null ? organization.getId() : null;
    }

    public MaintenanceIntervention organization(Organization organization) {
        this.setOrganization(organization);
        return this;
    }

    public WorkOrder getWorkOrder() {
        return this.workOrder;
    }

    public void setWorkOrder(WorkOrder workOrder) {
        this.workOrder = workOrder;
        this.workOrderId = workOrder != null ? workOrder.getId() : null;
    }

    public MaintenanceIntervention workOrder(WorkOrder workOrder) {
        this.setWorkOrder(workOrder);
        return this;
    }

    public OrganizationMembership getPerformedByMembership() {
        return this.performedByMembership;
    }

    public void setPerformedByMembership(OrganizationMembership organizationMembership) {
        this.performedByMembership = organizationMembership;
        this.performedByMembershipId = organizationMembership != null ? organizationMembership.getId() : null;
    }

    public MaintenanceIntervention performedByMembership(OrganizationMembership organizationMembership) {
        this.setPerformedByMembership(organizationMembership);
        return this;
    }

    public Long getOrganizationId() {
        return this.organizationId;
    }

    public void setOrganizationId(Long organization) {
        this.organizationId = organization;
    }

    public Long getWorkOrderId() {
        return this.workOrderId;
    }

    public void setWorkOrderId(Long workOrder) {
        this.workOrderId = workOrder;
    }

    public Long getPerformedByMembershipId() {
        return this.performedByMembershipId;
    }

    public void setPerformedByMembershipId(Long organizationMembership) {
        this.performedByMembershipId = organizationMembership;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof MaintenanceIntervention)) {
            return false;
        }
        return getId() != null && getId().equals(((MaintenanceIntervention) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "MaintenanceIntervention{" +
            "id=" + getId() +
            ", type='" + getType() + "'" +
            ", startedAt='" + getStartedAt() + "'" +
            ", finishedAt='" + getFinishedAt() + "'" +
            ", workCost=" + getWorkCost() +
            ", currencyCode='" + getCurrencyCode() + "'" +
            ", notes='" + getNotes() + "'" +
            "}";
    }
}

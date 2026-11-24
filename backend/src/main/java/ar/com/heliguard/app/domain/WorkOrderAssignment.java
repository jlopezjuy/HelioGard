package ar.com.heliguard.app.domain;

import ar.com.heliguard.app.domain.enumeration.AssignmentStatus;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A WorkOrderAssignment.
 */
@Table("work_order_assignment")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class WorkOrderAssignment implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @NotNull
    @Column("status")
    private AssignmentStatus status;

    @NotNull
    @Column("assigned_at")
    private Instant assignedAt;

    @Column("accepted_at")
    private Instant acceptedAt;

    @Column("released_at")
    private Instant releasedAt;

    @Size(max = 300)
    @Column("notes")
    private String notes;

    @org.springframework.data.annotation.Transient
    private Organization organization;

    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "organization", "installation", "incident", "subscription" }, allowSetters = true)
    private WorkOrder workOrder;

    @org.springframework.data.annotation.Transient
    private Organization providerOrganization;

    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "organization", "member" }, allowSetters = true)
    private OrganizationMembership assigneeMembership;

    @Column("organization_id")
    private Long organizationId;

    @Column("work_order_id")
    private Long workOrderId;

    @Column("provider_organization_id")
    private Long providerOrganizationId;

    @Column("assignee_membership_id")
    private Long assigneeMembershipId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public WorkOrderAssignment id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public AssignmentStatus getStatus() {
        return this.status;
    }

    public WorkOrderAssignment status(AssignmentStatus status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(AssignmentStatus status) {
        this.status = status;
    }

    public Instant getAssignedAt() {
        return this.assignedAt;
    }

    public WorkOrderAssignment assignedAt(Instant assignedAt) {
        this.setAssignedAt(assignedAt);
        return this;
    }

    public void setAssignedAt(Instant assignedAt) {
        this.assignedAt = assignedAt;
    }

    public Instant getAcceptedAt() {
        return this.acceptedAt;
    }

    public WorkOrderAssignment acceptedAt(Instant acceptedAt) {
        this.setAcceptedAt(acceptedAt);
        return this;
    }

    public void setAcceptedAt(Instant acceptedAt) {
        this.acceptedAt = acceptedAt;
    }

    public Instant getReleasedAt() {
        return this.releasedAt;
    }

    public WorkOrderAssignment releasedAt(Instant releasedAt) {
        this.setReleasedAt(releasedAt);
        return this;
    }

    public void setReleasedAt(Instant releasedAt) {
        this.releasedAt = releasedAt;
    }

    public String getNotes() {
        return this.notes;
    }

    public WorkOrderAssignment notes(String notes) {
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

    public WorkOrderAssignment organization(Organization organization) {
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

    public WorkOrderAssignment workOrder(WorkOrder workOrder) {
        this.setWorkOrder(workOrder);
        return this;
    }

    public Organization getProviderOrganization() {
        return this.providerOrganization;
    }

    public void setProviderOrganization(Organization organization) {
        this.providerOrganization = organization;
        this.providerOrganizationId = organization != null ? organization.getId() : null;
    }

    public WorkOrderAssignment providerOrganization(Organization organization) {
        this.setProviderOrganization(organization);
        return this;
    }

    public OrganizationMembership getAssigneeMembership() {
        return this.assigneeMembership;
    }

    public void setAssigneeMembership(OrganizationMembership organizationMembership) {
        this.assigneeMembership = organizationMembership;
        this.assigneeMembershipId = organizationMembership != null ? organizationMembership.getId() : null;
    }

    public WorkOrderAssignment assigneeMembership(OrganizationMembership organizationMembership) {
        this.setAssigneeMembership(organizationMembership);
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

    public Long getProviderOrganizationId() {
        return this.providerOrganizationId;
    }

    public void setProviderOrganizationId(Long organization) {
        this.providerOrganizationId = organization;
    }

    public Long getAssigneeMembershipId() {
        return this.assigneeMembershipId;
    }

    public void setAssigneeMembershipId(Long organizationMembership) {
        this.assigneeMembershipId = organizationMembership;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof WorkOrderAssignment)) {
            return false;
        }
        return getId() != null && getId().equals(((WorkOrderAssignment) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "WorkOrderAssignment{" +
            "id=" + getId() +
            ", status='" + getStatus() + "'" +
            ", assignedAt='" + getAssignedAt() + "'" +
            ", acceptedAt='" + getAcceptedAt() + "'" +
            ", releasedAt='" + getReleasedAt() + "'" +
            ", notes='" + getNotes() + "'" +
            "}";
    }
}

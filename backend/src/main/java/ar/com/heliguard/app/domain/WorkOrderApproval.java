package ar.com.heliguard.app.domain;

import ar.com.heliguard.app.domain.enumeration.WorkApprovalStatus;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A WorkOrderApproval.
 */
@Table("work_order_approval")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class WorkOrderApproval implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @NotNull
    @Column("status")
    private WorkApprovalStatus status;

    @NotNull
    @Column("requested_at")
    private Instant requestedAt;

    @Column("decided_at")
    private Instant decidedAt;

    @Column("expires_at")
    private Instant expiresAt;

    @Size(max = 400)
    @Column("consent_reference")
    private String consentReference;

    @Size(max = 300)
    @Column("decision_note")
    private String decisionNote;

    @org.springframework.data.annotation.Transient
    private Organization organization;

    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "organization", "installation", "incident", "subscription" }, allowSetters = true)
    private WorkOrder workOrder;

    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "organization", "member" }, allowSetters = true)
    private OrganizationMembership decidedByMembership;

    @Column("organization_id")
    private Long organizationId;

    @Column("work_order_id")
    private Long workOrderId;

    @Column("decided_by_membership_id")
    private Long decidedByMembershipId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public WorkOrderApproval id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public WorkApprovalStatus getStatus() {
        return this.status;
    }

    public WorkOrderApproval status(WorkApprovalStatus status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(WorkApprovalStatus status) {
        this.status = status;
    }

    public Instant getRequestedAt() {
        return this.requestedAt;
    }

    public WorkOrderApproval requestedAt(Instant requestedAt) {
        this.setRequestedAt(requestedAt);
        return this;
    }

    public void setRequestedAt(Instant requestedAt) {
        this.requestedAt = requestedAt;
    }

    public Instant getDecidedAt() {
        return this.decidedAt;
    }

    public WorkOrderApproval decidedAt(Instant decidedAt) {
        this.setDecidedAt(decidedAt);
        return this;
    }

    public void setDecidedAt(Instant decidedAt) {
        this.decidedAt = decidedAt;
    }

    public Instant getExpiresAt() {
        return this.expiresAt;
    }

    public WorkOrderApproval expiresAt(Instant expiresAt) {
        this.setExpiresAt(expiresAt);
        return this;
    }

    public void setExpiresAt(Instant expiresAt) {
        this.expiresAt = expiresAt;
    }

    public String getConsentReference() {
        return this.consentReference;
    }

    public WorkOrderApproval consentReference(String consentReference) {
        this.setConsentReference(consentReference);
        return this;
    }

    public void setConsentReference(String consentReference) {
        this.consentReference = consentReference;
    }

    public String getDecisionNote() {
        return this.decisionNote;
    }

    public WorkOrderApproval decisionNote(String decisionNote) {
        this.setDecisionNote(decisionNote);
        return this;
    }

    public void setDecisionNote(String decisionNote) {
        this.decisionNote = decisionNote;
    }

    public Organization getOrganization() {
        return this.organization;
    }

    public void setOrganization(Organization organization) {
        this.organization = organization;
        this.organizationId = organization != null ? organization.getId() : null;
    }

    public WorkOrderApproval organization(Organization organization) {
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

    public WorkOrderApproval workOrder(WorkOrder workOrder) {
        this.setWorkOrder(workOrder);
        return this;
    }

    public OrganizationMembership getDecidedByMembership() {
        return this.decidedByMembership;
    }

    public void setDecidedByMembership(OrganizationMembership organizationMembership) {
        this.decidedByMembership = organizationMembership;
        this.decidedByMembershipId = organizationMembership != null ? organizationMembership.getId() : null;
    }

    public WorkOrderApproval decidedByMembership(OrganizationMembership organizationMembership) {
        this.setDecidedByMembership(organizationMembership);
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

    public Long getDecidedByMembershipId() {
        return this.decidedByMembershipId;
    }

    public void setDecidedByMembershipId(Long organizationMembership) {
        this.decidedByMembershipId = organizationMembership;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof WorkOrderApproval)) {
            return false;
        }
        return getId() != null && getId().equals(((WorkOrderApproval) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "WorkOrderApproval{" +
            "id=" + getId() +
            ", status='" + getStatus() + "'" +
            ", requestedAt='" + getRequestedAt() + "'" +
            ", decidedAt='" + getDecidedAt() + "'" +
            ", expiresAt='" + getExpiresAt() + "'" +
            ", consentReference='" + getConsentReference() + "'" +
            ", decisionNote='" + getDecisionNote() + "'" +
            "}";
    }
}

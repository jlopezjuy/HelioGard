package ar.com.heliguard.app.service.dto;

import ar.com.heliguard.app.domain.enumeration.WorkApprovalStatus;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link ar.com.heliguard.app.domain.WorkOrderApproval} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class WorkOrderApprovalDTO implements Serializable {

    private Long id;

    @NotNull
    private WorkApprovalStatus status;

    @NotNull
    private Instant requestedAt;

    private Instant decidedAt;

    private Instant expiresAt;

    @Size(max = 400)
    private String consentReference;

    @Size(max = 300)
    private String decisionNote;

    private OrganizationDTO organization;

    @NotNull
    private WorkOrderDTO workOrder;

    private OrganizationMembershipDTO decidedByMembership;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public WorkApprovalStatus getStatus() {
        return status;
    }

    public void setStatus(WorkApprovalStatus status) {
        this.status = status;
    }

    public Instant getRequestedAt() {
        return requestedAt;
    }

    public void setRequestedAt(Instant requestedAt) {
        this.requestedAt = requestedAt;
    }

    public Instant getDecidedAt() {
        return decidedAt;
    }

    public void setDecidedAt(Instant decidedAt) {
        this.decidedAt = decidedAt;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(Instant expiresAt) {
        this.expiresAt = expiresAt;
    }

    public String getConsentReference() {
        return consentReference;
    }

    public void setConsentReference(String consentReference) {
        this.consentReference = consentReference;
    }

    public String getDecisionNote() {
        return decisionNote;
    }

    public void setDecisionNote(String decisionNote) {
        this.decisionNote = decisionNote;
    }

    public OrganizationDTO getOrganization() {
        return organization;
    }

    public void setOrganization(OrganizationDTO organization) {
        this.organization = organization;
    }

    public WorkOrderDTO getWorkOrder() {
        return workOrder;
    }

    public void setWorkOrder(WorkOrderDTO workOrder) {
        this.workOrder = workOrder;
    }

    public OrganizationMembershipDTO getDecidedByMembership() {
        return decidedByMembership;
    }

    public void setDecidedByMembership(OrganizationMembershipDTO decidedByMembership) {
        this.decidedByMembership = decidedByMembership;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof WorkOrderApprovalDTO)) {
            return false;
        }

        WorkOrderApprovalDTO workOrderApprovalDTO = (WorkOrderApprovalDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, workOrderApprovalDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "WorkOrderApprovalDTO{" +
            "id=" + getId() +
            ", status='" + getStatus() + "'" +
            ", requestedAt='" + getRequestedAt() + "'" +
            ", decidedAt='" + getDecidedAt() + "'" +
            ", expiresAt='" + getExpiresAt() + "'" +
            ", consentReference='" + getConsentReference() + "'" +
            ", decisionNote='" + getDecisionNote() + "'" +
            ", organization=" + getOrganization() +
            ", workOrder=" + getWorkOrder() +
            ", decidedByMembership=" + getDecidedByMembership() +
            "}";
    }
}

package ar.com.heliguard.app.service.dto;

import ar.com.heliguard.app.domain.enumeration.AssignmentStatus;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link ar.com.heliguard.app.domain.WorkOrderAssignment} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class WorkOrderAssignmentDTO implements Serializable {

    private Long id;

    @NotNull
    private AssignmentStatus status;

    @NotNull
    private Instant assignedAt;

    private Instant acceptedAt;

    private Instant releasedAt;

    @Size(max = 300)
    private String notes;

    private OrganizationDTO organization;

    @NotNull
    private WorkOrderDTO workOrder;

    @NotNull
    private OrganizationDTO providerOrganization;

    @NotNull
    private OrganizationMembershipDTO assigneeMembership;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public AssignmentStatus getStatus() {
        return status;
    }

    public void setStatus(AssignmentStatus status) {
        this.status = status;
    }

    public Instant getAssignedAt() {
        return assignedAt;
    }

    public void setAssignedAt(Instant assignedAt) {
        this.assignedAt = assignedAt;
    }

    public Instant getAcceptedAt() {
        return acceptedAt;
    }

    public void setAcceptedAt(Instant acceptedAt) {
        this.acceptedAt = acceptedAt;
    }

    public Instant getReleasedAt() {
        return releasedAt;
    }

    public void setReleasedAt(Instant releasedAt) {
        this.releasedAt = releasedAt;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
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

    public OrganizationDTO getProviderOrganization() {
        return providerOrganization;
    }

    public void setProviderOrganization(OrganizationDTO providerOrganization) {
        this.providerOrganization = providerOrganization;
    }

    public OrganizationMembershipDTO getAssigneeMembership() {
        return assigneeMembership;
    }

    public void setAssigneeMembership(OrganizationMembershipDTO assigneeMembership) {
        this.assigneeMembership = assigneeMembership;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof WorkOrderAssignmentDTO)) {
            return false;
        }

        WorkOrderAssignmentDTO workOrderAssignmentDTO = (WorkOrderAssignmentDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, workOrderAssignmentDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "WorkOrderAssignmentDTO{" +
            "id=" + getId() +
            ", status='" + getStatus() + "'" +
            ", assignedAt='" + getAssignedAt() + "'" +
            ", acceptedAt='" + getAcceptedAt() + "'" +
            ", releasedAt='" + getReleasedAt() + "'" +
            ", notes='" + getNotes() + "'" +
            ", organization=" + getOrganization() +
            ", workOrder=" + getWorkOrder() +
            ", providerOrganization=" + getProviderOrganization() +
            ", assigneeMembership=" + getAssigneeMembership() +
            "}";
    }
}

package ar.com.heliguard.app.service.dto;

import ar.com.heliguard.app.domain.enumeration.InterventionType;
import jakarta.persistence.Lob;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link ar.com.heliguard.app.domain.MaintenanceIntervention} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class MaintenanceInterventionDTO implements Serializable {

    private Long id;

    @NotNull
    private InterventionType type;

    private Instant startedAt;

    private Instant finishedAt;

    private BigDecimal workCost;

    @Size(max = 3)
    private String currencyCode;

    @Lob
    private String notes;

    @NotNull
    private OrganizationDTO organization;

    @NotNull
    private WorkOrderDTO workOrder;

    private OrganizationMembershipDTO performedByMembership;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public InterventionType getType() {
        return type;
    }

    public void setType(InterventionType type) {
        this.type = type;
    }

    public Instant getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(Instant startedAt) {
        this.startedAt = startedAt;
    }

    public Instant getFinishedAt() {
        return finishedAt;
    }

    public void setFinishedAt(Instant finishedAt) {
        this.finishedAt = finishedAt;
    }

    public BigDecimal getWorkCost() {
        return workCost;
    }

    public void setWorkCost(BigDecimal workCost) {
        this.workCost = workCost;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
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

    public OrganizationMembershipDTO getPerformedByMembership() {
        return performedByMembership;
    }

    public void setPerformedByMembership(OrganizationMembershipDTO performedByMembership) {
        this.performedByMembership = performedByMembership;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof MaintenanceInterventionDTO)) {
            return false;
        }

        MaintenanceInterventionDTO maintenanceInterventionDTO = (MaintenanceInterventionDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, maintenanceInterventionDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "MaintenanceInterventionDTO{" +
            "id=" + getId() +
            ", type='" + getType() + "'" +
            ", startedAt='" + getStartedAt() + "'" +
            ", finishedAt='" + getFinishedAt() + "'" +
            ", workCost=" + getWorkCost() +
            ", currencyCode='" + getCurrencyCode() + "'" +
            ", notes='" + getNotes() + "'" +
            ", organization=" + getOrganization() +
            ", workOrder=" + getWorkOrder() +
            ", performedByMembership=" + getPerformedByMembership() +
            "}";
    }
}

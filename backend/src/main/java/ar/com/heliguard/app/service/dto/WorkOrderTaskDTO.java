package ar.com.heliguard.app.service.dto;

import ar.com.heliguard.app.domain.enumeration.WorkTaskStatus;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link ar.com.heliguard.app.domain.WorkOrderTask} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class WorkOrderTaskDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(max = 300)
    private String description;

    @NotNull
    private WorkTaskStatus status;

    @NotNull
    private Integer sortOrder;

    private OrganizationDTO organization;

    @NotNull
    private WorkOrderDTO workOrder;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public WorkTaskStatus getStatus() {
        return status;
    }

    public void setStatus(WorkTaskStatus status) {
        this.status = status;
    }

    public Integer getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof WorkOrderTaskDTO)) {
            return false;
        }

        WorkOrderTaskDTO workOrderTaskDTO = (WorkOrderTaskDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, workOrderTaskDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "WorkOrderTaskDTO{" +
            "id=" + getId() +
            ", description='" + getDescription() + "'" +
            ", status='" + getStatus() + "'" +
            ", sortOrder=" + getSortOrder() +
            ", organization=" + getOrganization() +
            ", workOrder=" + getWorkOrder() +
            "}";
    }
}

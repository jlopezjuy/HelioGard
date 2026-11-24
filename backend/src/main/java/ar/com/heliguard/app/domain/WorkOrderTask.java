package ar.com.heliguard.app.domain;

import ar.com.heliguard.app.domain.enumeration.WorkTaskStatus;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A WorkOrderTask.
 */
@Table("work_order_task")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class WorkOrderTask implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @NotNull
    @Size(max = 300)
    @Column("description")
    private String description;

    @NotNull
    @Column("status")
    private WorkTaskStatus status;

    @NotNull
    @Column("sort_order")
    private Integer sortOrder;

    @org.springframework.data.annotation.Transient
    private Organization organization;

    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "organization", "installation", "incident", "subscription" }, allowSetters = true)
    private WorkOrder workOrder;

    @Column("organization_id")
    private Long organizationId;

    @Column("work_order_id")
    private Long workOrderId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public WorkOrderTask id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescription() {
        return this.description;
    }

    public WorkOrderTask description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public WorkTaskStatus getStatus() {
        return this.status;
    }

    public WorkOrderTask status(WorkTaskStatus status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(WorkTaskStatus status) {
        this.status = status;
    }

    public Integer getSortOrder() {
        return this.sortOrder;
    }

    public WorkOrderTask sortOrder(Integer sortOrder) {
        this.setSortOrder(sortOrder);
        return this;
    }

    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }

    public Organization getOrganization() {
        return this.organization;
    }

    public void setOrganization(Organization organization) {
        this.organization = organization;
        this.organizationId = organization != null ? organization.getId() : null;
    }

    public WorkOrderTask organization(Organization organization) {
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

    public WorkOrderTask workOrder(WorkOrder workOrder) {
        this.setWorkOrder(workOrder);
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

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof WorkOrderTask)) {
            return false;
        }
        return getId() != null && getId().equals(((WorkOrderTask) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "WorkOrderTask{" +
            "id=" + getId() +
            ", description='" + getDescription() + "'" +
            ", status='" + getStatus() + "'" +
            ", sortOrder=" + getSortOrder() +
            "}";
    }
}

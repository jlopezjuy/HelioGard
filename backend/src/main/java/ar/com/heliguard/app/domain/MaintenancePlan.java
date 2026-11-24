package ar.com.heliguard.app.domain;

import ar.com.heliguard.app.domain.enumeration.MaintenancePlanStatus;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A MaintenancePlan.
 */
@Table("maintenance_plan")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class MaintenancePlan implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @NotNull
    @Size(max = 150)
    @Column("name")
    private String name;

    @NotNull
    @Column("status")
    private MaintenancePlanStatus status;

    @org.springframework.data.annotation.Transient
    private Organization organization;

    @Column("organization_id")
    private Long organizationId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public MaintenancePlan id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public MaintenancePlan name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public MaintenancePlanStatus getStatus() {
        return this.status;
    }

    public MaintenancePlan status(MaintenancePlanStatus status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(MaintenancePlanStatus status) {
        this.status = status;
    }

    public Organization getOrganization() {
        return this.organization;
    }

    public void setOrganization(Organization organization) {
        this.organization = organization;
        this.organizationId = organization != null ? organization.getId() : null;
    }

    public MaintenancePlan organization(Organization organization) {
        this.setOrganization(organization);
        return this;
    }

    public Long getOrganizationId() {
        return this.organizationId;
    }

    public void setOrganizationId(Long organization) {
        this.organizationId = organization;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof MaintenancePlan)) {
            return false;
        }
        return getId() != null && getId().equals(((MaintenancePlan) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "MaintenancePlan{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", status='" + getStatus() + "'" +
            "}";
    }
}

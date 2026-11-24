package ar.com.heliguard.app.service.dto;

import ar.com.heliguard.app.domain.enumeration.MaintenancePlanStatus;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link ar.com.heliguard.app.domain.MaintenancePlan} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class MaintenancePlanDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(max = 150)
    private String name;

    @NotNull
    private MaintenancePlanStatus status;

    private OrganizationDTO organization;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public MaintenancePlanStatus getStatus() {
        return status;
    }

    public void setStatus(MaintenancePlanStatus status) {
        this.status = status;
    }

    public OrganizationDTO getOrganization() {
        return organization;
    }

    public void setOrganization(OrganizationDTO organization) {
        this.organization = organization;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof MaintenancePlanDTO)) {
            return false;
        }

        MaintenancePlanDTO maintenancePlanDTO = (MaintenancePlanDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, maintenancePlanDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "MaintenancePlanDTO{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", status='" + getStatus() + "'" +
            ", organization=" + getOrganization() +
            "}";
    }
}

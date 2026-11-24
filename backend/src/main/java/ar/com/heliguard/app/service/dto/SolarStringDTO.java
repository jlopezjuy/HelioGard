package ar.com.heliguard.app.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link ar.com.heliguard.app.domain.SolarString} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class SolarStringDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(max = 80)
    private String stringCode;

    @NotNull
    @Min(value = 1)
    private Integer modulesInSeries;

    @NotNull
    private Boolean active;

    private OrganizationDTO organization;

    @NotNull
    private SolarArrayDTO solarArray;

    private EquipmentModelDTO moduleModel;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStringCode() {
        return stringCode;
    }

    public void setStringCode(String stringCode) {
        this.stringCode = stringCode;
    }

    public Integer getModulesInSeries() {
        return modulesInSeries;
    }

    public void setModulesInSeries(Integer modulesInSeries) {
        this.modulesInSeries = modulesInSeries;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public OrganizationDTO getOrganization() {
        return organization;
    }

    public void setOrganization(OrganizationDTO organization) {
        this.organization = organization;
    }

    public SolarArrayDTO getSolarArray() {
        return solarArray;
    }

    public void setSolarArray(SolarArrayDTO solarArray) {
        this.solarArray = solarArray;
    }

    public EquipmentModelDTO getModuleModel() {
        return moduleModel;
    }

    public void setModuleModel(EquipmentModelDTO moduleModel) {
        this.moduleModel = moduleModel;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SolarStringDTO)) {
            return false;
        }

        SolarStringDTO solarStringDTO = (SolarStringDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, solarStringDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "SolarStringDTO{" +
            "id=" + getId() +
            ", stringCode='" + getStringCode() + "'" +
            ", modulesInSeries=" + getModulesInSeries() +
            ", active='" + getActive() + "'" +
            ", organization=" + getOrganization() +
            ", solarArray=" + getSolarArray() +
            ", moduleModel=" + getModuleModel() +
            "}";
    }
}

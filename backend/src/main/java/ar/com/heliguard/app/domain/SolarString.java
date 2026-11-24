package ar.com.heliguard.app.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A SolarString.
 */
@Table("solar_string")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class SolarString implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @NotNull
    @Size(max = 80)
    @Column("string_code")
    private String stringCode;

    @NotNull
    @Min(value = 1)
    @Column("modules_in_series")
    private Integer modulesInSeries;

    @NotNull
    @Column("active")
    private Boolean active;

    @org.springframework.data.annotation.Transient
    private Organization organization;

    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "organization", "installation", "solarSector" }, allowSetters = true)
    private SolarArray solarArray;

    @org.springframework.data.annotation.Transient
    private EquipmentModel moduleModel;

    @Column("organization_id")
    private Long organizationId;

    @Column("solar_array_id")
    private Long solarArrayId;

    @Column("module_model_id")
    private Long moduleModelId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public SolarString id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStringCode() {
        return this.stringCode;
    }

    public SolarString stringCode(String stringCode) {
        this.setStringCode(stringCode);
        return this;
    }

    public void setStringCode(String stringCode) {
        this.stringCode = stringCode;
    }

    public Integer getModulesInSeries() {
        return this.modulesInSeries;
    }

    public SolarString modulesInSeries(Integer modulesInSeries) {
        this.setModulesInSeries(modulesInSeries);
        return this;
    }

    public void setModulesInSeries(Integer modulesInSeries) {
        this.modulesInSeries = modulesInSeries;
    }

    public Boolean getActive() {
        return this.active;
    }

    public SolarString active(Boolean active) {
        this.setActive(active);
        return this;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public Organization getOrganization() {
        return this.organization;
    }

    public void setOrganization(Organization organization) {
        this.organization = organization;
        this.organizationId = organization != null ? organization.getId() : null;
    }

    public SolarString organization(Organization organization) {
        this.setOrganization(organization);
        return this;
    }

    public SolarArray getSolarArray() {
        return this.solarArray;
    }

    public void setSolarArray(SolarArray solarArray) {
        this.solarArray = solarArray;
        this.solarArrayId = solarArray != null ? solarArray.getId() : null;
    }

    public SolarString solarArray(SolarArray solarArray) {
        this.setSolarArray(solarArray);
        return this;
    }

    public EquipmentModel getModuleModel() {
        return this.moduleModel;
    }

    public void setModuleModel(EquipmentModel equipmentModel) {
        this.moduleModel = equipmentModel;
        this.moduleModelId = equipmentModel != null ? equipmentModel.getId() : null;
    }

    public SolarString moduleModel(EquipmentModel equipmentModel) {
        this.setModuleModel(equipmentModel);
        return this;
    }

    public Long getOrganizationId() {
        return this.organizationId;
    }

    public void setOrganizationId(Long organization) {
        this.organizationId = organization;
    }

    public Long getSolarArrayId() {
        return this.solarArrayId;
    }

    public void setSolarArrayId(Long solarArray) {
        this.solarArrayId = solarArray;
    }

    public Long getModuleModelId() {
        return this.moduleModelId;
    }

    public void setModuleModelId(Long equipmentModel) {
        this.moduleModelId = equipmentModel;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SolarString)) {
            return false;
        }
        return getId() != null && getId().equals(((SolarString) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "SolarString{" +
            "id=" + getId() +
            ", stringCode='" + getStringCode() + "'" +
            ", modulesInSeries=" + getModulesInSeries() +
            ", active='" + getActive() + "'" +
            "}";
    }
}

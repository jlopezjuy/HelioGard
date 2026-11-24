package ar.com.heliguard.app.domain;

import ar.com.heliguard.app.domain.enumeration.EquipmentStatus;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A Equipment.
 */
@Table("equipment")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Equipment implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @NotNull
    @Size(max = 120)
    @Column("tag")
    private String tag;

    @Size(max = 160)
    @Column("serial_number")
    private String serialNumber;

    @NotNull
    @Column("status")
    private EquipmentStatus status;

    @Column("installed_on")
    private LocalDate installedOn;

    @Column("removed_on")
    private LocalDate removedOn;

    @org.springframework.data.annotation.Transient
    private Organization organization;

    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "organization", "site", "climateRiskProfile" }, allowSetters = true)
    private Installation installation;

    @org.springframework.data.annotation.Transient
    private EquipmentModel equipmentModel;

    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "organization", "solarArray", "moduleModel" }, allowSetters = true)
    private SolarString solarString;

    @Column("organization_id")
    private Long organizationId;

    @Column("installation_id")
    private Long installationId;

    @Column("equipment_model_id")
    private Long equipmentModelId;

    @Column("solar_string_id")
    private Long solarStringId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Equipment id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTag() {
        return this.tag;
    }

    public Equipment tag(String tag) {
        this.setTag(tag);
        return this;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getSerialNumber() {
        return this.serialNumber;
    }

    public Equipment serialNumber(String serialNumber) {
        this.setSerialNumber(serialNumber);
        return this;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public EquipmentStatus getStatus() {
        return this.status;
    }

    public Equipment status(EquipmentStatus status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(EquipmentStatus status) {
        this.status = status;
    }

    public LocalDate getInstalledOn() {
        return this.installedOn;
    }

    public Equipment installedOn(LocalDate installedOn) {
        this.setInstalledOn(installedOn);
        return this;
    }

    public void setInstalledOn(LocalDate installedOn) {
        this.installedOn = installedOn;
    }

    public LocalDate getRemovedOn() {
        return this.removedOn;
    }

    public Equipment removedOn(LocalDate removedOn) {
        this.setRemovedOn(removedOn);
        return this;
    }

    public void setRemovedOn(LocalDate removedOn) {
        this.removedOn = removedOn;
    }

    public Organization getOrganization() {
        return this.organization;
    }

    public void setOrganization(Organization organization) {
        this.organization = organization;
        this.organizationId = organization != null ? organization.getId() : null;
    }

    public Equipment organization(Organization organization) {
        this.setOrganization(organization);
        return this;
    }

    public Installation getInstallation() {
        return this.installation;
    }

    public void setInstallation(Installation installation) {
        this.installation = installation;
        this.installationId = installation != null ? installation.getId() : null;
    }

    public Equipment installation(Installation installation) {
        this.setInstallation(installation);
        return this;
    }

    public EquipmentModel getEquipmentModel() {
        return this.equipmentModel;
    }

    public void setEquipmentModel(EquipmentModel equipmentModel) {
        this.equipmentModel = equipmentModel;
        this.equipmentModelId = equipmentModel != null ? equipmentModel.getId() : null;
    }

    public Equipment equipmentModel(EquipmentModel equipmentModel) {
        this.setEquipmentModel(equipmentModel);
        return this;
    }

    public SolarString getSolarString() {
        return this.solarString;
    }

    public void setSolarString(SolarString solarString) {
        this.solarString = solarString;
        this.solarStringId = solarString != null ? solarString.getId() : null;
    }

    public Equipment solarString(SolarString solarString) {
        this.setSolarString(solarString);
        return this;
    }

    public Long getOrganizationId() {
        return this.organizationId;
    }

    public void setOrganizationId(Long organization) {
        this.organizationId = organization;
    }

    public Long getInstallationId() {
        return this.installationId;
    }

    public void setInstallationId(Long installation) {
        this.installationId = installation;
    }

    public Long getEquipmentModelId() {
        return this.equipmentModelId;
    }

    public void setEquipmentModelId(Long equipmentModel) {
        this.equipmentModelId = equipmentModel;
    }

    public Long getSolarStringId() {
        return this.solarStringId;
    }

    public void setSolarStringId(Long solarString) {
        this.solarStringId = solarString;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Equipment)) {
            return false;
        }
        return getId() != null && getId().equals(((Equipment) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Equipment{" +
            "id=" + getId() +
            ", tag='" + getTag() + "'" +
            ", serialNumber='" + getSerialNumber() + "'" +
            ", status='" + getStatus() + "'" +
            ", installedOn='" + getInstalledOn() + "'" +
            ", removedOn='" + getRemovedOn() + "'" +
            "}";
    }
}

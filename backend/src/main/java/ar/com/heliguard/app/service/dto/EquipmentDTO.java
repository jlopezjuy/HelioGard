package ar.com.heliguard.app.service.dto;

import ar.com.heliguard.app.domain.enumeration.EquipmentStatus;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

/**
 * A DTO for the {@link ar.com.heliguard.app.domain.Equipment} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class EquipmentDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(max = 120)
    private String tag;

    @Size(max = 160)
    private String serialNumber;

    @NotNull
    private EquipmentStatus status;

    private LocalDate installedOn;

    private LocalDate removedOn;

    private OrganizationDTO organization;

    @NotNull
    private InstallationDTO installation;

    @NotNull
    private EquipmentModelDTO equipmentModel;

    private SolarStringDTO solarString;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public EquipmentStatus getStatus() {
        return status;
    }

    public void setStatus(EquipmentStatus status) {
        this.status = status;
    }

    public LocalDate getInstalledOn() {
        return installedOn;
    }

    public void setInstalledOn(LocalDate installedOn) {
        this.installedOn = installedOn;
    }

    public LocalDate getRemovedOn() {
        return removedOn;
    }

    public void setRemovedOn(LocalDate removedOn) {
        this.removedOn = removedOn;
    }

    public OrganizationDTO getOrganization() {
        return organization;
    }

    public void setOrganization(OrganizationDTO organization) {
        this.organization = organization;
    }

    public InstallationDTO getInstallation() {
        return installation;
    }

    public void setInstallation(InstallationDTO installation) {
        this.installation = installation;
    }

    public EquipmentModelDTO getEquipmentModel() {
        return equipmentModel;
    }

    public void setEquipmentModel(EquipmentModelDTO equipmentModel) {
        this.equipmentModel = equipmentModel;
    }

    public SolarStringDTO getSolarString() {
        return solarString;
    }

    public void setSolarString(SolarStringDTO solarString) {
        this.solarString = solarString;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof EquipmentDTO)) {
            return false;
        }

        EquipmentDTO equipmentDTO = (EquipmentDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, equipmentDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "EquipmentDTO{" +
            "id=" + getId() +
            ", tag='" + getTag() + "'" +
            ", serialNumber='" + getSerialNumber() + "'" +
            ", status='" + getStatus() + "'" +
            ", installedOn='" + getInstalledOn() + "'" +
            ", removedOn='" + getRemovedOn() + "'" +
            ", organization=" + getOrganization() +
            ", installation=" + getInstallation() +
            ", equipmentModel=" + getEquipmentModel() +
            ", solarString=" + getSolarString() +
            "}";
    }
}

package ar.com.heliguard.app.service.dto;

import ar.com.heliguard.app.domain.enumeration.MissionTargetKind;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link ar.com.heliguard.app.domain.DroneMissionTarget} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class DroneMissionTargetDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(max = 100)
    private String targetCode;

    @NotNull
    private MissionTargetKind kind;

    @Min(value = 1)
    private Integer priority;

    @Size(max = 250)
    private String notes;

    @NotNull
    private OrganizationDTO organization;

    @NotNull
    private DroneMissionDTO mission;

    private SolarSectorDTO solarSector;

    private SolarStringDTO solarString;

    private EquipmentDTO equipment;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTargetCode() {
        return targetCode;
    }

    public void setTargetCode(String targetCode) {
        this.targetCode = targetCode;
    }

    public MissionTargetKind getKind() {
        return kind;
    }

    public void setKind(MissionTargetKind kind) {
        this.kind = kind;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
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

    public DroneMissionDTO getMission() {
        return mission;
    }

    public void setMission(DroneMissionDTO mission) {
        this.mission = mission;
    }

    public SolarSectorDTO getSolarSector() {
        return solarSector;
    }

    public void setSolarSector(SolarSectorDTO solarSector) {
        this.solarSector = solarSector;
    }

    public SolarStringDTO getSolarString() {
        return solarString;
    }

    public void setSolarString(SolarStringDTO solarString) {
        this.solarString = solarString;
    }

    public EquipmentDTO getEquipment() {
        return equipment;
    }

    public void setEquipment(EquipmentDTO equipment) {
        this.equipment = equipment;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DroneMissionTargetDTO)) {
            return false;
        }

        DroneMissionTargetDTO droneMissionTargetDTO = (DroneMissionTargetDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, droneMissionTargetDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "DroneMissionTargetDTO{" +
            "id=" + getId() +
            ", targetCode='" + getTargetCode() + "'" +
            ", kind='" + getKind() + "'" +
            ", priority=" + getPriority() +
            ", notes='" + getNotes() + "'" +
            ", organization=" + getOrganization() +
            ", mission=" + getMission() +
            ", solarSector=" + getSolarSector() +
            ", solarString=" + getSolarString() +
            ", equipment=" + getEquipment() +
            "}";
    }
}

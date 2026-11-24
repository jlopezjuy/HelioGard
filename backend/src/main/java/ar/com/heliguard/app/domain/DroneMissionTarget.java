package ar.com.heliguard.app.domain;

import ar.com.heliguard.app.domain.enumeration.MissionTargetKind;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A DroneMissionTarget.
 */
@Table("drone_mission_target")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class DroneMissionTarget implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @NotNull
    @Size(max = 100)
    @Column("target_code")
    private String targetCode;

    @NotNull
    @Column("kind")
    private MissionTargetKind kind;

    @Min(value = 1)
    @Column("priority")
    private Integer priority;

    @Size(max = 250)
    @Column("notes")
    private String notes;

    @org.springframework.data.annotation.Transient
    private Organization organization;

    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(
        value = { "organization", "installation", "workOrder", "droneAsset", "providerOrganization" },
        allowSetters = true
    )
    private DroneMission mission;

    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "organization", "installation" }, allowSetters = true)
    private SolarSector solarSector;

    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "organization", "solarArray", "moduleModel" }, allowSetters = true)
    private SolarString solarString;

    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "organization", "installation", "equipmentModel", "solarString" }, allowSetters = true)
    private Equipment equipment;

    @Column("organization_id")
    private Long organizationId;

    @Column("mission_id")
    private Long missionId;

    @Column("solar_sector_id")
    private Long solarSectorId;

    @Column("solar_string_id")
    private Long solarStringId;

    @Column("equipment_id")
    private Long equipmentId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public DroneMissionTarget id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTargetCode() {
        return this.targetCode;
    }

    public DroneMissionTarget targetCode(String targetCode) {
        this.setTargetCode(targetCode);
        return this;
    }

    public void setTargetCode(String targetCode) {
        this.targetCode = targetCode;
    }

    public MissionTargetKind getKind() {
        return this.kind;
    }

    public DroneMissionTarget kind(MissionTargetKind kind) {
        this.setKind(kind);
        return this;
    }

    public void setKind(MissionTargetKind kind) {
        this.kind = kind;
    }

    public Integer getPriority() {
        return this.priority;
    }

    public DroneMissionTarget priority(Integer priority) {
        this.setPriority(priority);
        return this;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public String getNotes() {
        return this.notes;
    }

    public DroneMissionTarget notes(String notes) {
        this.setNotes(notes);
        return this;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Organization getOrganization() {
        return this.organization;
    }

    public void setOrganization(Organization organization) {
        this.organization = organization;
        this.organizationId = organization != null ? organization.getId() : null;
    }

    public DroneMissionTarget organization(Organization organization) {
        this.setOrganization(organization);
        return this;
    }

    public DroneMission getMission() {
        return this.mission;
    }

    public void setMission(DroneMission droneMission) {
        this.mission = droneMission;
        this.missionId = droneMission != null ? droneMission.getId() : null;
    }

    public DroneMissionTarget mission(DroneMission droneMission) {
        this.setMission(droneMission);
        return this;
    }

    public SolarSector getSolarSector() {
        return this.solarSector;
    }

    public void setSolarSector(SolarSector solarSector) {
        this.solarSector = solarSector;
        this.solarSectorId = solarSector != null ? solarSector.getId() : null;
    }

    public DroneMissionTarget solarSector(SolarSector solarSector) {
        this.setSolarSector(solarSector);
        return this;
    }

    public SolarString getSolarString() {
        return this.solarString;
    }

    public void setSolarString(SolarString solarString) {
        this.solarString = solarString;
        this.solarStringId = solarString != null ? solarString.getId() : null;
    }

    public DroneMissionTarget solarString(SolarString solarString) {
        this.setSolarString(solarString);
        return this;
    }

    public Equipment getEquipment() {
        return this.equipment;
    }

    public void setEquipment(Equipment equipment) {
        this.equipment = equipment;
        this.equipmentId = equipment != null ? equipment.getId() : null;
    }

    public DroneMissionTarget equipment(Equipment equipment) {
        this.setEquipment(equipment);
        return this;
    }

    public Long getOrganizationId() {
        return this.organizationId;
    }

    public void setOrganizationId(Long organization) {
        this.organizationId = organization;
    }

    public Long getMissionId() {
        return this.missionId;
    }

    public void setMissionId(Long droneMission) {
        this.missionId = droneMission;
    }

    public Long getSolarSectorId() {
        return this.solarSectorId;
    }

    public void setSolarSectorId(Long solarSector) {
        this.solarSectorId = solarSector;
    }

    public Long getSolarStringId() {
        return this.solarStringId;
    }

    public void setSolarStringId(Long solarString) {
        this.solarStringId = solarString;
    }

    public Long getEquipmentId() {
        return this.equipmentId;
    }

    public void setEquipmentId(Long equipment) {
        this.equipmentId = equipment;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DroneMissionTarget)) {
            return false;
        }
        return getId() != null && getId().equals(((DroneMissionTarget) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "DroneMissionTarget{" +
            "id=" + getId() +
            ", targetCode='" + getTargetCode() + "'" +
            ", kind='" + getKind() + "'" +
            ", priority=" + getPriority() +
            ", notes='" + getNotes() + "'" +
            "}";
    }
}

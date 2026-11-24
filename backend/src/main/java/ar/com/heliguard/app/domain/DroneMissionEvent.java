package ar.com.heliguard.app.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A DroneMissionEvent.
 */
@Table("drone_mission_event")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class DroneMissionEvent implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @NotNull
    @Size(max = 100)
    @Column("event_code")
    private String eventCode;

    @NotNull
    @Column("occurred_at")
    private Instant occurredAt;

    @Column("description")
    private String description;

    @org.springframework.data.annotation.Transient
    private Organization organization;

    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(
        value = { "organization", "installation", "workOrder", "droneAsset", "providerOrganization" },
        allowSetters = true
    )
    private DroneMission mission;

    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "organization", "member" }, allowSetters = true)
    private OrganizationMembership operatorMembership;

    @Column("organization_id")
    private Long organizationId;

    @Column("mission_id")
    private Long missionId;

    @Column("operator_membership_id")
    private Long operatorMembershipId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public DroneMissionEvent id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEventCode() {
        return this.eventCode;
    }

    public DroneMissionEvent eventCode(String eventCode) {
        this.setEventCode(eventCode);
        return this;
    }

    public void setEventCode(String eventCode) {
        this.eventCode = eventCode;
    }

    public Instant getOccurredAt() {
        return this.occurredAt;
    }

    public DroneMissionEvent occurredAt(Instant occurredAt) {
        this.setOccurredAt(occurredAt);
        return this;
    }

    public void setOccurredAt(Instant occurredAt) {
        this.occurredAt = occurredAt;
    }

    public String getDescription() {
        return this.description;
    }

    public DroneMissionEvent description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Organization getOrganization() {
        return this.organization;
    }

    public void setOrganization(Organization organization) {
        this.organization = organization;
        this.organizationId = organization != null ? organization.getId() : null;
    }

    public DroneMissionEvent organization(Organization organization) {
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

    public DroneMissionEvent mission(DroneMission droneMission) {
        this.setMission(droneMission);
        return this;
    }

    public OrganizationMembership getOperatorMembership() {
        return this.operatorMembership;
    }

    public void setOperatorMembership(OrganizationMembership organizationMembership) {
        this.operatorMembership = organizationMembership;
        this.operatorMembershipId = organizationMembership != null ? organizationMembership.getId() : null;
    }

    public DroneMissionEvent operatorMembership(OrganizationMembership organizationMembership) {
        this.setOperatorMembership(organizationMembership);
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

    public Long getOperatorMembershipId() {
        return this.operatorMembershipId;
    }

    public void setOperatorMembershipId(Long organizationMembership) {
        this.operatorMembershipId = organizationMembership;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DroneMissionEvent)) {
            return false;
        }
        return getId() != null && getId().equals(((DroneMissionEvent) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "DroneMissionEvent{" +
            "id=" + getId() +
            ", eventCode='" + getEventCode() + "'" +
            ", occurredAt='" + getOccurredAt() + "'" +
            ", description='" + getDescription() + "'" +
            "}";
    }
}

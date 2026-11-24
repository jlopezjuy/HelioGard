package ar.com.heliguard.app.service.dto;

import jakarta.persistence.Lob;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link ar.com.heliguard.app.domain.DroneMissionEvent} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class DroneMissionEventDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(max = 100)
    private String eventCode;

    @NotNull
    private Instant occurredAt;

    @Lob
    private String description;

    private OrganizationDTO organization;

    @NotNull
    private DroneMissionDTO mission;

    private OrganizationMembershipDTO operatorMembership;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEventCode() {
        return eventCode;
    }

    public void setEventCode(String eventCode) {
        this.eventCode = eventCode;
    }

    public Instant getOccurredAt() {
        return occurredAt;
    }

    public void setOccurredAt(Instant occurredAt) {
        this.occurredAt = occurredAt;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public OrganizationMembershipDTO getOperatorMembership() {
        return operatorMembership;
    }

    public void setOperatorMembership(OrganizationMembershipDTO operatorMembership) {
        this.operatorMembership = operatorMembership;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DroneMissionEventDTO)) {
            return false;
        }

        DroneMissionEventDTO droneMissionEventDTO = (DroneMissionEventDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, droneMissionEventDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "DroneMissionEventDTO{" +
            "id=" + getId() +
            ", eventCode='" + getEventCode() + "'" +
            ", occurredAt='" + getOccurredAt() + "'" +
            ", description='" + getDescription() + "'" +
            ", organization=" + getOrganization() +
            ", mission=" + getMission() +
            ", operatorMembership=" + getOperatorMembership() +
            "}";
    }
}

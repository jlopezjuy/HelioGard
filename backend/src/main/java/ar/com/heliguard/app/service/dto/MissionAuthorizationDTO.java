package ar.com.heliguard.app.service.dto;

import ar.com.heliguard.app.domain.enumeration.MissionAuthorizationStatus;
import jakarta.persistence.Lob;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link ar.com.heliguard.app.domain.MissionAuthorization} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class MissionAuthorizationDTO implements Serializable {

    private Long id;

    @NotNull
    private MissionAuthorizationStatus status;

    private Instant decidedAt;

    private Instant expiresAt;

    @Size(max = 120)
    private String operatorReference;

    @Lob
    private String decisionNote;

    @NotNull
    private OrganizationDTO organization;

    @NotNull
    private DroneMissionDTO mission;

    private OrganizationMembershipDTO authorizedByMembership;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public MissionAuthorizationStatus getStatus() {
        return status;
    }

    public void setStatus(MissionAuthorizationStatus status) {
        this.status = status;
    }

    public Instant getDecidedAt() {
        return decidedAt;
    }

    public void setDecidedAt(Instant decidedAt) {
        this.decidedAt = decidedAt;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(Instant expiresAt) {
        this.expiresAt = expiresAt;
    }

    public String getOperatorReference() {
        return operatorReference;
    }

    public void setOperatorReference(String operatorReference) {
        this.operatorReference = operatorReference;
    }

    public String getDecisionNote() {
        return decisionNote;
    }

    public void setDecisionNote(String decisionNote) {
        this.decisionNote = decisionNote;
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

    public OrganizationMembershipDTO getAuthorizedByMembership() {
        return authorizedByMembership;
    }

    public void setAuthorizedByMembership(OrganizationMembershipDTO authorizedByMembership) {
        this.authorizedByMembership = authorizedByMembership;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof MissionAuthorizationDTO)) {
            return false;
        }

        MissionAuthorizationDTO missionAuthorizationDTO = (MissionAuthorizationDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, missionAuthorizationDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "MissionAuthorizationDTO{" +
            "id=" + getId() +
            ", status='" + getStatus() + "'" +
            ", decidedAt='" + getDecidedAt() + "'" +
            ", expiresAt='" + getExpiresAt() + "'" +
            ", operatorReference='" + getOperatorReference() + "'" +
            ", decisionNote='" + getDecisionNote() + "'" +
            ", organization=" + getOrganization() +
            ", mission=" + getMission() +
            ", authorizedByMembership=" + getAuthorizedByMembership() +
            "}";
    }
}

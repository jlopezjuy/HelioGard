package ar.com.heliguard.app.service.dto;

import ar.com.heliguard.app.domain.enumeration.SafetyCheckResult;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link ar.com.heliguard.app.domain.MissionSafetyCheck} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class MissionSafetyCheckDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(max = 100)
    private String checkCode;

    @NotNull
    private SafetyCheckResult result;

    @NotNull
    private Instant checkedAt;

    @Size(max = 400)
    private String evidenceReference;

    @NotNull
    private OrganizationDTO organization;

    @NotNull
    private DroneMissionDTO mission;

    private OrganizationMembershipDTO checkedByMembership;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCheckCode() {
        return checkCode;
    }

    public void setCheckCode(String checkCode) {
        this.checkCode = checkCode;
    }

    public SafetyCheckResult getResult() {
        return result;
    }

    public void setResult(SafetyCheckResult result) {
        this.result = result;
    }

    public Instant getCheckedAt() {
        return checkedAt;
    }

    public void setCheckedAt(Instant checkedAt) {
        this.checkedAt = checkedAt;
    }

    public String getEvidenceReference() {
        return evidenceReference;
    }

    public void setEvidenceReference(String evidenceReference) {
        this.evidenceReference = evidenceReference;
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

    public OrganizationMembershipDTO getCheckedByMembership() {
        return checkedByMembership;
    }

    public void setCheckedByMembership(OrganizationMembershipDTO checkedByMembership) {
        this.checkedByMembership = checkedByMembership;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof MissionSafetyCheckDTO)) {
            return false;
        }

        MissionSafetyCheckDTO missionSafetyCheckDTO = (MissionSafetyCheckDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, missionSafetyCheckDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "MissionSafetyCheckDTO{" +
            "id=" + getId() +
            ", checkCode='" + getCheckCode() + "'" +
            ", result='" + getResult() + "'" +
            ", checkedAt='" + getCheckedAt() + "'" +
            ", evidenceReference='" + getEvidenceReference() + "'" +
            ", organization=" + getOrganization() +
            ", mission=" + getMission() +
            ", checkedByMembership=" + getCheckedByMembership() +
            "}";
    }
}

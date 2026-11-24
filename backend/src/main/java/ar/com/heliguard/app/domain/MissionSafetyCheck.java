package ar.com.heliguard.app.domain;

import ar.com.heliguard.app.domain.enumeration.SafetyCheckResult;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A MissionSafetyCheck.
 */
@Table("mission_safety_check")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class MissionSafetyCheck implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @NotNull
    @Size(max = 100)
    @Column("check_code")
    private String checkCode;

    @NotNull
    @Column("result")
    private SafetyCheckResult result;

    @NotNull
    @Column("checked_at")
    private Instant checkedAt;

    @Size(max = 400)
    @Column("evidence_reference")
    private String evidenceReference;

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
    private OrganizationMembership checkedByMembership;

    @Column("organization_id")
    private Long organizationId;

    @Column("mission_id")
    private Long missionId;

    @Column("checked_by_membership_id")
    private Long checkedByMembershipId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public MissionSafetyCheck id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCheckCode() {
        return this.checkCode;
    }

    public MissionSafetyCheck checkCode(String checkCode) {
        this.setCheckCode(checkCode);
        return this;
    }

    public void setCheckCode(String checkCode) {
        this.checkCode = checkCode;
    }

    public SafetyCheckResult getResult() {
        return this.result;
    }

    public MissionSafetyCheck result(SafetyCheckResult result) {
        this.setResult(result);
        return this;
    }

    public void setResult(SafetyCheckResult result) {
        this.result = result;
    }

    public Instant getCheckedAt() {
        return this.checkedAt;
    }

    public MissionSafetyCheck checkedAt(Instant checkedAt) {
        this.setCheckedAt(checkedAt);
        return this;
    }

    public void setCheckedAt(Instant checkedAt) {
        this.checkedAt = checkedAt;
    }

    public String getEvidenceReference() {
        return this.evidenceReference;
    }

    public MissionSafetyCheck evidenceReference(String evidenceReference) {
        this.setEvidenceReference(evidenceReference);
        return this;
    }

    public void setEvidenceReference(String evidenceReference) {
        this.evidenceReference = evidenceReference;
    }

    public Organization getOrganization() {
        return this.organization;
    }

    public void setOrganization(Organization organization) {
        this.organization = organization;
        this.organizationId = organization != null ? organization.getId() : null;
    }

    public MissionSafetyCheck organization(Organization organization) {
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

    public MissionSafetyCheck mission(DroneMission droneMission) {
        this.setMission(droneMission);
        return this;
    }

    public OrganizationMembership getCheckedByMembership() {
        return this.checkedByMembership;
    }

    public void setCheckedByMembership(OrganizationMembership organizationMembership) {
        this.checkedByMembership = organizationMembership;
        this.checkedByMembershipId = organizationMembership != null ? organizationMembership.getId() : null;
    }

    public MissionSafetyCheck checkedByMembership(OrganizationMembership organizationMembership) {
        this.setCheckedByMembership(organizationMembership);
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

    public Long getCheckedByMembershipId() {
        return this.checkedByMembershipId;
    }

    public void setCheckedByMembershipId(Long organizationMembership) {
        this.checkedByMembershipId = organizationMembership;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof MissionSafetyCheck)) {
            return false;
        }
        return getId() != null && getId().equals(((MissionSafetyCheck) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "MissionSafetyCheck{" +
            "id=" + getId() +
            ", checkCode='" + getCheckCode() + "'" +
            ", result='" + getResult() + "'" +
            ", checkedAt='" + getCheckedAt() + "'" +
            ", evidenceReference='" + getEvidenceReference() + "'" +
            "}";
    }
}

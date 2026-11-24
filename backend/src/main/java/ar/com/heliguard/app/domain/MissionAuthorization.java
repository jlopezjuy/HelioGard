package ar.com.heliguard.app.domain;

import ar.com.heliguard.app.domain.enumeration.MissionAuthorizationStatus;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A MissionAuthorization.
 */
@Table("mission_authorization")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class MissionAuthorization implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @NotNull
    @Column("status")
    private MissionAuthorizationStatus status;

    @Column("decided_at")
    private Instant decidedAt;

    @Column("expires_at")
    private Instant expiresAt;

    @Size(max = 120)
    @Column("operator_reference")
    private String operatorReference;

    @Column("decision_note")
    private String decisionNote;

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
    private OrganizationMembership authorizedByMembership;

    @Column("organization_id")
    private Long organizationId;

    @Column("mission_id")
    private Long missionId;

    @Column("authorized_by_membership_id")
    private Long authorizedByMembershipId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public MissionAuthorization id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public MissionAuthorizationStatus getStatus() {
        return this.status;
    }

    public MissionAuthorization status(MissionAuthorizationStatus status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(MissionAuthorizationStatus status) {
        this.status = status;
    }

    public Instant getDecidedAt() {
        return this.decidedAt;
    }

    public MissionAuthorization decidedAt(Instant decidedAt) {
        this.setDecidedAt(decidedAt);
        return this;
    }

    public void setDecidedAt(Instant decidedAt) {
        this.decidedAt = decidedAt;
    }

    public Instant getExpiresAt() {
        return this.expiresAt;
    }

    public MissionAuthorization expiresAt(Instant expiresAt) {
        this.setExpiresAt(expiresAt);
        return this;
    }

    public void setExpiresAt(Instant expiresAt) {
        this.expiresAt = expiresAt;
    }

    public String getOperatorReference() {
        return this.operatorReference;
    }

    public MissionAuthorization operatorReference(String operatorReference) {
        this.setOperatorReference(operatorReference);
        return this;
    }

    public void setOperatorReference(String operatorReference) {
        this.operatorReference = operatorReference;
    }

    public String getDecisionNote() {
        return this.decisionNote;
    }

    public MissionAuthorization decisionNote(String decisionNote) {
        this.setDecisionNote(decisionNote);
        return this;
    }

    public void setDecisionNote(String decisionNote) {
        this.decisionNote = decisionNote;
    }

    public Organization getOrganization() {
        return this.organization;
    }

    public void setOrganization(Organization organization) {
        this.organization = organization;
        this.organizationId = organization != null ? organization.getId() : null;
    }

    public MissionAuthorization organization(Organization organization) {
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

    public MissionAuthorization mission(DroneMission droneMission) {
        this.setMission(droneMission);
        return this;
    }

    public OrganizationMembership getAuthorizedByMembership() {
        return this.authorizedByMembership;
    }

    public void setAuthorizedByMembership(OrganizationMembership organizationMembership) {
        this.authorizedByMembership = organizationMembership;
        this.authorizedByMembershipId = organizationMembership != null ? organizationMembership.getId() : null;
    }

    public MissionAuthorization authorizedByMembership(OrganizationMembership organizationMembership) {
        this.setAuthorizedByMembership(organizationMembership);
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

    public Long getAuthorizedByMembershipId() {
        return this.authorizedByMembershipId;
    }

    public void setAuthorizedByMembershipId(Long organizationMembership) {
        this.authorizedByMembershipId = organizationMembership;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof MissionAuthorization)) {
            return false;
        }
        return getId() != null && getId().equals(((MissionAuthorization) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "MissionAuthorization{" +
            "id=" + getId() +
            ", status='" + getStatus() + "'" +
            ", decidedAt='" + getDecidedAt() + "'" +
            ", expiresAt='" + getExpiresAt() + "'" +
            ", operatorReference='" + getOperatorReference() + "'" +
            ", decisionNote='" + getDecisionNote() + "'" +
            "}";
    }
}

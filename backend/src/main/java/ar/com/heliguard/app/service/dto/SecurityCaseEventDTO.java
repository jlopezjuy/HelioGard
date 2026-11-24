package ar.com.heliguard.app.service.dto;

import jakarta.persistence.Lob;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link ar.com.heliguard.app.domain.SecurityCaseEvent} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class SecurityCaseEventDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(max = 100)
    private String eventCode;

    @NotNull
    private Instant occurredAt;

    @Lob
    private String detail;

    @NotNull
    private OrganizationDTO organization;

    @NotNull
    private SecurityIncidentDTO securityIncident;

    private OrganizationMembershipDTO actorMembership;

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

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public OrganizationDTO getOrganization() {
        return organization;
    }

    public void setOrganization(OrganizationDTO organization) {
        this.organization = organization;
    }

    public SecurityIncidentDTO getSecurityIncident() {
        return securityIncident;
    }

    public void setSecurityIncident(SecurityIncidentDTO securityIncident) {
        this.securityIncident = securityIncident;
    }

    public OrganizationMembershipDTO getActorMembership() {
        return actorMembership;
    }

    public void setActorMembership(OrganizationMembershipDTO actorMembership) {
        this.actorMembership = actorMembership;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SecurityCaseEventDTO)) {
            return false;
        }

        SecurityCaseEventDTO securityCaseEventDTO = (SecurityCaseEventDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, securityCaseEventDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "SecurityCaseEventDTO{" +
            "id=" + getId() +
            ", eventCode='" + getEventCode() + "'" +
            ", occurredAt='" + getOccurredAt() + "'" +
            ", detail='" + getDetail() + "'" +
            ", organization=" + getOrganization() +
            ", securityIncident=" + getSecurityIncident() +
            ", actorMembership=" + getActorMembership() +
            "}";
    }
}

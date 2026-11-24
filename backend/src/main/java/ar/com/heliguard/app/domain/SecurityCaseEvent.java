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
 * A SecurityCaseEvent.
 */
@Table("security_case_event")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class SecurityCaseEvent implements Serializable {

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

    @Column("detail")
    private String detail;

    @org.springframework.data.annotation.Transient
    private Organization organization;

    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "organization", "installation", "integrityAssessment" }, allowSetters = true)
    private SecurityIncident securityIncident;

    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "organization", "member" }, allowSetters = true)
    private OrganizationMembership actorMembership;

    @Column("organization_id")
    private Long organizationId;

    @Column("security_incident_id")
    private Long securityIncidentId;

    @Column("actor_membership_id")
    private Long actorMembershipId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public SecurityCaseEvent id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEventCode() {
        return this.eventCode;
    }

    public SecurityCaseEvent eventCode(String eventCode) {
        this.setEventCode(eventCode);
        return this;
    }

    public void setEventCode(String eventCode) {
        this.eventCode = eventCode;
    }

    public Instant getOccurredAt() {
        return this.occurredAt;
    }

    public SecurityCaseEvent occurredAt(Instant occurredAt) {
        this.setOccurredAt(occurredAt);
        return this;
    }

    public void setOccurredAt(Instant occurredAt) {
        this.occurredAt = occurredAt;
    }

    public String getDetail() {
        return this.detail;
    }

    public SecurityCaseEvent detail(String detail) {
        this.setDetail(detail);
        return this;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public Organization getOrganization() {
        return this.organization;
    }

    public void setOrganization(Organization organization) {
        this.organization = organization;
        this.organizationId = organization != null ? organization.getId() : null;
    }

    public SecurityCaseEvent organization(Organization organization) {
        this.setOrganization(organization);
        return this;
    }

    public SecurityIncident getSecurityIncident() {
        return this.securityIncident;
    }

    public void setSecurityIncident(SecurityIncident securityIncident) {
        this.securityIncident = securityIncident;
        this.securityIncidentId = securityIncident != null ? securityIncident.getId() : null;
    }

    public SecurityCaseEvent securityIncident(SecurityIncident securityIncident) {
        this.setSecurityIncident(securityIncident);
        return this;
    }

    public OrganizationMembership getActorMembership() {
        return this.actorMembership;
    }

    public void setActorMembership(OrganizationMembership organizationMembership) {
        this.actorMembership = organizationMembership;
        this.actorMembershipId = organizationMembership != null ? organizationMembership.getId() : null;
    }

    public SecurityCaseEvent actorMembership(OrganizationMembership organizationMembership) {
        this.setActorMembership(organizationMembership);
        return this;
    }

    public Long getOrganizationId() {
        return this.organizationId;
    }

    public void setOrganizationId(Long organization) {
        this.organizationId = organization;
    }

    public Long getSecurityIncidentId() {
        return this.securityIncidentId;
    }

    public void setSecurityIncidentId(Long securityIncident) {
        this.securityIncidentId = securityIncident;
    }

    public Long getActorMembershipId() {
        return this.actorMembershipId;
    }

    public void setActorMembershipId(Long organizationMembership) {
        this.actorMembershipId = organizationMembership;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SecurityCaseEvent)) {
            return false;
        }
        return getId() != null && getId().equals(((SecurityCaseEvent) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "SecurityCaseEvent{" +
            "id=" + getId() +
            ", eventCode='" + getEventCode() + "'" +
            ", occurredAt='" + getOccurredAt() + "'" +
            ", detail='" + getDetail() + "'" +
            "}";
    }
}

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
 * A IncidentEvent.
 */
@Table("incident_event")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class IncidentEvent implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @NotNull
    @Column("occurred_at")
    private Instant occurredAt;

    @NotNull
    @Size(max = 80)
    @Column("event_code")
    private String eventCode;

    @Column("detail")
    private String detail;

    @org.springframework.data.annotation.Transient
    private Organization organization;

    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "organization", "installation", "alertRuleVersion" }, allowSetters = true)
    private Incident incident;

    @Column("organization_id")
    private Long organizationId;

    @Column("incident_id")
    private Long incidentId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public IncidentEvent id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instant getOccurredAt() {
        return this.occurredAt;
    }

    public IncidentEvent occurredAt(Instant occurredAt) {
        this.setOccurredAt(occurredAt);
        return this;
    }

    public void setOccurredAt(Instant occurredAt) {
        this.occurredAt = occurredAt;
    }

    public String getEventCode() {
        return this.eventCode;
    }

    public IncidentEvent eventCode(String eventCode) {
        this.setEventCode(eventCode);
        return this;
    }

    public void setEventCode(String eventCode) {
        this.eventCode = eventCode;
    }

    public String getDetail() {
        return this.detail;
    }

    public IncidentEvent detail(String detail) {
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

    public IncidentEvent organization(Organization organization) {
        this.setOrganization(organization);
        return this;
    }

    public Incident getIncident() {
        return this.incident;
    }

    public void setIncident(Incident incident) {
        this.incident = incident;
        this.incidentId = incident != null ? incident.getId() : null;
    }

    public IncidentEvent incident(Incident incident) {
        this.setIncident(incident);
        return this;
    }

    public Long getOrganizationId() {
        return this.organizationId;
    }

    public void setOrganizationId(Long organization) {
        this.organizationId = organization;
    }

    public Long getIncidentId() {
        return this.incidentId;
    }

    public void setIncidentId(Long incident) {
        this.incidentId = incident;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof IncidentEvent)) {
            return false;
        }
        return getId() != null && getId().equals(((IncidentEvent) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "IncidentEvent{" +
            "id=" + getId() +
            ", occurredAt='" + getOccurredAt() + "'" +
            ", eventCode='" + getEventCode() + "'" +
            ", detail='" + getDetail() + "'" +
            "}";
    }
}

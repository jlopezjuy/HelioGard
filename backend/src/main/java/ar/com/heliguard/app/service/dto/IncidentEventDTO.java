package ar.com.heliguard.app.service.dto;

import jakarta.persistence.Lob;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link ar.com.heliguard.app.domain.IncidentEvent} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class IncidentEventDTO implements Serializable {

    private Long id;

    @NotNull
    private Instant occurredAt;

    @NotNull
    @Size(max = 80)
    private String eventCode;

    @Lob
    private String detail;

    @NotNull
    private OrganizationDTO organization;

    @NotNull
    private IncidentDTO incident;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instant getOccurredAt() {
        return occurredAt;
    }

    public void setOccurredAt(Instant occurredAt) {
        this.occurredAt = occurredAt;
    }

    public String getEventCode() {
        return eventCode;
    }

    public void setEventCode(String eventCode) {
        this.eventCode = eventCode;
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

    public IncidentDTO getIncident() {
        return incident;
    }

    public void setIncident(IncidentDTO incident) {
        this.incident = incident;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof IncidentEventDTO)) {
            return false;
        }

        IncidentEventDTO incidentEventDTO = (IncidentEventDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, incidentEventDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "IncidentEventDTO{" +
            "id=" + getId() +
            ", occurredAt='" + getOccurredAt() + "'" +
            ", eventCode='" + getEventCode() + "'" +
            ", detail='" + getDetail() + "'" +
            ", organization=" + getOrganization() +
            ", incident=" + getIncident() +
            "}";
    }
}

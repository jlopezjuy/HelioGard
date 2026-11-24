package ar.com.heliguard.app.service.dto;

import ar.com.heliguard.app.domain.enumeration.AlertSeverity;
import ar.com.heliguard.app.domain.enumeration.IncidentStatus;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link ar.com.heliguard.app.domain.Incident} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class IncidentDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(max = 180)
    private String title;

    @NotNull
    private AlertSeverity severity;

    @NotNull
    private IncidentStatus status;

    @NotNull
    private Instant firstSeenAt;

    @NotNull
    private Instant lastSeenAt;

    private Instant acknowledgedAt;

    private Instant resolvedAt;

    @NotNull
    @Size(max = 180)
    private String dedupKey;

    private OrganizationDTO organization;

    @NotNull
    private InstallationDTO installation;

    private AlertRuleVersionDTO alertRuleVersion;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public AlertSeverity getSeverity() {
        return severity;
    }

    public void setSeverity(AlertSeverity severity) {
        this.severity = severity;
    }

    public IncidentStatus getStatus() {
        return status;
    }

    public void setStatus(IncidentStatus status) {
        this.status = status;
    }

    public Instant getFirstSeenAt() {
        return firstSeenAt;
    }

    public void setFirstSeenAt(Instant firstSeenAt) {
        this.firstSeenAt = firstSeenAt;
    }

    public Instant getLastSeenAt() {
        return lastSeenAt;
    }

    public void setLastSeenAt(Instant lastSeenAt) {
        this.lastSeenAt = lastSeenAt;
    }

    public Instant getAcknowledgedAt() {
        return acknowledgedAt;
    }

    public void setAcknowledgedAt(Instant acknowledgedAt) {
        this.acknowledgedAt = acknowledgedAt;
    }

    public Instant getResolvedAt() {
        return resolvedAt;
    }

    public void setResolvedAt(Instant resolvedAt) {
        this.resolvedAt = resolvedAt;
    }

    public String getDedupKey() {
        return dedupKey;
    }

    public void setDedupKey(String dedupKey) {
        this.dedupKey = dedupKey;
    }

    public OrganizationDTO getOrganization() {
        return organization;
    }

    public void setOrganization(OrganizationDTO organization) {
        this.organization = organization;
    }

    public InstallationDTO getInstallation() {
        return installation;
    }

    public void setInstallation(InstallationDTO installation) {
        this.installation = installation;
    }

    public AlertRuleVersionDTO getAlertRuleVersion() {
        return alertRuleVersion;
    }

    public void setAlertRuleVersion(AlertRuleVersionDTO alertRuleVersion) {
        this.alertRuleVersion = alertRuleVersion;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof IncidentDTO)) {
            return false;
        }

        IncidentDTO incidentDTO = (IncidentDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, incidentDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "IncidentDTO{" +
            "id=" + getId() +
            ", title='" + getTitle() + "'" +
            ", severity='" + getSeverity() + "'" +
            ", status='" + getStatus() + "'" +
            ", firstSeenAt='" + getFirstSeenAt() + "'" +
            ", lastSeenAt='" + getLastSeenAt() + "'" +
            ", acknowledgedAt='" + getAcknowledgedAt() + "'" +
            ", resolvedAt='" + getResolvedAt() + "'" +
            ", dedupKey='" + getDedupKey() + "'" +
            ", organization=" + getOrganization() +
            ", installation=" + getInstallation() +
            ", alertRuleVersion=" + getAlertRuleVersion() +
            "}";
    }
}

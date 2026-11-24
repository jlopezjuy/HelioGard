package ar.com.heliguard.app.domain;

import ar.com.heliguard.app.domain.enumeration.AlertSeverity;
import ar.com.heliguard.app.domain.enumeration.IncidentStatus;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A Incident.
 */
@Table("incident")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Incident implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @NotNull
    @Size(max = 180)
    @Column("title")
    private String title;

    @NotNull
    @Column("severity")
    private AlertSeverity severity;

    @NotNull
    @Column("status")
    private IncidentStatus status;

    @NotNull
    @Column("first_seen_at")
    private Instant firstSeenAt;

    @NotNull
    @Column("last_seen_at")
    private Instant lastSeenAt;

    @Column("acknowledged_at")
    private Instant acknowledgedAt;

    @Column("resolved_at")
    private Instant resolvedAt;

    @NotNull
    @Size(max = 180)
    @Column("dedup_key")
    private String dedupKey;

    @org.springframework.data.annotation.Transient
    private Organization organization;

    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "organization", "site", "climateRiskProfile" }, allowSetters = true)
    private Installation installation;

    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "organization", "alertRule" }, allowSetters = true)
    private AlertRuleVersion alertRuleVersion;

    @Column("organization_id")
    private Long organizationId;

    @Column("installation_id")
    private Long installationId;

    @Column("alert_rule_version_id")
    private Long alertRuleVersionId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Incident id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return this.title;
    }

    public Incident title(String title) {
        this.setTitle(title);
        return this;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public AlertSeverity getSeverity() {
        return this.severity;
    }

    public Incident severity(AlertSeverity severity) {
        this.setSeverity(severity);
        return this;
    }

    public void setSeverity(AlertSeverity severity) {
        this.severity = severity;
    }

    public IncidentStatus getStatus() {
        return this.status;
    }

    public Incident status(IncidentStatus status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(IncidentStatus status) {
        this.status = status;
    }

    public Instant getFirstSeenAt() {
        return this.firstSeenAt;
    }

    public Incident firstSeenAt(Instant firstSeenAt) {
        this.setFirstSeenAt(firstSeenAt);
        return this;
    }

    public void setFirstSeenAt(Instant firstSeenAt) {
        this.firstSeenAt = firstSeenAt;
    }

    public Instant getLastSeenAt() {
        return this.lastSeenAt;
    }

    public Incident lastSeenAt(Instant lastSeenAt) {
        this.setLastSeenAt(lastSeenAt);
        return this;
    }

    public void setLastSeenAt(Instant lastSeenAt) {
        this.lastSeenAt = lastSeenAt;
    }

    public Instant getAcknowledgedAt() {
        return this.acknowledgedAt;
    }

    public Incident acknowledgedAt(Instant acknowledgedAt) {
        this.setAcknowledgedAt(acknowledgedAt);
        return this;
    }

    public void setAcknowledgedAt(Instant acknowledgedAt) {
        this.acknowledgedAt = acknowledgedAt;
    }

    public Instant getResolvedAt() {
        return this.resolvedAt;
    }

    public Incident resolvedAt(Instant resolvedAt) {
        this.setResolvedAt(resolvedAt);
        return this;
    }

    public void setResolvedAt(Instant resolvedAt) {
        this.resolvedAt = resolvedAt;
    }

    public String getDedupKey() {
        return this.dedupKey;
    }

    public Incident dedupKey(String dedupKey) {
        this.setDedupKey(dedupKey);
        return this;
    }

    public void setDedupKey(String dedupKey) {
        this.dedupKey = dedupKey;
    }

    public Organization getOrganization() {
        return this.organization;
    }

    public void setOrganization(Organization organization) {
        this.organization = organization;
        this.organizationId = organization != null ? organization.getId() : null;
    }

    public Incident organization(Organization organization) {
        this.setOrganization(organization);
        return this;
    }

    public Installation getInstallation() {
        return this.installation;
    }

    public void setInstallation(Installation installation) {
        this.installation = installation;
        this.installationId = installation != null ? installation.getId() : null;
    }

    public Incident installation(Installation installation) {
        this.setInstallation(installation);
        return this;
    }

    public AlertRuleVersion getAlertRuleVersion() {
        return this.alertRuleVersion;
    }

    public void setAlertRuleVersion(AlertRuleVersion alertRuleVersion) {
        this.alertRuleVersion = alertRuleVersion;
        this.alertRuleVersionId = alertRuleVersion != null ? alertRuleVersion.getId() : null;
    }

    public Incident alertRuleVersion(AlertRuleVersion alertRuleVersion) {
        this.setAlertRuleVersion(alertRuleVersion);
        return this;
    }

    public Long getOrganizationId() {
        return this.organizationId;
    }

    public void setOrganizationId(Long organization) {
        this.organizationId = organization;
    }

    public Long getInstallationId() {
        return this.installationId;
    }

    public void setInstallationId(Long installation) {
        this.installationId = installation;
    }

    public Long getAlertRuleVersionId() {
        return this.alertRuleVersionId;
    }

    public void setAlertRuleVersionId(Long alertRuleVersion) {
        this.alertRuleVersionId = alertRuleVersion;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Incident)) {
            return false;
        }
        return getId() != null && getId().equals(((Incident) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Incident{" +
            "id=" + getId() +
            ", title='" + getTitle() + "'" +
            ", severity='" + getSeverity() + "'" +
            ", status='" + getStatus() + "'" +
            ", firstSeenAt='" + getFirstSeenAt() + "'" +
            ", lastSeenAt='" + getLastSeenAt() + "'" +
            ", acknowledgedAt='" + getAcknowledgedAt() + "'" +
            ", resolvedAt='" + getResolvedAt() + "'" +
            ", dedupKey='" + getDedupKey() + "'" +
            "}";
    }
}

package ar.com.heliguard.app.domain;

import ar.com.heliguard.app.domain.enumeration.DataTrustLevel;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A IntegrityAssessment.
 */
@Table("integrity_assessment")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class IntegrityAssessment implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @NotNull
    @Column("assessed_at")
    private Instant assessedAt;

    @NotNull
    @Column("trust_level")
    private DataTrustLevel trustLevel;

    @NotNull
    @Size(max = 80)
    @Column("rule_version")
    private String ruleVersion;

    @Column("reason")
    private String reason;

    @org.springframework.data.annotation.Transient
    private Organization organization;

    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "organization", "site", "climateRiskProfile" }, allowSetters = true)
    private Installation installation;

    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "organization", "installation", "deviceIdentity", "telemetryChannel" }, allowSetters = true)
    private TelemetryIntegrityEvent integrityEvent;

    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "organization", "installation", "modelConfiguration", "weatherBatch" }, allowSetters = true)
    private EnergyModelRun modelRun;

    @Column("organization_id")
    private Long organizationId;

    @Column("installation_id")
    private Long installationId;

    @Column("integrity_event_id")
    private Long integrityEventId;

    @Column("model_run_id")
    private Long modelRunId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public IntegrityAssessment id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instant getAssessedAt() {
        return this.assessedAt;
    }

    public IntegrityAssessment assessedAt(Instant assessedAt) {
        this.setAssessedAt(assessedAt);
        return this;
    }

    public void setAssessedAt(Instant assessedAt) {
        this.assessedAt = assessedAt;
    }

    public DataTrustLevel getTrustLevel() {
        return this.trustLevel;
    }

    public IntegrityAssessment trustLevel(DataTrustLevel trustLevel) {
        this.setTrustLevel(trustLevel);
        return this;
    }

    public void setTrustLevel(DataTrustLevel trustLevel) {
        this.trustLevel = trustLevel;
    }

    public String getRuleVersion() {
        return this.ruleVersion;
    }

    public IntegrityAssessment ruleVersion(String ruleVersion) {
        this.setRuleVersion(ruleVersion);
        return this;
    }

    public void setRuleVersion(String ruleVersion) {
        this.ruleVersion = ruleVersion;
    }

    public String getReason() {
        return this.reason;
    }

    public IntegrityAssessment reason(String reason) {
        this.setReason(reason);
        return this;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Organization getOrganization() {
        return this.organization;
    }

    public void setOrganization(Organization organization) {
        this.organization = organization;
        this.organizationId = organization != null ? organization.getId() : null;
    }

    public IntegrityAssessment organization(Organization organization) {
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

    public IntegrityAssessment installation(Installation installation) {
        this.setInstallation(installation);
        return this;
    }

    public TelemetryIntegrityEvent getIntegrityEvent() {
        return this.integrityEvent;
    }

    public void setIntegrityEvent(TelemetryIntegrityEvent telemetryIntegrityEvent) {
        this.integrityEvent = telemetryIntegrityEvent;
        this.integrityEventId = telemetryIntegrityEvent != null ? telemetryIntegrityEvent.getId() : null;
    }

    public IntegrityAssessment integrityEvent(TelemetryIntegrityEvent telemetryIntegrityEvent) {
        this.setIntegrityEvent(telemetryIntegrityEvent);
        return this;
    }

    public EnergyModelRun getModelRun() {
        return this.modelRun;
    }

    public void setModelRun(EnergyModelRun energyModelRun) {
        this.modelRun = energyModelRun;
        this.modelRunId = energyModelRun != null ? energyModelRun.getId() : null;
    }

    public IntegrityAssessment modelRun(EnergyModelRun energyModelRun) {
        this.setModelRun(energyModelRun);
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

    public Long getIntegrityEventId() {
        return this.integrityEventId;
    }

    public void setIntegrityEventId(Long telemetryIntegrityEvent) {
        this.integrityEventId = telemetryIntegrityEvent;
    }

    public Long getModelRunId() {
        return this.modelRunId;
    }

    public void setModelRunId(Long energyModelRun) {
        this.modelRunId = energyModelRun;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof IntegrityAssessment)) {
            return false;
        }
        return getId() != null && getId().equals(((IntegrityAssessment) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "IntegrityAssessment{" +
            "id=" + getId() +
            ", assessedAt='" + getAssessedAt() + "'" +
            ", trustLevel='" + getTrustLevel() + "'" +
            ", ruleVersion='" + getRuleVersion() + "'" +
            ", reason='" + getReason() + "'" +
            "}";
    }
}

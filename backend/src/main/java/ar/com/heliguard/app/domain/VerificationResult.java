package ar.com.heliguard.app.domain;

import ar.com.heliguard.app.domain.enumeration.VerificationStatus;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A VerificationResult.
 */
@Table("verification_result")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class VerificationResult implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @NotNull
    @Column("status")
    private VerificationStatus status;

    @NotNull
    @Column("assessed_at")
    private Instant assessedAt;

    @NotNull
    @Size(max = 120)
    @Column("baseline_method")
    private String baselineMethod;

    @Column("recovered_wh")
    private BigDecimal recoveredWh;

    @Column("evidence_summary")
    private String evidenceSummary;

    @org.springframework.data.annotation.Transient
    private Organization organization;

    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "organization", "site", "climateRiskProfile" }, allowSetters = true)
    private Installation installation;

    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(
        value = { "organization", "installation", "workOrder", "droneAsset", "providerOrganization" },
        allowSetters = true
    )
    private DroneMission droneMission;

    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "organization", "intervention", "modelRun" }, allowSetters = true)
    private RecoveryAssessment recoveryAssessment;

    @Column("organization_id")
    private Long organizationId;

    @Column("installation_id")
    private Long installationId;

    @Column("drone_mission_id")
    private Long droneMissionId;

    @Column("recovery_assessment_id")
    private Long recoveryAssessmentId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public VerificationResult id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public VerificationStatus getStatus() {
        return this.status;
    }

    public VerificationResult status(VerificationStatus status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(VerificationStatus status) {
        this.status = status;
    }

    public Instant getAssessedAt() {
        return this.assessedAt;
    }

    public VerificationResult assessedAt(Instant assessedAt) {
        this.setAssessedAt(assessedAt);
        return this;
    }

    public void setAssessedAt(Instant assessedAt) {
        this.assessedAt = assessedAt;
    }

    public String getBaselineMethod() {
        return this.baselineMethod;
    }

    public VerificationResult baselineMethod(String baselineMethod) {
        this.setBaselineMethod(baselineMethod);
        return this;
    }

    public void setBaselineMethod(String baselineMethod) {
        this.baselineMethod = baselineMethod;
    }

    public BigDecimal getRecoveredWh() {
        return this.recoveredWh;
    }

    public VerificationResult recoveredWh(BigDecimal recoveredWh) {
        this.setRecoveredWh(recoveredWh);
        return this;
    }

    public void setRecoveredWh(BigDecimal recoveredWh) {
        this.recoveredWh = recoveredWh;
    }

    public String getEvidenceSummary() {
        return this.evidenceSummary;
    }

    public VerificationResult evidenceSummary(String evidenceSummary) {
        this.setEvidenceSummary(evidenceSummary);
        return this;
    }

    public void setEvidenceSummary(String evidenceSummary) {
        this.evidenceSummary = evidenceSummary;
    }

    public Organization getOrganization() {
        return this.organization;
    }

    public void setOrganization(Organization organization) {
        this.organization = organization;
        this.organizationId = organization != null ? organization.getId() : null;
    }

    public VerificationResult organization(Organization organization) {
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

    public VerificationResult installation(Installation installation) {
        this.setInstallation(installation);
        return this;
    }

    public DroneMission getDroneMission() {
        return this.droneMission;
    }

    public void setDroneMission(DroneMission droneMission) {
        this.droneMission = droneMission;
        this.droneMissionId = droneMission != null ? droneMission.getId() : null;
    }

    public VerificationResult droneMission(DroneMission droneMission) {
        this.setDroneMission(droneMission);
        return this;
    }

    public RecoveryAssessment getRecoveryAssessment() {
        return this.recoveryAssessment;
    }

    public void setRecoveryAssessment(RecoveryAssessment recoveryAssessment) {
        this.recoveryAssessment = recoveryAssessment;
        this.recoveryAssessmentId = recoveryAssessment != null ? recoveryAssessment.getId() : null;
    }

    public VerificationResult recoveryAssessment(RecoveryAssessment recoveryAssessment) {
        this.setRecoveryAssessment(recoveryAssessment);
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

    public Long getDroneMissionId() {
        return this.droneMissionId;
    }

    public void setDroneMissionId(Long droneMission) {
        this.droneMissionId = droneMission;
    }

    public Long getRecoveryAssessmentId() {
        return this.recoveryAssessmentId;
    }

    public void setRecoveryAssessmentId(Long recoveryAssessment) {
        this.recoveryAssessmentId = recoveryAssessment;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof VerificationResult)) {
            return false;
        }
        return getId() != null && getId().equals(((VerificationResult) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "VerificationResult{" +
            "id=" + getId() +
            ", status='" + getStatus() + "'" +
            ", assessedAt='" + getAssessedAt() + "'" +
            ", baselineMethod='" + getBaselineMethod() + "'" +
            ", recoveredWh=" + getRecoveredWh() +
            ", evidenceSummary='" + getEvidenceSummary() + "'" +
            "}";
    }
}

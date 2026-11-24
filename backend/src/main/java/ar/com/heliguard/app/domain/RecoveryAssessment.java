package ar.com.heliguard.app.domain;

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
 * A RecoveryAssessment.
 */
@Table("recovery_assessment")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class RecoveryAssessment implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @NotNull
    @Column("assessed_at")
    private Instant assessedAt;

    @NotNull
    @Size(max = 120)
    @Column("baseline_method")
    private String baselineMethod;

    @Column("expected_recovered_wh")
    private BigDecimal expectedRecoveredWh;

    @Column("verified_recovered_wh")
    private BigDecimal verifiedRecoveredWh;

    @Column("confidence_note")
    private String confidenceNote;

    @org.springframework.data.annotation.Transient
    private Organization organization;

    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "organization", "workOrder", "performedByMembership" }, allowSetters = true)
    private MaintenanceIntervention intervention;

    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "organization", "installation", "modelConfiguration", "weatherBatch" }, allowSetters = true)
    private EnergyModelRun modelRun;

    @Column("organization_id")
    private Long organizationId;

    @Column("intervention_id")
    private Long interventionId;

    @Column("model_run_id")
    private Long modelRunId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public RecoveryAssessment id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instant getAssessedAt() {
        return this.assessedAt;
    }

    public RecoveryAssessment assessedAt(Instant assessedAt) {
        this.setAssessedAt(assessedAt);
        return this;
    }

    public void setAssessedAt(Instant assessedAt) {
        this.assessedAt = assessedAt;
    }

    public String getBaselineMethod() {
        return this.baselineMethod;
    }

    public RecoveryAssessment baselineMethod(String baselineMethod) {
        this.setBaselineMethod(baselineMethod);
        return this;
    }

    public void setBaselineMethod(String baselineMethod) {
        this.baselineMethod = baselineMethod;
    }

    public BigDecimal getExpectedRecoveredWh() {
        return this.expectedRecoveredWh;
    }

    public RecoveryAssessment expectedRecoveredWh(BigDecimal expectedRecoveredWh) {
        this.setExpectedRecoveredWh(expectedRecoveredWh);
        return this;
    }

    public void setExpectedRecoveredWh(BigDecimal expectedRecoveredWh) {
        this.expectedRecoveredWh = expectedRecoveredWh;
    }

    public BigDecimal getVerifiedRecoveredWh() {
        return this.verifiedRecoveredWh;
    }

    public RecoveryAssessment verifiedRecoveredWh(BigDecimal verifiedRecoveredWh) {
        this.setVerifiedRecoveredWh(verifiedRecoveredWh);
        return this;
    }

    public void setVerifiedRecoveredWh(BigDecimal verifiedRecoveredWh) {
        this.verifiedRecoveredWh = verifiedRecoveredWh;
    }

    public String getConfidenceNote() {
        return this.confidenceNote;
    }

    public RecoveryAssessment confidenceNote(String confidenceNote) {
        this.setConfidenceNote(confidenceNote);
        return this;
    }

    public void setConfidenceNote(String confidenceNote) {
        this.confidenceNote = confidenceNote;
    }

    public Organization getOrganization() {
        return this.organization;
    }

    public void setOrganization(Organization organization) {
        this.organization = organization;
        this.organizationId = organization != null ? organization.getId() : null;
    }

    public RecoveryAssessment organization(Organization organization) {
        this.setOrganization(organization);
        return this;
    }

    public MaintenanceIntervention getIntervention() {
        return this.intervention;
    }

    public void setIntervention(MaintenanceIntervention maintenanceIntervention) {
        this.intervention = maintenanceIntervention;
        this.interventionId = maintenanceIntervention != null ? maintenanceIntervention.getId() : null;
    }

    public RecoveryAssessment intervention(MaintenanceIntervention maintenanceIntervention) {
        this.setIntervention(maintenanceIntervention);
        return this;
    }

    public EnergyModelRun getModelRun() {
        return this.modelRun;
    }

    public void setModelRun(EnergyModelRun energyModelRun) {
        this.modelRun = energyModelRun;
        this.modelRunId = energyModelRun != null ? energyModelRun.getId() : null;
    }

    public RecoveryAssessment modelRun(EnergyModelRun energyModelRun) {
        this.setModelRun(energyModelRun);
        return this;
    }

    public Long getOrganizationId() {
        return this.organizationId;
    }

    public void setOrganizationId(Long organization) {
        this.organizationId = organization;
    }

    public Long getInterventionId() {
        return this.interventionId;
    }

    public void setInterventionId(Long maintenanceIntervention) {
        this.interventionId = maintenanceIntervention;
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
        if (!(o instanceof RecoveryAssessment)) {
            return false;
        }
        return getId() != null && getId().equals(((RecoveryAssessment) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "RecoveryAssessment{" +
            "id=" + getId() +
            ", assessedAt='" + getAssessedAt() + "'" +
            ", baselineMethod='" + getBaselineMethod() + "'" +
            ", expectedRecoveredWh=" + getExpectedRecoveredWh() +
            ", verifiedRecoveredWh=" + getVerifiedRecoveredWh() +
            ", confidenceNote='" + getConfidenceNote() + "'" +
            "}";
    }
}

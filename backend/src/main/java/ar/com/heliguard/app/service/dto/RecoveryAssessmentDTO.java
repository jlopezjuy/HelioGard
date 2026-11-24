package ar.com.heliguard.app.service.dto;

import jakarta.persistence.Lob;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link ar.com.heliguard.app.domain.RecoveryAssessment} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class RecoveryAssessmentDTO implements Serializable {

    private Long id;

    @NotNull
    private Instant assessedAt;

    @NotNull
    @Size(max = 120)
    private String baselineMethod;

    private BigDecimal expectedRecoveredWh;

    private BigDecimal verifiedRecoveredWh;

    @Lob
    private String confidenceNote;

    private OrganizationDTO organization;

    @NotNull
    private MaintenanceInterventionDTO intervention;

    private EnergyModelRunDTO modelRun;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instant getAssessedAt() {
        return assessedAt;
    }

    public void setAssessedAt(Instant assessedAt) {
        this.assessedAt = assessedAt;
    }

    public String getBaselineMethod() {
        return baselineMethod;
    }

    public void setBaselineMethod(String baselineMethod) {
        this.baselineMethod = baselineMethod;
    }

    public BigDecimal getExpectedRecoveredWh() {
        return expectedRecoveredWh;
    }

    public void setExpectedRecoveredWh(BigDecimal expectedRecoveredWh) {
        this.expectedRecoveredWh = expectedRecoveredWh;
    }

    public BigDecimal getVerifiedRecoveredWh() {
        return verifiedRecoveredWh;
    }

    public void setVerifiedRecoveredWh(BigDecimal verifiedRecoveredWh) {
        this.verifiedRecoveredWh = verifiedRecoveredWh;
    }

    public String getConfidenceNote() {
        return confidenceNote;
    }

    public void setConfidenceNote(String confidenceNote) {
        this.confidenceNote = confidenceNote;
    }

    public OrganizationDTO getOrganization() {
        return organization;
    }

    public void setOrganization(OrganizationDTO organization) {
        this.organization = organization;
    }

    public MaintenanceInterventionDTO getIntervention() {
        return intervention;
    }

    public void setIntervention(MaintenanceInterventionDTO intervention) {
        this.intervention = intervention;
    }

    public EnergyModelRunDTO getModelRun() {
        return modelRun;
    }

    public void setModelRun(EnergyModelRunDTO modelRun) {
        this.modelRun = modelRun;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof RecoveryAssessmentDTO)) {
            return false;
        }

        RecoveryAssessmentDTO recoveryAssessmentDTO = (RecoveryAssessmentDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, recoveryAssessmentDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "RecoveryAssessmentDTO{" +
            "id=" + getId() +
            ", assessedAt='" + getAssessedAt() + "'" +
            ", baselineMethod='" + getBaselineMethod() + "'" +
            ", expectedRecoveredWh=" + getExpectedRecoveredWh() +
            ", verifiedRecoveredWh=" + getVerifiedRecoveredWh() +
            ", confidenceNote='" + getConfidenceNote() + "'" +
            ", organization=" + getOrganization() +
            ", intervention=" + getIntervention() +
            ", modelRun=" + getModelRun() +
            "}";
    }
}

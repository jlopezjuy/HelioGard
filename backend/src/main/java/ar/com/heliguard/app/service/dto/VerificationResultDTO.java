package ar.com.heliguard.app.service.dto;

import ar.com.heliguard.app.domain.enumeration.VerificationStatus;
import jakarta.persistence.Lob;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link ar.com.heliguard.app.domain.VerificationResult} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class VerificationResultDTO implements Serializable {

    private Long id;

    @NotNull
    private VerificationStatus status;

    @NotNull
    private Instant assessedAt;

    @NotNull
    @Size(max = 120)
    private String baselineMethod;

    private BigDecimal recoveredWh;

    @Lob
    private String evidenceSummary;

    private OrganizationDTO organization;

    @NotNull
    private InstallationDTO installation;

    private DroneMissionDTO droneMission;

    private RecoveryAssessmentDTO recoveryAssessment;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public VerificationStatus getStatus() {
        return status;
    }

    public void setStatus(VerificationStatus status) {
        this.status = status;
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

    public BigDecimal getRecoveredWh() {
        return recoveredWh;
    }

    public void setRecoveredWh(BigDecimal recoveredWh) {
        this.recoveredWh = recoveredWh;
    }

    public String getEvidenceSummary() {
        return evidenceSummary;
    }

    public void setEvidenceSummary(String evidenceSummary) {
        this.evidenceSummary = evidenceSummary;
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

    public DroneMissionDTO getDroneMission() {
        return droneMission;
    }

    public void setDroneMission(DroneMissionDTO droneMission) {
        this.droneMission = droneMission;
    }

    public RecoveryAssessmentDTO getRecoveryAssessment() {
        return recoveryAssessment;
    }

    public void setRecoveryAssessment(RecoveryAssessmentDTO recoveryAssessment) {
        this.recoveryAssessment = recoveryAssessment;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof VerificationResultDTO)) {
            return false;
        }

        VerificationResultDTO verificationResultDTO = (VerificationResultDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, verificationResultDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "VerificationResultDTO{" +
            "id=" + getId() +
            ", status='" + getStatus() + "'" +
            ", assessedAt='" + getAssessedAt() + "'" +
            ", baselineMethod='" + getBaselineMethod() + "'" +
            ", recoveredWh=" + getRecoveredWh() +
            ", evidenceSummary='" + getEvidenceSummary() + "'" +
            ", organization=" + getOrganization() +
            ", installation=" + getInstallation() +
            ", droneMission=" + getDroneMission() +
            ", recoveryAssessment=" + getRecoveryAssessment() +
            "}";
    }
}

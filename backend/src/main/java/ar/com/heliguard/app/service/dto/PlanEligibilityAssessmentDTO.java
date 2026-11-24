package ar.com.heliguard.app.service.dto;

import ar.com.heliguard.app.domain.enumeration.PlanEligibilityStatus;
import jakarta.persistence.Lob;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link ar.com.heliguard.app.domain.PlanEligibilityAssessment} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class PlanEligibilityAssessmentDTO implements Serializable {

    private Long id;

    @NotNull
    private PlanEligibilityStatus status;

    @NotNull
    private Instant assessedAt;

    @Lob
    private String rationale;

    @NotNull
    private Boolean requiresCorrection;

    @NotNull
    private OrganizationDTO organization;

    @NotNull
    private InstallationDTO installation;

    private AuditReportDTO auditReport;

    private MaintenancePlanVersionDTO planVersion;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public PlanEligibilityStatus getStatus() {
        return status;
    }

    public void setStatus(PlanEligibilityStatus status) {
        this.status = status;
    }

    public Instant getAssessedAt() {
        return assessedAt;
    }

    public void setAssessedAt(Instant assessedAt) {
        this.assessedAt = assessedAt;
    }

    public String getRationale() {
        return rationale;
    }

    public void setRationale(String rationale) {
        this.rationale = rationale;
    }

    public Boolean getRequiresCorrection() {
        return requiresCorrection;
    }

    public void setRequiresCorrection(Boolean requiresCorrection) {
        this.requiresCorrection = requiresCorrection;
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

    public AuditReportDTO getAuditReport() {
        return auditReport;
    }

    public void setAuditReport(AuditReportDTO auditReport) {
        this.auditReport = auditReport;
    }

    public MaintenancePlanVersionDTO getPlanVersion() {
        return planVersion;
    }

    public void setPlanVersion(MaintenancePlanVersionDTO planVersion) {
        this.planVersion = planVersion;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PlanEligibilityAssessmentDTO)) {
            return false;
        }

        PlanEligibilityAssessmentDTO planEligibilityAssessmentDTO = (PlanEligibilityAssessmentDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, planEligibilityAssessmentDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "PlanEligibilityAssessmentDTO{" +
            "id=" + getId() +
            ", status='" + getStatus() + "'" +
            ", assessedAt='" + getAssessedAt() + "'" +
            ", rationale='" + getRationale() + "'" +
            ", requiresCorrection='" + getRequiresCorrection() + "'" +
            ", organization=" + getOrganization() +
            ", installation=" + getInstallation() +
            ", auditReport=" + getAuditReport() +
            ", planVersion=" + getPlanVersion() +
            "}";
    }
}

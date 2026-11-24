package ar.com.heliguard.app.domain;

import ar.com.heliguard.app.domain.enumeration.PlanEligibilityStatus;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A PlanEligibilityAssessment.
 */
@Table("plan_eligibility_assessment")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class PlanEligibilityAssessment implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @NotNull
    @Column("status")
    private PlanEligibilityStatus status;

    @NotNull
    @Column("assessed_at")
    private Instant assessedAt;

    @Column("rationale")
    private String rationale;

    @NotNull
    @Column("requires_correction")
    private Boolean requiresCorrection;

    @org.springframework.data.annotation.Transient
    private Organization organization;

    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "organization", "site", "climateRiskProfile" }, allowSetters = true)
    private Installation installation;

    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "organization", "installation", "auditTemplate", "auditorMembership" }, allowSetters = true)
    private AuditReport auditReport;

    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "organization", "maintenancePlan" }, allowSetters = true)
    private MaintenancePlanVersion planVersion;

    @Column("organization_id")
    private Long organizationId;

    @Column("installation_id")
    private Long installationId;

    @Column("audit_report_id")
    private Long auditReportId;

    @Column("plan_version_id")
    private Long planVersionId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public PlanEligibilityAssessment id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public PlanEligibilityStatus getStatus() {
        return this.status;
    }

    public PlanEligibilityAssessment status(PlanEligibilityStatus status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(PlanEligibilityStatus status) {
        this.status = status;
    }

    public Instant getAssessedAt() {
        return this.assessedAt;
    }

    public PlanEligibilityAssessment assessedAt(Instant assessedAt) {
        this.setAssessedAt(assessedAt);
        return this;
    }

    public void setAssessedAt(Instant assessedAt) {
        this.assessedAt = assessedAt;
    }

    public String getRationale() {
        return this.rationale;
    }

    public PlanEligibilityAssessment rationale(String rationale) {
        this.setRationale(rationale);
        return this;
    }

    public void setRationale(String rationale) {
        this.rationale = rationale;
    }

    public Boolean getRequiresCorrection() {
        return this.requiresCorrection;
    }

    public PlanEligibilityAssessment requiresCorrection(Boolean requiresCorrection) {
        this.setRequiresCorrection(requiresCorrection);
        return this;
    }

    public void setRequiresCorrection(Boolean requiresCorrection) {
        this.requiresCorrection = requiresCorrection;
    }

    public Organization getOrganization() {
        return this.organization;
    }

    public void setOrganization(Organization organization) {
        this.organization = organization;
        this.organizationId = organization != null ? organization.getId() : null;
    }

    public PlanEligibilityAssessment organization(Organization organization) {
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

    public PlanEligibilityAssessment installation(Installation installation) {
        this.setInstallation(installation);
        return this;
    }

    public AuditReport getAuditReport() {
        return this.auditReport;
    }

    public void setAuditReport(AuditReport auditReport) {
        this.auditReport = auditReport;
        this.auditReportId = auditReport != null ? auditReport.getId() : null;
    }

    public PlanEligibilityAssessment auditReport(AuditReport auditReport) {
        this.setAuditReport(auditReport);
        return this;
    }

    public MaintenancePlanVersion getPlanVersion() {
        return this.planVersion;
    }

    public void setPlanVersion(MaintenancePlanVersion maintenancePlanVersion) {
        this.planVersion = maintenancePlanVersion;
        this.planVersionId = maintenancePlanVersion != null ? maintenancePlanVersion.getId() : null;
    }

    public PlanEligibilityAssessment planVersion(MaintenancePlanVersion maintenancePlanVersion) {
        this.setPlanVersion(maintenancePlanVersion);
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

    public Long getAuditReportId() {
        return this.auditReportId;
    }

    public void setAuditReportId(Long auditReport) {
        this.auditReportId = auditReport;
    }

    public Long getPlanVersionId() {
        return this.planVersionId;
    }

    public void setPlanVersionId(Long maintenancePlanVersion) {
        this.planVersionId = maintenancePlanVersion;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PlanEligibilityAssessment)) {
            return false;
        }
        return getId() != null && getId().equals(((PlanEligibilityAssessment) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "PlanEligibilityAssessment{" +
            "id=" + getId() +
            ", status='" + getStatus() + "'" +
            ", assessedAt='" + getAssessedAt() + "'" +
            ", rationale='" + getRationale() + "'" +
            ", requiresCorrection='" + getRequiresCorrection() + "'" +
            "}";
    }
}

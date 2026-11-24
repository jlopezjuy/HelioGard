package ar.com.heliguard.app.service.dto;

import ar.com.heliguard.app.domain.enumeration.AuditStatus;
import jakarta.persistence.Lob;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link ar.com.heliguard.app.domain.AuditReport} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class AuditReportDTO implements Serializable {

    private Long id;

    @NotNull
    private AuditStatus status;

    @NotNull
    private Instant startedAt;

    private Instant completedAt;

    @Lob
    private String summary;

    @NotNull
    private OrganizationDTO organization;

    @NotNull
    private InstallationDTO installation;

    @NotNull
    private AuditTemplateDTO auditTemplate;

    private OrganizationMembershipDTO auditorMembership;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public AuditStatus getStatus() {
        return status;
    }

    public void setStatus(AuditStatus status) {
        this.status = status;
    }

    public Instant getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(Instant startedAt) {
        this.startedAt = startedAt;
    }

    public Instant getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(Instant completedAt) {
        this.completedAt = completedAt;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
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

    public AuditTemplateDTO getAuditTemplate() {
        return auditTemplate;
    }

    public void setAuditTemplate(AuditTemplateDTO auditTemplate) {
        this.auditTemplate = auditTemplate;
    }

    public OrganizationMembershipDTO getAuditorMembership() {
        return auditorMembership;
    }

    public void setAuditorMembership(OrganizationMembershipDTO auditorMembership) {
        this.auditorMembership = auditorMembership;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AuditReportDTO)) {
            return false;
        }

        AuditReportDTO auditReportDTO = (AuditReportDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, auditReportDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "AuditReportDTO{" +
            "id=" + getId() +
            ", status='" + getStatus() + "'" +
            ", startedAt='" + getStartedAt() + "'" +
            ", completedAt='" + getCompletedAt() + "'" +
            ", summary='" + getSummary() + "'" +
            ", organization=" + getOrganization() +
            ", installation=" + getInstallation() +
            ", auditTemplate=" + getAuditTemplate() +
            ", auditorMembership=" + getAuditorMembership() +
            "}";
    }
}

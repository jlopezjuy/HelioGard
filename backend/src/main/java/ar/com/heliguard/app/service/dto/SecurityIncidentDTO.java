package ar.com.heliguard.app.service.dto;

import ar.com.heliguard.app.domain.enumeration.SecurityCaseStatus;
import jakarta.persistence.Lob;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link ar.com.heliguard.app.domain.SecurityIncident} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class SecurityIncidentDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(max = 100)
    private String referenceCode;

    @NotNull
    @Size(max = 180)
    private String title;

    @NotNull
    private SecurityCaseStatus status;

    @NotNull
    private Instant openedAt;

    private Instant closedAt;

    @Lob
    private String resolutionNote;

    private OrganizationDTO organization;

    @NotNull
    private InstallationDTO installation;

    private IntegrityAssessmentDTO integrityAssessment;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getReferenceCode() {
        return referenceCode;
    }

    public void setReferenceCode(String referenceCode) {
        this.referenceCode = referenceCode;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public SecurityCaseStatus getStatus() {
        return status;
    }

    public void setStatus(SecurityCaseStatus status) {
        this.status = status;
    }

    public Instant getOpenedAt() {
        return openedAt;
    }

    public void setOpenedAt(Instant openedAt) {
        this.openedAt = openedAt;
    }

    public Instant getClosedAt() {
        return closedAt;
    }

    public void setClosedAt(Instant closedAt) {
        this.closedAt = closedAt;
    }

    public String getResolutionNote() {
        return resolutionNote;
    }

    public void setResolutionNote(String resolutionNote) {
        this.resolutionNote = resolutionNote;
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

    public IntegrityAssessmentDTO getIntegrityAssessment() {
        return integrityAssessment;
    }

    public void setIntegrityAssessment(IntegrityAssessmentDTO integrityAssessment) {
        this.integrityAssessment = integrityAssessment;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SecurityIncidentDTO)) {
            return false;
        }

        SecurityIncidentDTO securityIncidentDTO = (SecurityIncidentDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, securityIncidentDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "SecurityIncidentDTO{" +
            "id=" + getId() +
            ", referenceCode='" + getReferenceCode() + "'" +
            ", title='" + getTitle() + "'" +
            ", status='" + getStatus() + "'" +
            ", openedAt='" + getOpenedAt() + "'" +
            ", closedAt='" + getClosedAt() + "'" +
            ", resolutionNote='" + getResolutionNote() + "'" +
            ", organization=" + getOrganization() +
            ", installation=" + getInstallation() +
            ", integrityAssessment=" + getIntegrityAssessment() +
            "}";
    }
}

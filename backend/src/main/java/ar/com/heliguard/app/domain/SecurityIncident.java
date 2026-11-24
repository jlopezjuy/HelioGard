package ar.com.heliguard.app.domain;

import ar.com.heliguard.app.domain.enumeration.SecurityCaseStatus;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A SecurityIncident.
 */
@Table("security_incident")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class SecurityIncident implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @NotNull
    @Size(max = 100)
    @Column("reference_code")
    private String referenceCode;

    @NotNull
    @Size(max = 180)
    @Column("title")
    private String title;

    @NotNull
    @Column("status")
    private SecurityCaseStatus status;

    @NotNull
    @Column("opened_at")
    private Instant openedAt;

    @Column("closed_at")
    private Instant closedAt;

    @Column("resolution_note")
    private String resolutionNote;

    @org.springframework.data.annotation.Transient
    private Organization organization;

    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "organization", "site", "climateRiskProfile" }, allowSetters = true)
    private Installation installation;

    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "organization", "installation", "integrityEvent", "modelRun" }, allowSetters = true)
    private IntegrityAssessment integrityAssessment;

    @Column("organization_id")
    private Long organizationId;

    @Column("installation_id")
    private Long installationId;

    @Column("integrity_assessment_id")
    private Long integrityAssessmentId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public SecurityIncident id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getReferenceCode() {
        return this.referenceCode;
    }

    public SecurityIncident referenceCode(String referenceCode) {
        this.setReferenceCode(referenceCode);
        return this;
    }

    public void setReferenceCode(String referenceCode) {
        this.referenceCode = referenceCode;
    }

    public String getTitle() {
        return this.title;
    }

    public SecurityIncident title(String title) {
        this.setTitle(title);
        return this;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public SecurityCaseStatus getStatus() {
        return this.status;
    }

    public SecurityIncident status(SecurityCaseStatus status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(SecurityCaseStatus status) {
        this.status = status;
    }

    public Instant getOpenedAt() {
        return this.openedAt;
    }

    public SecurityIncident openedAt(Instant openedAt) {
        this.setOpenedAt(openedAt);
        return this;
    }

    public void setOpenedAt(Instant openedAt) {
        this.openedAt = openedAt;
    }

    public Instant getClosedAt() {
        return this.closedAt;
    }

    public SecurityIncident closedAt(Instant closedAt) {
        this.setClosedAt(closedAt);
        return this;
    }

    public void setClosedAt(Instant closedAt) {
        this.closedAt = closedAt;
    }

    public String getResolutionNote() {
        return this.resolutionNote;
    }

    public SecurityIncident resolutionNote(String resolutionNote) {
        this.setResolutionNote(resolutionNote);
        return this;
    }

    public void setResolutionNote(String resolutionNote) {
        this.resolutionNote = resolutionNote;
    }

    public Organization getOrganization() {
        return this.organization;
    }

    public void setOrganization(Organization organization) {
        this.organization = organization;
        this.organizationId = organization != null ? organization.getId() : null;
    }

    public SecurityIncident organization(Organization organization) {
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

    public SecurityIncident installation(Installation installation) {
        this.setInstallation(installation);
        return this;
    }

    public IntegrityAssessment getIntegrityAssessment() {
        return this.integrityAssessment;
    }

    public void setIntegrityAssessment(IntegrityAssessment integrityAssessment) {
        this.integrityAssessment = integrityAssessment;
        this.integrityAssessmentId = integrityAssessment != null ? integrityAssessment.getId() : null;
    }

    public SecurityIncident integrityAssessment(IntegrityAssessment integrityAssessment) {
        this.setIntegrityAssessment(integrityAssessment);
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

    public Long getIntegrityAssessmentId() {
        return this.integrityAssessmentId;
    }

    public void setIntegrityAssessmentId(Long integrityAssessment) {
        this.integrityAssessmentId = integrityAssessment;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SecurityIncident)) {
            return false;
        }
        return getId() != null && getId().equals(((SecurityIncident) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "SecurityIncident{" +
            "id=" + getId() +
            ", referenceCode='" + getReferenceCode() + "'" +
            ", title='" + getTitle() + "'" +
            ", status='" + getStatus() + "'" +
            ", openedAt='" + getOpenedAt() + "'" +
            ", closedAt='" + getClosedAt() + "'" +
            ", resolutionNote='" + getResolutionNote() + "'" +
            "}";
    }
}

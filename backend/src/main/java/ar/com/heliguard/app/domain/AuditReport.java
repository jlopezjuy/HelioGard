package ar.com.heliguard.app.domain;

import ar.com.heliguard.app.domain.enumeration.AuditStatus;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A AuditReport.
 */
@Table("audit_report")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class AuditReport implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @NotNull
    @Column("status")
    private AuditStatus status;

    @NotNull
    @Column("started_at")
    private Instant startedAt;

    @Column("completed_at")
    private Instant completedAt;

    @Column("summary")
    private String summary;

    @org.springframework.data.annotation.Transient
    private Organization organization;

    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "organization", "site", "climateRiskProfile" }, allowSetters = true)
    private Installation installation;

    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "organization" }, allowSetters = true)
    private AuditTemplate auditTemplate;

    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "organization", "member" }, allowSetters = true)
    private OrganizationMembership auditorMembership;

    @Column("organization_id")
    private Long organizationId;

    @Column("installation_id")
    private Long installationId;

    @Column("audit_template_id")
    private Long auditTemplateId;

    @Column("auditor_membership_id")
    private Long auditorMembershipId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public AuditReport id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public AuditStatus getStatus() {
        return this.status;
    }

    public AuditReport status(AuditStatus status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(AuditStatus status) {
        this.status = status;
    }

    public Instant getStartedAt() {
        return this.startedAt;
    }

    public AuditReport startedAt(Instant startedAt) {
        this.setStartedAt(startedAt);
        return this;
    }

    public void setStartedAt(Instant startedAt) {
        this.startedAt = startedAt;
    }

    public Instant getCompletedAt() {
        return this.completedAt;
    }

    public AuditReport completedAt(Instant completedAt) {
        this.setCompletedAt(completedAt);
        return this;
    }

    public void setCompletedAt(Instant completedAt) {
        this.completedAt = completedAt;
    }

    public String getSummary() {
        return this.summary;
    }

    public AuditReport summary(String summary) {
        this.setSummary(summary);
        return this;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public Organization getOrganization() {
        return this.organization;
    }

    public void setOrganization(Organization organization) {
        this.organization = organization;
        this.organizationId = organization != null ? organization.getId() : null;
    }

    public AuditReport organization(Organization organization) {
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

    public AuditReport installation(Installation installation) {
        this.setInstallation(installation);
        return this;
    }

    public AuditTemplate getAuditTemplate() {
        return this.auditTemplate;
    }

    public void setAuditTemplate(AuditTemplate auditTemplate) {
        this.auditTemplate = auditTemplate;
        this.auditTemplateId = auditTemplate != null ? auditTemplate.getId() : null;
    }

    public AuditReport auditTemplate(AuditTemplate auditTemplate) {
        this.setAuditTemplate(auditTemplate);
        return this;
    }

    public OrganizationMembership getAuditorMembership() {
        return this.auditorMembership;
    }

    public void setAuditorMembership(OrganizationMembership organizationMembership) {
        this.auditorMembership = organizationMembership;
        this.auditorMembershipId = organizationMembership != null ? organizationMembership.getId() : null;
    }

    public AuditReport auditorMembership(OrganizationMembership organizationMembership) {
        this.setAuditorMembership(organizationMembership);
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

    public Long getAuditTemplateId() {
        return this.auditTemplateId;
    }

    public void setAuditTemplateId(Long auditTemplate) {
        this.auditTemplateId = auditTemplate;
    }

    public Long getAuditorMembershipId() {
        return this.auditorMembershipId;
    }

    public void setAuditorMembershipId(Long organizationMembership) {
        this.auditorMembershipId = organizationMembership;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AuditReport)) {
            return false;
        }
        return getId() != null && getId().equals(((AuditReport) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "AuditReport{" +
            "id=" + getId() +
            ", status='" + getStatus() + "'" +
            ", startedAt='" + getStartedAt() + "'" +
            ", completedAt='" + getCompletedAt() + "'" +
            ", summary='" + getSummary() + "'" +
            "}";
    }
}

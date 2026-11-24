package ar.com.heliguard.app.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A AuditReportSignature.
 */
@Table("audit_report_signature")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class AuditReportSignature implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @NotNull
    @Column("signed_at")
    private Instant signedAt;

    @NotNull
    @Size(max = 64)
    @Column("document_sha_256")
    private String documentSha256;

    @Size(max = 400)
    @Column("signature_reference")
    private String signatureReference;

    @org.springframework.data.annotation.Transient
    private Organization organization;

    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "organization", "installation", "auditTemplate", "auditorMembership" }, allowSetters = true)
    private AuditReport auditReport;

    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "organization", "member" }, allowSetters = true)
    private OrganizationMembership signerMembership;

    @Column("organization_id")
    private Long organizationId;

    @Column("audit_report_id")
    private Long auditReportId;

    @Column("signer_membership_id")
    private Long signerMembershipId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public AuditReportSignature id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instant getSignedAt() {
        return this.signedAt;
    }

    public AuditReportSignature signedAt(Instant signedAt) {
        this.setSignedAt(signedAt);
        return this;
    }

    public void setSignedAt(Instant signedAt) {
        this.signedAt = signedAt;
    }

    public String getDocumentSha256() {
        return this.documentSha256;
    }

    public AuditReportSignature documentSha256(String documentSha256) {
        this.setDocumentSha256(documentSha256);
        return this;
    }

    public void setDocumentSha256(String documentSha256) {
        this.documentSha256 = documentSha256;
    }

    public String getSignatureReference() {
        return this.signatureReference;
    }

    public AuditReportSignature signatureReference(String signatureReference) {
        this.setSignatureReference(signatureReference);
        return this;
    }

    public void setSignatureReference(String signatureReference) {
        this.signatureReference = signatureReference;
    }

    public Organization getOrganization() {
        return this.organization;
    }

    public void setOrganization(Organization organization) {
        this.organization = organization;
        this.organizationId = organization != null ? organization.getId() : null;
    }

    public AuditReportSignature organization(Organization organization) {
        this.setOrganization(organization);
        return this;
    }

    public AuditReport getAuditReport() {
        return this.auditReport;
    }

    public void setAuditReport(AuditReport auditReport) {
        this.auditReport = auditReport;
        this.auditReportId = auditReport != null ? auditReport.getId() : null;
    }

    public AuditReportSignature auditReport(AuditReport auditReport) {
        this.setAuditReport(auditReport);
        return this;
    }

    public OrganizationMembership getSignerMembership() {
        return this.signerMembership;
    }

    public void setSignerMembership(OrganizationMembership organizationMembership) {
        this.signerMembership = organizationMembership;
        this.signerMembershipId = organizationMembership != null ? organizationMembership.getId() : null;
    }

    public AuditReportSignature signerMembership(OrganizationMembership organizationMembership) {
        this.setSignerMembership(organizationMembership);
        return this;
    }

    public Long getOrganizationId() {
        return this.organizationId;
    }

    public void setOrganizationId(Long organization) {
        this.organizationId = organization;
    }

    public Long getAuditReportId() {
        return this.auditReportId;
    }

    public void setAuditReportId(Long auditReport) {
        this.auditReportId = auditReport;
    }

    public Long getSignerMembershipId() {
        return this.signerMembershipId;
    }

    public void setSignerMembershipId(Long organizationMembership) {
        this.signerMembershipId = organizationMembership;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AuditReportSignature)) {
            return false;
        }
        return getId() != null && getId().equals(((AuditReportSignature) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "AuditReportSignature{" +
            "id=" + getId() +
            ", signedAt='" + getSignedAt() + "'" +
            ", documentSha256='" + getDocumentSha256() + "'" +
            ", signatureReference='" + getSignatureReference() + "'" +
            "}";
    }
}

package ar.com.heliguard.app.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link ar.com.heliguard.app.domain.AuditReportSignature} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class AuditReportSignatureDTO implements Serializable {

    private Long id;

    @NotNull
    private Instant signedAt;

    @NotNull
    @Size(max = 64)
    private String documentSha256;

    @Size(max = 400)
    private String signatureReference;

    private OrganizationDTO organization;

    @NotNull
    private AuditReportDTO auditReport;

    @NotNull
    private OrganizationMembershipDTO signerMembership;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instant getSignedAt() {
        return signedAt;
    }

    public void setSignedAt(Instant signedAt) {
        this.signedAt = signedAt;
    }

    public String getDocumentSha256() {
        return documentSha256;
    }

    public void setDocumentSha256(String documentSha256) {
        this.documentSha256 = documentSha256;
    }

    public String getSignatureReference() {
        return signatureReference;
    }

    public void setSignatureReference(String signatureReference) {
        this.signatureReference = signatureReference;
    }

    public OrganizationDTO getOrganization() {
        return organization;
    }

    public void setOrganization(OrganizationDTO organization) {
        this.organization = organization;
    }

    public AuditReportDTO getAuditReport() {
        return auditReport;
    }

    public void setAuditReport(AuditReportDTO auditReport) {
        this.auditReport = auditReport;
    }

    public OrganizationMembershipDTO getSignerMembership() {
        return signerMembership;
    }

    public void setSignerMembership(OrganizationMembershipDTO signerMembership) {
        this.signerMembership = signerMembership;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AuditReportSignatureDTO)) {
            return false;
        }

        AuditReportSignatureDTO auditReportSignatureDTO = (AuditReportSignatureDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, auditReportSignatureDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "AuditReportSignatureDTO{" +
            "id=" + getId() +
            ", signedAt='" + getSignedAt() + "'" +
            ", documentSha256='" + getDocumentSha256() + "'" +
            ", signatureReference='" + getSignatureReference() + "'" +
            ", organization=" + getOrganization() +
            ", auditReport=" + getAuditReport() +
            ", signerMembership=" + getSignerMembership() +
            "}";
    }
}

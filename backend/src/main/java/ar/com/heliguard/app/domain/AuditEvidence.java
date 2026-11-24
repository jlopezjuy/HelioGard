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
 * A AuditEvidence.
 */
@Table("audit_evidence")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class AuditEvidence implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @NotNull
    @Size(max = 400)
    @Column("storage_key")
    private String storageKey;

    @NotNull
    @Size(max = 100)
    @Column("mime_type")
    private String mimeType;

    @Size(max = 64)
    @Column("sha_256")
    private String sha256;

    @Column("captured_at")
    private Instant capturedAt;

    @org.springframework.data.annotation.Transient
    private Organization organization;

    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "organization", "auditReport", "equipment" }, allowSetters = true)
    private AuditFinding finding;

    @Column("organization_id")
    private Long organizationId;

    @Column("finding_id")
    private Long findingId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public AuditEvidence id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStorageKey() {
        return this.storageKey;
    }

    public AuditEvidence storageKey(String storageKey) {
        this.setStorageKey(storageKey);
        return this;
    }

    public void setStorageKey(String storageKey) {
        this.storageKey = storageKey;
    }

    public String getMimeType() {
        return this.mimeType;
    }

    public AuditEvidence mimeType(String mimeType) {
        this.setMimeType(mimeType);
        return this;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public String getSha256() {
        return this.sha256;
    }

    public AuditEvidence sha256(String sha256) {
        this.setSha256(sha256);
        return this;
    }

    public void setSha256(String sha256) {
        this.sha256 = sha256;
    }

    public Instant getCapturedAt() {
        return this.capturedAt;
    }

    public AuditEvidence capturedAt(Instant capturedAt) {
        this.setCapturedAt(capturedAt);
        return this;
    }

    public void setCapturedAt(Instant capturedAt) {
        this.capturedAt = capturedAt;
    }

    public Organization getOrganization() {
        return this.organization;
    }

    public void setOrganization(Organization organization) {
        this.organization = organization;
        this.organizationId = organization != null ? organization.getId() : null;
    }

    public AuditEvidence organization(Organization organization) {
        this.setOrganization(organization);
        return this;
    }

    public AuditFinding getFinding() {
        return this.finding;
    }

    public void setFinding(AuditFinding auditFinding) {
        this.finding = auditFinding;
        this.findingId = auditFinding != null ? auditFinding.getId() : null;
    }

    public AuditEvidence finding(AuditFinding auditFinding) {
        this.setFinding(auditFinding);
        return this;
    }

    public Long getOrganizationId() {
        return this.organizationId;
    }

    public void setOrganizationId(Long organization) {
        this.organizationId = organization;
    }

    public Long getFindingId() {
        return this.findingId;
    }

    public void setFindingId(Long auditFinding) {
        this.findingId = auditFinding;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AuditEvidence)) {
            return false;
        }
        return getId() != null && getId().equals(((AuditEvidence) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "AuditEvidence{" +
            "id=" + getId() +
            ", storageKey='" + getStorageKey() + "'" +
            ", mimeType='" + getMimeType() + "'" +
            ", sha256='" + getSha256() + "'" +
            ", capturedAt='" + getCapturedAt() + "'" +
            "}";
    }
}

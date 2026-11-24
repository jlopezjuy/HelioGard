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
 * A InterventionEvidence.
 */
@Table("intervention_evidence")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class InterventionEvidence implements Serializable {

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
    @JsonIgnoreProperties(value = { "organization", "workOrder", "performedByMembership" }, allowSetters = true)
    private MaintenanceIntervention intervention;

    @Column("organization_id")
    private Long organizationId;

    @Column("intervention_id")
    private Long interventionId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public InterventionEvidence id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStorageKey() {
        return this.storageKey;
    }

    public InterventionEvidence storageKey(String storageKey) {
        this.setStorageKey(storageKey);
        return this;
    }

    public void setStorageKey(String storageKey) {
        this.storageKey = storageKey;
    }

    public String getMimeType() {
        return this.mimeType;
    }

    public InterventionEvidence mimeType(String mimeType) {
        this.setMimeType(mimeType);
        return this;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public String getSha256() {
        return this.sha256;
    }

    public InterventionEvidence sha256(String sha256) {
        this.setSha256(sha256);
        return this;
    }

    public void setSha256(String sha256) {
        this.sha256 = sha256;
    }

    public Instant getCapturedAt() {
        return this.capturedAt;
    }

    public InterventionEvidence capturedAt(Instant capturedAt) {
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

    public InterventionEvidence organization(Organization organization) {
        this.setOrganization(organization);
        return this;
    }

    public MaintenanceIntervention getIntervention() {
        return this.intervention;
    }

    public void setIntervention(MaintenanceIntervention maintenanceIntervention) {
        this.intervention = maintenanceIntervention;
        this.interventionId = maintenanceIntervention != null ? maintenanceIntervention.getId() : null;
    }

    public InterventionEvidence intervention(MaintenanceIntervention maintenanceIntervention) {
        this.setIntervention(maintenanceIntervention);
        return this;
    }

    public Long getOrganizationId() {
        return this.organizationId;
    }

    public void setOrganizationId(Long organization) {
        this.organizationId = organization;
    }

    public Long getInterventionId() {
        return this.interventionId;
    }

    public void setInterventionId(Long maintenanceIntervention) {
        this.interventionId = maintenanceIntervention;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof InterventionEvidence)) {
            return false;
        }
        return getId() != null && getId().equals(((InterventionEvidence) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "InterventionEvidence{" +
            "id=" + getId() +
            ", storageKey='" + getStorageKey() + "'" +
            ", mimeType='" + getMimeType() + "'" +
            ", sha256='" + getSha256() + "'" +
            ", capturedAt='" + getCapturedAt() + "'" +
            "}";
    }
}

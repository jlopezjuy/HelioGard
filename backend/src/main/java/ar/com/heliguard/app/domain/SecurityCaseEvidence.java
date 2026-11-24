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
 * A SecurityCaseEvidence.
 */
@Table("security_case_evidence")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class SecurityCaseEvidence implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @NotNull
    @Size(max = 100)
    @Column("evidence_code")
    private String evidenceCode;

    @Size(max = 400)
    @Column("storage_reference")
    private String storageReference;

    @Size(max = 64)
    @Column("sha_256")
    private String sha256;

    @NotNull
    @Column("recorded_at")
    private Instant recordedAt;

    @Size(max = 250)
    @Column("note")
    private String note;

    @org.springframework.data.annotation.Transient
    private Organization organization;

    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "organization", "installation", "integrityAssessment" }, allowSetters = true)
    private SecurityIncident securityIncident;

    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "organization", "installation", "integrityEvent", "modelRun" }, allowSetters = true)
    private IntegrityAssessment integrityAssessment;

    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "organization", "installation", "evidenceSource", "modelRun" }, allowSetters = true)
    private DataProvenance dataProvenance;

    @Column("organization_id")
    private Long organizationId;

    @Column("security_incident_id")
    private Long securityIncidentId;

    @Column("integrity_assessment_id")
    private Long integrityAssessmentId;

    @Column("data_provenance_id")
    private Long dataProvenanceId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public SecurityCaseEvidence id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEvidenceCode() {
        return this.evidenceCode;
    }

    public SecurityCaseEvidence evidenceCode(String evidenceCode) {
        this.setEvidenceCode(evidenceCode);
        return this;
    }

    public void setEvidenceCode(String evidenceCode) {
        this.evidenceCode = evidenceCode;
    }

    public String getStorageReference() {
        return this.storageReference;
    }

    public SecurityCaseEvidence storageReference(String storageReference) {
        this.setStorageReference(storageReference);
        return this;
    }

    public void setStorageReference(String storageReference) {
        this.storageReference = storageReference;
    }

    public String getSha256() {
        return this.sha256;
    }

    public SecurityCaseEvidence sha256(String sha256) {
        this.setSha256(sha256);
        return this;
    }

    public void setSha256(String sha256) {
        this.sha256 = sha256;
    }

    public Instant getRecordedAt() {
        return this.recordedAt;
    }

    public SecurityCaseEvidence recordedAt(Instant recordedAt) {
        this.setRecordedAt(recordedAt);
        return this;
    }

    public void setRecordedAt(Instant recordedAt) {
        this.recordedAt = recordedAt;
    }

    public String getNote() {
        return this.note;
    }

    public SecurityCaseEvidence note(String note) {
        this.setNote(note);
        return this;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Organization getOrganization() {
        return this.organization;
    }

    public void setOrganization(Organization organization) {
        this.organization = organization;
        this.organizationId = organization != null ? organization.getId() : null;
    }

    public SecurityCaseEvidence organization(Organization organization) {
        this.setOrganization(organization);
        return this;
    }

    public SecurityIncident getSecurityIncident() {
        return this.securityIncident;
    }

    public void setSecurityIncident(SecurityIncident securityIncident) {
        this.securityIncident = securityIncident;
        this.securityIncidentId = securityIncident != null ? securityIncident.getId() : null;
    }

    public SecurityCaseEvidence securityIncident(SecurityIncident securityIncident) {
        this.setSecurityIncident(securityIncident);
        return this;
    }

    public IntegrityAssessment getIntegrityAssessment() {
        return this.integrityAssessment;
    }

    public void setIntegrityAssessment(IntegrityAssessment integrityAssessment) {
        this.integrityAssessment = integrityAssessment;
        this.integrityAssessmentId = integrityAssessment != null ? integrityAssessment.getId() : null;
    }

    public SecurityCaseEvidence integrityAssessment(IntegrityAssessment integrityAssessment) {
        this.setIntegrityAssessment(integrityAssessment);
        return this;
    }

    public DataProvenance getDataProvenance() {
        return this.dataProvenance;
    }

    public void setDataProvenance(DataProvenance dataProvenance) {
        this.dataProvenance = dataProvenance;
        this.dataProvenanceId = dataProvenance != null ? dataProvenance.getId() : null;
    }

    public SecurityCaseEvidence dataProvenance(DataProvenance dataProvenance) {
        this.setDataProvenance(dataProvenance);
        return this;
    }

    public Long getOrganizationId() {
        return this.organizationId;
    }

    public void setOrganizationId(Long organization) {
        this.organizationId = organization;
    }

    public Long getSecurityIncidentId() {
        return this.securityIncidentId;
    }

    public void setSecurityIncidentId(Long securityIncident) {
        this.securityIncidentId = securityIncident;
    }

    public Long getIntegrityAssessmentId() {
        return this.integrityAssessmentId;
    }

    public void setIntegrityAssessmentId(Long integrityAssessment) {
        this.integrityAssessmentId = integrityAssessment;
    }

    public Long getDataProvenanceId() {
        return this.dataProvenanceId;
    }

    public void setDataProvenanceId(Long dataProvenance) {
        this.dataProvenanceId = dataProvenance;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SecurityCaseEvidence)) {
            return false;
        }
        return getId() != null && getId().equals(((SecurityCaseEvidence) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "SecurityCaseEvidence{" +
            "id=" + getId() +
            ", evidenceCode='" + getEvidenceCode() + "'" +
            ", storageReference='" + getStorageReference() + "'" +
            ", sha256='" + getSha256() + "'" +
            ", recordedAt='" + getRecordedAt() + "'" +
            ", note='" + getNote() + "'" +
            "}";
    }
}

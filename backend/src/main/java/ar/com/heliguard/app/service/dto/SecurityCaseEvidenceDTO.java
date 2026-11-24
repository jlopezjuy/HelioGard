package ar.com.heliguard.app.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link ar.com.heliguard.app.domain.SecurityCaseEvidence} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class SecurityCaseEvidenceDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(max = 100)
    private String evidenceCode;

    @Size(max = 400)
    private String storageReference;

    @Size(max = 64)
    private String sha256;

    @NotNull
    private Instant recordedAt;

    @Size(max = 250)
    private String note;

    private OrganizationDTO organization;

    @NotNull
    private SecurityIncidentDTO securityIncident;

    private IntegrityAssessmentDTO integrityAssessment;

    private DataProvenanceDTO dataProvenance;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEvidenceCode() {
        return evidenceCode;
    }

    public void setEvidenceCode(String evidenceCode) {
        this.evidenceCode = evidenceCode;
    }

    public String getStorageReference() {
        return storageReference;
    }

    public void setStorageReference(String storageReference) {
        this.storageReference = storageReference;
    }

    public String getSha256() {
        return sha256;
    }

    public void setSha256(String sha256) {
        this.sha256 = sha256;
    }

    public Instant getRecordedAt() {
        return recordedAt;
    }

    public void setRecordedAt(Instant recordedAt) {
        this.recordedAt = recordedAt;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public OrganizationDTO getOrganization() {
        return organization;
    }

    public void setOrganization(OrganizationDTO organization) {
        this.organization = organization;
    }

    public SecurityIncidentDTO getSecurityIncident() {
        return securityIncident;
    }

    public void setSecurityIncident(SecurityIncidentDTO securityIncident) {
        this.securityIncident = securityIncident;
    }

    public IntegrityAssessmentDTO getIntegrityAssessment() {
        return integrityAssessment;
    }

    public void setIntegrityAssessment(IntegrityAssessmentDTO integrityAssessment) {
        this.integrityAssessment = integrityAssessment;
    }

    public DataProvenanceDTO getDataProvenance() {
        return dataProvenance;
    }

    public void setDataProvenance(DataProvenanceDTO dataProvenance) {
        this.dataProvenance = dataProvenance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SecurityCaseEvidenceDTO)) {
            return false;
        }

        SecurityCaseEvidenceDTO securityCaseEvidenceDTO = (SecurityCaseEvidenceDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, securityCaseEvidenceDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "SecurityCaseEvidenceDTO{" +
            "id=" + getId() +
            ", evidenceCode='" + getEvidenceCode() + "'" +
            ", storageReference='" + getStorageReference() + "'" +
            ", sha256='" + getSha256() + "'" +
            ", recordedAt='" + getRecordedAt() + "'" +
            ", note='" + getNote() + "'" +
            ", organization=" + getOrganization() +
            ", securityIncident=" + getSecurityIncident() +
            ", integrityAssessment=" + getIntegrityAssessment() +
            ", dataProvenance=" + getDataProvenance() +
            "}";
    }
}

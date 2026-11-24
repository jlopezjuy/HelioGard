package ar.com.heliguard.app.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link ar.com.heliguard.app.domain.InterventionEvidence} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class InterventionEvidenceDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(max = 400)
    private String storageKey;

    @NotNull
    @Size(max = 100)
    private String mimeType;

    @Size(max = 64)
    private String sha256;

    private Instant capturedAt;

    @NotNull
    private OrganizationDTO organization;

    @NotNull
    private MaintenanceInterventionDTO intervention;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStorageKey() {
        return storageKey;
    }

    public void setStorageKey(String storageKey) {
        this.storageKey = storageKey;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public String getSha256() {
        return sha256;
    }

    public void setSha256(String sha256) {
        this.sha256 = sha256;
    }

    public Instant getCapturedAt() {
        return capturedAt;
    }

    public void setCapturedAt(Instant capturedAt) {
        this.capturedAt = capturedAt;
    }

    public OrganizationDTO getOrganization() {
        return organization;
    }

    public void setOrganization(OrganizationDTO organization) {
        this.organization = organization;
    }

    public MaintenanceInterventionDTO getIntervention() {
        return intervention;
    }

    public void setIntervention(MaintenanceInterventionDTO intervention) {
        this.intervention = intervention;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof InterventionEvidenceDTO)) {
            return false;
        }

        InterventionEvidenceDTO interventionEvidenceDTO = (InterventionEvidenceDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, interventionEvidenceDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "InterventionEvidenceDTO{" +
            "id=" + getId() +
            ", storageKey='" + getStorageKey() + "'" +
            ", mimeType='" + getMimeType() + "'" +
            ", sha256='" + getSha256() + "'" +
            ", capturedAt='" + getCapturedAt() + "'" +
            ", organization=" + getOrganization() +
            ", intervention=" + getIntervention() +
            "}";
    }
}

package ar.com.heliguard.app.service.dto;

import ar.com.heliguard.app.domain.enumeration.DataTrustLevel;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link ar.com.heliguard.app.domain.DataProvenance} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class DataProvenanceDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(max = 180)
    private String sourceRecordKey;

    @NotNull
    private Instant observedAt;

    private Instant periodStart;

    private Instant periodEnd;

    @NotNull
    private Instant ingestedAt;

    @Size(max = 140)
    private String sourceProductCode;

    private BigDecimal spatialResolutionM;

    @Size(max = 64)
    private String contentSha256;

    @NotNull
    private DataTrustLevel trustLevel;

    @Size(max = 300)
    private String qualityFlags;

    @Size(max = 80)
    private String algorithmVersion;

    @NotNull
    private OrganizationDTO organization;

    @NotNull
    private InstallationDTO installation;

    @NotNull
    private EvidenceSourceDTO evidenceSource;

    private EnergyModelRunDTO modelRun;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSourceRecordKey() {
        return sourceRecordKey;
    }

    public void setSourceRecordKey(String sourceRecordKey) {
        this.sourceRecordKey = sourceRecordKey;
    }

    public Instant getObservedAt() {
        return observedAt;
    }

    public void setObservedAt(Instant observedAt) {
        this.observedAt = observedAt;
    }

    public Instant getPeriodStart() {
        return periodStart;
    }

    public void setPeriodStart(Instant periodStart) {
        this.periodStart = periodStart;
    }

    public Instant getPeriodEnd() {
        return periodEnd;
    }

    public void setPeriodEnd(Instant periodEnd) {
        this.periodEnd = periodEnd;
    }

    public Instant getIngestedAt() {
        return ingestedAt;
    }

    public void setIngestedAt(Instant ingestedAt) {
        this.ingestedAt = ingestedAt;
    }

    public String getSourceProductCode() {
        return sourceProductCode;
    }

    public void setSourceProductCode(String sourceProductCode) {
        this.sourceProductCode = sourceProductCode;
    }

    public BigDecimal getSpatialResolutionM() {
        return spatialResolutionM;
    }

    public void setSpatialResolutionM(BigDecimal spatialResolutionM) {
        this.spatialResolutionM = spatialResolutionM;
    }

    public String getContentSha256() {
        return contentSha256;
    }

    public void setContentSha256(String contentSha256) {
        this.contentSha256 = contentSha256;
    }

    public DataTrustLevel getTrustLevel() {
        return trustLevel;
    }

    public void setTrustLevel(DataTrustLevel trustLevel) {
        this.trustLevel = trustLevel;
    }

    public String getQualityFlags() {
        return qualityFlags;
    }

    public void setQualityFlags(String qualityFlags) {
        this.qualityFlags = qualityFlags;
    }

    public String getAlgorithmVersion() {
        return algorithmVersion;
    }

    public void setAlgorithmVersion(String algorithmVersion) {
        this.algorithmVersion = algorithmVersion;
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

    public EvidenceSourceDTO getEvidenceSource() {
        return evidenceSource;
    }

    public void setEvidenceSource(EvidenceSourceDTO evidenceSource) {
        this.evidenceSource = evidenceSource;
    }

    public EnergyModelRunDTO getModelRun() {
        return modelRun;
    }

    public void setModelRun(EnergyModelRunDTO modelRun) {
        this.modelRun = modelRun;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DataProvenanceDTO)) {
            return false;
        }

        DataProvenanceDTO dataProvenanceDTO = (DataProvenanceDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, dataProvenanceDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "DataProvenanceDTO{" +
            "id=" + getId() +
            ", sourceRecordKey='" + getSourceRecordKey() + "'" +
            ", observedAt='" + getObservedAt() + "'" +
            ", periodStart='" + getPeriodStart() + "'" +
            ", periodEnd='" + getPeriodEnd() + "'" +
            ", ingestedAt='" + getIngestedAt() + "'" +
            ", sourceProductCode='" + getSourceProductCode() + "'" +
            ", spatialResolutionM=" + getSpatialResolutionM() +
            ", contentSha256='" + getContentSha256() + "'" +
            ", trustLevel='" + getTrustLevel() + "'" +
            ", qualityFlags='" + getQualityFlags() + "'" +
            ", algorithmVersion='" + getAlgorithmVersion() + "'" +
            ", organization=" + getOrganization() +
            ", installation=" + getInstallation() +
            ", evidenceSource=" + getEvidenceSource() +
            ", modelRun=" + getModelRun() +
            "}";
    }
}

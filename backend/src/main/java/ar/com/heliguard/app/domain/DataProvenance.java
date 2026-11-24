package ar.com.heliguard.app.domain;

import ar.com.heliguard.app.domain.enumeration.DataTrustLevel;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A DataProvenance.
 */
@Table("data_provenance")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class DataProvenance implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @NotNull
    @Size(max = 180)
    @Column("source_record_key")
    private String sourceRecordKey;

    @NotNull
    @Column("observed_at")
    private Instant observedAt;

    @Column("period_start")
    private Instant periodStart;

    @Column("period_end")
    private Instant periodEnd;

    @NotNull
    @Column("ingested_at")
    private Instant ingestedAt;

    @Size(max = 140)
    @Column("source_product_code")
    private String sourceProductCode;

    @Column("spatial_resolution_m")
    private BigDecimal spatialResolutionM;

    @Size(max = 64)
    @Column("content_sha_256")
    private String contentSha256;

    @NotNull
    @Column("trust_level")
    private DataTrustLevel trustLevel;

    @Size(max = 300)
    @Column("quality_flags")
    private String qualityFlags;

    @Size(max = 80)
    @Column("algorithm_version")
    private String algorithmVersion;

    @org.springframework.data.annotation.Transient
    private Organization organization;

    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "organization", "site", "climateRiskProfile" }, allowSetters = true)
    private Installation installation;

    @org.springframework.data.annotation.Transient
    private EvidenceSource evidenceSource;

    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "organization", "installation", "modelConfiguration", "weatherBatch" }, allowSetters = true)
    private EnergyModelRun modelRun;

    @Column("organization_id")
    private Long organizationId;

    @Column("installation_id")
    private Long installationId;

    @Column("evidence_source_id")
    private Long evidenceSourceId;

    @Column("model_run_id")
    private Long modelRunId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public DataProvenance id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSourceRecordKey() {
        return this.sourceRecordKey;
    }

    public DataProvenance sourceRecordKey(String sourceRecordKey) {
        this.setSourceRecordKey(sourceRecordKey);
        return this;
    }

    public void setSourceRecordKey(String sourceRecordKey) {
        this.sourceRecordKey = sourceRecordKey;
    }

    public Instant getObservedAt() {
        return this.observedAt;
    }

    public DataProvenance observedAt(Instant observedAt) {
        this.setObservedAt(observedAt);
        return this;
    }

    public void setObservedAt(Instant observedAt) {
        this.observedAt = observedAt;
    }

    public Instant getPeriodStart() {
        return this.periodStart;
    }

    public DataProvenance periodStart(Instant periodStart) {
        this.setPeriodStart(periodStart);
        return this;
    }

    public void setPeriodStart(Instant periodStart) {
        this.periodStart = periodStart;
    }

    public Instant getPeriodEnd() {
        return this.periodEnd;
    }

    public DataProvenance periodEnd(Instant periodEnd) {
        this.setPeriodEnd(periodEnd);
        return this;
    }

    public void setPeriodEnd(Instant periodEnd) {
        this.periodEnd = periodEnd;
    }

    public Instant getIngestedAt() {
        return this.ingestedAt;
    }

    public DataProvenance ingestedAt(Instant ingestedAt) {
        this.setIngestedAt(ingestedAt);
        return this;
    }

    public void setIngestedAt(Instant ingestedAt) {
        this.ingestedAt = ingestedAt;
    }

    public String getSourceProductCode() {
        return this.sourceProductCode;
    }

    public DataProvenance sourceProductCode(String sourceProductCode) {
        this.setSourceProductCode(sourceProductCode);
        return this;
    }

    public void setSourceProductCode(String sourceProductCode) {
        this.sourceProductCode = sourceProductCode;
    }

    public BigDecimal getSpatialResolutionM() {
        return this.spatialResolutionM;
    }

    public DataProvenance spatialResolutionM(BigDecimal spatialResolutionM) {
        this.setSpatialResolutionM(spatialResolutionM);
        return this;
    }

    public void setSpatialResolutionM(BigDecimal spatialResolutionM) {
        this.spatialResolutionM = spatialResolutionM;
    }

    public String getContentSha256() {
        return this.contentSha256;
    }

    public DataProvenance contentSha256(String contentSha256) {
        this.setContentSha256(contentSha256);
        return this;
    }

    public void setContentSha256(String contentSha256) {
        this.contentSha256 = contentSha256;
    }

    public DataTrustLevel getTrustLevel() {
        return this.trustLevel;
    }

    public DataProvenance trustLevel(DataTrustLevel trustLevel) {
        this.setTrustLevel(trustLevel);
        return this;
    }

    public void setTrustLevel(DataTrustLevel trustLevel) {
        this.trustLevel = trustLevel;
    }

    public String getQualityFlags() {
        return this.qualityFlags;
    }

    public DataProvenance qualityFlags(String qualityFlags) {
        this.setQualityFlags(qualityFlags);
        return this;
    }

    public void setQualityFlags(String qualityFlags) {
        this.qualityFlags = qualityFlags;
    }

    public String getAlgorithmVersion() {
        return this.algorithmVersion;
    }

    public DataProvenance algorithmVersion(String algorithmVersion) {
        this.setAlgorithmVersion(algorithmVersion);
        return this;
    }

    public void setAlgorithmVersion(String algorithmVersion) {
        this.algorithmVersion = algorithmVersion;
    }

    public Organization getOrganization() {
        return this.organization;
    }

    public void setOrganization(Organization organization) {
        this.organization = organization;
        this.organizationId = organization != null ? organization.getId() : null;
    }

    public DataProvenance organization(Organization organization) {
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

    public DataProvenance installation(Installation installation) {
        this.setInstallation(installation);
        return this;
    }

    public EvidenceSource getEvidenceSource() {
        return this.evidenceSource;
    }

    public void setEvidenceSource(EvidenceSource evidenceSource) {
        this.evidenceSource = evidenceSource;
        this.evidenceSourceId = evidenceSource != null ? evidenceSource.getId() : null;
    }

    public DataProvenance evidenceSource(EvidenceSource evidenceSource) {
        this.setEvidenceSource(evidenceSource);
        return this;
    }

    public EnergyModelRun getModelRun() {
        return this.modelRun;
    }

    public void setModelRun(EnergyModelRun energyModelRun) {
        this.modelRun = energyModelRun;
        this.modelRunId = energyModelRun != null ? energyModelRun.getId() : null;
    }

    public DataProvenance modelRun(EnergyModelRun energyModelRun) {
        this.setModelRun(energyModelRun);
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

    public Long getEvidenceSourceId() {
        return this.evidenceSourceId;
    }

    public void setEvidenceSourceId(Long evidenceSource) {
        this.evidenceSourceId = evidenceSource;
    }

    public Long getModelRunId() {
        return this.modelRunId;
    }

    public void setModelRunId(Long energyModelRun) {
        this.modelRunId = energyModelRun;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DataProvenance)) {
            return false;
        }
        return getId() != null && getId().equals(((DataProvenance) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "DataProvenance{" +
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
            "}";
    }
}

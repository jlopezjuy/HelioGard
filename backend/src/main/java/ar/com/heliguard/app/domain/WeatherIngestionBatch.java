package ar.com.heliguard.app.domain;

import ar.com.heliguard.app.domain.enumeration.WeatherBatchStatus;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A WeatherIngestionBatch.
 */
@Table("weather_ingestion_batch")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class WeatherIngestionBatch implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @NotNull
    @Size(max = 150)
    @Column("provider_batch_code")
    private String providerBatchCode;

    @NotNull
    @Size(max = 100)
    @Column("dataset_code")
    private String datasetCode;

    @NotNull
    @Column("period_start")
    private Instant periodStart;

    @NotNull
    @Column("period_end")
    private Instant periodEnd;

    @NotNull
    @Column("fetched_at")
    private Instant fetchedAt;

    @NotNull
    @Column("status")
    private WeatherBatchStatus status;

    @Size(max = 300)
    @Column("quality_note")
    private String qualityNote;

    @org.springframework.data.annotation.Transient
    private Organization organization;

    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "organization", "site", "climateRiskProfile" }, allowSetters = true)
    private Installation installation;

    @org.springframework.data.annotation.Transient
    private EvidenceSource evidenceSource;

    @Column("organization_id")
    private Long organizationId;

    @Column("installation_id")
    private Long installationId;

    @Column("evidence_source_id")
    private Long evidenceSourceId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public WeatherIngestionBatch id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getProviderBatchCode() {
        return this.providerBatchCode;
    }

    public WeatherIngestionBatch providerBatchCode(String providerBatchCode) {
        this.setProviderBatchCode(providerBatchCode);
        return this;
    }

    public void setProviderBatchCode(String providerBatchCode) {
        this.providerBatchCode = providerBatchCode;
    }

    public String getDatasetCode() {
        return this.datasetCode;
    }

    public WeatherIngestionBatch datasetCode(String datasetCode) {
        this.setDatasetCode(datasetCode);
        return this;
    }

    public void setDatasetCode(String datasetCode) {
        this.datasetCode = datasetCode;
    }

    public Instant getPeriodStart() {
        return this.periodStart;
    }

    public WeatherIngestionBatch periodStart(Instant periodStart) {
        this.setPeriodStart(periodStart);
        return this;
    }

    public void setPeriodStart(Instant periodStart) {
        this.periodStart = periodStart;
    }

    public Instant getPeriodEnd() {
        return this.periodEnd;
    }

    public WeatherIngestionBatch periodEnd(Instant periodEnd) {
        this.setPeriodEnd(periodEnd);
        return this;
    }

    public void setPeriodEnd(Instant periodEnd) {
        this.periodEnd = periodEnd;
    }

    public Instant getFetchedAt() {
        return this.fetchedAt;
    }

    public WeatherIngestionBatch fetchedAt(Instant fetchedAt) {
        this.setFetchedAt(fetchedAt);
        return this;
    }

    public void setFetchedAt(Instant fetchedAt) {
        this.fetchedAt = fetchedAt;
    }

    public WeatherBatchStatus getStatus() {
        return this.status;
    }

    public WeatherIngestionBatch status(WeatherBatchStatus status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(WeatherBatchStatus status) {
        this.status = status;
    }

    public String getQualityNote() {
        return this.qualityNote;
    }

    public WeatherIngestionBatch qualityNote(String qualityNote) {
        this.setQualityNote(qualityNote);
        return this;
    }

    public void setQualityNote(String qualityNote) {
        this.qualityNote = qualityNote;
    }

    public Organization getOrganization() {
        return this.organization;
    }

    public void setOrganization(Organization organization) {
        this.organization = organization;
        this.organizationId = organization != null ? organization.getId() : null;
    }

    public WeatherIngestionBatch organization(Organization organization) {
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

    public WeatherIngestionBatch installation(Installation installation) {
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

    public WeatherIngestionBatch evidenceSource(EvidenceSource evidenceSource) {
        this.setEvidenceSource(evidenceSource);
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

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof WeatherIngestionBatch)) {
            return false;
        }
        return getId() != null && getId().equals(((WeatherIngestionBatch) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "WeatherIngestionBatch{" +
            "id=" + getId() +
            ", providerBatchCode='" + getProviderBatchCode() + "'" +
            ", datasetCode='" + getDatasetCode() + "'" +
            ", periodStart='" + getPeriodStart() + "'" +
            ", periodEnd='" + getPeriodEnd() + "'" +
            ", fetchedAt='" + getFetchedAt() + "'" +
            ", status='" + getStatus() + "'" +
            ", qualityNote='" + getQualityNote() + "'" +
            "}";
    }
}

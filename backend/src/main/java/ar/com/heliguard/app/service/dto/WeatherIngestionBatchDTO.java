package ar.com.heliguard.app.service.dto;

import ar.com.heliguard.app.domain.enumeration.WeatherBatchStatus;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link ar.com.heliguard.app.domain.WeatherIngestionBatch} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class WeatherIngestionBatchDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(max = 150)
    private String providerBatchCode;

    @NotNull
    @Size(max = 100)
    private String datasetCode;

    @NotNull
    private Instant periodStart;

    @NotNull
    private Instant periodEnd;

    @NotNull
    private Instant fetchedAt;

    @NotNull
    private WeatherBatchStatus status;

    @Size(max = 300)
    private String qualityNote;

    private OrganizationDTO organization;

    @NotNull
    private InstallationDTO installation;

    @NotNull
    private EvidenceSourceDTO evidenceSource;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getProviderBatchCode() {
        return providerBatchCode;
    }

    public void setProviderBatchCode(String providerBatchCode) {
        this.providerBatchCode = providerBatchCode;
    }

    public String getDatasetCode() {
        return datasetCode;
    }

    public void setDatasetCode(String datasetCode) {
        this.datasetCode = datasetCode;
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

    public Instant getFetchedAt() {
        return fetchedAt;
    }

    public void setFetchedAt(Instant fetchedAt) {
        this.fetchedAt = fetchedAt;
    }

    public WeatherBatchStatus getStatus() {
        return status;
    }

    public void setStatus(WeatherBatchStatus status) {
        this.status = status;
    }

    public String getQualityNote() {
        return qualityNote;
    }

    public void setQualityNote(String qualityNote) {
        this.qualityNote = qualityNote;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof WeatherIngestionBatchDTO)) {
            return false;
        }

        WeatherIngestionBatchDTO weatherIngestionBatchDTO = (WeatherIngestionBatchDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, weatherIngestionBatchDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "WeatherIngestionBatchDTO{" +
            "id=" + getId() +
            ", providerBatchCode='" + getProviderBatchCode() + "'" +
            ", datasetCode='" + getDatasetCode() + "'" +
            ", periodStart='" + getPeriodStart() + "'" +
            ", periodEnd='" + getPeriodEnd() + "'" +
            ", fetchedAt='" + getFetchedAt() + "'" +
            ", status='" + getStatus() + "'" +
            ", qualityNote='" + getQualityNote() + "'" +
            ", organization=" + getOrganization() +
            ", installation=" + getInstallation() +
            ", evidenceSource=" + getEvidenceSource() +
            "}";
    }
}

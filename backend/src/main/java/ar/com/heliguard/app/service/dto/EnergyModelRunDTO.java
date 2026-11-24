package ar.com.heliguard.app.service.dto;

import ar.com.heliguard.app.domain.enumeration.EnergyModelRunStatus;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link ar.com.heliguard.app.domain.EnergyModelRun} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class EnergyModelRunDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(max = 100)
    private String modelName;

    @NotNull
    @Size(max = 50)
    private String modelVersion;

    @NotNull
    private Instant periodStart;

    @NotNull
    private Instant periodEnd;

    @NotNull
    private EnergyModelRunStatus status;

    private BigDecimal expectedWh;

    private BigDecimal expectedLowerWh;

    private BigDecimal expectedUpperWh;

    private BigDecimal actualWh;

    @DecimalMin(value = "0")
    @DecimalMax(value = "100")
    private BigDecimal coveragePct;

    @Size(max = 100)
    private String inputFingerprint;

    @Size(max = 300)
    private String qualityNote;

    @NotNull
    private Instant calculatedAt;

    private OrganizationDTO organization;

    @NotNull
    private InstallationDTO installation;

    private EnergyModelConfigurationDTO modelConfiguration;

    private WeatherIngestionBatchDTO weatherBatch;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public String getModelVersion() {
        return modelVersion;
    }

    public void setModelVersion(String modelVersion) {
        this.modelVersion = modelVersion;
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

    public EnergyModelRunStatus getStatus() {
        return status;
    }

    public void setStatus(EnergyModelRunStatus status) {
        this.status = status;
    }

    public BigDecimal getExpectedWh() {
        return expectedWh;
    }

    public void setExpectedWh(BigDecimal expectedWh) {
        this.expectedWh = expectedWh;
    }

    public BigDecimal getExpectedLowerWh() {
        return expectedLowerWh;
    }

    public void setExpectedLowerWh(BigDecimal expectedLowerWh) {
        this.expectedLowerWh = expectedLowerWh;
    }

    public BigDecimal getExpectedUpperWh() {
        return expectedUpperWh;
    }

    public void setExpectedUpperWh(BigDecimal expectedUpperWh) {
        this.expectedUpperWh = expectedUpperWh;
    }

    public BigDecimal getActualWh() {
        return actualWh;
    }

    public void setActualWh(BigDecimal actualWh) {
        this.actualWh = actualWh;
    }

    public BigDecimal getCoveragePct() {
        return coveragePct;
    }

    public void setCoveragePct(BigDecimal coveragePct) {
        this.coveragePct = coveragePct;
    }

    public String getInputFingerprint() {
        return inputFingerprint;
    }

    public void setInputFingerprint(String inputFingerprint) {
        this.inputFingerprint = inputFingerprint;
    }

    public String getQualityNote() {
        return qualityNote;
    }

    public void setQualityNote(String qualityNote) {
        this.qualityNote = qualityNote;
    }

    public Instant getCalculatedAt() {
        return calculatedAt;
    }

    public void setCalculatedAt(Instant calculatedAt) {
        this.calculatedAt = calculatedAt;
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

    public EnergyModelConfigurationDTO getModelConfiguration() {
        return modelConfiguration;
    }

    public void setModelConfiguration(EnergyModelConfigurationDTO modelConfiguration) {
        this.modelConfiguration = modelConfiguration;
    }

    public WeatherIngestionBatchDTO getWeatherBatch() {
        return weatherBatch;
    }

    public void setWeatherBatch(WeatherIngestionBatchDTO weatherBatch) {
        this.weatherBatch = weatherBatch;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof EnergyModelRunDTO)) {
            return false;
        }

        EnergyModelRunDTO energyModelRunDTO = (EnergyModelRunDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, energyModelRunDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "EnergyModelRunDTO{" +
            "id=" + getId() +
            ", modelName='" + getModelName() + "'" +
            ", modelVersion='" + getModelVersion() + "'" +
            ", periodStart='" + getPeriodStart() + "'" +
            ", periodEnd='" + getPeriodEnd() + "'" +
            ", status='" + getStatus() + "'" +
            ", expectedWh=" + getExpectedWh() +
            ", expectedLowerWh=" + getExpectedLowerWh() +
            ", expectedUpperWh=" + getExpectedUpperWh() +
            ", actualWh=" + getActualWh() +
            ", coveragePct=" + getCoveragePct() +
            ", inputFingerprint='" + getInputFingerprint() + "'" +
            ", qualityNote='" + getQualityNote() + "'" +
            ", calculatedAt='" + getCalculatedAt() + "'" +
            ", organization=" + getOrganization() +
            ", installation=" + getInstallation() +
            ", modelConfiguration=" + getModelConfiguration() +
            ", weatherBatch=" + getWeatherBatch() +
            "}";
    }
}

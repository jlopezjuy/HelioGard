package ar.com.heliguard.app.domain;

import ar.com.heliguard.app.domain.enumeration.EnergyModelRunStatus;
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
 * A EnergyModelRun.
 */
@Table("energy_model_run")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class EnergyModelRun implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @NotNull
    @Size(max = 100)
    @Column("model_name")
    private String modelName;

    @NotNull
    @Size(max = 50)
    @Column("model_version")
    private String modelVersion;

    @NotNull
    @Column("period_start")
    private Instant periodStart;

    @NotNull
    @Column("period_end")
    private Instant periodEnd;

    @NotNull
    @Column("status")
    private EnergyModelRunStatus status;

    @Column("expected_wh")
    private BigDecimal expectedWh;

    @Column("expected_lower_wh")
    private BigDecimal expectedLowerWh;

    @Column("expected_upper_wh")
    private BigDecimal expectedUpperWh;

    @Column("actual_wh")
    private BigDecimal actualWh;

    @DecimalMin(value = "0")
    @DecimalMax(value = "100")
    @Column("coverage_pct")
    private BigDecimal coveragePct;

    @Size(max = 100)
    @Column("input_fingerprint")
    private String inputFingerprint;

    @Size(max = 300)
    @Column("quality_note")
    private String qualityNote;

    @NotNull
    @Column("calculated_at")
    private Instant calculatedAt;

    @org.springframework.data.annotation.Transient
    private Organization organization;

    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "organization", "site", "climateRiskProfile" }, allowSetters = true)
    private Installation installation;

    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "organization", "installation" }, allowSetters = true)
    private EnergyModelConfiguration modelConfiguration;

    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "organization", "installation", "evidenceSource" }, allowSetters = true)
    private WeatherIngestionBatch weatherBatch;

    @Column("organization_id")
    private Long organizationId;

    @Column("installation_id")
    private Long installationId;

    @Column("model_configuration_id")
    private Long modelConfigurationId;

    @Column("weather_batch_id")
    private Long weatherBatchId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public EnergyModelRun id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getModelName() {
        return this.modelName;
    }

    public EnergyModelRun modelName(String modelName) {
        this.setModelName(modelName);
        return this;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public String getModelVersion() {
        return this.modelVersion;
    }

    public EnergyModelRun modelVersion(String modelVersion) {
        this.setModelVersion(modelVersion);
        return this;
    }

    public void setModelVersion(String modelVersion) {
        this.modelVersion = modelVersion;
    }

    public Instant getPeriodStart() {
        return this.periodStart;
    }

    public EnergyModelRun periodStart(Instant periodStart) {
        this.setPeriodStart(periodStart);
        return this;
    }

    public void setPeriodStart(Instant periodStart) {
        this.periodStart = periodStart;
    }

    public Instant getPeriodEnd() {
        return this.periodEnd;
    }

    public EnergyModelRun periodEnd(Instant periodEnd) {
        this.setPeriodEnd(periodEnd);
        return this;
    }

    public void setPeriodEnd(Instant periodEnd) {
        this.periodEnd = periodEnd;
    }

    public EnergyModelRunStatus getStatus() {
        return this.status;
    }

    public EnergyModelRun status(EnergyModelRunStatus status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(EnergyModelRunStatus status) {
        this.status = status;
    }

    public BigDecimal getExpectedWh() {
        return this.expectedWh;
    }

    public EnergyModelRun expectedWh(BigDecimal expectedWh) {
        this.setExpectedWh(expectedWh);
        return this;
    }

    public void setExpectedWh(BigDecimal expectedWh) {
        this.expectedWh = expectedWh;
    }

    public BigDecimal getExpectedLowerWh() {
        return this.expectedLowerWh;
    }

    public EnergyModelRun expectedLowerWh(BigDecimal expectedLowerWh) {
        this.setExpectedLowerWh(expectedLowerWh);
        return this;
    }

    public void setExpectedLowerWh(BigDecimal expectedLowerWh) {
        this.expectedLowerWh = expectedLowerWh;
    }

    public BigDecimal getExpectedUpperWh() {
        return this.expectedUpperWh;
    }

    public EnergyModelRun expectedUpperWh(BigDecimal expectedUpperWh) {
        this.setExpectedUpperWh(expectedUpperWh);
        return this;
    }

    public void setExpectedUpperWh(BigDecimal expectedUpperWh) {
        this.expectedUpperWh = expectedUpperWh;
    }

    public BigDecimal getActualWh() {
        return this.actualWh;
    }

    public EnergyModelRun actualWh(BigDecimal actualWh) {
        this.setActualWh(actualWh);
        return this;
    }

    public void setActualWh(BigDecimal actualWh) {
        this.actualWh = actualWh;
    }

    public BigDecimal getCoveragePct() {
        return this.coveragePct;
    }

    public EnergyModelRun coveragePct(BigDecimal coveragePct) {
        this.setCoveragePct(coveragePct);
        return this;
    }

    public void setCoveragePct(BigDecimal coveragePct) {
        this.coveragePct = coveragePct;
    }

    public String getInputFingerprint() {
        return this.inputFingerprint;
    }

    public EnergyModelRun inputFingerprint(String inputFingerprint) {
        this.setInputFingerprint(inputFingerprint);
        return this;
    }

    public void setInputFingerprint(String inputFingerprint) {
        this.inputFingerprint = inputFingerprint;
    }

    public String getQualityNote() {
        return this.qualityNote;
    }

    public EnergyModelRun qualityNote(String qualityNote) {
        this.setQualityNote(qualityNote);
        return this;
    }

    public void setQualityNote(String qualityNote) {
        this.qualityNote = qualityNote;
    }

    public Instant getCalculatedAt() {
        return this.calculatedAt;
    }

    public EnergyModelRun calculatedAt(Instant calculatedAt) {
        this.setCalculatedAt(calculatedAt);
        return this;
    }

    public void setCalculatedAt(Instant calculatedAt) {
        this.calculatedAt = calculatedAt;
    }

    public Organization getOrganization() {
        return this.organization;
    }

    public void setOrganization(Organization organization) {
        this.organization = organization;
        this.organizationId = organization != null ? organization.getId() : null;
    }

    public EnergyModelRun organization(Organization organization) {
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

    public EnergyModelRun installation(Installation installation) {
        this.setInstallation(installation);
        return this;
    }

    public EnergyModelConfiguration getModelConfiguration() {
        return this.modelConfiguration;
    }

    public void setModelConfiguration(EnergyModelConfiguration energyModelConfiguration) {
        this.modelConfiguration = energyModelConfiguration;
        this.modelConfigurationId = energyModelConfiguration != null ? energyModelConfiguration.getId() : null;
    }

    public EnergyModelRun modelConfiguration(EnergyModelConfiguration energyModelConfiguration) {
        this.setModelConfiguration(energyModelConfiguration);
        return this;
    }

    public WeatherIngestionBatch getWeatherBatch() {
        return this.weatherBatch;
    }

    public void setWeatherBatch(WeatherIngestionBatch weatherIngestionBatch) {
        this.weatherBatch = weatherIngestionBatch;
        this.weatherBatchId = weatherIngestionBatch != null ? weatherIngestionBatch.getId() : null;
    }

    public EnergyModelRun weatherBatch(WeatherIngestionBatch weatherIngestionBatch) {
        this.setWeatherBatch(weatherIngestionBatch);
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

    public Long getModelConfigurationId() {
        return this.modelConfigurationId;
    }

    public void setModelConfigurationId(Long energyModelConfiguration) {
        this.modelConfigurationId = energyModelConfiguration;
    }

    public Long getWeatherBatchId() {
        return this.weatherBatchId;
    }

    public void setWeatherBatchId(Long weatherIngestionBatch) {
        this.weatherBatchId = weatherIngestionBatch;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof EnergyModelRun)) {
            return false;
        }
        return getId() != null && getId().equals(((EnergyModelRun) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "EnergyModelRun{" +
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
            "}";
    }
}

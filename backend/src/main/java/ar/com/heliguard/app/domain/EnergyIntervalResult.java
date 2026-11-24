package ar.com.heliguard.app.domain;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * Interval-level physical energy calculation result linked to an {@link EnergyModelRun}.
 */
@Table("energy_interval_result")
public class EnergyIntervalResult implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @Column("organization_id")
    private Long organizationId;

    @Column("installation_id")
    private Long installationId;

    @Column("model_run_id")
    private Long modelRunId;

    @Column("interval_start")
    private Instant intervalStart;

    @Column("interval_end")
    private Instant intervalEnd;

    @Column("irradiance_wm2")
    private BigDecimal irradianceWm2;

    @Column("ambient_temp_c")
    private BigDecimal ambientTempC;

    @Column("expected_wh")
    private BigDecimal expectedWh;

    @Column("expected_lower_wh")
    private BigDecimal expectedLowerWh;

    @Column("expected_upper_wh")
    private BigDecimal expectedUpperWh;

    @Column("actual_wh")
    private BigDecimal actualWh;

    @Column("deficit_wh")
    private BigDecimal deficitWh;

    @Column("interval_status")
    private String intervalStatus;

    @Column("soiling_evidence_eligible")
    private Boolean soilingEvidenceEligible;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }

    public Long getInstallationId() {
        return installationId;
    }

    public void setInstallationId(Long installationId) {
        this.installationId = installationId;
    }

    public Long getModelRunId() {
        return modelRunId;
    }

    public void setModelRunId(Long modelRunId) {
        this.modelRunId = modelRunId;
    }

    public Instant getIntervalStart() {
        return intervalStart;
    }

    public void setIntervalStart(Instant intervalStart) {
        this.intervalStart = intervalStart;
    }

    public Instant getIntervalEnd() {
        return intervalEnd;
    }

    public void setIntervalEnd(Instant intervalEnd) {
        this.intervalEnd = intervalEnd;
    }

    public BigDecimal getIrradianceWm2() {
        return irradianceWm2;
    }

    public void setIrradianceWm2(BigDecimal irradianceWm2) {
        this.irradianceWm2 = irradianceWm2;
    }

    public BigDecimal getAmbientTempC() {
        return ambientTempC;
    }

    public void setAmbientTempC(BigDecimal ambientTempC) {
        this.ambientTempC = ambientTempC;
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

    public BigDecimal getDeficitWh() {
        return deficitWh;
    }

    public void setDeficitWh(BigDecimal deficitWh) {
        this.deficitWh = deficitWh;
    }

    public String getIntervalStatus() {
        return intervalStatus;
    }

    public void setIntervalStatus(String intervalStatus) {
        this.intervalStatus = intervalStatus;
    }

    public Boolean getSoilingEvidenceEligible() {
        return soilingEvidenceEligible;
    }

    public void setSoilingEvidenceEligible(Boolean soilingEvidenceEligible) {
        this.soilingEvidenceEligible = soilingEvidenceEligible;
    }
}

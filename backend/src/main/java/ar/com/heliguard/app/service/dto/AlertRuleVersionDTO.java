package ar.com.heliguard.app.service.dto;

import ar.com.heliguard.app.domain.enumeration.RuleComparison;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link ar.com.heliguard.app.domain.AlertRuleVersion} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class AlertRuleVersionDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(max = 50)
    private String versionCode;

    @NotNull
    private RuleComparison comparator;

    private BigDecimal thresholdValue;

    private BigDecimal thresholdMaxValue;

    @Size(max = 24)
    private String metricUnitCode;

    @Min(value = 1)
    private Integer windowSeconds;

    @Min(value = 0)
    private Integer cooldownSeconds;

    @DecimalMin(value = "0")
    @DecimalMax(value = "100")
    private BigDecimal minCoveragePct;

    @NotNull
    private Instant validFrom;

    private Instant validUntil;

    @NotNull
    private OrganizationDTO organization;

    @NotNull
    private AlertRuleDTO alertRule;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(String versionCode) {
        this.versionCode = versionCode;
    }

    public RuleComparison getComparator() {
        return comparator;
    }

    public void setComparator(RuleComparison comparator) {
        this.comparator = comparator;
    }

    public BigDecimal getThresholdValue() {
        return thresholdValue;
    }

    public void setThresholdValue(BigDecimal thresholdValue) {
        this.thresholdValue = thresholdValue;
    }

    public BigDecimal getThresholdMaxValue() {
        return thresholdMaxValue;
    }

    public void setThresholdMaxValue(BigDecimal thresholdMaxValue) {
        this.thresholdMaxValue = thresholdMaxValue;
    }

    public String getMetricUnitCode() {
        return metricUnitCode;
    }

    public void setMetricUnitCode(String metricUnitCode) {
        this.metricUnitCode = metricUnitCode;
    }

    public Integer getWindowSeconds() {
        return windowSeconds;
    }

    public void setWindowSeconds(Integer windowSeconds) {
        this.windowSeconds = windowSeconds;
    }

    public Integer getCooldownSeconds() {
        return cooldownSeconds;
    }

    public void setCooldownSeconds(Integer cooldownSeconds) {
        this.cooldownSeconds = cooldownSeconds;
    }

    public BigDecimal getMinCoveragePct() {
        return minCoveragePct;
    }

    public void setMinCoveragePct(BigDecimal minCoveragePct) {
        this.minCoveragePct = minCoveragePct;
    }

    public Instant getValidFrom() {
        return validFrom;
    }

    public void setValidFrom(Instant validFrom) {
        this.validFrom = validFrom;
    }

    public Instant getValidUntil() {
        return validUntil;
    }

    public void setValidUntil(Instant validUntil) {
        this.validUntil = validUntil;
    }

    public OrganizationDTO getOrganization() {
        return organization;
    }

    public void setOrganization(OrganizationDTO organization) {
        this.organization = organization;
    }

    public AlertRuleDTO getAlertRule() {
        return alertRule;
    }

    public void setAlertRule(AlertRuleDTO alertRule) {
        this.alertRule = alertRule;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AlertRuleVersionDTO)) {
            return false;
        }

        AlertRuleVersionDTO alertRuleVersionDTO = (AlertRuleVersionDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, alertRuleVersionDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "AlertRuleVersionDTO{" +
            "id=" + getId() +
            ", versionCode='" + getVersionCode() + "'" +
            ", comparator='" + getComparator() + "'" +
            ", thresholdValue=" + getThresholdValue() +
            ", thresholdMaxValue=" + getThresholdMaxValue() +
            ", metricUnitCode='" + getMetricUnitCode() + "'" +
            ", windowSeconds=" + getWindowSeconds() +
            ", cooldownSeconds=" + getCooldownSeconds() +
            ", minCoveragePct=" + getMinCoveragePct() +
            ", validFrom='" + getValidFrom() + "'" +
            ", validUntil='" + getValidUntil() + "'" +
            ", organization=" + getOrganization() +
            ", alertRule=" + getAlertRule() +
            "}";
    }
}

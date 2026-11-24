package ar.com.heliguard.app.domain;

import ar.com.heliguard.app.domain.enumeration.RuleComparison;
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
 * A AlertRuleVersion.
 */
@Table("alert_rule_version")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class AlertRuleVersion implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @NotNull
    @Size(max = 50)
    @Column("version_code")
    private String versionCode;

    @NotNull
    @Column("comparator")
    private RuleComparison comparator;

    @Column("threshold_value")
    private BigDecimal thresholdValue;

    @Column("threshold_max_value")
    private BigDecimal thresholdMaxValue;

    @Size(max = 24)
    @Column("metric_unit_code")
    private String metricUnitCode;

    @Min(value = 1)
    @Column("window_seconds")
    private Integer windowSeconds;

    @Min(value = 0)
    @Column("cooldown_seconds")
    private Integer cooldownSeconds;

    @DecimalMin(value = "0")
    @DecimalMax(value = "100")
    @Column("min_coverage_pct")
    private BigDecimal minCoveragePct;

    @NotNull
    @Column("valid_from")
    private Instant validFrom;

    @Column("valid_until")
    private Instant validUntil;

    @org.springframework.data.annotation.Transient
    private Organization organization;

    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "organization", "installation" }, allowSetters = true)
    private AlertRule alertRule;

    @Column("organization_id")
    private Long organizationId;

    @Column("alert_rule_id")
    private Long alertRuleId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public AlertRuleVersion id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getVersionCode() {
        return this.versionCode;
    }

    public AlertRuleVersion versionCode(String versionCode) {
        this.setVersionCode(versionCode);
        return this;
    }

    public void setVersionCode(String versionCode) {
        this.versionCode = versionCode;
    }

    public RuleComparison getComparator() {
        return this.comparator;
    }

    public AlertRuleVersion comparator(RuleComparison comparator) {
        this.setComparator(comparator);
        return this;
    }

    public void setComparator(RuleComparison comparator) {
        this.comparator = comparator;
    }

    public BigDecimal getThresholdValue() {
        return this.thresholdValue;
    }

    public AlertRuleVersion thresholdValue(BigDecimal thresholdValue) {
        this.setThresholdValue(thresholdValue);
        return this;
    }

    public void setThresholdValue(BigDecimal thresholdValue) {
        this.thresholdValue = thresholdValue;
    }

    public BigDecimal getThresholdMaxValue() {
        return this.thresholdMaxValue;
    }

    public AlertRuleVersion thresholdMaxValue(BigDecimal thresholdMaxValue) {
        this.setThresholdMaxValue(thresholdMaxValue);
        return this;
    }

    public void setThresholdMaxValue(BigDecimal thresholdMaxValue) {
        this.thresholdMaxValue = thresholdMaxValue;
    }

    public String getMetricUnitCode() {
        return this.metricUnitCode;
    }

    public AlertRuleVersion metricUnitCode(String metricUnitCode) {
        this.setMetricUnitCode(metricUnitCode);
        return this;
    }

    public void setMetricUnitCode(String metricUnitCode) {
        this.metricUnitCode = metricUnitCode;
    }

    public Integer getWindowSeconds() {
        return this.windowSeconds;
    }

    public AlertRuleVersion windowSeconds(Integer windowSeconds) {
        this.setWindowSeconds(windowSeconds);
        return this;
    }

    public void setWindowSeconds(Integer windowSeconds) {
        this.windowSeconds = windowSeconds;
    }

    public Integer getCooldownSeconds() {
        return this.cooldownSeconds;
    }

    public AlertRuleVersion cooldownSeconds(Integer cooldownSeconds) {
        this.setCooldownSeconds(cooldownSeconds);
        return this;
    }

    public void setCooldownSeconds(Integer cooldownSeconds) {
        this.cooldownSeconds = cooldownSeconds;
    }

    public BigDecimal getMinCoveragePct() {
        return this.minCoveragePct;
    }

    public AlertRuleVersion minCoveragePct(BigDecimal minCoveragePct) {
        this.setMinCoveragePct(minCoveragePct);
        return this;
    }

    public void setMinCoveragePct(BigDecimal minCoveragePct) {
        this.minCoveragePct = minCoveragePct;
    }

    public Instant getValidFrom() {
        return this.validFrom;
    }

    public AlertRuleVersion validFrom(Instant validFrom) {
        this.setValidFrom(validFrom);
        return this;
    }

    public void setValidFrom(Instant validFrom) {
        this.validFrom = validFrom;
    }

    public Instant getValidUntil() {
        return this.validUntil;
    }

    public AlertRuleVersion validUntil(Instant validUntil) {
        this.setValidUntil(validUntil);
        return this;
    }

    public void setValidUntil(Instant validUntil) {
        this.validUntil = validUntil;
    }

    public Organization getOrganization() {
        return this.organization;
    }

    public void setOrganization(Organization organization) {
        this.organization = organization;
        this.organizationId = organization != null ? organization.getId() : null;
    }

    public AlertRuleVersion organization(Organization organization) {
        this.setOrganization(organization);
        return this;
    }

    public AlertRule getAlertRule() {
        return this.alertRule;
    }

    public void setAlertRule(AlertRule alertRule) {
        this.alertRule = alertRule;
        this.alertRuleId = alertRule != null ? alertRule.getId() : null;
    }

    public AlertRuleVersion alertRule(AlertRule alertRule) {
        this.setAlertRule(alertRule);
        return this;
    }

    public Long getOrganizationId() {
        return this.organizationId;
    }

    public void setOrganizationId(Long organization) {
        this.organizationId = organization;
    }

    public Long getAlertRuleId() {
        return this.alertRuleId;
    }

    public void setAlertRuleId(Long alertRule) {
        this.alertRuleId = alertRule;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AlertRuleVersion)) {
            return false;
        }
        return getId() != null && getId().equals(((AlertRuleVersion) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "AlertRuleVersion{" +
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
            "}";
    }
}

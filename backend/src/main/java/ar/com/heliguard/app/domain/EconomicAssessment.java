package ar.com.heliguard.app.domain;

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
 * A EconomicAssessment.
 */
@Table("economic_assessment")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class EconomicAssessment implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @NotNull
    @Column("assessed_at")
    private Instant assessedAt;

    @NotNull
    @Size(max = 3)
    @Column("currency_code")
    private String currencyCode;

    @Column("self_consumption_lost_wh")
    private BigDecimal selfConsumptionLostWh;

    @Column("exported_lost_wh")
    private BigDecimal exportedLostWh;

    @NotNull
    @Column("estimated_loss_amount")
    private BigDecimal estimatedLossAmount;

    @Size(max = 300)
    @Column("tariff_snapshot_note")
    private String tariffSnapshotNote;

    @org.springframework.data.annotation.Transient
    private Organization organization;

    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "organization", "modelRun" }, allowSetters = true)
    private LossAssessment lossAssessment;

    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "organization", "tariffPlan" }, allowSetters = true)
    private TariffRate tariffRate;

    @Column("organization_id")
    private Long organizationId;

    @Column("loss_assessment_id")
    private Long lossAssessmentId;

    @Column("tariff_rate_id")
    private Long tariffRateId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public EconomicAssessment id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instant getAssessedAt() {
        return this.assessedAt;
    }

    public EconomicAssessment assessedAt(Instant assessedAt) {
        this.setAssessedAt(assessedAt);
        return this;
    }

    public void setAssessedAt(Instant assessedAt) {
        this.assessedAt = assessedAt;
    }

    public String getCurrencyCode() {
        return this.currencyCode;
    }

    public EconomicAssessment currencyCode(String currencyCode) {
        this.setCurrencyCode(currencyCode);
        return this;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public BigDecimal getSelfConsumptionLostWh() {
        return this.selfConsumptionLostWh;
    }

    public EconomicAssessment selfConsumptionLostWh(BigDecimal selfConsumptionLostWh) {
        this.setSelfConsumptionLostWh(selfConsumptionLostWh);
        return this;
    }

    public void setSelfConsumptionLostWh(BigDecimal selfConsumptionLostWh) {
        this.selfConsumptionLostWh = selfConsumptionLostWh;
    }

    public BigDecimal getExportedLostWh() {
        return this.exportedLostWh;
    }

    public EconomicAssessment exportedLostWh(BigDecimal exportedLostWh) {
        this.setExportedLostWh(exportedLostWh);
        return this;
    }

    public void setExportedLostWh(BigDecimal exportedLostWh) {
        this.exportedLostWh = exportedLostWh;
    }

    public BigDecimal getEstimatedLossAmount() {
        return this.estimatedLossAmount;
    }

    public EconomicAssessment estimatedLossAmount(BigDecimal estimatedLossAmount) {
        this.setEstimatedLossAmount(estimatedLossAmount);
        return this;
    }

    public void setEstimatedLossAmount(BigDecimal estimatedLossAmount) {
        this.estimatedLossAmount = estimatedLossAmount;
    }

    public String getTariffSnapshotNote() {
        return this.tariffSnapshotNote;
    }

    public EconomicAssessment tariffSnapshotNote(String tariffSnapshotNote) {
        this.setTariffSnapshotNote(tariffSnapshotNote);
        return this;
    }

    public void setTariffSnapshotNote(String tariffSnapshotNote) {
        this.tariffSnapshotNote = tariffSnapshotNote;
    }

    public Organization getOrganization() {
        return this.organization;
    }

    public void setOrganization(Organization organization) {
        this.organization = organization;
        this.organizationId = organization != null ? organization.getId() : null;
    }

    public EconomicAssessment organization(Organization organization) {
        this.setOrganization(organization);
        return this;
    }

    public LossAssessment getLossAssessment() {
        return this.lossAssessment;
    }

    public void setLossAssessment(LossAssessment lossAssessment) {
        this.lossAssessment = lossAssessment;
        this.lossAssessmentId = lossAssessment != null ? lossAssessment.getId() : null;
    }

    public EconomicAssessment lossAssessment(LossAssessment lossAssessment) {
        this.setLossAssessment(lossAssessment);
        return this;
    }

    public TariffRate getTariffRate() {
        return this.tariffRate;
    }

    public void setTariffRate(TariffRate tariffRate) {
        this.tariffRate = tariffRate;
        this.tariffRateId = tariffRate != null ? tariffRate.getId() : null;
    }

    public EconomicAssessment tariffRate(TariffRate tariffRate) {
        this.setTariffRate(tariffRate);
        return this;
    }

    public Long getOrganizationId() {
        return this.organizationId;
    }

    public void setOrganizationId(Long organization) {
        this.organizationId = organization;
    }

    public Long getLossAssessmentId() {
        return this.lossAssessmentId;
    }

    public void setLossAssessmentId(Long lossAssessment) {
        this.lossAssessmentId = lossAssessment;
    }

    public Long getTariffRateId() {
        return this.tariffRateId;
    }

    public void setTariffRateId(Long tariffRate) {
        this.tariffRateId = tariffRate;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof EconomicAssessment)) {
            return false;
        }
        return getId() != null && getId().equals(((EconomicAssessment) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "EconomicAssessment{" +
            "id=" + getId() +
            ", assessedAt='" + getAssessedAt() + "'" +
            ", currencyCode='" + getCurrencyCode() + "'" +
            ", selfConsumptionLostWh=" + getSelfConsumptionLostWh() +
            ", exportedLostWh=" + getExportedLostWh() +
            ", estimatedLossAmount=" + getEstimatedLossAmount() +
            ", tariffSnapshotNote='" + getTariffSnapshotNote() + "'" +
            "}";
    }
}

package ar.com.heliguard.app.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link ar.com.heliguard.app.domain.EconomicAssessment} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class EconomicAssessmentDTO implements Serializable {

    private Long id;

    @NotNull
    private Instant assessedAt;

    @NotNull
    @Size(max = 3)
    private String currencyCode;

    private BigDecimal selfConsumptionLostWh;

    private BigDecimal exportedLostWh;

    @NotNull
    private BigDecimal estimatedLossAmount;

    @Size(max = 300)
    private String tariffSnapshotNote;

    @NotNull
    private OrganizationDTO organization;

    @NotNull
    private LossAssessmentDTO lossAssessment;

    private TariffRateDTO tariffRate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instant getAssessedAt() {
        return assessedAt;
    }

    public void setAssessedAt(Instant assessedAt) {
        this.assessedAt = assessedAt;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public BigDecimal getSelfConsumptionLostWh() {
        return selfConsumptionLostWh;
    }

    public void setSelfConsumptionLostWh(BigDecimal selfConsumptionLostWh) {
        this.selfConsumptionLostWh = selfConsumptionLostWh;
    }

    public BigDecimal getExportedLostWh() {
        return exportedLostWh;
    }

    public void setExportedLostWh(BigDecimal exportedLostWh) {
        this.exportedLostWh = exportedLostWh;
    }

    public BigDecimal getEstimatedLossAmount() {
        return estimatedLossAmount;
    }

    public void setEstimatedLossAmount(BigDecimal estimatedLossAmount) {
        this.estimatedLossAmount = estimatedLossAmount;
    }

    public String getTariffSnapshotNote() {
        return tariffSnapshotNote;
    }

    public void setTariffSnapshotNote(String tariffSnapshotNote) {
        this.tariffSnapshotNote = tariffSnapshotNote;
    }

    public OrganizationDTO getOrganization() {
        return organization;
    }

    public void setOrganization(OrganizationDTO organization) {
        this.organization = organization;
    }

    public LossAssessmentDTO getLossAssessment() {
        return lossAssessment;
    }

    public void setLossAssessment(LossAssessmentDTO lossAssessment) {
        this.lossAssessment = lossAssessment;
    }

    public TariffRateDTO getTariffRate() {
        return tariffRate;
    }

    public void setTariffRate(TariffRateDTO tariffRate) {
        this.tariffRate = tariffRate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof EconomicAssessmentDTO)) {
            return false;
        }

        EconomicAssessmentDTO economicAssessmentDTO = (EconomicAssessmentDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, economicAssessmentDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "EconomicAssessmentDTO{" +
            "id=" + getId() +
            ", assessedAt='" + getAssessedAt() + "'" +
            ", currencyCode='" + getCurrencyCode() + "'" +
            ", selfConsumptionLostWh=" + getSelfConsumptionLostWh() +
            ", exportedLostWh=" + getExportedLostWh() +
            ", estimatedLossAmount=" + getEstimatedLossAmount() +
            ", tariffSnapshotNote='" + getTariffSnapshotNote() + "'" +
            ", organization=" + getOrganization() +
            ", lossAssessment=" + getLossAssessment() +
            ", tariffRate=" + getTariffRate() +
            "}";
    }
}

package ar.com.heliguard.app.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link ar.com.heliguard.app.domain.TariffRate} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class TariffRateDTO implements Serializable {

    private Long id;

    @NotNull
    private Instant validFrom;

    private Instant validTo;

    @NotNull
    private BigDecimal purchasePricePerKwh;

    private BigDecimal exportPricePerKwh;

    private OrganizationDTO organization;

    @NotNull
    private TariffPlanDTO tariffPlan;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instant getValidFrom() {
        return validFrom;
    }

    public void setValidFrom(Instant validFrom) {
        this.validFrom = validFrom;
    }

    public Instant getValidTo() {
        return validTo;
    }

    public void setValidTo(Instant validTo) {
        this.validTo = validTo;
    }

    public BigDecimal getPurchasePricePerKwh() {
        return purchasePricePerKwh;
    }

    public void setPurchasePricePerKwh(BigDecimal purchasePricePerKwh) {
        this.purchasePricePerKwh = purchasePricePerKwh;
    }

    public BigDecimal getExportPricePerKwh() {
        return exportPricePerKwh;
    }

    public void setExportPricePerKwh(BigDecimal exportPricePerKwh) {
        this.exportPricePerKwh = exportPricePerKwh;
    }

    public OrganizationDTO getOrganization() {
        return organization;
    }

    public void setOrganization(OrganizationDTO organization) {
        this.organization = organization;
    }

    public TariffPlanDTO getTariffPlan() {
        return tariffPlan;
    }

    public void setTariffPlan(TariffPlanDTO tariffPlan) {
        this.tariffPlan = tariffPlan;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TariffRateDTO)) {
            return false;
        }

        TariffRateDTO tariffRateDTO = (TariffRateDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, tariffRateDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TariffRateDTO{" +
            "id=" + getId() +
            ", validFrom='" + getValidFrom() + "'" +
            ", validTo='" + getValidTo() + "'" +
            ", purchasePricePerKwh=" + getPurchasePricePerKwh() +
            ", exportPricePerKwh=" + getExportPricePerKwh() +
            ", organization=" + getOrganization() +
            ", tariffPlan=" + getTariffPlan() +
            "}";
    }
}

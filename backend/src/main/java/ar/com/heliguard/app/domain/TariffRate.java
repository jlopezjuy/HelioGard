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
 * A TariffRate.
 */
@Table("tariff_rate")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class TariffRate implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @NotNull
    @Column("valid_from")
    private Instant validFrom;

    @Column("valid_to")
    private Instant validTo;

    @NotNull
    @Column("purchase_price_per_kwh")
    private BigDecimal purchasePricePerKwh;

    @Column("export_price_per_kwh")
    private BigDecimal exportPricePerKwh;

    @org.springframework.data.annotation.Transient
    private Organization organization;

    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "organization", "installation" }, allowSetters = true)
    private TariffPlan tariffPlan;

    @Column("organization_id")
    private Long organizationId;

    @Column("tariff_plan_id")
    private Long tariffPlanId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public TariffRate id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instant getValidFrom() {
        return this.validFrom;
    }

    public TariffRate validFrom(Instant validFrom) {
        this.setValidFrom(validFrom);
        return this;
    }

    public void setValidFrom(Instant validFrom) {
        this.validFrom = validFrom;
    }

    public Instant getValidTo() {
        return this.validTo;
    }

    public TariffRate validTo(Instant validTo) {
        this.setValidTo(validTo);
        return this;
    }

    public void setValidTo(Instant validTo) {
        this.validTo = validTo;
    }

    public BigDecimal getPurchasePricePerKwh() {
        return this.purchasePricePerKwh;
    }

    public TariffRate purchasePricePerKwh(BigDecimal purchasePricePerKwh) {
        this.setPurchasePricePerKwh(purchasePricePerKwh);
        return this;
    }

    public void setPurchasePricePerKwh(BigDecimal purchasePricePerKwh) {
        this.purchasePricePerKwh = purchasePricePerKwh;
    }

    public BigDecimal getExportPricePerKwh() {
        return this.exportPricePerKwh;
    }

    public TariffRate exportPricePerKwh(BigDecimal exportPricePerKwh) {
        this.setExportPricePerKwh(exportPricePerKwh);
        return this;
    }

    public void setExportPricePerKwh(BigDecimal exportPricePerKwh) {
        this.exportPricePerKwh = exportPricePerKwh;
    }

    public Organization getOrganization() {
        return this.organization;
    }

    public void setOrganization(Organization organization) {
        this.organization = organization;
        this.organizationId = organization != null ? organization.getId() : null;
    }

    public TariffRate organization(Organization organization) {
        this.setOrganization(organization);
        return this;
    }

    public TariffPlan getTariffPlan() {
        return this.tariffPlan;
    }

    public void setTariffPlan(TariffPlan tariffPlan) {
        this.tariffPlan = tariffPlan;
        this.tariffPlanId = tariffPlan != null ? tariffPlan.getId() : null;
    }

    public TariffRate tariffPlan(TariffPlan tariffPlan) {
        this.setTariffPlan(tariffPlan);
        return this;
    }

    public Long getOrganizationId() {
        return this.organizationId;
    }

    public void setOrganizationId(Long organization) {
        this.organizationId = organization;
    }

    public Long getTariffPlanId() {
        return this.tariffPlanId;
    }

    public void setTariffPlanId(Long tariffPlan) {
        this.tariffPlanId = tariffPlan;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TariffRate)) {
            return false;
        }
        return getId() != null && getId().equals(((TariffRate) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TariffRate{" +
            "id=" + getId() +
            ", validFrom='" + getValidFrom() + "'" +
            ", validTo='" + getValidTo() + "'" +
            ", purchasePricePerKwh=" + getPurchasePricePerKwh() +
            ", exportPricePerKwh=" + getExportPricePerKwh() +
            "}";
    }
}

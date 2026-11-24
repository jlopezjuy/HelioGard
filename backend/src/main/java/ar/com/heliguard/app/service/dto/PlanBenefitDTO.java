package ar.com.heliguard.app.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

/**
 * A DTO for the {@link ar.com.heliguard.app.domain.PlanBenefit} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class PlanBenefitDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(max = 90)
    private String benefitCode;

    @NotNull
    @Size(max = 240)
    private String description;

    private BigDecimal includedQuantity;

    @Size(max = 35)
    private String unitCode;

    @Min(value = 1)
    private Integer intervalMonths;

    @Size(max = 350)
    private String termsReference;

    private OrganizationDTO organization;

    @NotNull
    private MaintenancePlanVersionDTO planVersion;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBenefitCode() {
        return benefitCode;
    }

    public void setBenefitCode(String benefitCode) {
        this.benefitCode = benefitCode;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getIncludedQuantity() {
        return includedQuantity;
    }

    public void setIncludedQuantity(BigDecimal includedQuantity) {
        this.includedQuantity = includedQuantity;
    }

    public String getUnitCode() {
        return unitCode;
    }

    public void setUnitCode(String unitCode) {
        this.unitCode = unitCode;
    }

    public Integer getIntervalMonths() {
        return intervalMonths;
    }

    public void setIntervalMonths(Integer intervalMonths) {
        this.intervalMonths = intervalMonths;
    }

    public String getTermsReference() {
        return termsReference;
    }

    public void setTermsReference(String termsReference) {
        this.termsReference = termsReference;
    }

    public OrganizationDTO getOrganization() {
        return organization;
    }

    public void setOrganization(OrganizationDTO organization) {
        this.organization = organization;
    }

    public MaintenancePlanVersionDTO getPlanVersion() {
        return planVersion;
    }

    public void setPlanVersion(MaintenancePlanVersionDTO planVersion) {
        this.planVersion = planVersion;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PlanBenefitDTO)) {
            return false;
        }

        PlanBenefitDTO planBenefitDTO = (PlanBenefitDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, planBenefitDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "PlanBenefitDTO{" +
            "id=" + getId() +
            ", benefitCode='" + getBenefitCode() + "'" +
            ", description='" + getDescription() + "'" +
            ", includedQuantity=" + getIncludedQuantity() +
            ", unitCode='" + getUnitCode() + "'" +
            ", intervalMonths=" + getIntervalMonths() +
            ", termsReference='" + getTermsReference() + "'" +
            ", organization=" + getOrganization() +
            ", planVersion=" + getPlanVersion() +
            "}";
    }
}

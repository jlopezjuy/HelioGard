package ar.com.heliguard.app.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A PlanBenefit.
 */
@Table("plan_benefit")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class PlanBenefit implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @NotNull
    @Size(max = 90)
    @Column("benefit_code")
    private String benefitCode;

    @NotNull
    @Size(max = 240)
    @Column("description")
    private String description;

    @Column("included_quantity")
    private BigDecimal includedQuantity;

    @Size(max = 35)
    @Column("unit_code")
    private String unitCode;

    @Min(value = 1)
    @Column("interval_months")
    private Integer intervalMonths;

    @Size(max = 350)
    @Column("terms_reference")
    private String termsReference;

    @org.springframework.data.annotation.Transient
    private Organization organization;

    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "organization", "maintenancePlan" }, allowSetters = true)
    private MaintenancePlanVersion planVersion;

    @Column("organization_id")
    private Long organizationId;

    @Column("plan_version_id")
    private Long planVersionId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public PlanBenefit id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBenefitCode() {
        return this.benefitCode;
    }

    public PlanBenefit benefitCode(String benefitCode) {
        this.setBenefitCode(benefitCode);
        return this;
    }

    public void setBenefitCode(String benefitCode) {
        this.benefitCode = benefitCode;
    }

    public String getDescription() {
        return this.description;
    }

    public PlanBenefit description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getIncludedQuantity() {
        return this.includedQuantity;
    }

    public PlanBenefit includedQuantity(BigDecimal includedQuantity) {
        this.setIncludedQuantity(includedQuantity);
        return this;
    }

    public void setIncludedQuantity(BigDecimal includedQuantity) {
        this.includedQuantity = includedQuantity;
    }

    public String getUnitCode() {
        return this.unitCode;
    }

    public PlanBenefit unitCode(String unitCode) {
        this.setUnitCode(unitCode);
        return this;
    }

    public void setUnitCode(String unitCode) {
        this.unitCode = unitCode;
    }

    public Integer getIntervalMonths() {
        return this.intervalMonths;
    }

    public PlanBenefit intervalMonths(Integer intervalMonths) {
        this.setIntervalMonths(intervalMonths);
        return this;
    }

    public void setIntervalMonths(Integer intervalMonths) {
        this.intervalMonths = intervalMonths;
    }

    public String getTermsReference() {
        return this.termsReference;
    }

    public PlanBenefit termsReference(String termsReference) {
        this.setTermsReference(termsReference);
        return this;
    }

    public void setTermsReference(String termsReference) {
        this.termsReference = termsReference;
    }

    public Organization getOrganization() {
        return this.organization;
    }

    public void setOrganization(Organization organization) {
        this.organization = organization;
        this.organizationId = organization != null ? organization.getId() : null;
    }

    public PlanBenefit organization(Organization organization) {
        this.setOrganization(organization);
        return this;
    }

    public MaintenancePlanVersion getPlanVersion() {
        return this.planVersion;
    }

    public void setPlanVersion(MaintenancePlanVersion maintenancePlanVersion) {
        this.planVersion = maintenancePlanVersion;
        this.planVersionId = maintenancePlanVersion != null ? maintenancePlanVersion.getId() : null;
    }

    public PlanBenefit planVersion(MaintenancePlanVersion maintenancePlanVersion) {
        this.setPlanVersion(maintenancePlanVersion);
        return this;
    }

    public Long getOrganizationId() {
        return this.organizationId;
    }

    public void setOrganizationId(Long organization) {
        this.organizationId = organization;
    }

    public Long getPlanVersionId() {
        return this.planVersionId;
    }

    public void setPlanVersionId(Long maintenancePlanVersion) {
        this.planVersionId = maintenancePlanVersion;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PlanBenefit)) {
            return false;
        }
        return getId() != null && getId().equals(((PlanBenefit) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "PlanBenefit{" +
            "id=" + getId() +
            ", benefitCode='" + getBenefitCode() + "'" +
            ", description='" + getDescription() + "'" +
            ", includedQuantity=" + getIncludedQuantity() +
            ", unitCode='" + getUnitCode() + "'" +
            ", intervalMonths=" + getIntervalMonths() +
            ", termsReference='" + getTermsReference() + "'" +
            "}";
    }
}

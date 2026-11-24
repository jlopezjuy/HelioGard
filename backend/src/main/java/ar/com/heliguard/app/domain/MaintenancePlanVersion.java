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
 * A MaintenancePlanVersion.
 */
@Table("maintenance_plan_version")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class MaintenancePlanVersion implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @NotNull
    @Size(max = 50)
    @Column("version_code")
    private String versionCode;

    @Column("published_at")
    private Instant publishedAt;

    @NotNull
    @Size(max = 3)
    @Column("currency_code")
    private String currencyCode;

    @NotNull
    @Column("recurring_amount")
    private BigDecimal recurringAmount;

    @NotNull
    @Size(max = 400)
    @Column("terms_reference")
    private String termsReference;

    @org.springframework.data.annotation.Transient
    private Organization organization;

    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "organization" }, allowSetters = true)
    private MaintenancePlan maintenancePlan;

    @Column("organization_id")
    private Long organizationId;

    @Column("maintenance_plan_id")
    private Long maintenancePlanId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public MaintenancePlanVersion id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getVersionCode() {
        return this.versionCode;
    }

    public MaintenancePlanVersion versionCode(String versionCode) {
        this.setVersionCode(versionCode);
        return this;
    }

    public void setVersionCode(String versionCode) {
        this.versionCode = versionCode;
    }

    public Instant getPublishedAt() {
        return this.publishedAt;
    }

    public MaintenancePlanVersion publishedAt(Instant publishedAt) {
        this.setPublishedAt(publishedAt);
        return this;
    }

    public void setPublishedAt(Instant publishedAt) {
        this.publishedAt = publishedAt;
    }

    public String getCurrencyCode() {
        return this.currencyCode;
    }

    public MaintenancePlanVersion currencyCode(String currencyCode) {
        this.setCurrencyCode(currencyCode);
        return this;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public BigDecimal getRecurringAmount() {
        return this.recurringAmount;
    }

    public MaintenancePlanVersion recurringAmount(BigDecimal recurringAmount) {
        this.setRecurringAmount(recurringAmount);
        return this;
    }

    public void setRecurringAmount(BigDecimal recurringAmount) {
        this.recurringAmount = recurringAmount;
    }

    public String getTermsReference() {
        return this.termsReference;
    }

    public MaintenancePlanVersion termsReference(String termsReference) {
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

    public MaintenancePlanVersion organization(Organization organization) {
        this.setOrganization(organization);
        return this;
    }

    public MaintenancePlan getMaintenancePlan() {
        return this.maintenancePlan;
    }

    public void setMaintenancePlan(MaintenancePlan maintenancePlan) {
        this.maintenancePlan = maintenancePlan;
        this.maintenancePlanId = maintenancePlan != null ? maintenancePlan.getId() : null;
    }

    public MaintenancePlanVersion maintenancePlan(MaintenancePlan maintenancePlan) {
        this.setMaintenancePlan(maintenancePlan);
        return this;
    }

    public Long getOrganizationId() {
        return this.organizationId;
    }

    public void setOrganizationId(Long organization) {
        this.organizationId = organization;
    }

    public Long getMaintenancePlanId() {
        return this.maintenancePlanId;
    }

    public void setMaintenancePlanId(Long maintenancePlan) {
        this.maintenancePlanId = maintenancePlan;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof MaintenancePlanVersion)) {
            return false;
        }
        return getId() != null && getId().equals(((MaintenancePlanVersion) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "MaintenancePlanVersion{" +
            "id=" + getId() +
            ", versionCode='" + getVersionCode() + "'" +
            ", publishedAt='" + getPublishedAt() + "'" +
            ", currencyCode='" + getCurrencyCode() + "'" +
            ", recurringAmount=" + getRecurringAmount() +
            ", termsReference='" + getTermsReference() + "'" +
            "}";
    }
}

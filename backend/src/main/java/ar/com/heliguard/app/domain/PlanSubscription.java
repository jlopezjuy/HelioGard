package ar.com.heliguard.app.domain;

import ar.com.heliguard.app.domain.enumeration.SubscriptionStatus;
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
 * A PlanSubscription.
 */
@Table("plan_subscription")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class PlanSubscription implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @NotNull
    @Column("status")
    private SubscriptionStatus status;

    @Column("activated_at")
    private Instant activatedAt;

    @Column("expires_at")
    private Instant expiresAt;

    @Column("agreed_amount")
    private BigDecimal agreedAmount;

    @Size(max = 3)
    @Column("currency_code")
    private String currencyCode;

    @org.springframework.data.annotation.Transient
    private Organization organization;

    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "organization", "site", "climateRiskProfile" }, allowSetters = true)
    private Installation installation;

    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "organization", "maintenancePlan" }, allowSetters = true)
    private MaintenancePlanVersion planVersion;

    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "organization", "providerOrganization", "installation" }, allowSetters = true)
    private ServiceAgreement serviceAgreement;

    @Column("organization_id")
    private Long organizationId;

    @Column("installation_id")
    private Long installationId;

    @Column("plan_version_id")
    private Long planVersionId;

    @Column("service_agreement_id")
    private Long serviceAgreementId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public PlanSubscription id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public SubscriptionStatus getStatus() {
        return this.status;
    }

    public PlanSubscription status(SubscriptionStatus status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(SubscriptionStatus status) {
        this.status = status;
    }

    public Instant getActivatedAt() {
        return this.activatedAt;
    }

    public PlanSubscription activatedAt(Instant activatedAt) {
        this.setActivatedAt(activatedAt);
        return this;
    }

    public void setActivatedAt(Instant activatedAt) {
        this.activatedAt = activatedAt;
    }

    public Instant getExpiresAt() {
        return this.expiresAt;
    }

    public PlanSubscription expiresAt(Instant expiresAt) {
        this.setExpiresAt(expiresAt);
        return this;
    }

    public void setExpiresAt(Instant expiresAt) {
        this.expiresAt = expiresAt;
    }

    public BigDecimal getAgreedAmount() {
        return this.agreedAmount;
    }

    public PlanSubscription agreedAmount(BigDecimal agreedAmount) {
        this.setAgreedAmount(agreedAmount);
        return this;
    }

    public void setAgreedAmount(BigDecimal agreedAmount) {
        this.agreedAmount = agreedAmount;
    }

    public String getCurrencyCode() {
        return this.currencyCode;
    }

    public PlanSubscription currencyCode(String currencyCode) {
        this.setCurrencyCode(currencyCode);
        return this;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public Organization getOrganization() {
        return this.organization;
    }

    public void setOrganization(Organization organization) {
        this.organization = organization;
        this.organizationId = organization != null ? organization.getId() : null;
    }

    public PlanSubscription organization(Organization organization) {
        this.setOrganization(organization);
        return this;
    }

    public Installation getInstallation() {
        return this.installation;
    }

    public void setInstallation(Installation installation) {
        this.installation = installation;
        this.installationId = installation != null ? installation.getId() : null;
    }

    public PlanSubscription installation(Installation installation) {
        this.setInstallation(installation);
        return this;
    }

    public MaintenancePlanVersion getPlanVersion() {
        return this.planVersion;
    }

    public void setPlanVersion(MaintenancePlanVersion maintenancePlanVersion) {
        this.planVersion = maintenancePlanVersion;
        this.planVersionId = maintenancePlanVersion != null ? maintenancePlanVersion.getId() : null;
    }

    public PlanSubscription planVersion(MaintenancePlanVersion maintenancePlanVersion) {
        this.setPlanVersion(maintenancePlanVersion);
        return this;
    }

    public ServiceAgreement getServiceAgreement() {
        return this.serviceAgreement;
    }

    public void setServiceAgreement(ServiceAgreement serviceAgreement) {
        this.serviceAgreement = serviceAgreement;
        this.serviceAgreementId = serviceAgreement != null ? serviceAgreement.getId() : null;
    }

    public PlanSubscription serviceAgreement(ServiceAgreement serviceAgreement) {
        this.setServiceAgreement(serviceAgreement);
        return this;
    }

    public Long getOrganizationId() {
        return this.organizationId;
    }

    public void setOrganizationId(Long organization) {
        this.organizationId = organization;
    }

    public Long getInstallationId() {
        return this.installationId;
    }

    public void setInstallationId(Long installation) {
        this.installationId = installation;
    }

    public Long getPlanVersionId() {
        return this.planVersionId;
    }

    public void setPlanVersionId(Long maintenancePlanVersion) {
        this.planVersionId = maintenancePlanVersion;
    }

    public Long getServiceAgreementId() {
        return this.serviceAgreementId;
    }

    public void setServiceAgreementId(Long serviceAgreement) {
        this.serviceAgreementId = serviceAgreement;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PlanSubscription)) {
            return false;
        }
        return getId() != null && getId().equals(((PlanSubscription) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "PlanSubscription{" +
            "id=" + getId() +
            ", status='" + getStatus() + "'" +
            ", activatedAt='" + getActivatedAt() + "'" +
            ", expiresAt='" + getExpiresAt() + "'" +
            ", agreedAmount=" + getAgreedAmount() +
            ", currencyCode='" + getCurrencyCode() + "'" +
            "}";
    }
}

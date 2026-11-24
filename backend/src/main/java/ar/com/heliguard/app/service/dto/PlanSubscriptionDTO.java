package ar.com.heliguard.app.service.dto;

import ar.com.heliguard.app.domain.enumeration.SubscriptionStatus;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link ar.com.heliguard.app.domain.PlanSubscription} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class PlanSubscriptionDTO implements Serializable {

    private Long id;

    @NotNull
    private SubscriptionStatus status;

    private Instant activatedAt;

    private Instant expiresAt;

    private BigDecimal agreedAmount;

    @Size(max = 3)
    private String currencyCode;

    @NotNull
    private OrganizationDTO organization;

    @NotNull
    private InstallationDTO installation;

    @NotNull
    private MaintenancePlanVersionDTO planVersion;

    @NotNull
    private ServiceAgreementDTO serviceAgreement;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public SubscriptionStatus getStatus() {
        return status;
    }

    public void setStatus(SubscriptionStatus status) {
        this.status = status;
    }

    public Instant getActivatedAt() {
        return activatedAt;
    }

    public void setActivatedAt(Instant activatedAt) {
        this.activatedAt = activatedAt;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(Instant expiresAt) {
        this.expiresAt = expiresAt;
    }

    public BigDecimal getAgreedAmount() {
        return agreedAmount;
    }

    public void setAgreedAmount(BigDecimal agreedAmount) {
        this.agreedAmount = agreedAmount;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public OrganizationDTO getOrganization() {
        return organization;
    }

    public void setOrganization(OrganizationDTO organization) {
        this.organization = organization;
    }

    public InstallationDTO getInstallation() {
        return installation;
    }

    public void setInstallation(InstallationDTO installation) {
        this.installation = installation;
    }

    public MaintenancePlanVersionDTO getPlanVersion() {
        return planVersion;
    }

    public void setPlanVersion(MaintenancePlanVersionDTO planVersion) {
        this.planVersion = planVersion;
    }

    public ServiceAgreementDTO getServiceAgreement() {
        return serviceAgreement;
    }

    public void setServiceAgreement(ServiceAgreementDTO serviceAgreement) {
        this.serviceAgreement = serviceAgreement;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PlanSubscriptionDTO)) {
            return false;
        }

        PlanSubscriptionDTO planSubscriptionDTO = (PlanSubscriptionDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, planSubscriptionDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "PlanSubscriptionDTO{" +
            "id=" + getId() +
            ", status='" + getStatus() + "'" +
            ", activatedAt='" + getActivatedAt() + "'" +
            ", expiresAt='" + getExpiresAt() + "'" +
            ", agreedAmount=" + getAgreedAmount() +
            ", currencyCode='" + getCurrencyCode() + "'" +
            ", organization=" + getOrganization() +
            ", installation=" + getInstallation() +
            ", planVersion=" + getPlanVersion() +
            ", serviceAgreement=" + getServiceAgreement() +
            "}";
    }
}

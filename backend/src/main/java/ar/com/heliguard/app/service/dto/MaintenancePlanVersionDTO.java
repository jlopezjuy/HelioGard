package ar.com.heliguard.app.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link ar.com.heliguard.app.domain.MaintenancePlanVersion} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class MaintenancePlanVersionDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(max = 50)
    private String versionCode;

    private Instant publishedAt;

    @NotNull
    @Size(max = 3)
    private String currencyCode;

    @NotNull
    private BigDecimal recurringAmount;

    @NotNull
    @Size(max = 400)
    private String termsReference;

    private OrganizationDTO organization;

    @NotNull
    private MaintenancePlanDTO maintenancePlan;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(String versionCode) {
        this.versionCode = versionCode;
    }

    public Instant getPublishedAt() {
        return publishedAt;
    }

    public void setPublishedAt(Instant publishedAt) {
        this.publishedAt = publishedAt;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public BigDecimal getRecurringAmount() {
        return recurringAmount;
    }

    public void setRecurringAmount(BigDecimal recurringAmount) {
        this.recurringAmount = recurringAmount;
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

    public MaintenancePlanDTO getMaintenancePlan() {
        return maintenancePlan;
    }

    public void setMaintenancePlan(MaintenancePlanDTO maintenancePlan) {
        this.maintenancePlan = maintenancePlan;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof MaintenancePlanVersionDTO)) {
            return false;
        }

        MaintenancePlanVersionDTO maintenancePlanVersionDTO = (MaintenancePlanVersionDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, maintenancePlanVersionDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "MaintenancePlanVersionDTO{" +
            "id=" + getId() +
            ", versionCode='" + getVersionCode() + "'" +
            ", publishedAt='" + getPublishedAt() + "'" +
            ", currencyCode='" + getCurrencyCode() + "'" +
            ", recurringAmount=" + getRecurringAmount() +
            ", termsReference='" + getTermsReference() + "'" +
            ", organization=" + getOrganization() +
            ", maintenancePlan=" + getMaintenancePlan() +
            "}";
    }
}

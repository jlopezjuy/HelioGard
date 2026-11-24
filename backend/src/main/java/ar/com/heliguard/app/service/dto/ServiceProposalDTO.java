package ar.com.heliguard.app.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link ar.com.heliguard.app.domain.ServiceProposal} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ServiceProposalDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(max = 70)
    private String referenceCode;

    @NotNull
    private Instant proposedAt;

    private Instant validUntil;

    @NotNull
    private BigDecimal amount;

    @NotNull
    @Size(max = 3)
    private String currencyCode;

    private Instant acceptedAt;

    @NotNull
    private OrganizationDTO organization;

    @NotNull
    private InstallationDTO installation;

    private MaintenancePlanVersionDTO planVersion;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getReferenceCode() {
        return referenceCode;
    }

    public void setReferenceCode(String referenceCode) {
        this.referenceCode = referenceCode;
    }

    public Instant getProposedAt() {
        return proposedAt;
    }

    public void setProposedAt(Instant proposedAt) {
        this.proposedAt = proposedAt;
    }

    public Instant getValidUntil() {
        return validUntil;
    }

    public void setValidUntil(Instant validUntil) {
        this.validUntil = validUntil;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public Instant getAcceptedAt() {
        return acceptedAt;
    }

    public void setAcceptedAt(Instant acceptedAt) {
        this.acceptedAt = acceptedAt;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ServiceProposalDTO)) {
            return false;
        }

        ServiceProposalDTO serviceProposalDTO = (ServiceProposalDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, serviceProposalDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ServiceProposalDTO{" +
            "id=" + getId() +
            ", referenceCode='" + getReferenceCode() + "'" +
            ", proposedAt='" + getProposedAt() + "'" +
            ", validUntil='" + getValidUntil() + "'" +
            ", amount=" + getAmount() +
            ", currencyCode='" + getCurrencyCode() + "'" +
            ", acceptedAt='" + getAcceptedAt() + "'" +
            ", organization=" + getOrganization() +
            ", installation=" + getInstallation() +
            ", planVersion=" + getPlanVersion() +
            "}";
    }
}

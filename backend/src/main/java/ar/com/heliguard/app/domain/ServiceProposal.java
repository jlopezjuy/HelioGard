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
 * A ServiceProposal.
 */
@Table("service_proposal")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ServiceProposal implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @NotNull
    @Size(max = 70)
    @Column("reference_code")
    private String referenceCode;

    @NotNull
    @Column("proposed_at")
    private Instant proposedAt;

    @Column("valid_until")
    private Instant validUntil;

    @NotNull
    @Column("amount")
    private BigDecimal amount;

    @NotNull
    @Size(max = 3)
    @Column("currency_code")
    private String currencyCode;

    @Column("accepted_at")
    private Instant acceptedAt;

    @org.springframework.data.annotation.Transient
    private Organization organization;

    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "organization", "site", "climateRiskProfile" }, allowSetters = true)
    private Installation installation;

    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "organization", "maintenancePlan" }, allowSetters = true)
    private MaintenancePlanVersion planVersion;

    @Column("organization_id")
    private Long organizationId;

    @Column("installation_id")
    private Long installationId;

    @Column("plan_version_id")
    private Long planVersionId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public ServiceProposal id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getReferenceCode() {
        return this.referenceCode;
    }

    public ServiceProposal referenceCode(String referenceCode) {
        this.setReferenceCode(referenceCode);
        return this;
    }

    public void setReferenceCode(String referenceCode) {
        this.referenceCode = referenceCode;
    }

    public Instant getProposedAt() {
        return this.proposedAt;
    }

    public ServiceProposal proposedAt(Instant proposedAt) {
        this.setProposedAt(proposedAt);
        return this;
    }

    public void setProposedAt(Instant proposedAt) {
        this.proposedAt = proposedAt;
    }

    public Instant getValidUntil() {
        return this.validUntil;
    }

    public ServiceProposal validUntil(Instant validUntil) {
        this.setValidUntil(validUntil);
        return this;
    }

    public void setValidUntil(Instant validUntil) {
        this.validUntil = validUntil;
    }

    public BigDecimal getAmount() {
        return this.amount;
    }

    public ServiceProposal amount(BigDecimal amount) {
        this.setAmount(amount);
        return this;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getCurrencyCode() {
        return this.currencyCode;
    }

    public ServiceProposal currencyCode(String currencyCode) {
        this.setCurrencyCode(currencyCode);
        return this;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public Instant getAcceptedAt() {
        return this.acceptedAt;
    }

    public ServiceProposal acceptedAt(Instant acceptedAt) {
        this.setAcceptedAt(acceptedAt);
        return this;
    }

    public void setAcceptedAt(Instant acceptedAt) {
        this.acceptedAt = acceptedAt;
    }

    public Organization getOrganization() {
        return this.organization;
    }

    public void setOrganization(Organization organization) {
        this.organization = organization;
        this.organizationId = organization != null ? organization.getId() : null;
    }

    public ServiceProposal organization(Organization organization) {
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

    public ServiceProposal installation(Installation installation) {
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

    public ServiceProposal planVersion(MaintenancePlanVersion maintenancePlanVersion) {
        this.setPlanVersion(maintenancePlanVersion);
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

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ServiceProposal)) {
            return false;
        }
        return getId() != null && getId().equals(((ServiceProposal) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ServiceProposal{" +
            "id=" + getId() +
            ", referenceCode='" + getReferenceCode() + "'" +
            ", proposedAt='" + getProposedAt() + "'" +
            ", validUntil='" + getValidUntil() + "'" +
            ", amount=" + getAmount() +
            ", currencyCode='" + getCurrencyCode() + "'" +
            ", acceptedAt='" + getAcceptedAt() + "'" +
            "}";
    }
}

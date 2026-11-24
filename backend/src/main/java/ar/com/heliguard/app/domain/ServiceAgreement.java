package ar.com.heliguard.app.domain;

import ar.com.heliguard.app.domain.enumeration.ServiceAgreementStatus;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A ServiceAgreement.
 */
@Table("service_agreement")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ServiceAgreement implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @NotNull
    @Column("status")
    private ServiceAgreementStatus status;

    @NotNull
    @Column("starts_at")
    private Instant startsAt;

    @Column("ends_at")
    private Instant endsAt;

    @Column("approved_at")
    private Instant approvedAt;

    @org.springframework.data.annotation.Transient
    private Organization organization;

    @org.springframework.data.annotation.Transient
    private Organization providerOrganization;

    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "organization", "site", "climateRiskProfile" }, allowSetters = true)
    private Installation installation;

    @Column("organization_id")
    private Long organizationId;

    @Column("provider_organization_id")
    private Long providerOrganizationId;

    @Column("installation_id")
    private Long installationId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public ServiceAgreement id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ServiceAgreementStatus getStatus() {
        return this.status;
    }

    public ServiceAgreement status(ServiceAgreementStatus status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(ServiceAgreementStatus status) {
        this.status = status;
    }

    public Instant getStartsAt() {
        return this.startsAt;
    }

    public ServiceAgreement startsAt(Instant startsAt) {
        this.setStartsAt(startsAt);
        return this;
    }

    public void setStartsAt(Instant startsAt) {
        this.startsAt = startsAt;
    }

    public Instant getEndsAt() {
        return this.endsAt;
    }

    public ServiceAgreement endsAt(Instant endsAt) {
        this.setEndsAt(endsAt);
        return this;
    }

    public void setEndsAt(Instant endsAt) {
        this.endsAt = endsAt;
    }

    public Instant getApprovedAt() {
        return this.approvedAt;
    }

    public ServiceAgreement approvedAt(Instant approvedAt) {
        this.setApprovedAt(approvedAt);
        return this;
    }

    public void setApprovedAt(Instant approvedAt) {
        this.approvedAt = approvedAt;
    }

    public Organization getOrganization() {
        return this.organization;
    }

    public void setOrganization(Organization organization) {
        this.organization = organization;
        this.organizationId = organization != null ? organization.getId() : null;
    }

    public ServiceAgreement organization(Organization organization) {
        this.setOrganization(organization);
        return this;
    }

    public Organization getProviderOrganization() {
        return this.providerOrganization;
    }

    public void setProviderOrganization(Organization organization) {
        this.providerOrganization = organization;
        this.providerOrganizationId = organization != null ? organization.getId() : null;
    }

    public ServiceAgreement providerOrganization(Organization organization) {
        this.setProviderOrganization(organization);
        return this;
    }

    public Installation getInstallation() {
        return this.installation;
    }

    public void setInstallation(Installation installation) {
        this.installation = installation;
        this.installationId = installation != null ? installation.getId() : null;
    }

    public ServiceAgreement installation(Installation installation) {
        this.setInstallation(installation);
        return this;
    }

    public Long getOrganizationId() {
        return this.organizationId;
    }

    public void setOrganizationId(Long organization) {
        this.organizationId = organization;
    }

    public Long getProviderOrganizationId() {
        return this.providerOrganizationId;
    }

    public void setProviderOrganizationId(Long organization) {
        this.providerOrganizationId = organization;
    }

    public Long getInstallationId() {
        return this.installationId;
    }

    public void setInstallationId(Long installation) {
        this.installationId = installation;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ServiceAgreement)) {
            return false;
        }
        return getId() != null && getId().equals(((ServiceAgreement) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ServiceAgreement{" +
            "id=" + getId() +
            ", status='" + getStatus() + "'" +
            ", startsAt='" + getStartsAt() + "'" +
            ", endsAt='" + getEndsAt() + "'" +
            ", approvedAt='" + getApprovedAt() + "'" +
            "}";
    }
}

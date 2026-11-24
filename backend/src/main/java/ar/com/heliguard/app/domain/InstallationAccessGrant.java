package ar.com.heliguard.app.domain;

import ar.com.heliguard.app.domain.enumeration.PermissionScope;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A InstallationAccessGrant.
 */
@Table("installation_access_grant")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class InstallationAccessGrant implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @NotNull
    @Column("scope")
    private PermissionScope scope;

    @NotNull
    @Column("valid_from")
    private Instant validFrom;

    @Column("valid_until")
    private Instant validUntil;

    @Column("revoked_at")
    private Instant revokedAt;

    @Size(max = 250)
    @Column("reason")
    private String reason;

    @org.springframework.data.annotation.Transient
    private Organization organization;

    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "organization", "site", "climateRiskProfile" }, allowSetters = true)
    private Installation installation;

    @org.springframework.data.annotation.Transient
    private Organization granteeOrganization;

    @Column("organization_id")
    private Long organizationId;

    @Column("installation_id")
    private Long installationId;

    @Column("grantee_organization_id")
    private Long granteeOrganizationId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public InstallationAccessGrant id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public PermissionScope getScope() {
        return this.scope;
    }

    public InstallationAccessGrant scope(PermissionScope scope) {
        this.setScope(scope);
        return this;
    }

    public void setScope(PermissionScope scope) {
        this.scope = scope;
    }

    public Instant getValidFrom() {
        return this.validFrom;
    }

    public InstallationAccessGrant validFrom(Instant validFrom) {
        this.setValidFrom(validFrom);
        return this;
    }

    public void setValidFrom(Instant validFrom) {
        this.validFrom = validFrom;
    }

    public Instant getValidUntil() {
        return this.validUntil;
    }

    public InstallationAccessGrant validUntil(Instant validUntil) {
        this.setValidUntil(validUntil);
        return this;
    }

    public void setValidUntil(Instant validUntil) {
        this.validUntil = validUntil;
    }

    public Instant getRevokedAt() {
        return this.revokedAt;
    }

    public InstallationAccessGrant revokedAt(Instant revokedAt) {
        this.setRevokedAt(revokedAt);
        return this;
    }

    public void setRevokedAt(Instant revokedAt) {
        this.revokedAt = revokedAt;
    }

    public String getReason() {
        return this.reason;
    }

    public InstallationAccessGrant reason(String reason) {
        this.setReason(reason);
        return this;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Organization getOrganization() {
        return this.organization;
    }

    public void setOrganization(Organization organization) {
        this.organization = organization;
        this.organizationId = organization != null ? organization.getId() : null;
    }

    public InstallationAccessGrant organization(Organization organization) {
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

    public InstallationAccessGrant installation(Installation installation) {
        this.setInstallation(installation);
        return this;
    }

    public Organization getGranteeOrganization() {
        return this.granteeOrganization;
    }

    public void setGranteeOrganization(Organization organization) {
        this.granteeOrganization = organization;
        this.granteeOrganizationId = organization != null ? organization.getId() : null;
    }

    public InstallationAccessGrant granteeOrganization(Organization organization) {
        this.setGranteeOrganization(organization);
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

    public Long getGranteeOrganizationId() {
        return this.granteeOrganizationId;
    }

    public void setGranteeOrganizationId(Long organization) {
        this.granteeOrganizationId = organization;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof InstallationAccessGrant)) {
            return false;
        }
        return getId() != null && getId().equals(((InstallationAccessGrant) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "InstallationAccessGrant{" +
            "id=" + getId() +
            ", scope='" + getScope() + "'" +
            ", validFrom='" + getValidFrom() + "'" +
            ", validUntil='" + getValidUntil() + "'" +
            ", revokedAt='" + getRevokedAt() + "'" +
            ", reason='" + getReason() + "'" +
            "}";
    }
}

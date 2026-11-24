package ar.com.heliguard.app.service.dto;

import ar.com.heliguard.app.domain.enumeration.PermissionScope;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link ar.com.heliguard.app.domain.InstallationAccessGrant} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class InstallationAccessGrantDTO implements Serializable {

    private Long id;

    @NotNull
    private PermissionScope scope;

    @NotNull
    private Instant validFrom;

    private Instant validUntil;

    private Instant revokedAt;

    @Size(max = 250)
    private String reason;

    private OrganizationDTO organization;

    @NotNull
    private InstallationDTO installation;

    @NotNull
    private OrganizationDTO granteeOrganization;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public PermissionScope getScope() {
        return scope;
    }

    public void setScope(PermissionScope scope) {
        this.scope = scope;
    }

    public Instant getValidFrom() {
        return validFrom;
    }

    public void setValidFrom(Instant validFrom) {
        this.validFrom = validFrom;
    }

    public Instant getValidUntil() {
        return validUntil;
    }

    public void setValidUntil(Instant validUntil) {
        this.validUntil = validUntil;
    }

    public Instant getRevokedAt() {
        return revokedAt;
    }

    public void setRevokedAt(Instant revokedAt) {
        this.revokedAt = revokedAt;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
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

    public OrganizationDTO getGranteeOrganization() {
        return granteeOrganization;
    }

    public void setGranteeOrganization(OrganizationDTO granteeOrganization) {
        this.granteeOrganization = granteeOrganization;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof InstallationAccessGrantDTO)) {
            return false;
        }

        InstallationAccessGrantDTO installationAccessGrantDTO = (InstallationAccessGrantDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, installationAccessGrantDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "InstallationAccessGrantDTO{" +
            "id=" + getId() +
            ", scope='" + getScope() + "'" +
            ", validFrom='" + getValidFrom() + "'" +
            ", validUntil='" + getValidUntil() + "'" +
            ", revokedAt='" + getRevokedAt() + "'" +
            ", reason='" + getReason() + "'" +
            ", organization=" + getOrganization() +
            ", installation=" + getInstallation() +
            ", granteeOrganization=" + getGranteeOrganization() +
            "}";
    }
}

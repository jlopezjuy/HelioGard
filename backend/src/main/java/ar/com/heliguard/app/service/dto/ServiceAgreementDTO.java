package ar.com.heliguard.app.service.dto;

import ar.com.heliguard.app.domain.enumeration.ServiceAgreementStatus;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link ar.com.heliguard.app.domain.ServiceAgreement} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ServiceAgreementDTO implements Serializable {

    private Long id;

    @NotNull
    private ServiceAgreementStatus status;

    @NotNull
    private Instant startsAt;

    private Instant endsAt;

    private Instant approvedAt;

    @NotNull
    private OrganizationDTO organization;

    @NotNull
    private OrganizationDTO providerOrganization;

    @NotNull
    private InstallationDTO installation;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ServiceAgreementStatus getStatus() {
        return status;
    }

    public void setStatus(ServiceAgreementStatus status) {
        this.status = status;
    }

    public Instant getStartsAt() {
        return startsAt;
    }

    public void setStartsAt(Instant startsAt) {
        this.startsAt = startsAt;
    }

    public Instant getEndsAt() {
        return endsAt;
    }

    public void setEndsAt(Instant endsAt) {
        this.endsAt = endsAt;
    }

    public Instant getApprovedAt() {
        return approvedAt;
    }

    public void setApprovedAt(Instant approvedAt) {
        this.approvedAt = approvedAt;
    }

    public OrganizationDTO getOrganization() {
        return organization;
    }

    public void setOrganization(OrganizationDTO organization) {
        this.organization = organization;
    }

    public OrganizationDTO getProviderOrganization() {
        return providerOrganization;
    }

    public void setProviderOrganization(OrganizationDTO providerOrganization) {
        this.providerOrganization = providerOrganization;
    }

    public InstallationDTO getInstallation() {
        return installation;
    }

    public void setInstallation(InstallationDTO installation) {
        this.installation = installation;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ServiceAgreementDTO)) {
            return false;
        }

        ServiceAgreementDTO serviceAgreementDTO = (ServiceAgreementDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, serviceAgreementDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ServiceAgreementDTO{" +
            "id=" + getId() +
            ", status='" + getStatus() + "'" +
            ", startsAt='" + getStartsAt() + "'" +
            ", endsAt='" + getEndsAt() + "'" +
            ", approvedAt='" + getApprovedAt() + "'" +
            ", organization=" + getOrganization() +
            ", providerOrganization=" + getProviderOrganization() +
            ", installation=" + getInstallation() +
            "}";
    }
}

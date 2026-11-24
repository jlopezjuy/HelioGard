package ar.com.heliguard.app.service.dto;

import ar.com.heliguard.app.domain.enumeration.GatewayStatus;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link ar.com.heliguard.app.domain.Gateway} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class GatewayDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(max = 120)
    private String hardwareId;

    @Size(max = 80)
    private String firmwareVersion;

    @NotNull
    private GatewayStatus status;

    private Instant lastSeenAt;

    private Instant provisionedAt;

    private OrganizationDTO organization;

    @NotNull
    private InstallationDTO installation;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getHardwareId() {
        return hardwareId;
    }

    public void setHardwareId(String hardwareId) {
        this.hardwareId = hardwareId;
    }

    public String getFirmwareVersion() {
        return firmwareVersion;
    }

    public void setFirmwareVersion(String firmwareVersion) {
        this.firmwareVersion = firmwareVersion;
    }

    public GatewayStatus getStatus() {
        return status;
    }

    public void setStatus(GatewayStatus status) {
        this.status = status;
    }

    public Instant getLastSeenAt() {
        return lastSeenAt;
    }

    public void setLastSeenAt(Instant lastSeenAt) {
        this.lastSeenAt = lastSeenAt;
    }

    public Instant getProvisionedAt() {
        return provisionedAt;
    }

    public void setProvisionedAt(Instant provisionedAt) {
        this.provisionedAt = provisionedAt;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof GatewayDTO)) {
            return false;
        }

        GatewayDTO gatewayDTO = (GatewayDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, gatewayDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "GatewayDTO{" +
            "id=" + getId() +
            ", hardwareId='" + getHardwareId() + "'" +
            ", firmwareVersion='" + getFirmwareVersion() + "'" +
            ", status='" + getStatus() + "'" +
            ", lastSeenAt='" + getLastSeenAt() + "'" +
            ", provisionedAt='" + getProvisionedAt() + "'" +
            ", organization=" + getOrganization() +
            ", installation=" + getInstallation() +
            "}";
    }
}

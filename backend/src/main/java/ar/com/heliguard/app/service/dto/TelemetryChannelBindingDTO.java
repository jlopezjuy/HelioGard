package ar.com.heliguard.app.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link ar.com.heliguard.app.domain.TelemetryChannelBinding} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class TelemetryChannelBindingDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(max = 180)
    private String externalPath;

    @NotNull
    private Instant validFrom;

    private Instant validUntil;

    @NotNull
    private OrganizationDTO organization;

    @NotNull
    private TelemetryChannelDTO telemetryChannel;

    @NotNull
    private GatewayDTO gateway;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getExternalPath() {
        return externalPath;
    }

    public void setExternalPath(String externalPath) {
        this.externalPath = externalPath;
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

    public OrganizationDTO getOrganization() {
        return organization;
    }

    public void setOrganization(OrganizationDTO organization) {
        this.organization = organization;
    }

    public TelemetryChannelDTO getTelemetryChannel() {
        return telemetryChannel;
    }

    public void setTelemetryChannel(TelemetryChannelDTO telemetryChannel) {
        this.telemetryChannel = telemetryChannel;
    }

    public GatewayDTO getGateway() {
        return gateway;
    }

    public void setGateway(GatewayDTO gateway) {
        this.gateway = gateway;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TelemetryChannelBindingDTO)) {
            return false;
        }

        TelemetryChannelBindingDTO telemetryChannelBindingDTO = (TelemetryChannelBindingDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, telemetryChannelBindingDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TelemetryChannelBindingDTO{" +
            "id=" + getId() +
            ", externalPath='" + getExternalPath() + "'" +
            ", validFrom='" + getValidFrom() + "'" +
            ", validUntil='" + getValidUntil() + "'" +
            ", organization=" + getOrganization() +
            ", telemetryChannel=" + getTelemetryChannel() +
            ", gateway=" + getGateway() +
            "}";
    }
}

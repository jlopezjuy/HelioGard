package ar.com.heliguard.app.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A TelemetryChannelBinding.
 */
@Table("telemetry_channel_binding")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class TelemetryChannelBinding implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @NotNull
    @Size(max = 180)
    @Column("external_path")
    private String externalPath;

    @NotNull
    @Column("valid_from")
    private Instant validFrom;

    @Column("valid_until")
    private Instant validUntil;

    @org.springframework.data.annotation.Transient
    private Organization organization;

    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "organization", "equipment", "metricDefinition" }, allowSetters = true)
    private TelemetryChannel telemetryChannel;

    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "organization", "installation" }, allowSetters = true)
    private Gateway gateway;

    @Column("organization_id")
    private Long organizationId;

    @Column("telemetry_channel_id")
    private Long telemetryChannelId;

    @Column("gateway_id")
    private Long gatewayId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public TelemetryChannelBinding id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getExternalPath() {
        return this.externalPath;
    }

    public TelemetryChannelBinding externalPath(String externalPath) {
        this.setExternalPath(externalPath);
        return this;
    }

    public void setExternalPath(String externalPath) {
        this.externalPath = externalPath;
    }

    public Instant getValidFrom() {
        return this.validFrom;
    }

    public TelemetryChannelBinding validFrom(Instant validFrom) {
        this.setValidFrom(validFrom);
        return this;
    }

    public void setValidFrom(Instant validFrom) {
        this.validFrom = validFrom;
    }

    public Instant getValidUntil() {
        return this.validUntil;
    }

    public TelemetryChannelBinding validUntil(Instant validUntil) {
        this.setValidUntil(validUntil);
        return this;
    }

    public void setValidUntil(Instant validUntil) {
        this.validUntil = validUntil;
    }

    public Organization getOrganization() {
        return this.organization;
    }

    public void setOrganization(Organization organization) {
        this.organization = organization;
        this.organizationId = organization != null ? organization.getId() : null;
    }

    public TelemetryChannelBinding organization(Organization organization) {
        this.setOrganization(organization);
        return this;
    }

    public TelemetryChannel getTelemetryChannel() {
        return this.telemetryChannel;
    }

    public void setTelemetryChannel(TelemetryChannel telemetryChannel) {
        this.telemetryChannel = telemetryChannel;
        this.telemetryChannelId = telemetryChannel != null ? telemetryChannel.getId() : null;
    }

    public TelemetryChannelBinding telemetryChannel(TelemetryChannel telemetryChannel) {
        this.setTelemetryChannel(telemetryChannel);
        return this;
    }

    public Gateway getGateway() {
        return this.gateway;
    }

    public void setGateway(Gateway gateway) {
        this.gateway = gateway;
        this.gatewayId = gateway != null ? gateway.getId() : null;
    }

    public TelemetryChannelBinding gateway(Gateway gateway) {
        this.setGateway(gateway);
        return this;
    }

    public Long getOrganizationId() {
        return this.organizationId;
    }

    public void setOrganizationId(Long organization) {
        this.organizationId = organization;
    }

    public Long getTelemetryChannelId() {
        return this.telemetryChannelId;
    }

    public void setTelemetryChannelId(Long telemetryChannel) {
        this.telemetryChannelId = telemetryChannel;
    }

    public Long getGatewayId() {
        return this.gatewayId;
    }

    public void setGatewayId(Long gateway) {
        this.gatewayId = gateway;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TelemetryChannelBinding)) {
            return false;
        }
        return getId() != null && getId().equals(((TelemetryChannelBinding) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TelemetryChannelBinding{" +
            "id=" + getId() +
            ", externalPath='" + getExternalPath() + "'" +
            ", validFrom='" + getValidFrom() + "'" +
            ", validUntil='" + getValidUntil() + "'" +
            "}";
    }
}

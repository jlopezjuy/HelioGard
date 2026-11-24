package ar.com.heliguard.app.service.dto;

import ar.com.heliguard.app.domain.enumeration.DeviceTrustStatus;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link ar.com.heliguard.app.domain.DeviceIdentity} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class DeviceIdentityDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(max = 140)
    private String deviceUid;

    @Size(max = 128)
    private String keyFingerprint;

    @NotNull
    @Size(max = 80)
    private String protocolName;

    @NotNull
    private DeviceTrustStatus trustStatus;

    private Instant keyRotatedAt;

    private Instant validUntil;

    @NotNull
    private OrganizationDTO organization;

    private GatewayDTO gateway;

    private EquipmentDTO equipment;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDeviceUid() {
        return deviceUid;
    }

    public void setDeviceUid(String deviceUid) {
        this.deviceUid = deviceUid;
    }

    public String getKeyFingerprint() {
        return keyFingerprint;
    }

    public void setKeyFingerprint(String keyFingerprint) {
        this.keyFingerprint = keyFingerprint;
    }

    public String getProtocolName() {
        return protocolName;
    }

    public void setProtocolName(String protocolName) {
        this.protocolName = protocolName;
    }

    public DeviceTrustStatus getTrustStatus() {
        return trustStatus;
    }

    public void setTrustStatus(DeviceTrustStatus trustStatus) {
        this.trustStatus = trustStatus;
    }

    public Instant getKeyRotatedAt() {
        return keyRotatedAt;
    }

    public void setKeyRotatedAt(Instant keyRotatedAt) {
        this.keyRotatedAt = keyRotatedAt;
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

    public GatewayDTO getGateway() {
        return gateway;
    }

    public void setGateway(GatewayDTO gateway) {
        this.gateway = gateway;
    }

    public EquipmentDTO getEquipment() {
        return equipment;
    }

    public void setEquipment(EquipmentDTO equipment) {
        this.equipment = equipment;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DeviceIdentityDTO)) {
            return false;
        }

        DeviceIdentityDTO deviceIdentityDTO = (DeviceIdentityDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, deviceIdentityDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "DeviceIdentityDTO{" +
            "id=" + getId() +
            ", deviceUid='" + getDeviceUid() + "'" +
            ", keyFingerprint='" + getKeyFingerprint() + "'" +
            ", protocolName='" + getProtocolName() + "'" +
            ", trustStatus='" + getTrustStatus() + "'" +
            ", keyRotatedAt='" + getKeyRotatedAt() + "'" +
            ", validUntil='" + getValidUntil() + "'" +
            ", organization=" + getOrganization() +
            ", gateway=" + getGateway() +
            ", equipment=" + getEquipment() +
            "}";
    }
}

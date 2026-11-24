package ar.com.heliguard.app.domain;

import ar.com.heliguard.app.domain.enumeration.DeviceTrustStatus;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A DeviceIdentity.
 */
@Table("device_identity")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class DeviceIdentity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @NotNull
    @Size(max = 140)
    @Column("device_uid")
    private String deviceUid;

    @Size(max = 128)
    @Column("key_fingerprint")
    private String keyFingerprint;

    @NotNull
    @Size(max = 80)
    @Column("protocol_name")
    private String protocolName;

    @NotNull
    @Column("trust_status")
    private DeviceTrustStatus trustStatus;

    @Column("key_rotated_at")
    private Instant keyRotatedAt;

    @Column("valid_until")
    private Instant validUntil;

    @org.springframework.data.annotation.Transient
    private Organization organization;

    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "organization", "installation" }, allowSetters = true)
    private Gateway gateway;

    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "organization", "installation", "equipmentModel", "solarString" }, allowSetters = true)
    private Equipment equipment;

    @Column("organization_id")
    private Long organizationId;

    @Column("gateway_id")
    private Long gatewayId;

    @Column("equipment_id")
    private Long equipmentId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public DeviceIdentity id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDeviceUid() {
        return this.deviceUid;
    }

    public DeviceIdentity deviceUid(String deviceUid) {
        this.setDeviceUid(deviceUid);
        return this;
    }

    public void setDeviceUid(String deviceUid) {
        this.deviceUid = deviceUid;
    }

    public String getKeyFingerprint() {
        return this.keyFingerprint;
    }

    public DeviceIdentity keyFingerprint(String keyFingerprint) {
        this.setKeyFingerprint(keyFingerprint);
        return this;
    }

    public void setKeyFingerprint(String keyFingerprint) {
        this.keyFingerprint = keyFingerprint;
    }

    public String getProtocolName() {
        return this.protocolName;
    }

    public DeviceIdentity protocolName(String protocolName) {
        this.setProtocolName(protocolName);
        return this;
    }

    public void setProtocolName(String protocolName) {
        this.protocolName = protocolName;
    }

    public DeviceTrustStatus getTrustStatus() {
        return this.trustStatus;
    }

    public DeviceIdentity trustStatus(DeviceTrustStatus trustStatus) {
        this.setTrustStatus(trustStatus);
        return this;
    }

    public void setTrustStatus(DeviceTrustStatus trustStatus) {
        this.trustStatus = trustStatus;
    }

    public Instant getKeyRotatedAt() {
        return this.keyRotatedAt;
    }

    public DeviceIdentity keyRotatedAt(Instant keyRotatedAt) {
        this.setKeyRotatedAt(keyRotatedAt);
        return this;
    }

    public void setKeyRotatedAt(Instant keyRotatedAt) {
        this.keyRotatedAt = keyRotatedAt;
    }

    public Instant getValidUntil() {
        return this.validUntil;
    }

    public DeviceIdentity validUntil(Instant validUntil) {
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

    public DeviceIdentity organization(Organization organization) {
        this.setOrganization(organization);
        return this;
    }

    public Gateway getGateway() {
        return this.gateway;
    }

    public void setGateway(Gateway gateway) {
        this.gateway = gateway;
        this.gatewayId = gateway != null ? gateway.getId() : null;
    }

    public DeviceIdentity gateway(Gateway gateway) {
        this.setGateway(gateway);
        return this;
    }

    public Equipment getEquipment() {
        return this.equipment;
    }

    public void setEquipment(Equipment equipment) {
        this.equipment = equipment;
        this.equipmentId = equipment != null ? equipment.getId() : null;
    }

    public DeviceIdentity equipment(Equipment equipment) {
        this.setEquipment(equipment);
        return this;
    }

    public Long getOrganizationId() {
        return this.organizationId;
    }

    public void setOrganizationId(Long organization) {
        this.organizationId = organization;
    }

    public Long getGatewayId() {
        return this.gatewayId;
    }

    public void setGatewayId(Long gateway) {
        this.gatewayId = gateway;
    }

    public Long getEquipmentId() {
        return this.equipmentId;
    }

    public void setEquipmentId(Long equipment) {
        this.equipmentId = equipment;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DeviceIdentity)) {
            return false;
        }
        return getId() != null && getId().equals(((DeviceIdentity) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "DeviceIdentity{" +
            "id=" + getId() +
            ", deviceUid='" + getDeviceUid() + "'" +
            ", keyFingerprint='" + getKeyFingerprint() + "'" +
            ", protocolName='" + getProtocolName() + "'" +
            ", trustStatus='" + getTrustStatus() + "'" +
            ", keyRotatedAt='" + getKeyRotatedAt() + "'" +
            ", validUntil='" + getValidUntil() + "'" +
            "}";
    }
}

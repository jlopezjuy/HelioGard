package ar.com.heliguard.app.domain;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * Tracks the latest boot_id and monotonic sequence_number per (gateway_id, device_identity_id).
 */
@Table("device_sequence_checkpoint")
public class DeviceSequenceCheckpoint implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @Column("organization_id")
    private Long organizationId;

    @Column("gateway_id")
    private Long gatewayId;

    @Column("device_identity_id")
    private Long deviceIdentityId;

    @Column("boot_id")
    private String bootId;

    @Column("last_sequence_number")
    private Long lastSequenceNumber;

    @Column("last_observed_at")
    private Instant lastObservedAt;

    @Column("updated_at")
    private Instant updatedAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }

    public Long getGatewayId() {
        return gatewayId;
    }

    public void setGatewayId(Long gatewayId) {
        this.gatewayId = gatewayId;
    }

    public Long getDeviceIdentityId() {
        return deviceIdentityId;
    }

    public void setDeviceIdentityId(Long deviceIdentityId) {
        this.deviceIdentityId = deviceIdentityId;
    }

    public String getBootId() {
        return bootId;
    }

    public void setBootId(String bootId) {
        this.bootId = bootId;
    }

    public Long getLastSequenceNumber() {
        return lastSequenceNumber;
    }

    public void setLastSequenceNumber(Long lastSequenceNumber) {
        this.lastSequenceNumber = lastSequenceNumber;
    }

    public Instant getLastObservedAt() {
        return lastObservedAt;
    }

    public void setLastObservedAt(Instant lastObservedAt) {
        this.lastObservedAt = lastObservedAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }
}

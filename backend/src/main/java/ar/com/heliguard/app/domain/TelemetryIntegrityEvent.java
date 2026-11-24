package ar.com.heliguard.app.domain;

import ar.com.heliguard.app.domain.enumeration.IntegrityEventStatus;
import ar.com.heliguard.app.domain.enumeration.IntegrityEventType;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A TelemetryIntegrityEvent.
 */
@Table("telemetry_integrity_event")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class TelemetryIntegrityEvent implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @NotNull
    @Size(max = 140)
    @Column("event_uid")
    private String eventUid;

    @NotNull
    @Column("event_type")
    private IntegrityEventType eventType;

    @NotNull
    @Column("status")
    private IntegrityEventStatus status;

    @NotNull
    @Column("observed_at")
    private Instant observedAt;

    @Column("source_observed_at")
    private Instant sourceObservedAt;

    @Size(max = 120)
    @Column("boot_id")
    private String bootId;

    @Column("sequence_number")
    private Long sequenceNumber;

    @Size(max = 64)
    @Column("payload_sha_256")
    private String payloadSha256;

    @Column("detail")
    private String detail;

    @org.springframework.data.annotation.Transient
    private Organization organization;

    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "organization", "site", "climateRiskProfile" }, allowSetters = true)
    private Installation installation;

    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "organization", "gateway", "equipment" }, allowSetters = true)
    private DeviceIdentity deviceIdentity;

    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "organization", "equipment", "metricDefinition" }, allowSetters = true)
    private TelemetryChannel telemetryChannel;

    @Column("organization_id")
    private Long organizationId;

    @Column("installation_id")
    private Long installationId;

    @Column("device_identity_id")
    private Long deviceIdentityId;

    @Column("telemetry_channel_id")
    private Long telemetryChannelId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public TelemetryIntegrityEvent id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEventUid() {
        return this.eventUid;
    }

    public TelemetryIntegrityEvent eventUid(String eventUid) {
        this.setEventUid(eventUid);
        return this;
    }

    public void setEventUid(String eventUid) {
        this.eventUid = eventUid;
    }

    public IntegrityEventType getEventType() {
        return this.eventType;
    }

    public TelemetryIntegrityEvent eventType(IntegrityEventType eventType) {
        this.setEventType(eventType);
        return this;
    }

    public void setEventType(IntegrityEventType eventType) {
        this.eventType = eventType;
    }

    public IntegrityEventStatus getStatus() {
        return this.status;
    }

    public TelemetryIntegrityEvent status(IntegrityEventStatus status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(IntegrityEventStatus status) {
        this.status = status;
    }

    public Instant getObservedAt() {
        return this.observedAt;
    }

    public TelemetryIntegrityEvent observedAt(Instant observedAt) {
        this.setObservedAt(observedAt);
        return this;
    }

    public void setObservedAt(Instant observedAt) {
        this.observedAt = observedAt;
    }

    public Instant getSourceObservedAt() {
        return this.sourceObservedAt;
    }

    public TelemetryIntegrityEvent sourceObservedAt(Instant sourceObservedAt) {
        this.setSourceObservedAt(sourceObservedAt);
        return this;
    }

    public void setSourceObservedAt(Instant sourceObservedAt) {
        this.sourceObservedAt = sourceObservedAt;
    }

    public String getBootId() {
        return this.bootId;
    }

    public TelemetryIntegrityEvent bootId(String bootId) {
        this.setBootId(bootId);
        return this;
    }

    public void setBootId(String bootId) {
        this.bootId = bootId;
    }

    public Long getSequenceNumber() {
        return this.sequenceNumber;
    }

    public TelemetryIntegrityEvent sequenceNumber(Long sequenceNumber) {
        this.setSequenceNumber(sequenceNumber);
        return this;
    }

    public void setSequenceNumber(Long sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }

    public String getPayloadSha256() {
        return this.payloadSha256;
    }

    public TelemetryIntegrityEvent payloadSha256(String payloadSha256) {
        this.setPayloadSha256(payloadSha256);
        return this;
    }

    public void setPayloadSha256(String payloadSha256) {
        this.payloadSha256 = payloadSha256;
    }

    public String getDetail() {
        return this.detail;
    }

    public TelemetryIntegrityEvent detail(String detail) {
        this.setDetail(detail);
        return this;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public Organization getOrganization() {
        return this.organization;
    }

    public void setOrganization(Organization organization) {
        this.organization = organization;
        this.organizationId = organization != null ? organization.getId() : null;
    }

    public TelemetryIntegrityEvent organization(Organization organization) {
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

    public TelemetryIntegrityEvent installation(Installation installation) {
        this.setInstallation(installation);
        return this;
    }

    public DeviceIdentity getDeviceIdentity() {
        return this.deviceIdentity;
    }

    public void setDeviceIdentity(DeviceIdentity deviceIdentity) {
        this.deviceIdentity = deviceIdentity;
        this.deviceIdentityId = deviceIdentity != null ? deviceIdentity.getId() : null;
    }

    public TelemetryIntegrityEvent deviceIdentity(DeviceIdentity deviceIdentity) {
        this.setDeviceIdentity(deviceIdentity);
        return this;
    }

    public TelemetryChannel getTelemetryChannel() {
        return this.telemetryChannel;
    }

    public void setTelemetryChannel(TelemetryChannel telemetryChannel) {
        this.telemetryChannel = telemetryChannel;
        this.telemetryChannelId = telemetryChannel != null ? telemetryChannel.getId() : null;
    }

    public TelemetryIntegrityEvent telemetryChannel(TelemetryChannel telemetryChannel) {
        this.setTelemetryChannel(telemetryChannel);
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

    public Long getDeviceIdentityId() {
        return this.deviceIdentityId;
    }

    public void setDeviceIdentityId(Long deviceIdentity) {
        this.deviceIdentityId = deviceIdentity;
    }

    public Long getTelemetryChannelId() {
        return this.telemetryChannelId;
    }

    public void setTelemetryChannelId(Long telemetryChannel) {
        this.telemetryChannelId = telemetryChannel;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TelemetryIntegrityEvent)) {
            return false;
        }
        return getId() != null && getId().equals(((TelemetryIntegrityEvent) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TelemetryIntegrityEvent{" +
            "id=" + getId() +
            ", eventUid='" + getEventUid() + "'" +
            ", eventType='" + getEventType() + "'" +
            ", status='" + getStatus() + "'" +
            ", observedAt='" + getObservedAt() + "'" +
            ", sourceObservedAt='" + getSourceObservedAt() + "'" +
            ", bootId='" + getBootId() + "'" +
            ", sequenceNumber=" + getSequenceNumber() +
            ", payloadSha256='" + getPayloadSha256() + "'" +
            ", detail='" + getDetail() + "'" +
            "}";
    }
}

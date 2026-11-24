package ar.com.heliguard.app.service.dto;

import ar.com.heliguard.app.domain.enumeration.IntegrityEventStatus;
import ar.com.heliguard.app.domain.enumeration.IntegrityEventType;
import jakarta.persistence.Lob;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link ar.com.heliguard.app.domain.TelemetryIntegrityEvent} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class TelemetryIntegrityEventDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(max = 140)
    private String eventUid;

    @NotNull
    private IntegrityEventType eventType;

    @NotNull
    private IntegrityEventStatus status;

    @NotNull
    private Instant observedAt;

    private Instant sourceObservedAt;

    @Size(max = 120)
    private String bootId;

    private Long sequenceNumber;

    @Size(max = 64)
    private String payloadSha256;

    @Lob
    private String detail;

    @NotNull
    private OrganizationDTO organization;

    @NotNull
    private InstallationDTO installation;

    private DeviceIdentityDTO deviceIdentity;

    private TelemetryChannelDTO telemetryChannel;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEventUid() {
        return eventUid;
    }

    public void setEventUid(String eventUid) {
        this.eventUid = eventUid;
    }

    public IntegrityEventType getEventType() {
        return eventType;
    }

    public void setEventType(IntegrityEventType eventType) {
        this.eventType = eventType;
    }

    public IntegrityEventStatus getStatus() {
        return status;
    }

    public void setStatus(IntegrityEventStatus status) {
        this.status = status;
    }

    public Instant getObservedAt() {
        return observedAt;
    }

    public void setObservedAt(Instant observedAt) {
        this.observedAt = observedAt;
    }

    public Instant getSourceObservedAt() {
        return sourceObservedAt;
    }

    public void setSourceObservedAt(Instant sourceObservedAt) {
        this.sourceObservedAt = sourceObservedAt;
    }

    public String getBootId() {
        return bootId;
    }

    public void setBootId(String bootId) {
        this.bootId = bootId;
    }

    public Long getSequenceNumber() {
        return sequenceNumber;
    }

    public void setSequenceNumber(Long sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }

    public String getPayloadSha256() {
        return payloadSha256;
    }

    public void setPayloadSha256(String payloadSha256) {
        this.payloadSha256 = payloadSha256;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
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

    public DeviceIdentityDTO getDeviceIdentity() {
        return deviceIdentity;
    }

    public void setDeviceIdentity(DeviceIdentityDTO deviceIdentity) {
        this.deviceIdentity = deviceIdentity;
    }

    public TelemetryChannelDTO getTelemetryChannel() {
        return telemetryChannel;
    }

    public void setTelemetryChannel(TelemetryChannelDTO telemetryChannel) {
        this.telemetryChannel = telemetryChannel;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TelemetryIntegrityEventDTO)) {
            return false;
        }

        TelemetryIntegrityEventDTO telemetryIntegrityEventDTO = (TelemetryIntegrityEventDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, telemetryIntegrityEventDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TelemetryIntegrityEventDTO{" +
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
            ", organization=" + getOrganization() +
            ", installation=" + getInstallation() +
            ", deviceIdentity=" + getDeviceIdentity() +
            ", telemetryChannel=" + getTelemetryChannel() +
            "}";
    }
}

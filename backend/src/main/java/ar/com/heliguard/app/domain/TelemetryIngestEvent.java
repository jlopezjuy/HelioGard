package ar.com.heliguard.app.domain;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * Persisted telemetry ingestion event with idempotency key (gateway_id, event_id) and payload SHA-256.
 */
@Table("telemetry_ingest_event")
public class TelemetryIngestEvent implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @Column("organization_id")
    private Long organizationId;

    @Column("installation_id")
    private Long installationId;

    @Column("gateway_id")
    private Long gatewayId;

    @Column("device_identity_id")
    private Long deviceIdentityId;

    @Column("telemetry_channel_id")
    private Long telemetryChannelId;

    @Column("event_id")
    private String eventId;

    @Column("boot_id")
    private String bootId;

    @Column("sequence_number")
    private Long sequenceNumber;

    @Column("metric_code")
    private String metricCode;

    @Column("unit_code")
    private String unitCode;

    @Column("numeric_value")
    private BigDecimal numericValue;

    @Column("observed_at")
    private Instant observedAt;

    @Column("received_at")
    private Instant receivedAt;

    @Column("payload_sha256")
    private String payloadSha256;

    @Column("ingest_status")
    private String ingestStatus;

    @Column("quality_flags")
    private String qualityFlags;

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

    public Long getInstallationId() {
        return installationId;
    }

    public void setInstallationId(Long installationId) {
        this.installationId = installationId;
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

    public Long getTelemetryChannelId() {
        return telemetryChannelId;
    }

    public void setTelemetryChannelId(Long telemetryChannelId) {
        this.telemetryChannelId = telemetryChannelId;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
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

    public String getMetricCode() {
        return metricCode;
    }

    public void setMetricCode(String metricCode) {
        this.metricCode = metricCode;
    }

    public String getUnitCode() {
        return unitCode;
    }

    public void setUnitCode(String unitCode) {
        this.unitCode = unitCode;
    }

    public BigDecimal getNumericValue() {
        return numericValue;
    }

    public void setNumericValue(BigDecimal numericValue) {
        this.numericValue = numericValue;
    }

    public Instant getObservedAt() {
        return observedAt;
    }

    public void setObservedAt(Instant observedAt) {
        this.observedAt = observedAt;
    }

    public Instant getReceivedAt() {
        return receivedAt;
    }

    public void setReceivedAt(Instant receivedAt) {
        this.receivedAt = receivedAt;
    }

    public String getPayloadSha256() {
        return payloadSha256;
    }

    public void setPayloadSha256(String payloadSha256) {
        this.payloadSha256 = payloadSha256;
    }

    public String getIngestStatus() {
        return ingestStatus;
    }

    public void setIngestStatus(String ingestStatus) {
        this.ingestStatus = ingestStatus;
    }

    public String getQualityFlags() {
        return qualityFlags;
    }

    public void setQualityFlags(String qualityFlags) {
        this.qualityFlags = qualityFlags;
    }
}

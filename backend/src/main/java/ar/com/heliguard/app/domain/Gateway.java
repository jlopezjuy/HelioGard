package ar.com.heliguard.app.domain;

import ar.com.heliguard.app.domain.enumeration.GatewayStatus;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A Gateway.
 */
@Table("gateway")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Gateway implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @NotNull
    @Size(max = 120)
    @Column("hardware_id")
    private String hardwareId;

    @Size(max = 80)
    @Column("firmware_version")
    private String firmwareVersion;

    @NotNull
    @Column("status")
    private GatewayStatus status;

    @Column("last_seen_at")
    private Instant lastSeenAt;

    @Column("provisioned_at")
    private Instant provisionedAt;

    @org.springframework.data.annotation.Transient
    private Organization organization;

    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "organization", "site", "climateRiskProfile" }, allowSetters = true)
    private Installation installation;

    @Column("organization_id")
    private Long organizationId;

    @Column("installation_id")
    private Long installationId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Gateway id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getHardwareId() {
        return this.hardwareId;
    }

    public Gateway hardwareId(String hardwareId) {
        this.setHardwareId(hardwareId);
        return this;
    }

    public void setHardwareId(String hardwareId) {
        this.hardwareId = hardwareId;
    }

    public String getFirmwareVersion() {
        return this.firmwareVersion;
    }

    public Gateway firmwareVersion(String firmwareVersion) {
        this.setFirmwareVersion(firmwareVersion);
        return this;
    }

    public void setFirmwareVersion(String firmwareVersion) {
        this.firmwareVersion = firmwareVersion;
    }

    public GatewayStatus getStatus() {
        return this.status;
    }

    public Gateway status(GatewayStatus status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(GatewayStatus status) {
        this.status = status;
    }

    public Instant getLastSeenAt() {
        return this.lastSeenAt;
    }

    public Gateway lastSeenAt(Instant lastSeenAt) {
        this.setLastSeenAt(lastSeenAt);
        return this;
    }

    public void setLastSeenAt(Instant lastSeenAt) {
        this.lastSeenAt = lastSeenAt;
    }

    public Instant getProvisionedAt() {
        return this.provisionedAt;
    }

    public Gateway provisionedAt(Instant provisionedAt) {
        this.setProvisionedAt(provisionedAt);
        return this;
    }

    public void setProvisionedAt(Instant provisionedAt) {
        this.provisionedAt = provisionedAt;
    }

    public Organization getOrganization() {
        return this.organization;
    }

    public void setOrganization(Organization organization) {
        this.organization = organization;
        this.organizationId = organization != null ? organization.getId() : null;
    }

    public Gateway organization(Organization organization) {
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

    public Gateway installation(Installation installation) {
        this.setInstallation(installation);
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

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Gateway)) {
            return false;
        }
        return getId() != null && getId().equals(((Gateway) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Gateway{" +
            "id=" + getId() +
            ", hardwareId='" + getHardwareId() + "'" +
            ", firmwareVersion='" + getFirmwareVersion() + "'" +
            ", status='" + getStatus() + "'" +
            ", lastSeenAt='" + getLastSeenAt() + "'" +
            ", provisionedAt='" + getProvisionedAt() + "'" +
            "}";
    }
}

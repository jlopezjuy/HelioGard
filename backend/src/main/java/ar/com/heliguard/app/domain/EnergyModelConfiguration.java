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
 * A EnergyModelConfiguration.
 */
@Table("energy_model_configuration")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class EnergyModelConfiguration implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @NotNull
    @Size(max = 80)
    @Column("algorithm_code")
    private String algorithmCode;

    @NotNull
    @Size(max = 60)
    @Column("config_version")
    private String configVersion;

    @NotNull
    @Size(max = 350)
    @Column("parameter_snapshot_ref")
    private String parameterSnapshotRef;

    @NotNull
    @Size(max = 100)
    @Column("input_fingerprint")
    private String inputFingerprint;

    @Column("calibrated_at")
    private Instant calibratedAt;

    @NotNull
    @Column("active_from")
    private Instant activeFrom;

    @Column("active_until")
    private Instant activeUntil;

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

    public EnergyModelConfiguration id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAlgorithmCode() {
        return this.algorithmCode;
    }

    public EnergyModelConfiguration algorithmCode(String algorithmCode) {
        this.setAlgorithmCode(algorithmCode);
        return this;
    }

    public void setAlgorithmCode(String algorithmCode) {
        this.algorithmCode = algorithmCode;
    }

    public String getConfigVersion() {
        return this.configVersion;
    }

    public EnergyModelConfiguration configVersion(String configVersion) {
        this.setConfigVersion(configVersion);
        return this;
    }

    public void setConfigVersion(String configVersion) {
        this.configVersion = configVersion;
    }

    public String getParameterSnapshotRef() {
        return this.parameterSnapshotRef;
    }

    public EnergyModelConfiguration parameterSnapshotRef(String parameterSnapshotRef) {
        this.setParameterSnapshotRef(parameterSnapshotRef);
        return this;
    }

    public void setParameterSnapshotRef(String parameterSnapshotRef) {
        this.parameterSnapshotRef = parameterSnapshotRef;
    }

    public String getInputFingerprint() {
        return this.inputFingerprint;
    }

    public EnergyModelConfiguration inputFingerprint(String inputFingerprint) {
        this.setInputFingerprint(inputFingerprint);
        return this;
    }

    public void setInputFingerprint(String inputFingerprint) {
        this.inputFingerprint = inputFingerprint;
    }

    public Instant getCalibratedAt() {
        return this.calibratedAt;
    }

    public EnergyModelConfiguration calibratedAt(Instant calibratedAt) {
        this.setCalibratedAt(calibratedAt);
        return this;
    }

    public void setCalibratedAt(Instant calibratedAt) {
        this.calibratedAt = calibratedAt;
    }

    public Instant getActiveFrom() {
        return this.activeFrom;
    }

    public EnergyModelConfiguration activeFrom(Instant activeFrom) {
        this.setActiveFrom(activeFrom);
        return this;
    }

    public void setActiveFrom(Instant activeFrom) {
        this.activeFrom = activeFrom;
    }

    public Instant getActiveUntil() {
        return this.activeUntil;
    }

    public EnergyModelConfiguration activeUntil(Instant activeUntil) {
        this.setActiveUntil(activeUntil);
        return this;
    }

    public void setActiveUntil(Instant activeUntil) {
        this.activeUntil = activeUntil;
    }

    public Organization getOrganization() {
        return this.organization;
    }

    public void setOrganization(Organization organization) {
        this.organization = organization;
        this.organizationId = organization != null ? organization.getId() : null;
    }

    public EnergyModelConfiguration organization(Organization organization) {
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

    public EnergyModelConfiguration installation(Installation installation) {
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
        if (!(o instanceof EnergyModelConfiguration)) {
            return false;
        }
        return getId() != null && getId().equals(((EnergyModelConfiguration) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "EnergyModelConfiguration{" +
            "id=" + getId() +
            ", algorithmCode='" + getAlgorithmCode() + "'" +
            ", configVersion='" + getConfigVersion() + "'" +
            ", parameterSnapshotRef='" + getParameterSnapshotRef() + "'" +
            ", inputFingerprint='" + getInputFingerprint() + "'" +
            ", calibratedAt='" + getCalibratedAt() + "'" +
            ", activeFrom='" + getActiveFrom() + "'" +
            ", activeUntil='" + getActiveUntil() + "'" +
            "}";
    }
}

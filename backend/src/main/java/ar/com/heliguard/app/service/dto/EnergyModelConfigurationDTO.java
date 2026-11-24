package ar.com.heliguard.app.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link ar.com.heliguard.app.domain.EnergyModelConfiguration} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class EnergyModelConfigurationDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(max = 80)
    private String algorithmCode;

    @NotNull
    @Size(max = 60)
    private String configVersion;

    @NotNull
    @Size(max = 350)
    private String parameterSnapshotRef;

    @NotNull
    @Size(max = 100)
    private String inputFingerprint;

    private Instant calibratedAt;

    @NotNull
    private Instant activeFrom;

    private Instant activeUntil;

    private OrganizationDTO organization;

    @NotNull
    private InstallationDTO installation;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAlgorithmCode() {
        return algorithmCode;
    }

    public void setAlgorithmCode(String algorithmCode) {
        this.algorithmCode = algorithmCode;
    }

    public String getConfigVersion() {
        return configVersion;
    }

    public void setConfigVersion(String configVersion) {
        this.configVersion = configVersion;
    }

    public String getParameterSnapshotRef() {
        return parameterSnapshotRef;
    }

    public void setParameterSnapshotRef(String parameterSnapshotRef) {
        this.parameterSnapshotRef = parameterSnapshotRef;
    }

    public String getInputFingerprint() {
        return inputFingerprint;
    }

    public void setInputFingerprint(String inputFingerprint) {
        this.inputFingerprint = inputFingerprint;
    }

    public Instant getCalibratedAt() {
        return calibratedAt;
    }

    public void setCalibratedAt(Instant calibratedAt) {
        this.calibratedAt = calibratedAt;
    }

    public Instant getActiveFrom() {
        return activeFrom;
    }

    public void setActiveFrom(Instant activeFrom) {
        this.activeFrom = activeFrom;
    }

    public Instant getActiveUntil() {
        return activeUntil;
    }

    public void setActiveUntil(Instant activeUntil) {
        this.activeUntil = activeUntil;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof EnergyModelConfigurationDTO)) {
            return false;
        }

        EnergyModelConfigurationDTO energyModelConfigurationDTO = (EnergyModelConfigurationDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, energyModelConfigurationDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "EnergyModelConfigurationDTO{" +
            "id=" + getId() +
            ", algorithmCode='" + getAlgorithmCode() + "'" +
            ", configVersion='" + getConfigVersion() + "'" +
            ", parameterSnapshotRef='" + getParameterSnapshotRef() + "'" +
            ", inputFingerprint='" + getInputFingerprint() + "'" +
            ", calibratedAt='" + getCalibratedAt() + "'" +
            ", activeFrom='" + getActiveFrom() + "'" +
            ", activeUntil='" + getActiveUntil() + "'" +
            ", organization=" + getOrganization() +
            ", installation=" + getInstallation() +
            "}";
    }
}

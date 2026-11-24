package ar.com.heliguard.app.service.dto;

import ar.com.heliguard.app.domain.enumeration.DataTrustLevel;
import jakarta.persistence.Lob;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link ar.com.heliguard.app.domain.IntegrityAssessment} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class IntegrityAssessmentDTO implements Serializable {

    private Long id;

    @NotNull
    private Instant assessedAt;

    @NotNull
    private DataTrustLevel trustLevel;

    @NotNull
    @Size(max = 80)
    private String ruleVersion;

    @Lob
    private String reason;

    @NotNull
    private OrganizationDTO organization;

    @NotNull
    private InstallationDTO installation;

    @NotNull
    private TelemetryIntegrityEventDTO integrityEvent;

    private EnergyModelRunDTO modelRun;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instant getAssessedAt() {
        return assessedAt;
    }

    public void setAssessedAt(Instant assessedAt) {
        this.assessedAt = assessedAt;
    }

    public DataTrustLevel getTrustLevel() {
        return trustLevel;
    }

    public void setTrustLevel(DataTrustLevel trustLevel) {
        this.trustLevel = trustLevel;
    }

    public String getRuleVersion() {
        return ruleVersion;
    }

    public void setRuleVersion(String ruleVersion) {
        this.ruleVersion = ruleVersion;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
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

    public TelemetryIntegrityEventDTO getIntegrityEvent() {
        return integrityEvent;
    }

    public void setIntegrityEvent(TelemetryIntegrityEventDTO integrityEvent) {
        this.integrityEvent = integrityEvent;
    }

    public EnergyModelRunDTO getModelRun() {
        return modelRun;
    }

    public void setModelRun(EnergyModelRunDTO modelRun) {
        this.modelRun = modelRun;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof IntegrityAssessmentDTO)) {
            return false;
        }

        IntegrityAssessmentDTO integrityAssessmentDTO = (IntegrityAssessmentDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, integrityAssessmentDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "IntegrityAssessmentDTO{" +
            "id=" + getId() +
            ", assessedAt='" + getAssessedAt() + "'" +
            ", trustLevel='" + getTrustLevel() + "'" +
            ", ruleVersion='" + getRuleVersion() + "'" +
            ", reason='" + getReason() + "'" +
            ", organization=" + getOrganization() +
            ", installation=" + getInstallation() +
            ", integrityEvent=" + getIntegrityEvent() +
            ", modelRun=" + getModelRun() +
            "}";
    }
}

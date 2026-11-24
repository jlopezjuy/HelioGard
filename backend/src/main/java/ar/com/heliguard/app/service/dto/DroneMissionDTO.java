package ar.com.heliguard.app.service.dto;

import ar.com.heliguard.app.domain.enumeration.DroneMissionKind;
import ar.com.heliguard.app.domain.enumeration.DroneMissionStatus;
import jakarta.persistence.Lob;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link ar.com.heliguard.app.domain.DroneMission} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class DroneMissionDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(max = 100)
    private String missionCode;

    @NotNull
    private DroneMissionKind kind;

    @NotNull
    private DroneMissionStatus status;

    @NotNull
    private Instant proposedAt;

    private Instant startedAt;

    private Instant completedAt;

    @Lob
    private String safetyChecklistSummary;

    @NotNull
    private OrganizationDTO organization;

    @NotNull
    private InstallationDTO installation;

    private WorkOrderDTO workOrder;

    private DroneAssetDTO droneAsset;

    private OrganizationDTO providerOrganization;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMissionCode() {
        return missionCode;
    }

    public void setMissionCode(String missionCode) {
        this.missionCode = missionCode;
    }

    public DroneMissionKind getKind() {
        return kind;
    }

    public void setKind(DroneMissionKind kind) {
        this.kind = kind;
    }

    public DroneMissionStatus getStatus() {
        return status;
    }

    public void setStatus(DroneMissionStatus status) {
        this.status = status;
    }

    public Instant getProposedAt() {
        return proposedAt;
    }

    public void setProposedAt(Instant proposedAt) {
        this.proposedAt = proposedAt;
    }

    public Instant getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(Instant startedAt) {
        this.startedAt = startedAt;
    }

    public Instant getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(Instant completedAt) {
        this.completedAt = completedAt;
    }

    public String getSafetyChecklistSummary() {
        return safetyChecklistSummary;
    }

    public void setSafetyChecklistSummary(String safetyChecklistSummary) {
        this.safetyChecklistSummary = safetyChecklistSummary;
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

    public WorkOrderDTO getWorkOrder() {
        return workOrder;
    }

    public void setWorkOrder(WorkOrderDTO workOrder) {
        this.workOrder = workOrder;
    }

    public DroneAssetDTO getDroneAsset() {
        return droneAsset;
    }

    public void setDroneAsset(DroneAssetDTO droneAsset) {
        this.droneAsset = droneAsset;
    }

    public OrganizationDTO getProviderOrganization() {
        return providerOrganization;
    }

    public void setProviderOrganization(OrganizationDTO providerOrganization) {
        this.providerOrganization = providerOrganization;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DroneMissionDTO)) {
            return false;
        }

        DroneMissionDTO droneMissionDTO = (DroneMissionDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, droneMissionDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "DroneMissionDTO{" +
            "id=" + getId() +
            ", missionCode='" + getMissionCode() + "'" +
            ", kind='" + getKind() + "'" +
            ", status='" + getStatus() + "'" +
            ", proposedAt='" + getProposedAt() + "'" +
            ", startedAt='" + getStartedAt() + "'" +
            ", completedAt='" + getCompletedAt() + "'" +
            ", safetyChecklistSummary='" + getSafetyChecklistSummary() + "'" +
            ", organization=" + getOrganization() +
            ", installation=" + getInstallation() +
            ", workOrder=" + getWorkOrder() +
            ", droneAsset=" + getDroneAsset() +
            ", providerOrganization=" + getProviderOrganization() +
            "}";
    }
}

package ar.com.heliguard.app.domain;

import ar.com.heliguard.app.domain.enumeration.DroneMissionKind;
import ar.com.heliguard.app.domain.enumeration.DroneMissionStatus;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A DroneMission.
 */
@Table("drone_mission")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class DroneMission implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @NotNull
    @Size(max = 100)
    @Column("mission_code")
    private String missionCode;

    @NotNull
    @Column("kind")
    private DroneMissionKind kind;

    @NotNull
    @Column("status")
    private DroneMissionStatus status;

    @NotNull
    @Column("proposed_at")
    private Instant proposedAt;

    @Column("started_at")
    private Instant startedAt;

    @Column("completed_at")
    private Instant completedAt;

    @Column("safety_checklist_summary")
    private String safetyChecklistSummary;

    @org.springframework.data.annotation.Transient
    private Organization organization;

    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "organization", "site", "climateRiskProfile" }, allowSetters = true)
    private Installation installation;

    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "organization", "installation", "incident", "subscription" }, allowSetters = true)
    private WorkOrder workOrder;

    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "organization" }, allowSetters = true)
    private DroneAsset droneAsset;

    @org.springframework.data.annotation.Transient
    private Organization providerOrganization;

    @Column("organization_id")
    private Long organizationId;

    @Column("installation_id")
    private Long installationId;

    @Column("work_order_id")
    private Long workOrderId;

    @Column("drone_asset_id")
    private Long droneAssetId;

    @Column("provider_organization_id")
    private Long providerOrganizationId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public DroneMission id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMissionCode() {
        return this.missionCode;
    }

    public DroneMission missionCode(String missionCode) {
        this.setMissionCode(missionCode);
        return this;
    }

    public void setMissionCode(String missionCode) {
        this.missionCode = missionCode;
    }

    public DroneMissionKind getKind() {
        return this.kind;
    }

    public DroneMission kind(DroneMissionKind kind) {
        this.setKind(kind);
        return this;
    }

    public void setKind(DroneMissionKind kind) {
        this.kind = kind;
    }

    public DroneMissionStatus getStatus() {
        return this.status;
    }

    public DroneMission status(DroneMissionStatus status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(DroneMissionStatus status) {
        this.status = status;
    }

    public Instant getProposedAt() {
        return this.proposedAt;
    }

    public DroneMission proposedAt(Instant proposedAt) {
        this.setProposedAt(proposedAt);
        return this;
    }

    public void setProposedAt(Instant proposedAt) {
        this.proposedAt = proposedAt;
    }

    public Instant getStartedAt() {
        return this.startedAt;
    }

    public DroneMission startedAt(Instant startedAt) {
        this.setStartedAt(startedAt);
        return this;
    }

    public void setStartedAt(Instant startedAt) {
        this.startedAt = startedAt;
    }

    public Instant getCompletedAt() {
        return this.completedAt;
    }

    public DroneMission completedAt(Instant completedAt) {
        this.setCompletedAt(completedAt);
        return this;
    }

    public void setCompletedAt(Instant completedAt) {
        this.completedAt = completedAt;
    }

    public String getSafetyChecklistSummary() {
        return this.safetyChecklistSummary;
    }

    public DroneMission safetyChecklistSummary(String safetyChecklistSummary) {
        this.setSafetyChecklistSummary(safetyChecklistSummary);
        return this;
    }

    public void setSafetyChecklistSummary(String safetyChecklistSummary) {
        this.safetyChecklistSummary = safetyChecklistSummary;
    }

    public Organization getOrganization() {
        return this.organization;
    }

    public void setOrganization(Organization organization) {
        this.organization = organization;
        this.organizationId = organization != null ? organization.getId() : null;
    }

    public DroneMission organization(Organization organization) {
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

    public DroneMission installation(Installation installation) {
        this.setInstallation(installation);
        return this;
    }

    public WorkOrder getWorkOrder() {
        return this.workOrder;
    }

    public void setWorkOrder(WorkOrder workOrder) {
        this.workOrder = workOrder;
        this.workOrderId = workOrder != null ? workOrder.getId() : null;
    }

    public DroneMission workOrder(WorkOrder workOrder) {
        this.setWorkOrder(workOrder);
        return this;
    }

    public DroneAsset getDroneAsset() {
        return this.droneAsset;
    }

    public void setDroneAsset(DroneAsset droneAsset) {
        this.droneAsset = droneAsset;
        this.droneAssetId = droneAsset != null ? droneAsset.getId() : null;
    }

    public DroneMission droneAsset(DroneAsset droneAsset) {
        this.setDroneAsset(droneAsset);
        return this;
    }

    public Organization getProviderOrganization() {
        return this.providerOrganization;
    }

    public void setProviderOrganization(Organization organization) {
        this.providerOrganization = organization;
        this.providerOrganizationId = organization != null ? organization.getId() : null;
    }

    public DroneMission providerOrganization(Organization organization) {
        this.setProviderOrganization(organization);
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

    public Long getWorkOrderId() {
        return this.workOrderId;
    }

    public void setWorkOrderId(Long workOrder) {
        this.workOrderId = workOrder;
    }

    public Long getDroneAssetId() {
        return this.droneAssetId;
    }

    public void setDroneAssetId(Long droneAsset) {
        this.droneAssetId = droneAsset;
    }

    public Long getProviderOrganizationId() {
        return this.providerOrganizationId;
    }

    public void setProviderOrganizationId(Long organization) {
        this.providerOrganizationId = organization;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DroneMission)) {
            return false;
        }
        return getId() != null && getId().equals(((DroneMission) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "DroneMission{" +
            "id=" + getId() +
            ", missionCode='" + getMissionCode() + "'" +
            ", kind='" + getKind() + "'" +
            ", status='" + getStatus() + "'" +
            ", proposedAt='" + getProposedAt() + "'" +
            ", startedAt='" + getStartedAt() + "'" +
            ", completedAt='" + getCompletedAt() + "'" +
            ", safetyChecklistSummary='" + getSafetyChecklistSummary() + "'" +
            "}";
    }
}

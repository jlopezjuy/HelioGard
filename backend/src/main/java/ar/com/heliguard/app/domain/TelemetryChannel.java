package ar.com.heliguard.app.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A TelemetryChannel.
 */
@Table("telemetry_channel")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class TelemetryChannel implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @NotNull
    @Size(max = 100)
    @Column("external_channel_code")
    private String externalChannelCode;

    @NotNull
    @Column("enabled")
    private Boolean enabled;

    @Min(value = 1)
    @Column("sample_period_seconds")
    private Integer samplePeriodSeconds;

    @org.springframework.data.annotation.Transient
    private Organization organization;

    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "organization", "installation", "equipmentModel", "solarString" }, allowSetters = true)
    private Equipment equipment;

    @org.springframework.data.annotation.Transient
    private MetricDefinition metricDefinition;

    @Column("organization_id")
    private Long organizationId;

    @Column("equipment_id")
    private Long equipmentId;

    @Column("metric_definition_id")
    private Long metricDefinitionId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public TelemetryChannel id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getExternalChannelCode() {
        return this.externalChannelCode;
    }

    public TelemetryChannel externalChannelCode(String externalChannelCode) {
        this.setExternalChannelCode(externalChannelCode);
        return this;
    }

    public void setExternalChannelCode(String externalChannelCode) {
        this.externalChannelCode = externalChannelCode;
    }

    public Boolean getEnabled() {
        return this.enabled;
    }

    public TelemetryChannel enabled(Boolean enabled) {
        this.setEnabled(enabled);
        return this;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public Integer getSamplePeriodSeconds() {
        return this.samplePeriodSeconds;
    }

    public TelemetryChannel samplePeriodSeconds(Integer samplePeriodSeconds) {
        this.setSamplePeriodSeconds(samplePeriodSeconds);
        return this;
    }

    public void setSamplePeriodSeconds(Integer samplePeriodSeconds) {
        this.samplePeriodSeconds = samplePeriodSeconds;
    }

    public Organization getOrganization() {
        return this.organization;
    }

    public void setOrganization(Organization organization) {
        this.organization = organization;
        this.organizationId = organization != null ? organization.getId() : null;
    }

    public TelemetryChannel organization(Organization organization) {
        this.setOrganization(organization);
        return this;
    }

    public Equipment getEquipment() {
        return this.equipment;
    }

    public void setEquipment(Equipment equipment) {
        this.equipment = equipment;
        this.equipmentId = equipment != null ? equipment.getId() : null;
    }

    public TelemetryChannel equipment(Equipment equipment) {
        this.setEquipment(equipment);
        return this;
    }

    public MetricDefinition getMetricDefinition() {
        return this.metricDefinition;
    }

    public void setMetricDefinition(MetricDefinition metricDefinition) {
        this.metricDefinition = metricDefinition;
        this.metricDefinitionId = metricDefinition != null ? metricDefinition.getId() : null;
    }

    public TelemetryChannel metricDefinition(MetricDefinition metricDefinition) {
        this.setMetricDefinition(metricDefinition);
        return this;
    }

    public Long getOrganizationId() {
        return this.organizationId;
    }

    public void setOrganizationId(Long organization) {
        this.organizationId = organization;
    }

    public Long getEquipmentId() {
        return this.equipmentId;
    }

    public void setEquipmentId(Long equipment) {
        this.equipmentId = equipment;
    }

    public Long getMetricDefinitionId() {
        return this.metricDefinitionId;
    }

    public void setMetricDefinitionId(Long metricDefinition) {
        this.metricDefinitionId = metricDefinition;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TelemetryChannel)) {
            return false;
        }
        return getId() != null && getId().equals(((TelemetryChannel) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TelemetryChannel{" +
            "id=" + getId() +
            ", externalChannelCode='" + getExternalChannelCode() + "'" +
            ", enabled='" + getEnabled() + "'" +
            ", samplePeriodSeconds=" + getSamplePeriodSeconds() +
            "}";
    }
}

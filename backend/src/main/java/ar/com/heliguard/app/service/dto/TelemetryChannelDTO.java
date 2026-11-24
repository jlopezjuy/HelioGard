package ar.com.heliguard.app.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link ar.com.heliguard.app.domain.TelemetryChannel} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class TelemetryChannelDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(max = 100)
    private String externalChannelCode;

    @NotNull
    private Boolean enabled;

    @Min(value = 1)
    private Integer samplePeriodSeconds;

    @NotNull
    private OrganizationDTO organization;

    @NotNull
    private EquipmentDTO equipment;

    @NotNull
    private MetricDefinitionDTO metricDefinition;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getExternalChannelCode() {
        return externalChannelCode;
    }

    public void setExternalChannelCode(String externalChannelCode) {
        this.externalChannelCode = externalChannelCode;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public Integer getSamplePeriodSeconds() {
        return samplePeriodSeconds;
    }

    public void setSamplePeriodSeconds(Integer samplePeriodSeconds) {
        this.samplePeriodSeconds = samplePeriodSeconds;
    }

    public OrganizationDTO getOrganization() {
        return organization;
    }

    public void setOrganization(OrganizationDTO organization) {
        this.organization = organization;
    }

    public EquipmentDTO getEquipment() {
        return equipment;
    }

    public void setEquipment(EquipmentDTO equipment) {
        this.equipment = equipment;
    }

    public MetricDefinitionDTO getMetricDefinition() {
        return metricDefinition;
    }

    public void setMetricDefinition(MetricDefinitionDTO metricDefinition) {
        this.metricDefinition = metricDefinition;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TelemetryChannelDTO)) {
            return false;
        }

        TelemetryChannelDTO telemetryChannelDTO = (TelemetryChannelDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, telemetryChannelDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TelemetryChannelDTO{" +
            "id=" + getId() +
            ", externalChannelCode='" + getExternalChannelCode() + "'" +
            ", enabled='" + getEnabled() + "'" +
            ", samplePeriodSeconds=" + getSamplePeriodSeconds() +
            ", organization=" + getOrganization() +
            ", equipment=" + getEquipment() +
            ", metricDefinition=" + getMetricDefinition() +
            "}";
    }
}

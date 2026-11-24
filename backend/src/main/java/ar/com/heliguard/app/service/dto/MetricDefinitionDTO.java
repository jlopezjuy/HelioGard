package ar.com.heliguard.app.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link ar.com.heliguard.app.domain.MetricDefinition} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class MetricDefinitionDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(max = 100)
    private String code;

    @NotNull
    @Size(max = 24)
    private String unitCode;

    @Size(max = 250)
    private String description;

    @NotNull
    private Boolean counter;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getUnitCode() {
        return unitCode;
    }

    public void setUnitCode(String unitCode) {
        this.unitCode = unitCode;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getCounter() {
        return counter;
    }

    public void setCounter(Boolean counter) {
        this.counter = counter;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof MetricDefinitionDTO)) {
            return false;
        }

        MetricDefinitionDTO metricDefinitionDTO = (MetricDefinitionDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, metricDefinitionDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "MetricDefinitionDTO{" +
            "id=" + getId() +
            ", code='" + getCode() + "'" +
            ", unitCode='" + getUnitCode() + "'" +
            ", description='" + getDescription() + "'" +
            ", counter='" + getCounter() + "'" +
            "}";
    }
}

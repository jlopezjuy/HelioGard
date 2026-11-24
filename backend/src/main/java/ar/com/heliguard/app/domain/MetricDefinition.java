package ar.com.heliguard.app.domain;

import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A MetricDefinition.
 */
@Table("metric_definition")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class MetricDefinition implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @NotNull
    @Size(max = 100)
    @Column("code")
    private String code;

    @NotNull
    @Size(max = 24)
    @Column("unit_code")
    private String unitCode;

    @Size(max = 250)
    @Column("description")
    private String description;

    @NotNull
    @Column("counter")
    private Boolean counter;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public MetricDefinition id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return this.code;
    }

    public MetricDefinition code(String code) {
        this.setCode(code);
        return this;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getUnitCode() {
        return this.unitCode;
    }

    public MetricDefinition unitCode(String unitCode) {
        this.setUnitCode(unitCode);
        return this;
    }

    public void setUnitCode(String unitCode) {
        this.unitCode = unitCode;
    }

    public String getDescription() {
        return this.description;
    }

    public MetricDefinition description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getCounter() {
        return this.counter;
    }

    public MetricDefinition counter(Boolean counter) {
        this.setCounter(counter);
        return this;
    }

    public void setCounter(Boolean counter) {
        this.counter = counter;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof MetricDefinition)) {
            return false;
        }
        return getId() != null && getId().equals(((MetricDefinition) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "MetricDefinition{" +
            "id=" + getId() +
            ", code='" + getCode() + "'" +
            ", unitCode='" + getUnitCode() + "'" +
            ", description='" + getDescription() + "'" +
            ", counter='" + getCounter() + "'" +
            "}";
    }
}

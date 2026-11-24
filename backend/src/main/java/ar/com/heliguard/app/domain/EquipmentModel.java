package ar.com.heliguard.app.domain;

import ar.com.heliguard.app.domain.enumeration.EquipmentKind;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A EquipmentModel.
 */
@Table("equipment_model")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class EquipmentModel implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @NotNull
    @Size(max = 120)
    @Column("manufacturer")
    private String manufacturer;

    @NotNull
    @Size(max = 120)
    @Column("model_code")
    private String modelCode;

    @Size(max = 60)
    @Column("revision")
    private String revision;

    @NotNull
    @Column("kind")
    private EquipmentKind kind;

    @Column("nominal_power_w")
    private BigDecimal nominalPowerW;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public EquipmentModel id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getManufacturer() {
        return this.manufacturer;
    }

    public EquipmentModel manufacturer(String manufacturer) {
        this.setManufacturer(manufacturer);
        return this;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public String getModelCode() {
        return this.modelCode;
    }

    public EquipmentModel modelCode(String modelCode) {
        this.setModelCode(modelCode);
        return this;
    }

    public void setModelCode(String modelCode) {
        this.modelCode = modelCode;
    }

    public String getRevision() {
        return this.revision;
    }

    public EquipmentModel revision(String revision) {
        this.setRevision(revision);
        return this;
    }

    public void setRevision(String revision) {
        this.revision = revision;
    }

    public EquipmentKind getKind() {
        return this.kind;
    }

    public EquipmentModel kind(EquipmentKind kind) {
        this.setKind(kind);
        return this;
    }

    public void setKind(EquipmentKind kind) {
        this.kind = kind;
    }

    public BigDecimal getNominalPowerW() {
        return this.nominalPowerW;
    }

    public EquipmentModel nominalPowerW(BigDecimal nominalPowerW) {
        this.setNominalPowerW(nominalPowerW);
        return this;
    }

    public void setNominalPowerW(BigDecimal nominalPowerW) {
        this.nominalPowerW = nominalPowerW;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof EquipmentModel)) {
            return false;
        }
        return getId() != null && getId().equals(((EquipmentModel) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "EquipmentModel{" +
            "id=" + getId() +
            ", manufacturer='" + getManufacturer() + "'" +
            ", modelCode='" + getModelCode() + "'" +
            ", revision='" + getRevision() + "'" +
            ", kind='" + getKind() + "'" +
            ", nominalPowerW=" + getNominalPowerW() +
            "}";
    }
}

package ar.com.heliguard.app.service.dto;

import ar.com.heliguard.app.domain.enumeration.EquipmentKind;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

/**
 * A DTO for the {@link ar.com.heliguard.app.domain.EquipmentModel} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class EquipmentModelDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(max = 120)
    private String manufacturer;

    @NotNull
    @Size(max = 120)
    private String modelCode;

    @Size(max = 60)
    private String revision;

    @NotNull
    private EquipmentKind kind;

    private BigDecimal nominalPowerW;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public String getModelCode() {
        return modelCode;
    }

    public void setModelCode(String modelCode) {
        this.modelCode = modelCode;
    }

    public String getRevision() {
        return revision;
    }

    public void setRevision(String revision) {
        this.revision = revision;
    }

    public EquipmentKind getKind() {
        return kind;
    }

    public void setKind(EquipmentKind kind) {
        this.kind = kind;
    }

    public BigDecimal getNominalPowerW() {
        return nominalPowerW;
    }

    public void setNominalPowerW(BigDecimal nominalPowerW) {
        this.nominalPowerW = nominalPowerW;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof EquipmentModelDTO)) {
            return false;
        }

        EquipmentModelDTO equipmentModelDTO = (EquipmentModelDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, equipmentModelDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "EquipmentModelDTO{" +
            "id=" + getId() +
            ", manufacturer='" + getManufacturer() + "'" +
            ", modelCode='" + getModelCode() + "'" +
            ", revision='" + getRevision() + "'" +
            ", kind='" + getKind() + "'" +
            ", nominalPowerW=" + getNominalPowerW() +
            "}";
    }
}

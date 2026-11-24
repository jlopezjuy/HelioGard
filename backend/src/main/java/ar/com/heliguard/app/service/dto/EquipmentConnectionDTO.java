package ar.com.heliguard.app.service.dto;

import ar.com.heliguard.app.domain.enumeration.TopologyConnectionKind;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link ar.com.heliguard.app.domain.EquipmentConnection} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class EquipmentConnectionDTO implements Serializable {

    private Long id;

    @NotNull
    private TopologyConnectionKind kind;

    @NotNull
    @Size(max = 100)
    private String connectionCode;

    @NotNull
    private Instant validFrom;

    private Instant validUntil;

    @NotNull
    private OrganizationDTO organization;

    @NotNull
    private EquipmentDTO fromEquipment;

    @NotNull
    private EquipmentDTO toEquipment;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TopologyConnectionKind getKind() {
        return kind;
    }

    public void setKind(TopologyConnectionKind kind) {
        this.kind = kind;
    }

    public String getConnectionCode() {
        return connectionCode;
    }

    public void setConnectionCode(String connectionCode) {
        this.connectionCode = connectionCode;
    }

    public Instant getValidFrom() {
        return validFrom;
    }

    public void setValidFrom(Instant validFrom) {
        this.validFrom = validFrom;
    }

    public Instant getValidUntil() {
        return validUntil;
    }

    public void setValidUntil(Instant validUntil) {
        this.validUntil = validUntil;
    }

    public OrganizationDTO getOrganization() {
        return organization;
    }

    public void setOrganization(OrganizationDTO organization) {
        this.organization = organization;
    }

    public EquipmentDTO getFromEquipment() {
        return fromEquipment;
    }

    public void setFromEquipment(EquipmentDTO fromEquipment) {
        this.fromEquipment = fromEquipment;
    }

    public EquipmentDTO getToEquipment() {
        return toEquipment;
    }

    public void setToEquipment(EquipmentDTO toEquipment) {
        this.toEquipment = toEquipment;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof EquipmentConnectionDTO)) {
            return false;
        }

        EquipmentConnectionDTO equipmentConnectionDTO = (EquipmentConnectionDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, equipmentConnectionDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "EquipmentConnectionDTO{" +
            "id=" + getId() +
            ", kind='" + getKind() + "'" +
            ", connectionCode='" + getConnectionCode() + "'" +
            ", validFrom='" + getValidFrom() + "'" +
            ", validUntil='" + getValidUntil() + "'" +
            ", organization=" + getOrganization() +
            ", fromEquipment=" + getFromEquipment() +
            ", toEquipment=" + getToEquipment() +
            "}";
    }
}

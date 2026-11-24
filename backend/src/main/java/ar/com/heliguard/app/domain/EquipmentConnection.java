package ar.com.heliguard.app.domain;

import ar.com.heliguard.app.domain.enumeration.TopologyConnectionKind;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A EquipmentConnection.
 */
@Table("equipment_connection")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class EquipmentConnection implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @NotNull
    @Column("kind")
    private TopologyConnectionKind kind;

    @NotNull
    @Size(max = 100)
    @Column("connection_code")
    private String connectionCode;

    @NotNull
    @Column("valid_from")
    private Instant validFrom;

    @Column("valid_until")
    private Instant validUntil;

    @org.springframework.data.annotation.Transient
    private Organization organization;

    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "organization", "installation", "equipmentModel", "solarString" }, allowSetters = true)
    private Equipment fromEquipment;

    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "organization", "installation", "equipmentModel", "solarString" }, allowSetters = true)
    private Equipment toEquipment;

    @Column("organization_id")
    private Long organizationId;

    @Column("from_equipment_id")
    private Long fromEquipmentId;

    @Column("to_equipment_id")
    private Long toEquipmentId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public EquipmentConnection id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TopologyConnectionKind getKind() {
        return this.kind;
    }

    public EquipmentConnection kind(TopologyConnectionKind kind) {
        this.setKind(kind);
        return this;
    }

    public void setKind(TopologyConnectionKind kind) {
        this.kind = kind;
    }

    public String getConnectionCode() {
        return this.connectionCode;
    }

    public EquipmentConnection connectionCode(String connectionCode) {
        this.setConnectionCode(connectionCode);
        return this;
    }

    public void setConnectionCode(String connectionCode) {
        this.connectionCode = connectionCode;
    }

    public Instant getValidFrom() {
        return this.validFrom;
    }

    public EquipmentConnection validFrom(Instant validFrom) {
        this.setValidFrom(validFrom);
        return this;
    }

    public void setValidFrom(Instant validFrom) {
        this.validFrom = validFrom;
    }

    public Instant getValidUntil() {
        return this.validUntil;
    }

    public EquipmentConnection validUntil(Instant validUntil) {
        this.setValidUntil(validUntil);
        return this;
    }

    public void setValidUntil(Instant validUntil) {
        this.validUntil = validUntil;
    }

    public Organization getOrganization() {
        return this.organization;
    }

    public void setOrganization(Organization organization) {
        this.organization = organization;
        this.organizationId = organization != null ? organization.getId() : null;
    }

    public EquipmentConnection organization(Organization organization) {
        this.setOrganization(organization);
        return this;
    }

    public Equipment getFromEquipment() {
        return this.fromEquipment;
    }

    public void setFromEquipment(Equipment equipment) {
        this.fromEquipment = equipment;
        this.fromEquipmentId = equipment != null ? equipment.getId() : null;
    }

    public EquipmentConnection fromEquipment(Equipment equipment) {
        this.setFromEquipment(equipment);
        return this;
    }

    public Equipment getToEquipment() {
        return this.toEquipment;
    }

    public void setToEquipment(Equipment equipment) {
        this.toEquipment = equipment;
        this.toEquipmentId = equipment != null ? equipment.getId() : null;
    }

    public EquipmentConnection toEquipment(Equipment equipment) {
        this.setToEquipment(equipment);
        return this;
    }

    public Long getOrganizationId() {
        return this.organizationId;
    }

    public void setOrganizationId(Long organization) {
        this.organizationId = organization;
    }

    public Long getFromEquipmentId() {
        return this.fromEquipmentId;
    }

    public void setFromEquipmentId(Long equipment) {
        this.fromEquipmentId = equipment;
    }

    public Long getToEquipmentId() {
        return this.toEquipmentId;
    }

    public void setToEquipmentId(Long equipment) {
        this.toEquipmentId = equipment;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof EquipmentConnection)) {
            return false;
        }
        return getId() != null && getId().equals(((EquipmentConnection) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "EquipmentConnection{" +
            "id=" + getId() +
            ", kind='" + getKind() + "'" +
            ", connectionCode='" + getConnectionCode() + "'" +
            ", validFrom='" + getValidFrom() + "'" +
            ", validUntil='" + getValidUntil() + "'" +
            "}";
    }
}

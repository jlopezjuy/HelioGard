package ar.com.heliguard.app.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A InverterMpptInput.
 */
@Table("inverter_mppt_input")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class InverterMpptInput implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @NotNull
    @Size(max = 80)
    @Column("input_code")
    private String inputCode;

    @NotNull
    @Column("enabled")
    private Boolean enabled;

    @NotNull
    @Column("valid_from")
    private Instant validFrom;

    @Column("valid_until")
    private Instant validUntil;

    @org.springframework.data.annotation.Transient
    private Organization organization;

    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "organization", "installation", "equipmentModel", "solarString" }, allowSetters = true)
    private Equipment inverter;

    @Column("organization_id")
    private Long organizationId;

    @Column("inverter_id")
    private Long inverterId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public InverterMpptInput id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getInputCode() {
        return this.inputCode;
    }

    public InverterMpptInput inputCode(String inputCode) {
        this.setInputCode(inputCode);
        return this;
    }

    public void setInputCode(String inputCode) {
        this.inputCode = inputCode;
    }

    public Boolean getEnabled() {
        return this.enabled;
    }

    public InverterMpptInput enabled(Boolean enabled) {
        this.setEnabled(enabled);
        return this;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public Instant getValidFrom() {
        return this.validFrom;
    }

    public InverterMpptInput validFrom(Instant validFrom) {
        this.setValidFrom(validFrom);
        return this;
    }

    public void setValidFrom(Instant validFrom) {
        this.validFrom = validFrom;
    }

    public Instant getValidUntil() {
        return this.validUntil;
    }

    public InverterMpptInput validUntil(Instant validUntil) {
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

    public InverterMpptInput organization(Organization organization) {
        this.setOrganization(organization);
        return this;
    }

    public Equipment getInverter() {
        return this.inverter;
    }

    public void setInverter(Equipment equipment) {
        this.inverter = equipment;
        this.inverterId = equipment != null ? equipment.getId() : null;
    }

    public InverterMpptInput inverter(Equipment equipment) {
        this.setInverter(equipment);
        return this;
    }

    public Long getOrganizationId() {
        return this.organizationId;
    }

    public void setOrganizationId(Long organization) {
        this.organizationId = organization;
    }

    public Long getInverterId() {
        return this.inverterId;
    }

    public void setInverterId(Long equipment) {
        this.inverterId = equipment;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof InverterMpptInput)) {
            return false;
        }
        return getId() != null && getId().equals(((InverterMpptInput) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "InverterMpptInput{" +
            "id=" + getId() +
            ", inputCode='" + getInputCode() + "'" +
            ", enabled='" + getEnabled() + "'" +
            ", validFrom='" + getValidFrom() + "'" +
            ", validUntil='" + getValidUntil() + "'" +
            "}";
    }
}

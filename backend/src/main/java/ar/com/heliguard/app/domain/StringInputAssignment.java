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
 * A StringInputAssignment.
 */
@Table("string_input_assignment")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class StringInputAssignment implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @NotNull
    @Column("valid_from")
    private Instant validFrom;

    @Column("valid_until")
    private Instant validUntil;

    @Size(max = 240)
    @Column("notes")
    private String notes;

    @org.springframework.data.annotation.Transient
    private Organization organization;

    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "organization", "solarArray", "moduleModel" }, allowSetters = true)
    private SolarString solarString;

    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "organization", "inverter" }, allowSetters = true)
    private InverterMpptInput mpptInput;

    @Column("organization_id")
    private Long organizationId;

    @Column("solar_string_id")
    private Long solarStringId;

    @Column("mppt_input_id")
    private Long mpptInputId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public StringInputAssignment id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instant getValidFrom() {
        return this.validFrom;
    }

    public StringInputAssignment validFrom(Instant validFrom) {
        this.setValidFrom(validFrom);
        return this;
    }

    public void setValidFrom(Instant validFrom) {
        this.validFrom = validFrom;
    }

    public Instant getValidUntil() {
        return this.validUntil;
    }

    public StringInputAssignment validUntil(Instant validUntil) {
        this.setValidUntil(validUntil);
        return this;
    }

    public void setValidUntil(Instant validUntil) {
        this.validUntil = validUntil;
    }

    public String getNotes() {
        return this.notes;
    }

    public StringInputAssignment notes(String notes) {
        this.setNotes(notes);
        return this;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Organization getOrganization() {
        return this.organization;
    }

    public void setOrganization(Organization organization) {
        this.organization = organization;
        this.organizationId = organization != null ? organization.getId() : null;
    }

    public StringInputAssignment organization(Organization organization) {
        this.setOrganization(organization);
        return this;
    }

    public SolarString getSolarString() {
        return this.solarString;
    }

    public void setSolarString(SolarString solarString) {
        this.solarString = solarString;
        this.solarStringId = solarString != null ? solarString.getId() : null;
    }

    public StringInputAssignment solarString(SolarString solarString) {
        this.setSolarString(solarString);
        return this;
    }

    public InverterMpptInput getMpptInput() {
        return this.mpptInput;
    }

    public void setMpptInput(InverterMpptInput inverterMpptInput) {
        this.mpptInput = inverterMpptInput;
        this.mpptInputId = inverterMpptInput != null ? inverterMpptInput.getId() : null;
    }

    public StringInputAssignment mpptInput(InverterMpptInput inverterMpptInput) {
        this.setMpptInput(inverterMpptInput);
        return this;
    }

    public Long getOrganizationId() {
        return this.organizationId;
    }

    public void setOrganizationId(Long organization) {
        this.organizationId = organization;
    }

    public Long getSolarStringId() {
        return this.solarStringId;
    }

    public void setSolarStringId(Long solarString) {
        this.solarStringId = solarString;
    }

    public Long getMpptInputId() {
        return this.mpptInputId;
    }

    public void setMpptInputId(Long inverterMpptInput) {
        this.mpptInputId = inverterMpptInput;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof StringInputAssignment)) {
            return false;
        }
        return getId() != null && getId().equals(((StringInputAssignment) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "StringInputAssignment{" +
            "id=" + getId() +
            ", validFrom='" + getValidFrom() + "'" +
            ", validUntil='" + getValidUntil() + "'" +
            ", notes='" + getNotes() + "'" +
            "}";
    }
}

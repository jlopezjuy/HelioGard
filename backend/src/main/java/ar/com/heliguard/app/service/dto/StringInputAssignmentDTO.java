package ar.com.heliguard.app.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link ar.com.heliguard.app.domain.StringInputAssignment} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class StringInputAssignmentDTO implements Serializable {

    private Long id;

    @NotNull
    private Instant validFrom;

    private Instant validUntil;

    @Size(max = 240)
    private String notes;

    @NotNull
    private OrganizationDTO organization;

    @NotNull
    private SolarStringDTO solarString;

    @NotNull
    private InverterMpptInputDTO mpptInput;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public OrganizationDTO getOrganization() {
        return organization;
    }

    public void setOrganization(OrganizationDTO organization) {
        this.organization = organization;
    }

    public SolarStringDTO getSolarString() {
        return solarString;
    }

    public void setSolarString(SolarStringDTO solarString) {
        this.solarString = solarString;
    }

    public InverterMpptInputDTO getMpptInput() {
        return mpptInput;
    }

    public void setMpptInput(InverterMpptInputDTO mpptInput) {
        this.mpptInput = mpptInput;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof StringInputAssignmentDTO)) {
            return false;
        }

        StringInputAssignmentDTO stringInputAssignmentDTO = (StringInputAssignmentDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, stringInputAssignmentDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "StringInputAssignmentDTO{" +
            "id=" + getId() +
            ", validFrom='" + getValidFrom() + "'" +
            ", validUntil='" + getValidUntil() + "'" +
            ", notes='" + getNotes() + "'" +
            ", organization=" + getOrganization() +
            ", solarString=" + getSolarString() +
            ", mpptInput=" + getMpptInput() +
            "}";
    }
}

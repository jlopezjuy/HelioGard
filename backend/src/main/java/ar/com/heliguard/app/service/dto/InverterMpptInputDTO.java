package ar.com.heliguard.app.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link ar.com.heliguard.app.domain.InverterMpptInput} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class InverterMpptInputDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(max = 80)
    private String inputCode;

    @NotNull
    private Boolean enabled;

    @NotNull
    private Instant validFrom;

    private Instant validUntil;

    private OrganizationDTO organization;

    @NotNull
    private EquipmentDTO inverter;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getInputCode() {
        return inputCode;
    }

    public void setInputCode(String inputCode) {
        this.inputCode = inputCode;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
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

    public EquipmentDTO getInverter() {
        return inverter;
    }

    public void setInverter(EquipmentDTO inverter) {
        this.inverter = inverter;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof InverterMpptInputDTO)) {
            return false;
        }

        InverterMpptInputDTO inverterMpptInputDTO = (InverterMpptInputDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, inverterMpptInputDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "InverterMpptInputDTO{" +
            "id=" + getId() +
            ", inputCode='" + getInputCode() + "'" +
            ", enabled='" + getEnabled() + "'" +
            ", validFrom='" + getValidFrom() + "'" +
            ", validUntil='" + getValidUntil() + "'" +
            ", organization=" + getOrganization() +
            ", inverter=" + getInverter() +
            "}";
    }
}

package ar.com.heliguard.app.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

/**
 * A DTO for the {@link ar.com.heliguard.app.domain.SolarArray} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class SolarArrayDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(max = 120)
    private String name;

    @DecimalMin(value = "0")
    @DecimalMax(value = "360")
    private BigDecimal azimuthDegrees;

    @DecimalMin(value = "0")
    @DecimalMax(value = "90")
    private BigDecimal tiltDegrees;

    private LocalDate commissionedOn;

    @NotNull
    private OrganizationDTO organization;

    @NotNull
    private InstallationDTO installation;

    private SolarSectorDTO solarSector;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getAzimuthDegrees() {
        return azimuthDegrees;
    }

    public void setAzimuthDegrees(BigDecimal azimuthDegrees) {
        this.azimuthDegrees = azimuthDegrees;
    }

    public BigDecimal getTiltDegrees() {
        return tiltDegrees;
    }

    public void setTiltDegrees(BigDecimal tiltDegrees) {
        this.tiltDegrees = tiltDegrees;
    }

    public LocalDate getCommissionedOn() {
        return commissionedOn;
    }

    public void setCommissionedOn(LocalDate commissionedOn) {
        this.commissionedOn = commissionedOn;
    }

    public OrganizationDTO getOrganization() {
        return organization;
    }

    public void setOrganization(OrganizationDTO organization) {
        this.organization = organization;
    }

    public InstallationDTO getInstallation() {
        return installation;
    }

    public void setInstallation(InstallationDTO installation) {
        this.installation = installation;
    }

    public SolarSectorDTO getSolarSector() {
        return solarSector;
    }

    public void setSolarSector(SolarSectorDTO solarSector) {
        this.solarSector = solarSector;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SolarArrayDTO)) {
            return false;
        }

        SolarArrayDTO solarArrayDTO = (SolarArrayDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, solarArrayDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "SolarArrayDTO{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", azimuthDegrees=" + getAzimuthDegrees() +
            ", tiltDegrees=" + getTiltDegrees() +
            ", commissionedOn='" + getCommissionedOn() + "'" +
            ", organization=" + getOrganization() +
            ", installation=" + getInstallation() +
            ", solarSector=" + getSolarSector() +
            "}";
    }
}

package ar.com.heliguard.app.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link ar.com.heliguard.app.domain.SolarSector} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class SolarSectorDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(max = 80)
    private String sectorCode;

    @NotNull
    @Size(max = 140)
    private String name;

    @Size(max = 350)
    private String geometryReference;

    @NotNull
    private Instant validFrom;

    private Instant validUntil;

    @NotNull
    private OrganizationDTO organization;

    @NotNull
    private InstallationDTO installation;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSectorCode() {
        return sectorCode;
    }

    public void setSectorCode(String sectorCode) {
        this.sectorCode = sectorCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGeometryReference() {
        return geometryReference;
    }

    public void setGeometryReference(String geometryReference) {
        this.geometryReference = geometryReference;
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

    public InstallationDTO getInstallation() {
        return installation;
    }

    public void setInstallation(InstallationDTO installation) {
        this.installation = installation;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SolarSectorDTO)) {
            return false;
        }

        SolarSectorDTO solarSectorDTO = (SolarSectorDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, solarSectorDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "SolarSectorDTO{" +
            "id=" + getId() +
            ", sectorCode='" + getSectorCode() + "'" +
            ", name='" + getName() + "'" +
            ", geometryReference='" + getGeometryReference() + "'" +
            ", validFrom='" + getValidFrom() + "'" +
            ", validUntil='" + getValidUntil() + "'" +
            ", organization=" + getOrganization() +
            ", installation=" + getInstallation() +
            "}";
    }
}

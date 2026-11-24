package ar.com.heliguard.app.service.dto;

import ar.com.heliguard.app.domain.enumeration.GridMode;
import ar.com.heliguard.app.domain.enumeration.InstallationStatus;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

/**
 * A DTO for the {@link ar.com.heliguard.app.domain.Installation} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class InstallationDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(max = 60)
    private String code;

    @NotNull
    @Size(max = 160)
    private String name;

    @NotNull
    private GridMode gridMode;

    private InstallationStatus status;

    @NotNull
    @DecimalMin(value = "0")
    private BigDecimal nominalDcKw;

    private LocalDate commissionedOn;

    private OrganizationDTO organization;

    @NotNull
    private SiteDTO site;

    private ClimateRiskProfileDTO climateRiskProfile;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public GridMode getGridMode() {
        return gridMode;
    }

    public void setGridMode(GridMode gridMode) {
        this.gridMode = gridMode;
    }

    public InstallationStatus getStatus() {
        return status;
    }

    public void setStatus(InstallationStatus status) {
        this.status = status;
    }

    public BigDecimal getNominalDcKw() {
        return nominalDcKw;
    }

    public void setNominalDcKw(BigDecimal nominalDcKw) {
        this.nominalDcKw = nominalDcKw;
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

    public SiteDTO getSite() {
        return site;
    }

    public void setSite(SiteDTO site) {
        this.site = site;
    }

    public ClimateRiskProfileDTO getClimateRiskProfile() {
        return climateRiskProfile;
    }

    public void setClimateRiskProfile(ClimateRiskProfileDTO climateRiskProfile) {
        this.climateRiskProfile = climateRiskProfile;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof InstallationDTO)) {
            return false;
        }

        InstallationDTO installationDTO = (InstallationDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, installationDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "InstallationDTO{" +
            "id=" + getId() +
            ", code='" + getCode() + "'" +
            ", name='" + getName() + "'" +
            ", gridMode='" + getGridMode() + "'" +
            ", status='" + getStatus() + "'" +
            ", nominalDcKw=" + getNominalDcKw() +
            ", commissionedOn='" + getCommissionedOn() + "'" +
            ", organization=" + getOrganization() +
            ", site=" + getSite() +
            ", climateRiskProfile=" + getClimateRiskProfile() +
            "}";
    }
}

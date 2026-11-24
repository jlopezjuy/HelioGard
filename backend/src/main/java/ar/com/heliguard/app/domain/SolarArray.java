package ar.com.heliguard.app.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A SolarArray.
 */
@Table("solar_array")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class SolarArray implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @NotNull
    @Size(max = 120)
    @Column("name")
    private String name;

    @DecimalMin(value = "0")
    @DecimalMax(value = "360")
    @Column("azimuth_degrees")
    private BigDecimal azimuthDegrees;

    @DecimalMin(value = "0")
    @DecimalMax(value = "90")
    @Column("tilt_degrees")
    private BigDecimal tiltDegrees;

    @Column("commissioned_on")
    private LocalDate commissionedOn;

    @org.springframework.data.annotation.Transient
    private Organization organization;

    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "organization", "site", "climateRiskProfile" }, allowSetters = true)
    private Installation installation;

    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "organization", "installation" }, allowSetters = true)
    private SolarSector solarSector;

    @Column("organization_id")
    private Long organizationId;

    @Column("installation_id")
    private Long installationId;

    @Column("solar_sector_id")
    private Long solarSectorId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public SolarArray id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public SolarArray name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getAzimuthDegrees() {
        return this.azimuthDegrees;
    }

    public SolarArray azimuthDegrees(BigDecimal azimuthDegrees) {
        this.setAzimuthDegrees(azimuthDegrees);
        return this;
    }

    public void setAzimuthDegrees(BigDecimal azimuthDegrees) {
        this.azimuthDegrees = azimuthDegrees;
    }

    public BigDecimal getTiltDegrees() {
        return this.tiltDegrees;
    }

    public SolarArray tiltDegrees(BigDecimal tiltDegrees) {
        this.setTiltDegrees(tiltDegrees);
        return this;
    }

    public void setTiltDegrees(BigDecimal tiltDegrees) {
        this.tiltDegrees = tiltDegrees;
    }

    public LocalDate getCommissionedOn() {
        return this.commissionedOn;
    }

    public SolarArray commissionedOn(LocalDate commissionedOn) {
        this.setCommissionedOn(commissionedOn);
        return this;
    }

    public void setCommissionedOn(LocalDate commissionedOn) {
        this.commissionedOn = commissionedOn;
    }

    public Organization getOrganization() {
        return this.organization;
    }

    public void setOrganization(Organization organization) {
        this.organization = organization;
        this.organizationId = organization != null ? organization.getId() : null;
    }

    public SolarArray organization(Organization organization) {
        this.setOrganization(organization);
        return this;
    }

    public Installation getInstallation() {
        return this.installation;
    }

    public void setInstallation(Installation installation) {
        this.installation = installation;
        this.installationId = installation != null ? installation.getId() : null;
    }

    public SolarArray installation(Installation installation) {
        this.setInstallation(installation);
        return this;
    }

    public SolarSector getSolarSector() {
        return this.solarSector;
    }

    public void setSolarSector(SolarSector solarSector) {
        this.solarSector = solarSector;
        this.solarSectorId = solarSector != null ? solarSector.getId() : null;
    }

    public SolarArray solarSector(SolarSector solarSector) {
        this.setSolarSector(solarSector);
        return this;
    }

    public Long getOrganizationId() {
        return this.organizationId;
    }

    public void setOrganizationId(Long organization) {
        this.organizationId = organization;
    }

    public Long getInstallationId() {
        return this.installationId;
    }

    public void setInstallationId(Long installation) {
        this.installationId = installation;
    }

    public Long getSolarSectorId() {
        return this.solarSectorId;
    }

    public void setSolarSectorId(Long solarSector) {
        this.solarSectorId = solarSector;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SolarArray)) {
            return false;
        }
        return getId() != null && getId().equals(((SolarArray) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "SolarArray{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", azimuthDegrees=" + getAzimuthDegrees() +
            ", tiltDegrees=" + getTiltDegrees() +
            ", commissionedOn='" + getCommissionedOn() + "'" +
            "}";
    }
}

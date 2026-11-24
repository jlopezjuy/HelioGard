package ar.com.heliguard.app.domain;

import ar.com.heliguard.app.domain.enumeration.GridMode;
import ar.com.heliguard.app.domain.enumeration.InstallationStatus;
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
 * A Installation.
 */
@Table("installation")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Installation implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @NotNull
    @Size(max = 60)
    @Column("code")
    private String code;

    @NotNull
    @Size(max = 160)
    @Column("name")
    private String name;

    @NotNull
    @Column("grid_mode")
    private GridMode gridMode;

    @NotNull
    @Column("status")
    private InstallationStatus status;

    @NotNull
    @DecimalMin(value = "0")
    @Column("nominal_dc_kw")
    private BigDecimal nominalDcKw;

    @Column("commissioned_on")
    private LocalDate commissionedOn;

    @org.springframework.data.annotation.Transient
    private Organization organization;

    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "organization" }, allowSetters = true)
    private Site site;

    @org.springframework.data.annotation.Transient
    private ClimateRiskProfile climateRiskProfile;

    @Column("organization_id")
    private Long organizationId;

    @Column("site_id")
    private Long siteId;

    @Column("climate_risk_profile_id")
    private Long climateRiskProfileId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Installation id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return this.code;
    }

    public Installation code(String code) {
        this.setCode(code);
        return this;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return this.name;
    }

    public Installation name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public GridMode getGridMode() {
        return this.gridMode;
    }

    public Installation gridMode(GridMode gridMode) {
        this.setGridMode(gridMode);
        return this;
    }

    public void setGridMode(GridMode gridMode) {
        this.gridMode = gridMode;
    }

    public InstallationStatus getStatus() {
        return this.status;
    }

    public Installation status(InstallationStatus status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(InstallationStatus status) {
        this.status = status;
    }

    public BigDecimal getNominalDcKw() {
        return this.nominalDcKw;
    }

    public Installation nominalDcKw(BigDecimal nominalDcKw) {
        this.setNominalDcKw(nominalDcKw);
        return this;
    }

    public void setNominalDcKw(BigDecimal nominalDcKw) {
        this.nominalDcKw = nominalDcKw;
    }

    public LocalDate getCommissionedOn() {
        return this.commissionedOn;
    }

    public Installation commissionedOn(LocalDate commissionedOn) {
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

    public Installation organization(Organization organization) {
        this.setOrganization(organization);
        return this;
    }

    public Site getSite() {
        return this.site;
    }

    public void setSite(Site site) {
        this.site = site;
        this.siteId = site != null ? site.getId() : null;
    }

    public Installation site(Site site) {
        this.setSite(site);
        return this;
    }

    public ClimateRiskProfile getClimateRiskProfile() {
        return this.climateRiskProfile;
    }

    public void setClimateRiskProfile(ClimateRiskProfile climateRiskProfile) {
        this.climateRiskProfile = climateRiskProfile;
        this.climateRiskProfileId = climateRiskProfile != null ? climateRiskProfile.getId() : null;
    }

    public Installation climateRiskProfile(ClimateRiskProfile climateRiskProfile) {
        this.setClimateRiskProfile(climateRiskProfile);
        return this;
    }

    public Long getOrganizationId() {
        return this.organizationId;
    }

    public void setOrganizationId(Long organization) {
        this.organizationId = organization;
    }

    public Long getSiteId() {
        return this.siteId;
    }

    public void setSiteId(Long site) {
        this.siteId = site;
    }

    public Long getClimateRiskProfileId() {
        return this.climateRiskProfileId;
    }

    public void setClimateRiskProfileId(Long climateRiskProfile) {
        this.climateRiskProfileId = climateRiskProfile;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Installation)) {
            return false;
        }
        return getId() != null && getId().equals(((Installation) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Installation{" +
            "id=" + getId() +
            ", code='" + getCode() + "'" +
            ", name='" + getName() + "'" +
            ", gridMode='" + getGridMode() + "'" +
            ", status='" + getStatus() + "'" +
            ", nominalDcKw=" + getNominalDcKw() +
            ", commissionedOn='" + getCommissionedOn() + "'" +
            "}";
    }
}

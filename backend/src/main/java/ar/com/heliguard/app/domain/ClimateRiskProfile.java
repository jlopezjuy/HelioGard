package ar.com.heliguard.app.domain;

import ar.com.heliguard.app.domain.enumeration.ClimateZone;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A ClimateRiskProfile.
 */
@Table("climate_risk_profile")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ClimateRiskProfile implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @NotNull
    @Size(max = 100)
    @Column("code")
    private String code;

    @NotNull
    @Column("region")
    private ClimateZone region;

    @NotNull
    @Size(max = 40)
    @Column("profile_version")
    private String profileVersion;

    @Size(max = 250)
    @Column("description")
    private String description;

    @NotNull
    @Column("active")
    private Boolean active;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public ClimateRiskProfile id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return this.code;
    }

    public ClimateRiskProfile code(String code) {
        this.setCode(code);
        return this;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public ClimateZone getRegion() {
        return this.region;
    }

    public ClimateRiskProfile region(ClimateZone region) {
        this.setRegion(region);
        return this;
    }

    public void setRegion(ClimateZone region) {
        this.region = region;
    }

    public String getProfileVersion() {
        return this.profileVersion;
    }

    public ClimateRiskProfile profileVersion(String profileVersion) {
        this.setProfileVersion(profileVersion);
        return this;
    }

    public void setProfileVersion(String profileVersion) {
        this.profileVersion = profileVersion;
    }

    public String getDescription() {
        return this.description;
    }

    public ClimateRiskProfile description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getActive() {
        return this.active;
    }

    public ClimateRiskProfile active(Boolean active) {
        this.setActive(active);
        return this;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ClimateRiskProfile)) {
            return false;
        }
        return getId() != null && getId().equals(((ClimateRiskProfile) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ClimateRiskProfile{" +
            "id=" + getId() +
            ", code='" + getCode() + "'" +
            ", region='" + getRegion() + "'" +
            ", profileVersion='" + getProfileVersion() + "'" +
            ", description='" + getDescription() + "'" +
            ", active='" + getActive() + "'" +
            "}";
    }
}

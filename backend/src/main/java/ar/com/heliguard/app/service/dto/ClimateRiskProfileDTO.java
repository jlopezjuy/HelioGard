package ar.com.heliguard.app.service.dto;

import ar.com.heliguard.app.domain.enumeration.ClimateZone;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link ar.com.heliguard.app.domain.ClimateRiskProfile} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ClimateRiskProfileDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(max = 100)
    private String code;

    @NotNull
    private ClimateZone region;

    @NotNull
    @Size(max = 40)
    private String profileVersion;

    @Size(max = 250)
    private String description;

    @NotNull
    private Boolean active;

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

    public ClimateZone getRegion() {
        return region;
    }

    public void setRegion(ClimateZone region) {
        this.region = region;
    }

    public String getProfileVersion() {
        return profileVersion;
    }

    public void setProfileVersion(String profileVersion) {
        this.profileVersion = profileVersion;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ClimateRiskProfileDTO)) {
            return false;
        }

        ClimateRiskProfileDTO climateRiskProfileDTO = (ClimateRiskProfileDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, climateRiskProfileDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ClimateRiskProfileDTO{" +
            "id=" + getId() +
            ", code='" + getCode() + "'" +
            ", region='" + getRegion() + "'" +
            ", profileVersion='" + getProfileVersion() + "'" +
            ", description='" + getDescription() + "'" +
            ", active='" + getActive() + "'" +
            "}";
    }
}

package ar.com.heliguard.app.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

/**
 * A DTO for the {@link ar.com.heliguard.app.domain.Site} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class SiteDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(max = 160)
    private String name;

    @Size(max = 250)
    private String address;

    @DecimalMin(value = "-90")
    @DecimalMax(value = "90")
    private BigDecimal latitude;

    @DecimalMin(value = "-180")
    @DecimalMax(value = "180")
    private BigDecimal longitude;

    private BigDecimal altitudeM;

    @NotNull
    @Size(max = 64)
    private String timeZone;

    @NotNull
    private OrganizationDTO organization;

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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public BigDecimal getLatitude() {
        return latitude;
    }

    public void setLatitude(BigDecimal latitude) {
        this.latitude = latitude;
    }

    public BigDecimal getLongitude() {
        return longitude;
    }

    public void setLongitude(BigDecimal longitude) {
        this.longitude = longitude;
    }

    public BigDecimal getAltitudeM() {
        return altitudeM;
    }

    public void setAltitudeM(BigDecimal altitudeM) {
        this.altitudeM = altitudeM;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

    public OrganizationDTO getOrganization() {
        return organization;
    }

    public void setOrganization(OrganizationDTO organization) {
        this.organization = organization;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SiteDTO)) {
            return false;
        }

        SiteDTO siteDTO = (SiteDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, siteDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "SiteDTO{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", address='" + getAddress() + "'" +
            ", latitude=" + getLatitude() +
            ", longitude=" + getLongitude() +
            ", altitudeM=" + getAltitudeM() +
            ", timeZone='" + getTimeZone() + "'" +
            ", organization=" + getOrganization() +
            "}";
    }
}

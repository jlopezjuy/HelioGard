package ar.com.heliguard.app.service.dto;

import ar.com.heliguard.app.domain.enumeration.DroneAssetStatus;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

/**
 * A DTO for the {@link ar.com.heliguard.app.domain.DroneAsset} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class DroneAssetDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(max = 100)
    private String assetCode;

    @Size(max = 120)
    private String modelName;

    @NotNull
    private DroneAssetStatus status;

    private BigDecimal maximumWindMs;

    @NotNull
    private OrganizationDTO organization;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAssetCode() {
        return assetCode;
    }

    public void setAssetCode(String assetCode) {
        this.assetCode = assetCode;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public DroneAssetStatus getStatus() {
        return status;
    }

    public void setStatus(DroneAssetStatus status) {
        this.status = status;
    }

    public BigDecimal getMaximumWindMs() {
        return maximumWindMs;
    }

    public void setMaximumWindMs(BigDecimal maximumWindMs) {
        this.maximumWindMs = maximumWindMs;
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
        if (!(o instanceof DroneAssetDTO)) {
            return false;
        }

        DroneAssetDTO droneAssetDTO = (DroneAssetDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, droneAssetDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "DroneAssetDTO{" +
            "id=" + getId() +
            ", assetCode='" + getAssetCode() + "'" +
            ", modelName='" + getModelName() + "'" +
            ", status='" + getStatus() + "'" +
            ", maximumWindMs=" + getMaximumWindMs() +
            ", organization=" + getOrganization() +
            "}";
    }
}

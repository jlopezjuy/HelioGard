package ar.com.heliguard.app.domain;

import ar.com.heliguard.app.domain.enumeration.DroneAssetStatus;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A DroneAsset.
 */
@Table("drone_asset")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class DroneAsset implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @NotNull
    @Size(max = 100)
    @Column("asset_code")
    private String assetCode;

    @Size(max = 120)
    @Column("model_name")
    private String modelName;

    @NotNull
    @Column("status")
    private DroneAssetStatus status;

    @Column("maximum_wind_ms")
    private BigDecimal maximumWindMs;

    @org.springframework.data.annotation.Transient
    private Organization organization;

    @Column("organization_id")
    private Long organizationId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public DroneAsset id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAssetCode() {
        return this.assetCode;
    }

    public DroneAsset assetCode(String assetCode) {
        this.setAssetCode(assetCode);
        return this;
    }

    public void setAssetCode(String assetCode) {
        this.assetCode = assetCode;
    }

    public String getModelName() {
        return this.modelName;
    }

    public DroneAsset modelName(String modelName) {
        this.setModelName(modelName);
        return this;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public DroneAssetStatus getStatus() {
        return this.status;
    }

    public DroneAsset status(DroneAssetStatus status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(DroneAssetStatus status) {
        this.status = status;
    }

    public BigDecimal getMaximumWindMs() {
        return this.maximumWindMs;
    }

    public DroneAsset maximumWindMs(BigDecimal maximumWindMs) {
        this.setMaximumWindMs(maximumWindMs);
        return this;
    }

    public void setMaximumWindMs(BigDecimal maximumWindMs) {
        this.maximumWindMs = maximumWindMs;
    }

    public Organization getOrganization() {
        return this.organization;
    }

    public void setOrganization(Organization organization) {
        this.organization = organization;
        this.organizationId = organization != null ? organization.getId() : null;
    }

    public DroneAsset organization(Organization organization) {
        this.setOrganization(organization);
        return this;
    }

    public Long getOrganizationId() {
        return this.organizationId;
    }

    public void setOrganizationId(Long organization) {
        this.organizationId = organization;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DroneAsset)) {
            return false;
        }
        return getId() != null && getId().equals(((DroneAsset) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "DroneAsset{" +
            "id=" + getId() +
            ", assetCode='" + getAssetCode() + "'" +
            ", modelName='" + getModelName() + "'" +
            ", status='" + getStatus() + "'" +
            ", maximumWindMs=" + getMaximumWindMs() +
            "}";
    }
}

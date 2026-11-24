package ar.com.heliguard.app.domain;

import ar.com.heliguard.app.domain.enumeration.EvidenceSourceKind;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A EvidenceSource.
 */
@Table("evidence_source")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class EvidenceSource implements Serializable {

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
    @Size(max = 160)
    @Column("provider_name")
    private String providerName;

    @NotNull
    @Column("kind")
    private EvidenceSourceKind kind;

    @Column("spatial_resolution_m")
    private BigDecimal spatialResolutionM;

    @Size(max = 300)
    @Column("license_reference")
    private String licenseReference;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public EvidenceSource id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return this.code;
    }

    public EvidenceSource code(String code) {
        this.setCode(code);
        return this;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getProviderName() {
        return this.providerName;
    }

    public EvidenceSource providerName(String providerName) {
        this.setProviderName(providerName);
        return this;
    }

    public void setProviderName(String providerName) {
        this.providerName = providerName;
    }

    public EvidenceSourceKind getKind() {
        return this.kind;
    }

    public EvidenceSource kind(EvidenceSourceKind kind) {
        this.setKind(kind);
        return this;
    }

    public void setKind(EvidenceSourceKind kind) {
        this.kind = kind;
    }

    public BigDecimal getSpatialResolutionM() {
        return this.spatialResolutionM;
    }

    public EvidenceSource spatialResolutionM(BigDecimal spatialResolutionM) {
        this.setSpatialResolutionM(spatialResolutionM);
        return this;
    }

    public void setSpatialResolutionM(BigDecimal spatialResolutionM) {
        this.spatialResolutionM = spatialResolutionM;
    }

    public String getLicenseReference() {
        return this.licenseReference;
    }

    public EvidenceSource licenseReference(String licenseReference) {
        this.setLicenseReference(licenseReference);
        return this;
    }

    public void setLicenseReference(String licenseReference) {
        this.licenseReference = licenseReference;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof EvidenceSource)) {
            return false;
        }
        return getId() != null && getId().equals(((EvidenceSource) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "EvidenceSource{" +
            "id=" + getId() +
            ", code='" + getCode() + "'" +
            ", providerName='" + getProviderName() + "'" +
            ", kind='" + getKind() + "'" +
            ", spatialResolutionM=" + getSpatialResolutionM() +
            ", licenseReference='" + getLicenseReference() + "'" +
            "}";
    }
}

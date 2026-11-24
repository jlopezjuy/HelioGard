package ar.com.heliguard.app.service.dto;

import ar.com.heliguard.app.domain.enumeration.EvidenceSourceKind;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

/**
 * A DTO for the {@link ar.com.heliguard.app.domain.EvidenceSource} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class EvidenceSourceDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(max = 100)
    private String code;

    @NotNull
    @Size(max = 160)
    private String providerName;

    @NotNull
    private EvidenceSourceKind kind;

    private BigDecimal spatialResolutionM;

    @Size(max = 300)
    private String licenseReference;

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

    public String getProviderName() {
        return providerName;
    }

    public void setProviderName(String providerName) {
        this.providerName = providerName;
    }

    public EvidenceSourceKind getKind() {
        return kind;
    }

    public void setKind(EvidenceSourceKind kind) {
        this.kind = kind;
    }

    public BigDecimal getSpatialResolutionM() {
        return spatialResolutionM;
    }

    public void setSpatialResolutionM(BigDecimal spatialResolutionM) {
        this.spatialResolutionM = spatialResolutionM;
    }

    public String getLicenseReference() {
        return licenseReference;
    }

    public void setLicenseReference(String licenseReference) {
        this.licenseReference = licenseReference;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof EvidenceSourceDTO)) {
            return false;
        }

        EvidenceSourceDTO evidenceSourceDTO = (EvidenceSourceDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, evidenceSourceDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "EvidenceSourceDTO{" +
            "id=" + getId() +
            ", code='" + getCode() + "'" +
            ", providerName='" + getProviderName() + "'" +
            ", kind='" + getKind() + "'" +
            ", spatialResolutionM=" + getSpatialResolutionM() +
            ", licenseReference='" + getLicenseReference() + "'" +
            "}";
    }
}

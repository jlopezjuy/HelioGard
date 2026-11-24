package ar.com.heliguard.app.service.dto;

import ar.com.heliguard.app.domain.enumeration.LossAssessmentStatus;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link ar.com.heliguard.app.domain.LossAssessment} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class LossAssessmentDTO implements Serializable {

    private Long id;

    @NotNull
    private BigDecimal totalLossWh;

    private BigDecimal lossPct;

    @NotNull
    @Size(max = 100)
    private String methodology;

    @NotNull
    private Instant assessedAt;

    @NotNull
    private LossAssessmentStatus validationStatus;

    @NotNull
    private OrganizationDTO organization;

    @NotNull
    private EnergyModelRunDTO modelRun;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getTotalLossWh() {
        return totalLossWh;
    }

    public void setTotalLossWh(BigDecimal totalLossWh) {
        this.totalLossWh = totalLossWh;
    }

    public BigDecimal getLossPct() {
        return lossPct;
    }

    public void setLossPct(BigDecimal lossPct) {
        this.lossPct = lossPct;
    }

    public String getMethodology() {
        return methodology;
    }

    public void setMethodology(String methodology) {
        this.methodology = methodology;
    }

    public Instant getAssessedAt() {
        return assessedAt;
    }

    public void setAssessedAt(Instant assessedAt) {
        this.assessedAt = assessedAt;
    }

    public LossAssessmentStatus getValidationStatus() {
        return validationStatus;
    }

    public void setValidationStatus(LossAssessmentStatus validationStatus) {
        this.validationStatus = validationStatus;
    }

    public OrganizationDTO getOrganization() {
        return organization;
    }

    public void setOrganization(OrganizationDTO organization) {
        this.organization = organization;
    }

    public EnergyModelRunDTO getModelRun() {
        return modelRun;
    }

    public void setModelRun(EnergyModelRunDTO modelRun) {
        this.modelRun = modelRun;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof LossAssessmentDTO)) {
            return false;
        }

        LossAssessmentDTO lossAssessmentDTO = (LossAssessmentDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, lossAssessmentDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "LossAssessmentDTO{" +
            "id=" + getId() +
            ", totalLossWh=" + getTotalLossWh() +
            ", lossPct=" + getLossPct() +
            ", methodology='" + getMethodology() + "'" +
            ", assessedAt='" + getAssessedAt() + "'" +
            ", validationStatus='" + getValidationStatus() + "'" +
            ", organization=" + getOrganization() +
            ", modelRun=" + getModelRun() +
            "}";
    }
}

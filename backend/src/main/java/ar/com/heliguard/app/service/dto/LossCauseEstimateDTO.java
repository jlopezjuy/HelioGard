package ar.com.heliguard.app.service.dto;

import ar.com.heliguard.app.domain.enumeration.LossCause;
import jakarta.persistence.Lob;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

/**
 * A DTO for the {@link ar.com.heliguard.app.domain.LossCauseEstimate} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class LossCauseEstimateDTO implements Serializable {

    private Long id;

    @NotNull
    private LossCause cause;

    @NotNull
    private BigDecimal estimatedWh;

    @NotNull
    @Size(max = 100)
    private String method;

    @Lob
    private String evidenceNote;

    @NotNull
    private OrganizationDTO organization;

    @NotNull
    private LossAssessmentDTO lossAssessment;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LossCause getCause() {
        return cause;
    }

    public void setCause(LossCause cause) {
        this.cause = cause;
    }

    public BigDecimal getEstimatedWh() {
        return estimatedWh;
    }

    public void setEstimatedWh(BigDecimal estimatedWh) {
        this.estimatedWh = estimatedWh;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getEvidenceNote() {
        return evidenceNote;
    }

    public void setEvidenceNote(String evidenceNote) {
        this.evidenceNote = evidenceNote;
    }

    public OrganizationDTO getOrganization() {
        return organization;
    }

    public void setOrganization(OrganizationDTO organization) {
        this.organization = organization;
    }

    public LossAssessmentDTO getLossAssessment() {
        return lossAssessment;
    }

    public void setLossAssessment(LossAssessmentDTO lossAssessment) {
        this.lossAssessment = lossAssessment;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof LossCauseEstimateDTO)) {
            return false;
        }

        LossCauseEstimateDTO lossCauseEstimateDTO = (LossCauseEstimateDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, lossCauseEstimateDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "LossCauseEstimateDTO{" +
            "id=" + getId() +
            ", cause='" + getCause() + "'" +
            ", estimatedWh=" + getEstimatedWh() +
            ", method='" + getMethod() + "'" +
            ", evidenceNote='" + getEvidenceNote() + "'" +
            ", organization=" + getOrganization() +
            ", lossAssessment=" + getLossAssessment() +
            "}";
    }
}

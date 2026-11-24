package ar.com.heliguard.app.domain;

import ar.com.heliguard.app.domain.enumeration.LossCause;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A LossCauseEstimate.
 */
@Table("loss_cause_estimate")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class LossCauseEstimate implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @NotNull
    @Column("cause")
    private LossCause cause;

    @NotNull
    @Column("estimated_wh")
    private BigDecimal estimatedWh;

    @NotNull
    @Size(max = 100)
    @Column("method")
    private String method;

    @Column("evidence_note")
    private String evidenceNote;

    @org.springframework.data.annotation.Transient
    private Organization organization;

    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "organization", "modelRun" }, allowSetters = true)
    private LossAssessment lossAssessment;

    @Column("organization_id")
    private Long organizationId;

    @Column("loss_assessment_id")
    private Long lossAssessmentId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public LossCauseEstimate id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LossCause getCause() {
        return this.cause;
    }

    public LossCauseEstimate cause(LossCause cause) {
        this.setCause(cause);
        return this;
    }

    public void setCause(LossCause cause) {
        this.cause = cause;
    }

    public BigDecimal getEstimatedWh() {
        return this.estimatedWh;
    }

    public LossCauseEstimate estimatedWh(BigDecimal estimatedWh) {
        this.setEstimatedWh(estimatedWh);
        return this;
    }

    public void setEstimatedWh(BigDecimal estimatedWh) {
        this.estimatedWh = estimatedWh;
    }

    public String getMethod() {
        return this.method;
    }

    public LossCauseEstimate method(String method) {
        this.setMethod(method);
        return this;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getEvidenceNote() {
        return this.evidenceNote;
    }

    public LossCauseEstimate evidenceNote(String evidenceNote) {
        this.setEvidenceNote(evidenceNote);
        return this;
    }

    public void setEvidenceNote(String evidenceNote) {
        this.evidenceNote = evidenceNote;
    }

    public Organization getOrganization() {
        return this.organization;
    }

    public void setOrganization(Organization organization) {
        this.organization = organization;
        this.organizationId = organization != null ? organization.getId() : null;
    }

    public LossCauseEstimate organization(Organization organization) {
        this.setOrganization(organization);
        return this;
    }

    public LossAssessment getLossAssessment() {
        return this.lossAssessment;
    }

    public void setLossAssessment(LossAssessment lossAssessment) {
        this.lossAssessment = lossAssessment;
        this.lossAssessmentId = lossAssessment != null ? lossAssessment.getId() : null;
    }

    public LossCauseEstimate lossAssessment(LossAssessment lossAssessment) {
        this.setLossAssessment(lossAssessment);
        return this;
    }

    public Long getOrganizationId() {
        return this.organizationId;
    }

    public void setOrganizationId(Long organization) {
        this.organizationId = organization;
    }

    public Long getLossAssessmentId() {
        return this.lossAssessmentId;
    }

    public void setLossAssessmentId(Long lossAssessment) {
        this.lossAssessmentId = lossAssessment;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof LossCauseEstimate)) {
            return false;
        }
        return getId() != null && getId().equals(((LossCauseEstimate) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "LossCauseEstimate{" +
            "id=" + getId() +
            ", cause='" + getCause() + "'" +
            ", estimatedWh=" + getEstimatedWh() +
            ", method='" + getMethod() + "'" +
            ", evidenceNote='" + getEvidenceNote() + "'" +
            "}";
    }
}

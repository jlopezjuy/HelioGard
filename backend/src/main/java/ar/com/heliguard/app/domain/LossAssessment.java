package ar.com.heliguard.app.domain;

import ar.com.heliguard.app.domain.enumeration.LossAssessmentStatus;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A LossAssessment.
 */
@Table("loss_assessment")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class LossAssessment implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @NotNull
    @Column("total_loss_wh")
    private BigDecimal totalLossWh;

    @Column("loss_pct")
    private BigDecimal lossPct;

    @NotNull
    @Size(max = 100)
    @Column("methodology")
    private String methodology;

    @NotNull
    @Column("assessed_at")
    private Instant assessedAt;

    @NotNull
    @Column("validation_status")
    private LossAssessmentStatus validationStatus;

    @org.springframework.data.annotation.Transient
    private Organization organization;

    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "organization", "installation", "modelConfiguration", "weatherBatch" }, allowSetters = true)
    private EnergyModelRun modelRun;

    @Column("organization_id")
    private Long organizationId;

    @Column("model_run_id")
    private Long modelRunId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public LossAssessment id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getTotalLossWh() {
        return this.totalLossWh;
    }

    public LossAssessment totalLossWh(BigDecimal totalLossWh) {
        this.setTotalLossWh(totalLossWh);
        return this;
    }

    public void setTotalLossWh(BigDecimal totalLossWh) {
        this.totalLossWh = totalLossWh;
    }

    public BigDecimal getLossPct() {
        return this.lossPct;
    }

    public LossAssessment lossPct(BigDecimal lossPct) {
        this.setLossPct(lossPct);
        return this;
    }

    public void setLossPct(BigDecimal lossPct) {
        this.lossPct = lossPct;
    }

    public String getMethodology() {
        return this.methodology;
    }

    public LossAssessment methodology(String methodology) {
        this.setMethodology(methodology);
        return this;
    }

    public void setMethodology(String methodology) {
        this.methodology = methodology;
    }

    public Instant getAssessedAt() {
        return this.assessedAt;
    }

    public LossAssessment assessedAt(Instant assessedAt) {
        this.setAssessedAt(assessedAt);
        return this;
    }

    public void setAssessedAt(Instant assessedAt) {
        this.assessedAt = assessedAt;
    }

    public LossAssessmentStatus getValidationStatus() {
        return this.validationStatus;
    }

    public LossAssessment validationStatus(LossAssessmentStatus validationStatus) {
        this.setValidationStatus(validationStatus);
        return this;
    }

    public void setValidationStatus(LossAssessmentStatus validationStatus) {
        this.validationStatus = validationStatus;
    }

    public Organization getOrganization() {
        return this.organization;
    }

    public void setOrganization(Organization organization) {
        this.organization = organization;
        this.organizationId = organization != null ? organization.getId() : null;
    }

    public LossAssessment organization(Organization organization) {
        this.setOrganization(organization);
        return this;
    }

    public EnergyModelRun getModelRun() {
        return this.modelRun;
    }

    public void setModelRun(EnergyModelRun energyModelRun) {
        this.modelRun = energyModelRun;
        this.modelRunId = energyModelRun != null ? energyModelRun.getId() : null;
    }

    public LossAssessment modelRun(EnergyModelRun energyModelRun) {
        this.setModelRun(energyModelRun);
        return this;
    }

    public Long getOrganizationId() {
        return this.organizationId;
    }

    public void setOrganizationId(Long organization) {
        this.organizationId = organization;
    }

    public Long getModelRunId() {
        return this.modelRunId;
    }

    public void setModelRunId(Long energyModelRun) {
        this.modelRunId = energyModelRun;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof LossAssessment)) {
            return false;
        }
        return getId() != null && getId().equals(((LossAssessment) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "LossAssessment{" +
            "id=" + getId() +
            ", totalLossWh=" + getTotalLossWh() +
            ", lossPct=" + getLossPct() +
            ", methodology='" + getMethodology() + "'" +
            ", assessedAt='" + getAssessedAt() + "'" +
            ", validationStatus='" + getValidationStatus() + "'" +
            "}";
    }
}

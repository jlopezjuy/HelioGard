package ar.com.heliguard.app.domain;

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
 * A AuditAnswer.
 */
@Table("audit_answer")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class AuditAnswer implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @Column("answer_text")
    private String answerText;

    @Column("answer_number")
    private BigDecimal answerNumber;

    @Column("answer_boolean")
    private Boolean answerBoolean;

    @Size(max = 24)
    @Column("answer_unit_code")
    private String answerUnitCode;

    @Column("answered_at")
    private Instant answeredAt;

    @Column("compliant")
    private Boolean compliant;

    @org.springframework.data.annotation.Transient
    private Organization organization;

    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "organization", "installation", "auditTemplate", "auditorMembership" }, allowSetters = true)
    private AuditReport auditReport;

    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "organization", "auditTemplate" }, allowSetters = true)
    private AuditQuestion auditQuestion;

    @Column("organization_id")
    private Long organizationId;

    @Column("audit_report_id")
    private Long auditReportId;

    @Column("audit_question_id")
    private Long auditQuestionId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public AuditAnswer id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAnswerText() {
        return this.answerText;
    }

    public AuditAnswer answerText(String answerText) {
        this.setAnswerText(answerText);
        return this;
    }

    public void setAnswerText(String answerText) {
        this.answerText = answerText;
    }

    public BigDecimal getAnswerNumber() {
        return this.answerNumber;
    }

    public AuditAnswer answerNumber(BigDecimal answerNumber) {
        this.setAnswerNumber(answerNumber);
        return this;
    }

    public void setAnswerNumber(BigDecimal answerNumber) {
        this.answerNumber = answerNumber;
    }

    public Boolean getAnswerBoolean() {
        return this.answerBoolean;
    }

    public AuditAnswer answerBoolean(Boolean answerBoolean) {
        this.setAnswerBoolean(answerBoolean);
        return this;
    }

    public void setAnswerBoolean(Boolean answerBoolean) {
        this.answerBoolean = answerBoolean;
    }

    public String getAnswerUnitCode() {
        return this.answerUnitCode;
    }

    public AuditAnswer answerUnitCode(String answerUnitCode) {
        this.setAnswerUnitCode(answerUnitCode);
        return this;
    }

    public void setAnswerUnitCode(String answerUnitCode) {
        this.answerUnitCode = answerUnitCode;
    }

    public Instant getAnsweredAt() {
        return this.answeredAt;
    }

    public AuditAnswer answeredAt(Instant answeredAt) {
        this.setAnsweredAt(answeredAt);
        return this;
    }

    public void setAnsweredAt(Instant answeredAt) {
        this.answeredAt = answeredAt;
    }

    public Boolean getCompliant() {
        return this.compliant;
    }

    public AuditAnswer compliant(Boolean compliant) {
        this.setCompliant(compliant);
        return this;
    }

    public void setCompliant(Boolean compliant) {
        this.compliant = compliant;
    }

    public Organization getOrganization() {
        return this.organization;
    }

    public void setOrganization(Organization organization) {
        this.organization = organization;
        this.organizationId = organization != null ? organization.getId() : null;
    }

    public AuditAnswer organization(Organization organization) {
        this.setOrganization(organization);
        return this;
    }

    public AuditReport getAuditReport() {
        return this.auditReport;
    }

    public void setAuditReport(AuditReport auditReport) {
        this.auditReport = auditReport;
        this.auditReportId = auditReport != null ? auditReport.getId() : null;
    }

    public AuditAnswer auditReport(AuditReport auditReport) {
        this.setAuditReport(auditReport);
        return this;
    }

    public AuditQuestion getAuditQuestion() {
        return this.auditQuestion;
    }

    public void setAuditQuestion(AuditQuestion auditQuestion) {
        this.auditQuestion = auditQuestion;
        this.auditQuestionId = auditQuestion != null ? auditQuestion.getId() : null;
    }

    public AuditAnswer auditQuestion(AuditQuestion auditQuestion) {
        this.setAuditQuestion(auditQuestion);
        return this;
    }

    public Long getOrganizationId() {
        return this.organizationId;
    }

    public void setOrganizationId(Long organization) {
        this.organizationId = organization;
    }

    public Long getAuditReportId() {
        return this.auditReportId;
    }

    public void setAuditReportId(Long auditReport) {
        this.auditReportId = auditReport;
    }

    public Long getAuditQuestionId() {
        return this.auditQuestionId;
    }

    public void setAuditQuestionId(Long auditQuestion) {
        this.auditQuestionId = auditQuestion;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AuditAnswer)) {
            return false;
        }
        return getId() != null && getId().equals(((AuditAnswer) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "AuditAnswer{" +
            "id=" + getId() +
            ", answerText='" + getAnswerText() + "'" +
            ", answerNumber=" + getAnswerNumber() +
            ", answerBoolean='" + getAnswerBoolean() + "'" +
            ", answerUnitCode='" + getAnswerUnitCode() + "'" +
            ", answeredAt='" + getAnsweredAt() + "'" +
            ", compliant='" + getCompliant() + "'" +
            "}";
    }
}

package ar.com.heliguard.app.service.dto;

import jakarta.persistence.Lob;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link ar.com.heliguard.app.domain.AuditAnswer} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class AuditAnswerDTO implements Serializable {

    private Long id;

    @Lob
    private String answerText;

    private BigDecimal answerNumber;

    private Boolean answerBoolean;

    @Size(max = 24)
    private String answerUnitCode;

    private Instant answeredAt;

    private Boolean compliant;

    @NotNull
    private OrganizationDTO organization;

    @NotNull
    private AuditReportDTO auditReport;

    @NotNull
    private AuditQuestionDTO auditQuestion;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAnswerText() {
        return answerText;
    }

    public void setAnswerText(String answerText) {
        this.answerText = answerText;
    }

    public BigDecimal getAnswerNumber() {
        return answerNumber;
    }

    public void setAnswerNumber(BigDecimal answerNumber) {
        this.answerNumber = answerNumber;
    }

    public Boolean getAnswerBoolean() {
        return answerBoolean;
    }

    public void setAnswerBoolean(Boolean answerBoolean) {
        this.answerBoolean = answerBoolean;
    }

    public String getAnswerUnitCode() {
        return answerUnitCode;
    }

    public void setAnswerUnitCode(String answerUnitCode) {
        this.answerUnitCode = answerUnitCode;
    }

    public Instant getAnsweredAt() {
        return answeredAt;
    }

    public void setAnsweredAt(Instant answeredAt) {
        this.answeredAt = answeredAt;
    }

    public Boolean getCompliant() {
        return compliant;
    }

    public void setCompliant(Boolean compliant) {
        this.compliant = compliant;
    }

    public OrganizationDTO getOrganization() {
        return organization;
    }

    public void setOrganization(OrganizationDTO organization) {
        this.organization = organization;
    }

    public AuditReportDTO getAuditReport() {
        return auditReport;
    }

    public void setAuditReport(AuditReportDTO auditReport) {
        this.auditReport = auditReport;
    }

    public AuditQuestionDTO getAuditQuestion() {
        return auditQuestion;
    }

    public void setAuditQuestion(AuditQuestionDTO auditQuestion) {
        this.auditQuestion = auditQuestion;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AuditAnswerDTO)) {
            return false;
        }

        AuditAnswerDTO auditAnswerDTO = (AuditAnswerDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, auditAnswerDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "AuditAnswerDTO{" +
            "id=" + getId() +
            ", answerText='" + getAnswerText() + "'" +
            ", answerNumber=" + getAnswerNumber() +
            ", answerBoolean='" + getAnswerBoolean() + "'" +
            ", answerUnitCode='" + getAnswerUnitCode() + "'" +
            ", answeredAt='" + getAnsweredAt() + "'" +
            ", compliant='" + getCompliant() + "'" +
            ", organization=" + getOrganization() +
            ", auditReport=" + getAuditReport() +
            ", auditQuestion=" + getAuditQuestion() +
            "}";
    }
}

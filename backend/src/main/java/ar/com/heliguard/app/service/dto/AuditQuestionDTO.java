package ar.com.heliguard.app.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link ar.com.heliguard.app.domain.AuditQuestion} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class AuditQuestionDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(max = 80)
    private String questionCode;

    @NotNull
    @Size(max = 300)
    private String label;

    @NotNull
    @Size(max = 40)
    private String answerType;

    @NotNull
    private Integer sortOrder;

    @NotNull
    private Boolean mandatory;

    private OrganizationDTO organization;

    @NotNull
    private AuditTemplateDTO auditTemplate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getQuestionCode() {
        return questionCode;
    }

    public void setQuestionCode(String questionCode) {
        this.questionCode = questionCode;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getAnswerType() {
        return answerType;
    }

    public void setAnswerType(String answerType) {
        this.answerType = answerType;
    }

    public Integer getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }

    public Boolean getMandatory() {
        return mandatory;
    }

    public void setMandatory(Boolean mandatory) {
        this.mandatory = mandatory;
    }

    public OrganizationDTO getOrganization() {
        return organization;
    }

    public void setOrganization(OrganizationDTO organization) {
        this.organization = organization;
    }

    public AuditTemplateDTO getAuditTemplate() {
        return auditTemplate;
    }

    public void setAuditTemplate(AuditTemplateDTO auditTemplate) {
        this.auditTemplate = auditTemplate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AuditQuestionDTO)) {
            return false;
        }

        AuditQuestionDTO auditQuestionDTO = (AuditQuestionDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, auditQuestionDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "AuditQuestionDTO{" +
            "id=" + getId() +
            ", questionCode='" + getQuestionCode() + "'" +
            ", label='" + getLabel() + "'" +
            ", answerType='" + getAnswerType() + "'" +
            ", sortOrder=" + getSortOrder() +
            ", mandatory='" + getMandatory() + "'" +
            ", organization=" + getOrganization() +
            ", auditTemplate=" + getAuditTemplate() +
            "}";
    }
}

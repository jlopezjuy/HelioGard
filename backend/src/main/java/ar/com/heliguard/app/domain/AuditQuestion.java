package ar.com.heliguard.app.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A AuditQuestion.
 */
@Table("audit_question")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class AuditQuestion implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @NotNull
    @Size(max = 80)
    @Column("question_code")
    private String questionCode;

    @NotNull
    @Size(max = 300)
    @Column("label")
    private String label;

    @NotNull
    @Size(max = 40)
    @Column("answer_type")
    private String answerType;

    @NotNull
    @Column("sort_order")
    private Integer sortOrder;

    @NotNull
    @Column("mandatory")
    private Boolean mandatory;

    @org.springframework.data.annotation.Transient
    private Organization organization;

    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "organization" }, allowSetters = true)
    private AuditTemplate auditTemplate;

    @Column("organization_id")
    private Long organizationId;

    @Column("audit_template_id")
    private Long auditTemplateId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public AuditQuestion id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getQuestionCode() {
        return this.questionCode;
    }

    public AuditQuestion questionCode(String questionCode) {
        this.setQuestionCode(questionCode);
        return this;
    }

    public void setQuestionCode(String questionCode) {
        this.questionCode = questionCode;
    }

    public String getLabel() {
        return this.label;
    }

    public AuditQuestion label(String label) {
        this.setLabel(label);
        return this;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getAnswerType() {
        return this.answerType;
    }

    public AuditQuestion answerType(String answerType) {
        this.setAnswerType(answerType);
        return this;
    }

    public void setAnswerType(String answerType) {
        this.answerType = answerType;
    }

    public Integer getSortOrder() {
        return this.sortOrder;
    }

    public AuditQuestion sortOrder(Integer sortOrder) {
        this.setSortOrder(sortOrder);
        return this;
    }

    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }

    public Boolean getMandatory() {
        return this.mandatory;
    }

    public AuditQuestion mandatory(Boolean mandatory) {
        this.setMandatory(mandatory);
        return this;
    }

    public void setMandatory(Boolean mandatory) {
        this.mandatory = mandatory;
    }

    public Organization getOrganization() {
        return this.organization;
    }

    public void setOrganization(Organization organization) {
        this.organization = organization;
        this.organizationId = organization != null ? organization.getId() : null;
    }

    public AuditQuestion organization(Organization organization) {
        this.setOrganization(organization);
        return this;
    }

    public AuditTemplate getAuditTemplate() {
        return this.auditTemplate;
    }

    public void setAuditTemplate(AuditTemplate auditTemplate) {
        this.auditTemplate = auditTemplate;
        this.auditTemplateId = auditTemplate != null ? auditTemplate.getId() : null;
    }

    public AuditQuestion auditTemplate(AuditTemplate auditTemplate) {
        this.setAuditTemplate(auditTemplate);
        return this;
    }

    public Long getOrganizationId() {
        return this.organizationId;
    }

    public void setOrganizationId(Long organization) {
        this.organizationId = organization;
    }

    public Long getAuditTemplateId() {
        return this.auditTemplateId;
    }

    public void setAuditTemplateId(Long auditTemplate) {
        this.auditTemplateId = auditTemplate;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AuditQuestion)) {
            return false;
        }
        return getId() != null && getId().equals(((AuditQuestion) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "AuditQuestion{" +
            "id=" + getId() +
            ", questionCode='" + getQuestionCode() + "'" +
            ", label='" + getLabel() + "'" +
            ", answerType='" + getAnswerType() + "'" +
            ", sortOrder=" + getSortOrder() +
            ", mandatory='" + getMandatory() + "'" +
            "}";
    }
}

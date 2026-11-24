package ar.com.heliguard.app.domain;

import ar.com.heliguard.app.domain.enumeration.FindingSeverity;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A AuditFinding.
 */
@Table("audit_finding")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class AuditFinding implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @NotNull
    @Size(max = 160)
    @Column("title")
    private String title;

    @NotNull
    @Column("severity")
    private FindingSeverity severity;

    @Column("description")
    private String description;

    @Column("recommendation")
    private String recommendation;

    @Column("resolved_at")
    private Instant resolvedAt;

    @org.springframework.data.annotation.Transient
    private Organization organization;

    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "organization", "installation", "auditTemplate", "auditorMembership" }, allowSetters = true)
    private AuditReport auditReport;

    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "organization", "installation", "equipmentModel", "solarString" }, allowSetters = true)
    private Equipment equipment;

    @Column("organization_id")
    private Long organizationId;

    @Column("audit_report_id")
    private Long auditReportId;

    @Column("equipment_id")
    private Long equipmentId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public AuditFinding id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return this.title;
    }

    public AuditFinding title(String title) {
        this.setTitle(title);
        return this;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public FindingSeverity getSeverity() {
        return this.severity;
    }

    public AuditFinding severity(FindingSeverity severity) {
        this.setSeverity(severity);
        return this;
    }

    public void setSeverity(FindingSeverity severity) {
        this.severity = severity;
    }

    public String getDescription() {
        return this.description;
    }

    public AuditFinding description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getRecommendation() {
        return this.recommendation;
    }

    public AuditFinding recommendation(String recommendation) {
        this.setRecommendation(recommendation);
        return this;
    }

    public void setRecommendation(String recommendation) {
        this.recommendation = recommendation;
    }

    public Instant getResolvedAt() {
        return this.resolvedAt;
    }

    public AuditFinding resolvedAt(Instant resolvedAt) {
        this.setResolvedAt(resolvedAt);
        return this;
    }

    public void setResolvedAt(Instant resolvedAt) {
        this.resolvedAt = resolvedAt;
    }

    public Organization getOrganization() {
        return this.organization;
    }

    public void setOrganization(Organization organization) {
        this.organization = organization;
        this.organizationId = organization != null ? organization.getId() : null;
    }

    public AuditFinding organization(Organization organization) {
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

    public AuditFinding auditReport(AuditReport auditReport) {
        this.setAuditReport(auditReport);
        return this;
    }

    public Equipment getEquipment() {
        return this.equipment;
    }

    public void setEquipment(Equipment equipment) {
        this.equipment = equipment;
        this.equipmentId = equipment != null ? equipment.getId() : null;
    }

    public AuditFinding equipment(Equipment equipment) {
        this.setEquipment(equipment);
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

    public Long getEquipmentId() {
        return this.equipmentId;
    }

    public void setEquipmentId(Long equipment) {
        this.equipmentId = equipment;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AuditFinding)) {
            return false;
        }
        return getId() != null && getId().equals(((AuditFinding) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "AuditFinding{" +
            "id=" + getId() +
            ", title='" + getTitle() + "'" +
            ", severity='" + getSeverity() + "'" +
            ", description='" + getDescription() + "'" +
            ", recommendation='" + getRecommendation() + "'" +
            ", resolvedAt='" + getResolvedAt() + "'" +
            "}";
    }
}

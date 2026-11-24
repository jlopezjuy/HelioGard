package ar.com.heliguard.app.service.dto;

import ar.com.heliguard.app.domain.enumeration.FindingSeverity;
import jakarta.persistence.Lob;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link ar.com.heliguard.app.domain.AuditFinding} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class AuditFindingDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(max = 160)
    private String title;

    @NotNull
    private FindingSeverity severity;

    @Lob
    private String description;

    @Lob
    private String recommendation;

    private Instant resolvedAt;

    @NotNull
    private OrganizationDTO organization;

    @NotNull
    private AuditReportDTO auditReport;

    private EquipmentDTO equipment;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public FindingSeverity getSeverity() {
        return severity;
    }

    public void setSeverity(FindingSeverity severity) {
        this.severity = severity;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getRecommendation() {
        return recommendation;
    }

    public void setRecommendation(String recommendation) {
        this.recommendation = recommendation;
    }

    public Instant getResolvedAt() {
        return resolvedAt;
    }

    public void setResolvedAt(Instant resolvedAt) {
        this.resolvedAt = resolvedAt;
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

    public EquipmentDTO getEquipment() {
        return equipment;
    }

    public void setEquipment(EquipmentDTO equipment) {
        this.equipment = equipment;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AuditFindingDTO)) {
            return false;
        }

        AuditFindingDTO auditFindingDTO = (AuditFindingDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, auditFindingDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "AuditFindingDTO{" +
            "id=" + getId() +
            ", title='" + getTitle() + "'" +
            ", severity='" + getSeverity() + "'" +
            ", description='" + getDescription() + "'" +
            ", recommendation='" + getRecommendation() + "'" +
            ", resolvedAt='" + getResolvedAt() + "'" +
            ", organization=" + getOrganization() +
            ", auditReport=" + getAuditReport() +
            ", equipment=" + getEquipment() +
            "}";
    }
}

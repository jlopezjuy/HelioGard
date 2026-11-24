package ar.com.heliguard.app.domain;

import ar.com.heliguard.app.domain.enumeration.LocalizationScope;
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
 * A IncidentLocalization.
 */
@Table("incident_localization")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class IncidentLocalization implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @NotNull
    @Column("scope")
    private LocalizationScope scope;

    @NotNull
    @Column("located_at")
    private Instant locatedAt;

    @DecimalMin(value = "0")
    @DecimalMax(value = "100")
    @Column("confidence_pct")
    private BigDecimal confidencePct;

    @Column("evidence_summary")
    private String evidenceSummary;

    @org.springframework.data.annotation.Transient
    private Organization organization;

    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "organization", "installation", "alertRuleVersion" }, allowSetters = true)
    private Incident incident;

    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "organization", "installation" }, allowSetters = true)
    private SolarSector solarSector;

    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "organization", "solarArray", "moduleModel" }, allowSetters = true)
    private SolarString solarString;

    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "organization", "installation", "equipmentModel", "solarString" }, allowSetters = true)
    private Equipment equipment;

    @Column("organization_id")
    private Long organizationId;

    @Column("incident_id")
    private Long incidentId;

    @Column("solar_sector_id")
    private Long solarSectorId;

    @Column("solar_string_id")
    private Long solarStringId;

    @Column("equipment_id")
    private Long equipmentId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public IncidentLocalization id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalizationScope getScope() {
        return this.scope;
    }

    public IncidentLocalization scope(LocalizationScope scope) {
        this.setScope(scope);
        return this;
    }

    public void setScope(LocalizationScope scope) {
        this.scope = scope;
    }

    public Instant getLocatedAt() {
        return this.locatedAt;
    }

    public IncidentLocalization locatedAt(Instant locatedAt) {
        this.setLocatedAt(locatedAt);
        return this;
    }

    public void setLocatedAt(Instant locatedAt) {
        this.locatedAt = locatedAt;
    }

    public BigDecimal getConfidencePct() {
        return this.confidencePct;
    }

    public IncidentLocalization confidencePct(BigDecimal confidencePct) {
        this.setConfidencePct(confidencePct);
        return this;
    }

    public void setConfidencePct(BigDecimal confidencePct) {
        this.confidencePct = confidencePct;
    }

    public String getEvidenceSummary() {
        return this.evidenceSummary;
    }

    public IncidentLocalization evidenceSummary(String evidenceSummary) {
        this.setEvidenceSummary(evidenceSummary);
        return this;
    }

    public void setEvidenceSummary(String evidenceSummary) {
        this.evidenceSummary = evidenceSummary;
    }

    public Organization getOrganization() {
        return this.organization;
    }

    public void setOrganization(Organization organization) {
        this.organization = organization;
        this.organizationId = organization != null ? organization.getId() : null;
    }

    public IncidentLocalization organization(Organization organization) {
        this.setOrganization(organization);
        return this;
    }

    public Incident getIncident() {
        return this.incident;
    }

    public void setIncident(Incident incident) {
        this.incident = incident;
        this.incidentId = incident != null ? incident.getId() : null;
    }

    public IncidentLocalization incident(Incident incident) {
        this.setIncident(incident);
        return this;
    }

    public SolarSector getSolarSector() {
        return this.solarSector;
    }

    public void setSolarSector(SolarSector solarSector) {
        this.solarSector = solarSector;
        this.solarSectorId = solarSector != null ? solarSector.getId() : null;
    }

    public IncidentLocalization solarSector(SolarSector solarSector) {
        this.setSolarSector(solarSector);
        return this;
    }

    public SolarString getSolarString() {
        return this.solarString;
    }

    public void setSolarString(SolarString solarString) {
        this.solarString = solarString;
        this.solarStringId = solarString != null ? solarString.getId() : null;
    }

    public IncidentLocalization solarString(SolarString solarString) {
        this.setSolarString(solarString);
        return this;
    }

    public Equipment getEquipment() {
        return this.equipment;
    }

    public void setEquipment(Equipment equipment) {
        this.equipment = equipment;
        this.equipmentId = equipment != null ? equipment.getId() : null;
    }

    public IncidentLocalization equipment(Equipment equipment) {
        this.setEquipment(equipment);
        return this;
    }

    public Long getOrganizationId() {
        return this.organizationId;
    }

    public void setOrganizationId(Long organization) {
        this.organizationId = organization;
    }

    public Long getIncidentId() {
        return this.incidentId;
    }

    public void setIncidentId(Long incident) {
        this.incidentId = incident;
    }

    public Long getSolarSectorId() {
        return this.solarSectorId;
    }

    public void setSolarSectorId(Long solarSector) {
        this.solarSectorId = solarSector;
    }

    public Long getSolarStringId() {
        return this.solarStringId;
    }

    public void setSolarStringId(Long solarString) {
        this.solarStringId = solarString;
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
        if (!(o instanceof IncidentLocalization)) {
            return false;
        }
        return getId() != null && getId().equals(((IncidentLocalization) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "IncidentLocalization{" +
            "id=" + getId() +
            ", scope='" + getScope() + "'" +
            ", locatedAt='" + getLocatedAt() + "'" +
            ", confidencePct=" + getConfidencePct() +
            ", evidenceSummary='" + getEvidenceSummary() + "'" +
            "}";
    }
}

package ar.com.heliguard.app.service.dto;

import ar.com.heliguard.app.domain.enumeration.LocalizationScope;
import jakarta.persistence.Lob;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link ar.com.heliguard.app.domain.IncidentLocalization} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class IncidentLocalizationDTO implements Serializable {

    private Long id;

    @NotNull
    private LocalizationScope scope;

    @NotNull
    private Instant locatedAt;

    @DecimalMin(value = "0")
    @DecimalMax(value = "100")
    private BigDecimal confidencePct;

    @Lob
    private String evidenceSummary;

    private OrganizationDTO organization;

    @NotNull
    private IncidentDTO incident;

    private SolarSectorDTO solarSector;

    private SolarStringDTO solarString;

    private EquipmentDTO equipment;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalizationScope getScope() {
        return scope;
    }

    public void setScope(LocalizationScope scope) {
        this.scope = scope;
    }

    public Instant getLocatedAt() {
        return locatedAt;
    }

    public void setLocatedAt(Instant locatedAt) {
        this.locatedAt = locatedAt;
    }

    public BigDecimal getConfidencePct() {
        return confidencePct;
    }

    public void setConfidencePct(BigDecimal confidencePct) {
        this.confidencePct = confidencePct;
    }

    public String getEvidenceSummary() {
        return evidenceSummary;
    }

    public void setEvidenceSummary(String evidenceSummary) {
        this.evidenceSummary = evidenceSummary;
    }

    public OrganizationDTO getOrganization() {
        return organization;
    }

    public void setOrganization(OrganizationDTO organization) {
        this.organization = organization;
    }

    public IncidentDTO getIncident() {
        return incident;
    }

    public void setIncident(IncidentDTO incident) {
        this.incident = incident;
    }

    public SolarSectorDTO getSolarSector() {
        return solarSector;
    }

    public void setSolarSector(SolarSectorDTO solarSector) {
        this.solarSector = solarSector;
    }

    public SolarStringDTO getSolarString() {
        return solarString;
    }

    public void setSolarString(SolarStringDTO solarString) {
        this.solarString = solarString;
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
        if (!(o instanceof IncidentLocalizationDTO)) {
            return false;
        }

        IncidentLocalizationDTO incidentLocalizationDTO = (IncidentLocalizationDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, incidentLocalizationDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "IncidentLocalizationDTO{" +
            "id=" + getId() +
            ", scope='" + getScope() + "'" +
            ", locatedAt='" + getLocatedAt() + "'" +
            ", confidencePct=" + getConfidencePct() +
            ", evidenceSummary='" + getEvidenceSummary() + "'" +
            ", organization=" + getOrganization() +
            ", incident=" + getIncident() +
            ", solarSector=" + getSolarSector() +
            ", solarString=" + getSolarString() +
            ", equipment=" + getEquipment() +
            "}";
    }
}

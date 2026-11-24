package ar.com.heliguard.app.service.dto;

import ar.com.heliguard.app.domain.enumeration.AlertSeverity;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link ar.com.heliguard.app.domain.AlertRule} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class AlertRuleDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(max = 120)
    private String name;

    @NotNull
    @Size(max = 100)
    private String ruleCode;

    @NotNull
    private AlertSeverity severity;

    @NotNull
    private Boolean enabled;

    private OrganizationDTO organization;

    private InstallationDTO installation;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRuleCode() {
        return ruleCode;
    }

    public void setRuleCode(String ruleCode) {
        this.ruleCode = ruleCode;
    }

    public AlertSeverity getSeverity() {
        return severity;
    }

    public void setSeverity(AlertSeverity severity) {
        this.severity = severity;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public OrganizationDTO getOrganization() {
        return organization;
    }

    public void setOrganization(OrganizationDTO organization) {
        this.organization = organization;
    }

    public InstallationDTO getInstallation() {
        return installation;
    }

    public void setInstallation(InstallationDTO installation) {
        this.installation = installation;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AlertRuleDTO)) {
            return false;
        }

        AlertRuleDTO alertRuleDTO = (AlertRuleDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, alertRuleDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "AlertRuleDTO{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", ruleCode='" + getRuleCode() + "'" +
            ", severity='" + getSeverity() + "'" +
            ", enabled='" + getEnabled() + "'" +
            ", organization=" + getOrganization() +
            ", installation=" + getInstallation() +
            "}";
    }
}

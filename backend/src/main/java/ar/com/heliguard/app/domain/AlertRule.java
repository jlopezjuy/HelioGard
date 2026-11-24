package ar.com.heliguard.app.domain;

import ar.com.heliguard.app.domain.enumeration.AlertSeverity;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A AlertRule.
 */
@Table("alert_rule")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class AlertRule implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @NotNull
    @Size(max = 120)
    @Column("name")
    private String name;

    @NotNull
    @Size(max = 100)
    @Column("rule_code")
    private String ruleCode;

    @NotNull
    @Column("severity")
    private AlertSeverity severity;

    @NotNull
    @Column("enabled")
    private Boolean enabled;

    @org.springframework.data.annotation.Transient
    private Organization organization;

    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "organization", "site", "climateRiskProfile" }, allowSetters = true)
    private Installation installation;

    @Column("organization_id")
    private Long organizationId;

    @Column("installation_id")
    private Long installationId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public AlertRule id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public AlertRule name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRuleCode() {
        return this.ruleCode;
    }

    public AlertRule ruleCode(String ruleCode) {
        this.setRuleCode(ruleCode);
        return this;
    }

    public void setRuleCode(String ruleCode) {
        this.ruleCode = ruleCode;
    }

    public AlertSeverity getSeverity() {
        return this.severity;
    }

    public AlertRule severity(AlertSeverity severity) {
        this.setSeverity(severity);
        return this;
    }

    public void setSeverity(AlertSeverity severity) {
        this.severity = severity;
    }

    public Boolean getEnabled() {
        return this.enabled;
    }

    public AlertRule enabled(Boolean enabled) {
        this.setEnabled(enabled);
        return this;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public Organization getOrganization() {
        return this.organization;
    }

    public void setOrganization(Organization organization) {
        this.organization = organization;
        this.organizationId = organization != null ? organization.getId() : null;
    }

    public AlertRule organization(Organization organization) {
        this.setOrganization(organization);
        return this;
    }

    public Installation getInstallation() {
        return this.installation;
    }

    public void setInstallation(Installation installation) {
        this.installation = installation;
        this.installationId = installation != null ? installation.getId() : null;
    }

    public AlertRule installation(Installation installation) {
        this.setInstallation(installation);
        return this;
    }

    public Long getOrganizationId() {
        return this.organizationId;
    }

    public void setOrganizationId(Long organization) {
        this.organizationId = organization;
    }

    public Long getInstallationId() {
        return this.installationId;
    }

    public void setInstallationId(Long installation) {
        this.installationId = installation;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AlertRule)) {
            return false;
        }
        return getId() != null && getId().equals(((AlertRule) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "AlertRule{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", ruleCode='" + getRuleCode() + "'" +
            ", severity='" + getSeverity() + "'" +
            ", enabled='" + getEnabled() + "'" +
            "}";
    }
}

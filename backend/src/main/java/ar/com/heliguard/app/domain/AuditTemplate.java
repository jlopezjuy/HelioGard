package ar.com.heliguard.app.domain;

import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A AuditTemplate.
 */
@Table("audit_template")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class AuditTemplate implements Serializable {

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
    @Size(max = 50)
    @Column("template_version")
    private String templateVersion;

    @NotNull
    @Column("active")
    private Boolean active;

    @Column("published_at")
    private Instant publishedAt;

    @org.springframework.data.annotation.Transient
    private Organization organization;

    @Column("organization_id")
    private Long organizationId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public AuditTemplate id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return this.title;
    }

    public AuditTemplate title(String title) {
        this.setTitle(title);
        return this;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTemplateVersion() {
        return this.templateVersion;
    }

    public AuditTemplate templateVersion(String templateVersion) {
        this.setTemplateVersion(templateVersion);
        return this;
    }

    public void setTemplateVersion(String templateVersion) {
        this.templateVersion = templateVersion;
    }

    public Boolean getActive() {
        return this.active;
    }

    public AuditTemplate active(Boolean active) {
        this.setActive(active);
        return this;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public Instant getPublishedAt() {
        return this.publishedAt;
    }

    public AuditTemplate publishedAt(Instant publishedAt) {
        this.setPublishedAt(publishedAt);
        return this;
    }

    public void setPublishedAt(Instant publishedAt) {
        this.publishedAt = publishedAt;
    }

    public Organization getOrganization() {
        return this.organization;
    }

    public void setOrganization(Organization organization) {
        this.organization = organization;
        this.organizationId = organization != null ? organization.getId() : null;
    }

    public AuditTemplate organization(Organization organization) {
        this.setOrganization(organization);
        return this;
    }

    public Long getOrganizationId() {
        return this.organizationId;
    }

    public void setOrganizationId(Long organization) {
        this.organizationId = organization;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AuditTemplate)) {
            return false;
        }
        return getId() != null && getId().equals(((AuditTemplate) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "AuditTemplate{" +
            "id=" + getId() +
            ", title='" + getTitle() + "'" +
            ", templateVersion='" + getTemplateVersion() + "'" +
            ", active='" + getActive() + "'" +
            ", publishedAt='" + getPublishedAt() + "'" +
            "}";
    }
}

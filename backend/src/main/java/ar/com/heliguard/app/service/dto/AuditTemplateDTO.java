package ar.com.heliguard.app.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link ar.com.heliguard.app.domain.AuditTemplate} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class AuditTemplateDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(max = 160)
    private String title;

    @NotNull
    @Size(max = 50)
    private String templateVersion;

    @NotNull
    private Boolean active;

    private Instant publishedAt;

    private OrganizationDTO organization;

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

    public String getTemplateVersion() {
        return templateVersion;
    }

    public void setTemplateVersion(String templateVersion) {
        this.templateVersion = templateVersion;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public Instant getPublishedAt() {
        return publishedAt;
    }

    public void setPublishedAt(Instant publishedAt) {
        this.publishedAt = publishedAt;
    }

    public OrganizationDTO getOrganization() {
        return organization;
    }

    public void setOrganization(OrganizationDTO organization) {
        this.organization = organization;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AuditTemplateDTO)) {
            return false;
        }

        AuditTemplateDTO auditTemplateDTO = (AuditTemplateDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, auditTemplateDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "AuditTemplateDTO{" +
            "id=" + getId() +
            ", title='" + getTitle() + "'" +
            ", templateVersion='" + getTemplateVersion() + "'" +
            ", active='" + getActive() + "'" +
            ", publishedAt='" + getPublishedAt() + "'" +
            ", organization=" + getOrganization() +
            "}";
    }
}

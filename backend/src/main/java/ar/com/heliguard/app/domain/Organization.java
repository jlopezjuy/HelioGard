package ar.com.heliguard.app.domain;

import ar.com.heliguard.app.domain.enumeration.OrganizationKind;
import ar.com.heliguard.app.domain.enumeration.OrganizationStatus;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A Organization.
 */
@Table("organization")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Organization implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @NotNull
    @Size(max = 160)
    @Column("name")
    private String name;

    @NotNull
    @Size(max = 80)
    @Column("slug")
    private String slug;

    @NotNull
    @Column("status")
    private OrganizationStatus status;

    @NotNull
    @Column("kind")
    private OrganizationKind kind;

    @NotNull
    @Column("created_at")
    private Instant createdAt;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Organization id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public Organization name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSlug() {
        return this.slug;
    }

    public Organization slug(String slug) {
        this.setSlug(slug);
        return this;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public OrganizationStatus getStatus() {
        return this.status;
    }

    public Organization status(OrganizationStatus status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(OrganizationStatus status) {
        this.status = status;
    }

    public OrganizationKind getKind() {
        return this.kind;
    }

    public Organization kind(OrganizationKind kind) {
        this.setKind(kind);
        return this;
    }

    public void setKind(OrganizationKind kind) {
        this.kind = kind;
    }

    public Instant getCreatedAt() {
        return this.createdAt;
    }

    public Organization createdAt(Instant createdAt) {
        this.setCreatedAt(createdAt);
        return this;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Organization)) {
            return false;
        }
        return getId() != null && getId().equals(((Organization) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Organization{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", slug='" + getSlug() + "'" +
            ", status='" + getStatus() + "'" +
            ", kind='" + getKind() + "'" +
            ", createdAt='" + getCreatedAt() + "'" +
            "}";
    }
}

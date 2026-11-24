package ar.com.heliguard.app.domain;

import ar.com.heliguard.app.domain.enumeration.MembershipRole;
import ar.com.heliguard.app.domain.enumeration.MembershipStatus;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A OrganizationMembership.
 */
@Table("organization_membership")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class OrganizationMembership implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @NotNull
    @Column("role")
    private MembershipRole role;

    @NotNull
    @Column("status")
    private MembershipStatus status;

    @Column("invited_at")
    private Instant invitedAt;

    @Column("activated_at")
    private Instant activatedAt;

    @org.springframework.data.annotation.Transient
    private Organization organization;

    @org.springframework.data.annotation.Transient
    private User member;

    @Column("organization_id")
    private Long organizationId;

    @Column("member_id")
    private Long memberId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public OrganizationMembership id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public MembershipRole getRole() {
        return this.role;
    }

    public OrganizationMembership role(MembershipRole role) {
        this.setRole(role);
        return this;
    }

    public void setRole(MembershipRole role) {
        this.role = role;
    }

    public MembershipStatus getStatus() {
        return this.status;
    }

    public OrganizationMembership status(MembershipStatus status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(MembershipStatus status) {
        this.status = status;
    }

    public Instant getInvitedAt() {
        return this.invitedAt;
    }

    public OrganizationMembership invitedAt(Instant invitedAt) {
        this.setInvitedAt(invitedAt);
        return this;
    }

    public void setInvitedAt(Instant invitedAt) {
        this.invitedAt = invitedAt;
    }

    public Instant getActivatedAt() {
        return this.activatedAt;
    }

    public OrganizationMembership activatedAt(Instant activatedAt) {
        this.setActivatedAt(activatedAt);
        return this;
    }

    public void setActivatedAt(Instant activatedAt) {
        this.activatedAt = activatedAt;
    }

    public Organization getOrganization() {
        return this.organization;
    }

    public void setOrganization(Organization organization) {
        this.organization = organization;
        this.organizationId = organization != null ? organization.getId() : null;
    }

    public OrganizationMembership organization(Organization organization) {
        this.setOrganization(organization);
        return this;
    }

    public User getMember() {
        return this.member;
    }

    public void setMember(User user) {
        this.member = user;
        this.memberId = user != null ? user.getId() : null;
    }

    public OrganizationMembership member(User user) {
        this.setMember(user);
        return this;
    }

    public Long getOrganizationId() {
        return this.organizationId;
    }

    public void setOrganizationId(Long organization) {
        this.organizationId = organization;
    }

    public Long getMemberId() {
        return this.memberId;
    }

    public void setMemberId(Long user) {
        this.memberId = user;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof OrganizationMembership)) {
            return false;
        }
        return getId() != null && getId().equals(((OrganizationMembership) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "OrganizationMembership{" +
            "id=" + getId() +
            ", role='" + getRole() + "'" +
            ", status='" + getStatus() + "'" +
            ", invitedAt='" + getInvitedAt() + "'" +
            ", activatedAt='" + getActivatedAt() + "'" +
            "}";
    }
}

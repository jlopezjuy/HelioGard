package ar.com.heliguard.app.service.dto;

import ar.com.heliguard.app.domain.enumeration.MembershipRole;
import ar.com.heliguard.app.domain.enumeration.MembershipStatus;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link ar.com.heliguard.app.domain.OrganizationMembership} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class OrganizationMembershipDTO implements Serializable {

    private Long id;

    @NotNull
    private MembershipRole role;

    @NotNull
    private MembershipStatus status;

    private Instant invitedAt;

    private Instant activatedAt;

    @NotNull
    private OrganizationDTO organization;

    @NotNull
    private UserDTO member;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public MembershipRole getRole() {
        return role;
    }

    public void setRole(MembershipRole role) {
        this.role = role;
    }

    public MembershipStatus getStatus() {
        return status;
    }

    public void setStatus(MembershipStatus status) {
        this.status = status;
    }

    public Instant getInvitedAt() {
        return invitedAt;
    }

    public void setInvitedAt(Instant invitedAt) {
        this.invitedAt = invitedAt;
    }

    public Instant getActivatedAt() {
        return activatedAt;
    }

    public void setActivatedAt(Instant activatedAt) {
        this.activatedAt = activatedAt;
    }

    public OrganizationDTO getOrganization() {
        return organization;
    }

    public void setOrganization(OrganizationDTO organization) {
        this.organization = organization;
    }

    public UserDTO getMember() {
        return member;
    }

    public void setMember(UserDTO member) {
        this.member = member;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof OrganizationMembershipDTO)) {
            return false;
        }

        OrganizationMembershipDTO organizationMembershipDTO = (OrganizationMembershipDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, organizationMembershipDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "OrganizationMembershipDTO{" +
            "id=" + getId() +
            ", role='" + getRole() + "'" +
            ", status='" + getStatus() + "'" +
            ", invitedAt='" + getInvitedAt() + "'" +
            ", activatedAt='" + getActivatedAt() + "'" +
            ", organization=" + getOrganization() +
            ", member=" + getMember() +
            "}";
    }
}

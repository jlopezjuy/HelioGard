package ar.com.heliguard.app.service.dto;

import ar.com.heliguard.app.domain.enumeration.MembershipRole;
import ar.com.heliguard.app.domain.enumeration.MembershipStatus;
import ar.com.heliguard.app.domain.enumeration.OrganizationStatus;
import java.io.Serializable;
import java.util.List;

/**
 * Current user tenant context returned by {@code GET /api/me/context}.
 */
public class MeContextDTO implements Serializable {

    private Long userId;
    private String login;
    private Long activeOrganizationId;
    private MembershipRole activeRole;
    private boolean selectionRequired;
    private List<MembershipSummary> memberships = List.of();

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public Long getActiveOrganizationId() {
        return activeOrganizationId;
    }

    public void setActiveOrganizationId(Long activeOrganizationId) {
        this.activeOrganizationId = activeOrganizationId;
    }

    public MembershipRole getActiveRole() {
        return activeRole;
    }

    public void setActiveRole(MembershipRole activeRole) {
        this.activeRole = activeRole;
    }

    public boolean isSelectionRequired() {
        return selectionRequired;
    }

    public void setSelectionRequired(boolean selectionRequired) {
        this.selectionRequired = selectionRequired;
    }

    public List<MembershipSummary> getMemberships() {
        return memberships;
    }

    public void setMemberships(List<MembershipSummary> memberships) {
        this.memberships = memberships;
    }

    public static class MembershipSummary implements Serializable {

        private Long organizationId;
        private String organizationName;
        private String organizationSlug;
        private OrganizationStatus organizationStatus;
        private MembershipRole role;
        private MembershipStatus membershipStatus;

        public Long getOrganizationId() {
            return organizationId;
        }

        public void setOrganizationId(Long organizationId) {
            this.organizationId = organizationId;
        }

        public String getOrganizationName() {
            return organizationName;
        }

        public void setOrganizationName(String organizationName) {
            this.organizationName = organizationName;
        }

        public String getOrganizationSlug() {
            return organizationSlug;
        }

        public void setOrganizationSlug(String organizationSlug) {
            this.organizationSlug = organizationSlug;
        }

        public OrganizationStatus getOrganizationStatus() {
            return organizationStatus;
        }

        public void setOrganizationStatus(OrganizationStatus organizationStatus) {
            this.organizationStatus = organizationStatus;
        }

        public MembershipRole getRole() {
            return role;
        }

        public void setRole(MembershipRole role) {
            this.role = role;
        }

        public MembershipStatus getMembershipStatus() {
            return membershipStatus;
        }

        public void setMembershipStatus(MembershipStatus membershipStatus) {
            this.membershipStatus = membershipStatus;
        }
    }
}

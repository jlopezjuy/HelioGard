package ar.com.heliguard.app.service.tenant;

import ar.com.heliguard.app.domain.Organization;
import ar.com.heliguard.app.domain.OrganizationMembership;
import ar.com.heliguard.app.domain.User;
import ar.com.heliguard.app.domain.enumeration.MembershipStatus;
import ar.com.heliguard.app.domain.enumeration.OrganizationStatus;
import ar.com.heliguard.app.repository.OrganizationMembershipRepository;
import ar.com.heliguard.app.repository.OrganizationRepository;
import ar.com.heliguard.app.repository.UserRepository;
import ar.com.heliguard.app.security.SecurityUtils;
import ar.com.heliguard.app.service.dto.MeContextDTO;
import ar.com.heliguard.app.service.dto.MeContextDTO.MembershipSummary;
import ar.com.heliguard.app.service.error.DomainRuleException;
import java.util.List;
import java.util.Optional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

/**
 * Resolves and validates the active tenant from the authenticated JWT and DB memberships.
 */
@Service
@Transactional(readOnly = true)
public class TenantContextService {

    private final UserRepository userRepository;
    private final OrganizationMembershipRepository membershipRepository;
    private final OrganizationRepository organizationRepository;

    public TenantContextService(
        UserRepository userRepository,
        OrganizationMembershipRepository membershipRepository,
        OrganizationRepository organizationRepository
    ) {
        this.userRepository = userRepository;
        this.membershipRepository = membershipRepository;
        this.organizationRepository = organizationRepository;
    }

    public Mono<CurrentTenant> requireCurrentTenant() {
        return SecurityUtils.getCurrentUserLogin()
            .switchIfEmpty(Mono.error(new DomainRuleException(HttpStatus.UNAUTHORIZED, "UNAUTHORIZED", "Authentication required")))
            .flatMap(this::loadActiveUser)
            .flatMap(user ->
                loadActiveMemberships(user.getId())
                    .zipWith(SecurityUtils.getCurrentActiveOrgId().map(Optional::of).defaultIfEmpty(Optional.empty()))
                    .flatMap(tuple -> resolveTenant(user, tuple.getT1(), tuple.getT2().orElse(null)))
            );
    }

    public Mono<Long> validateActiveMembershipForSwitch(String login, Long requestedOrganizationId) {
        return loadActiveUser(login)
            .flatMap(user -> loadActiveMemberships(user.getId()))
            .flatMap(active ->
                active
                    .stream()
                    .filter(pair -> pair.organization().getId().equals(requestedOrganizationId))
                    .findAny()
                    .map(pair -> Mono.just(pair.organization().getId()))
                    .orElseGet(() ->
                        Mono.error(
                            new DomainRuleException(
                                HttpStatus.FORBIDDEN,
                                "INVALID_ACTIVE_ORGANIZATION",
                                "User does not have an active membership in the requested active organization"
                            )
                        )
                    )
            );
    }

    public Mono<Long> resolveSingleActiveOrganizationIdForLogin(String login) {
        return userRepository
            .findOneByLogin(login)
            .filter(User::isActivated)
            .flatMap(user -> loadActiveMemberships(user.getId()))
            .flatMap(active -> active.size() == 1 ? Mono.just(active.get(0).organization().getId()) : Mono.empty());
    }

    public Mono<MeContextDTO> getCurrentUserContext() {
        return SecurityUtils.getCurrentUserLogin()
            .switchIfEmpty(Mono.error(new DomainRuleException(HttpStatus.UNAUTHORIZED, "UNAUTHORIZED", "Authentication required")))
            .flatMap(this::loadActiveUser)
            .flatMap(user ->
                loadActiveMemberships(user.getId())
                    .zipWith(SecurityUtils.getCurrentActiveOrgId().map(Optional::of).defaultIfEmpty(Optional.empty()))
                    .map(tuple -> buildMeContext(user, tuple.getT1(), tuple.getT2().orElse(null)))
            );
    }

    private Mono<User> loadActiveUser(String login) {
        return userRepository
            .findOneByLogin(login)
            .filter(User::isActivated)
            .switchIfEmpty(Mono.error(new DomainRuleException(HttpStatus.UNAUTHORIZED, "UNAUTHORIZED", "User is not active")));
    }

    private Mono<List<ActiveMembership>> loadActiveMemberships(Long userId) {
        return membershipRepository
            .findByMember(userId)
            .filter(m -> m.getStatus() == MembershipStatus.ACTIVE && m.getOrganizationId() != null)
            .flatMap(m ->
                organizationRepository
                    .findById(m.getOrganizationId())
                    .filter(org -> org.getStatus() == OrganizationStatus.ACTIVE)
                    .map(org -> new ActiveMembership(m, org))
            )
            .collectList();
    }

    private Mono<CurrentTenant> resolveTenant(User user, List<ActiveMembership> active, Long claimedOrgId) {
        if (active.isEmpty()) {
            return Mono.error(
                new DomainRuleException(HttpStatus.FORBIDDEN, "NO_ACTIVE_ORGANIZATION", "User has no active organization membership")
            );
        }
        if (active.size() == 1) {
            ActiveMembership single = active.get(0);
            if (claimedOrgId != null && !single.organization().getId().equals(claimedOrgId)) {
                return Mono.error(
                    new DomainRuleException(
                        HttpStatus.FORBIDDEN,
                        "INVALID_ACTIVE_ORGANIZATION",
                        "Selected active organization is not valid for the current user"
                    )
                );
            }
            return Mono.just(new CurrentTenant(single.organization().getId(), user.getId(), user.getLogin(), single.membership().getRole()));
        }
        if (claimedOrgId == null) {
            return Mono.error(
                new DomainRuleException(
                    HttpStatus.CONFLICT,
                    "TENANT_SELECTION_REQUIRED",
                    "User belongs to multiple active organizations; select an active organization"
                )
            );
        }
        return active
            .stream()
            .filter(pair -> pair.organization().getId().equals(claimedOrgId))
            .findAny()
            .map(pair ->
                Mono.just(new CurrentTenant(pair.organization().getId(), user.getId(), user.getLogin(), pair.membership().getRole()))
            )
            .orElseGet(() ->
                Mono.error(
                    new DomainRuleException(
                        HttpStatus.FORBIDDEN,
                        "INVALID_ACTIVE_ORGANIZATION",
                        "Selected active organization is not active or membership was revoked"
                    )
                )
            );
    }

    private MeContextDTO buildMeContext(User user, List<ActiveMembership> active, Long claimedOrgId) {
        MeContextDTO dto = new MeContextDTO();
        dto.setUserId(user.getId());
        dto.setLogin(user.getLogin());
        dto.setMemberships(
            active
                .stream()
                .map(pair -> {
                    MembershipSummary s = new MembershipSummary();
                    s.setOrganizationId(pair.organization().getId());
                    s.setOrganizationName(pair.organization().getName());
                    s.setOrganizationSlug(pair.organization().getSlug());
                    s.setOrganizationStatus(pair.organization().getStatus());
                    s.setRole(pair.membership().getRole());
                    s.setMembershipStatus(pair.membership().getStatus());
                    return s;
                })
                .toList()
        );

        if (active.size() == 1 && (claimedOrgId == null || active.get(0).organization().getId().equals(claimedOrgId))) {
            dto.setActiveOrganizationId(active.get(0).organization().getId());
            dto.setActiveRole(active.get(0).membership().getRole());
            dto.setSelectionRequired(false);
        } else if (active.size() > 1 && claimedOrgId != null) {
            active
                .stream()
                .filter(pair -> pair.organization().getId().equals(claimedOrgId))
                .findAny()
                .ifPresentOrElse(
                    pair -> {
                        dto.setActiveOrganizationId(pair.organization().getId());
                        dto.setActiveRole(pair.membership().getRole());
                        dto.setSelectionRequired(false);
                    },
                    () -> dto.setSelectionRequired(true)
                );
        } else {
            dto.setSelectionRequired(active.size() > 1);
        }
        return dto;
    }

    private record ActiveMembership(OrganizationMembership membership, Organization organization) {}
}

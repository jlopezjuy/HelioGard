package ar.com.heliguard.app.web.rest;

import static ar.com.heliguard.app.security.SecurityUtils.ACTIVE_ORG_CLAIM;
import static ar.com.heliguard.app.security.SecurityUtils.AUTHORITIES_CLAIM;
import static ar.com.heliguard.app.security.SecurityUtils.JWT_ALGORITHM;
import static ar.com.heliguard.app.security.SecurityUtils.USER_ID_CLAIM;

import ar.com.heliguard.app.security.DomainUserDetailsService.UserWithId;
import ar.com.heliguard.app.service.tenant.TenantContextService;
import ar.com.heliguard.app.web.rest.vm.LoginVM;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.security.Principal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

/**
 * Controller to authenticate users.
 */
@RestController
@RequestMapping("/api")
public class AuthenticateController {

    private static final Logger LOG = LoggerFactory.getLogger(AuthenticateController.class);

    private final JwtEncoder jwtEncoder;

    @Value("${jhipster.security.authentication.jwt.token-validity-in-seconds:0}")
    private long tokenValidityInSeconds;

    @Value("${jhipster.security.authentication.jwt.token-validity-in-seconds-for-remember-me:0}")
    private long tokenValidityInSecondsForRememberMe;

    private final ReactiveAuthenticationManager authenticationManager;

    private final TenantContextService tenantContextService;

    public AuthenticateController(
        JwtEncoder jwtEncoder,
        ReactiveAuthenticationManager authenticationManager,
        TenantContextService tenantContextService
    ) {
        this.jwtEncoder = jwtEncoder;
        this.authenticationManager = authenticationManager;
        this.tenantContextService = tenantContextService;
    }

    @PostMapping("/authenticate")
    public Mono<ResponseEntity<JWTToken>> authorize(@Valid @RequestBody Mono<LoginVM> loginVM) {
        return loginVM
            .flatMap(login ->
                authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(login.getUsername(), login.getPassword()))
                    .flatMap(auth ->
                        tenantContextService
                            .resolveSingleActiveOrganizationIdForLogin(auth.getName())
                            .map(Optional::of)
                            .defaultIfEmpty(Optional.empty())
                            .flatMap(orgId -> Mono.fromCallable(() -> this.createToken(auth, login.isRememberMe(), orgId.orElse(null))))
                    )
            )
            .map(jwt -> {
                var httpHeaders = new HttpHeaders();
                httpHeaders.setBearerAuth(jwt);
                return new ResponseEntity<>(new JWTToken(jwt), httpHeaders, HttpStatus.OK);
            });
    }

    /**
     * {@code POST /auth/active-organization} : validate membership in {@code organizationId}
     * and issue a new signed JWT containing the {@code active_org} claim.
     */
    @PostMapping("/auth/active-organization")
    public Mono<ResponseEntity<JWTToken>> selectActiveOrganization(@Valid @RequestBody Mono<ActiveOrganizationVM> request) {
        return Mono.zip(
            ReactiveSecurityContextHolder.getContext().map(SecurityContext::getAuthentication),
            request
        )
            .flatMap(tuple -> {
                Authentication auth = tuple.getT1();
                Long orgId = tuple.getT2().organizationId();
                return tenantContextService
                    .validateActiveMembershipForSwitch(auth.getName(), orgId)
                    .flatMap(validOrgId -> Mono.fromCallable(() -> this.createToken(auth, false, validOrgId)));
            })
            .map(jwt -> {
                var httpHeaders = new HttpHeaders();
                httpHeaders.setBearerAuth(jwt);
                return new ResponseEntity<>(new JWTToken(jwt), httpHeaders, HttpStatus.OK);
            });
    }

    /**
     * {@code GET /authenticate} : check if the user is authenticated.
     *
     * @return the {@link ResponseEntity} with status {@code 204 (No Content)},
     * or with status {@code 401 (Unauthorized)} if not authenticated.
     */
    @GetMapping("/authenticate")
    public ResponseEntity<Void> isAuthenticated(Principal principal) {
        LOG.debug("REST request to check if the current user is authenticated");
        return ResponseEntity.status(principal == null ? HttpStatus.UNAUTHORIZED : HttpStatus.NO_CONTENT).build();
    }

    public String createToken(Authentication authentication, boolean rememberMe) {
        return createToken(authentication, rememberMe, null);
    }

    public String createToken(Authentication authentication, boolean rememberMe, Long activeOrgId) {
        String authorities = authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.joining(" "));

        var now = Instant.now();
        Instant validity;
        if (rememberMe) {
            validity = now.plus(this.tokenValidityInSecondsForRememberMe, ChronoUnit.SECONDS);
        } else {
            validity = now.plus(this.tokenValidityInSeconds, ChronoUnit.SECONDS);
        }

        // @formatter:off
        JwtClaimsSet.Builder builder = JwtClaimsSet.builder()
            .issuedAt(now)
            .expiresAt(validity)
            .subject(authentication.getName())
            .claim(AUTHORITIES_CLAIM, authorities);
        if (authentication.getPrincipal() instanceof UserWithId user) {
            builder.claim(USER_ID_CLAIM, user.getId());
        } else if (authentication.getPrincipal() instanceof org.springframework.security.oauth2.core.ClaimAccessor claims) {
            Object uid = claims.getClaim(USER_ID_CLAIM);
            if (uid != null) {
                builder.claim(USER_ID_CLAIM, uid);
            }
        }
        if (activeOrgId != null) {
            builder.claim(ACTIVE_ORG_CLAIM, activeOrgId);
        }

        JwsHeader jwsHeader = JwsHeader.with(JWT_ALGORITHM).build();
        return this.jwtEncoder.encode(JwtEncoderParameters.from(jwsHeader, builder.build())).getTokenValue();
    }

    public record ActiveOrganizationVM(@NotNull Long organizationId) {}

    /**
     * Object to return as body in JWT Authentication.
     */
    static class JWTToken {

        private String idToken;

        JWTToken(String idToken) {
            this.idToken = idToken;
        }

        @JsonProperty("id_token")
        String getIdToken() {
            return idToken;
        }

        void setIdToken(String idToken) {
            this.idToken = idToken;
        }
    }
}

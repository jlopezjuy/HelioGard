package ar.com.heliguard.app.repository;

import ar.com.heliguard.app.domain.Organization;
import ar.com.heliguard.app.domain.OrganizationMembership;
import ar.com.heliguard.app.domain.SecurityCaseEvent;
import ar.com.heliguard.app.domain.SecurityIncident;
import java.util.List;
import java.util.Map;
import org.springframework.data.r2dbc.convert.R2dbcConverter;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.r2dbc.dialect.R2dbcDialect;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC custom repository implementation for the SecurityCaseEvent entity.
 */
@SuppressWarnings("unused")
class SecurityCaseEventRepositoryInternalImpl
    extends AbstractR2dbcRepository<SecurityCaseEvent, Long>
    implements SecurityCaseEventRepositoryInternal
{

    private static final String SELECT_WITH_RELATIONS_SQL =
        "SELECT e.* FROM security_case_event e" +
        " LEFT JOIN organization e_organization ON e.organization_id = e_organization.id" +
        " LEFT JOIN security_incident securityIncident ON e.security_incident_id = securityIncident.id";

    // The clients sort by the related entity display field, with the entity id as secondary sort.
    private static final Map<String, String> SORT_COLUMNS = Map.ofEntries(
        Map.entry("id", "e.id"),
        Map.entry("organization.id", "e_organization.id"),
        Map.entry("organization.name", "e_organization.name"),
        Map.entry("securityIncident.id", "securityIncident.id"),
        Map.entry("securityIncident.referenceCode", "securityIncident.reference_code"),
        Map.entry("actorMembership.id", "e.actor_membership_id")
    );

    public SecurityCaseEventRepositoryInternalImpl(R2dbcEntityTemplate template, R2dbcConverter converter, R2dbcDialect dialect) {
        super(SecurityCaseEvent.class, template, converter, dialect);
    }

    @Override
    protected String selectWithRelationsSql() {
        return SELECT_WITH_RELATIONS_SQL;
    }

    @Override
    protected Map<String, String> sortColumns() {
        return SORT_COLUMNS;
    }

    @Override
    protected Mono<List<SecurityCaseEvent>> populateRelationships(List<SecurityCaseEvent> entities) {
        reference(
            entities,
            SecurityCaseEvent::getActorMembershipId,
            OrganizationMembership::new,
            OrganizationMembership::setId,
            SecurityCaseEvent::setActorMembership
        );
        return Mono.just(entities)
            .flatMap(populated ->
                populate(
                    populated,
                    SecurityCaseEvent::getOrganizationId,
                    Organization.class,
                    Organization::getId,
                    SecurityCaseEvent::setOrganization
                )
            )
            .flatMap(populated ->
                populate(
                    populated,
                    SecurityCaseEvent::getSecurityIncidentId,
                    SecurityIncident.class,
                    SecurityIncident::getId,
                    SecurityCaseEvent::setSecurityIncident
                )
            );
    }
}

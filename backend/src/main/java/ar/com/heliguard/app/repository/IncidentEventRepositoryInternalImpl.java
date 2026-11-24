package ar.com.heliguard.app.repository;

import ar.com.heliguard.app.domain.Incident;
import ar.com.heliguard.app.domain.IncidentEvent;
import ar.com.heliguard.app.domain.Organization;
import java.util.List;
import java.util.Map;
import org.springframework.data.r2dbc.convert.R2dbcConverter;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.r2dbc.dialect.R2dbcDialect;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC custom repository implementation for the IncidentEvent entity.
 */
@SuppressWarnings("unused")
class IncidentEventRepositoryInternalImpl extends AbstractR2dbcRepository<IncidentEvent, Long> implements IncidentEventRepositoryInternal {

    private static final String SELECT_WITH_RELATIONS_SQL =
        "SELECT e.* FROM incident_event e" +
        " LEFT JOIN organization e_organization ON e.organization_id = e_organization.id" +
        " LEFT JOIN incident incident ON e.incident_id = incident.id";

    // The clients sort by the related entity display field, with the entity id as secondary sort.
    private static final Map<String, String> SORT_COLUMNS = Map.ofEntries(
        Map.entry("id", "e.id"),
        Map.entry("organization.id", "e_organization.id"),
        Map.entry("organization.name", "e_organization.name"),
        Map.entry("incident.id", "incident.id"),
        Map.entry("incident.title", "incident.title")
    );

    public IncidentEventRepositoryInternalImpl(R2dbcEntityTemplate template, R2dbcConverter converter, R2dbcDialect dialect) {
        super(IncidentEvent.class, template, converter, dialect);
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
    protected Mono<List<IncidentEvent>> populateRelationships(List<IncidentEvent> entities) {
        return Mono.just(entities)
            .flatMap(populated ->
                populate(
                    populated,
                    IncidentEvent::getOrganizationId,
                    Organization.class,
                    Organization::getId,
                    IncidentEvent::setOrganization
                )
            )
            .flatMap(populated ->
                populate(populated, IncidentEvent::getIncidentId, Incident.class, Incident::getId, IncidentEvent::setIncident)
            );
    }
}

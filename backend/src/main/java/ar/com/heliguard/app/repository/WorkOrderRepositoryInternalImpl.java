package ar.com.heliguard.app.repository;

import ar.com.heliguard.app.domain.Incident;
import ar.com.heliguard.app.domain.Installation;
import ar.com.heliguard.app.domain.Organization;
import ar.com.heliguard.app.domain.PlanSubscription;
import ar.com.heliguard.app.domain.WorkOrder;
import java.util.List;
import java.util.Map;
import org.springframework.data.r2dbc.convert.R2dbcConverter;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.r2dbc.dialect.R2dbcDialect;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC custom repository implementation for the WorkOrder entity.
 */
@SuppressWarnings("unused")
class WorkOrderRepositoryInternalImpl extends AbstractR2dbcRepository<WorkOrder, Long> implements WorkOrderRepositoryInternal {

    private static final String SELECT_WITH_RELATIONS_SQL =
        "SELECT e.* FROM work_order e" +
        " LEFT JOIN organization e_organization ON e.organization_id = e_organization.id" +
        " LEFT JOIN installation installation ON e.installation_id = installation.id" +
        " LEFT JOIN incident incident ON e.incident_id = incident.id";

    // The clients sort by the related entity display field, with the entity id as secondary sort.
    private static final Map<String, String> SORT_COLUMNS = Map.ofEntries(
        Map.entry("id", "e.id"),
        Map.entry("organization.id", "e_organization.id"),
        Map.entry("organization.name", "e_organization.name"),
        Map.entry("installation.id", "installation.id"),
        Map.entry("installation.name", "installation.name"),
        Map.entry("incident.id", "incident.id"),
        Map.entry("incident.title", "incident.title"),
        Map.entry("subscription.id", "e.subscription_id")
    );

    public WorkOrderRepositoryInternalImpl(R2dbcEntityTemplate template, R2dbcConverter converter, R2dbcDialect dialect) {
        super(WorkOrder.class, template, converter, dialect);
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
    protected Mono<List<WorkOrder>> populateRelationships(List<WorkOrder> entities) {
        reference(entities, WorkOrder::getSubscriptionId, PlanSubscription::new, PlanSubscription::setId, WorkOrder::setSubscription);
        return Mono.just(entities)
            .flatMap(populated ->
                populate(populated, WorkOrder::getOrganizationId, Organization.class, Organization::getId, WorkOrder::setOrganization)
            )
            .flatMap(populated ->
                populate(populated, WorkOrder::getInstallationId, Installation.class, Installation::getId, WorkOrder::setInstallation)
            )
            .flatMap(populated -> populate(populated, WorkOrder::getIncidentId, Incident.class, Incident::getId, WorkOrder::setIncident));
    }
}

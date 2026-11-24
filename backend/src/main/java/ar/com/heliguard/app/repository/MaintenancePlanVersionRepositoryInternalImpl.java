package ar.com.heliguard.app.repository;

import ar.com.heliguard.app.domain.MaintenancePlan;
import ar.com.heliguard.app.domain.MaintenancePlanVersion;
import ar.com.heliguard.app.domain.Organization;
import java.util.List;
import java.util.Map;
import org.springframework.data.r2dbc.convert.R2dbcConverter;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.r2dbc.dialect.R2dbcDialect;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC custom repository implementation for the MaintenancePlanVersion entity.
 */
@SuppressWarnings("unused")
class MaintenancePlanVersionRepositoryInternalImpl
    extends AbstractR2dbcRepository<MaintenancePlanVersion, Long>
    implements MaintenancePlanVersionRepositoryInternal
{

    private static final String SELECT_WITH_RELATIONS_SQL =
        "SELECT e.* FROM maintenance_plan_version e" +
        " LEFT JOIN organization e_organization ON e.organization_id = e_organization.id" +
        " LEFT JOIN maintenance_plan maintenancePlan ON e.maintenance_plan_id = maintenancePlan.id";

    // The clients sort by the related entity display field, with the entity id as secondary sort.
    private static final Map<String, String> SORT_COLUMNS = Map.ofEntries(
        Map.entry("id", "e.id"),
        Map.entry("organization.id", "e_organization.id"),
        Map.entry("organization.name", "e_organization.name"),
        Map.entry("maintenancePlan.id", "maintenancePlan.id"),
        Map.entry("maintenancePlan.name", "maintenancePlan.name")
    );

    public MaintenancePlanVersionRepositoryInternalImpl(R2dbcEntityTemplate template, R2dbcConverter converter, R2dbcDialect dialect) {
        super(MaintenancePlanVersion.class, template, converter, dialect);
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
    protected Mono<List<MaintenancePlanVersion>> populateRelationships(List<MaintenancePlanVersion> entities) {
        return Mono.just(entities)
            .flatMap(populated ->
                populate(
                    populated,
                    MaintenancePlanVersion::getOrganizationId,
                    Organization.class,
                    Organization::getId,
                    MaintenancePlanVersion::setOrganization
                )
            )
            .flatMap(populated ->
                populate(
                    populated,
                    MaintenancePlanVersion::getMaintenancePlanId,
                    MaintenancePlan.class,
                    MaintenancePlan::getId,
                    MaintenancePlanVersion::setMaintenancePlan
                )
            );
    }
}

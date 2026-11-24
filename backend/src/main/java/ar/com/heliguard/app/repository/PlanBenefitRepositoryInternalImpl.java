package ar.com.heliguard.app.repository;

import ar.com.heliguard.app.domain.MaintenancePlanVersion;
import ar.com.heliguard.app.domain.Organization;
import ar.com.heliguard.app.domain.PlanBenefit;
import java.util.List;
import java.util.Map;
import org.springframework.data.r2dbc.convert.R2dbcConverter;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.r2dbc.dialect.R2dbcDialect;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC custom repository implementation for the PlanBenefit entity.
 */
@SuppressWarnings("unused")
class PlanBenefitRepositoryInternalImpl extends AbstractR2dbcRepository<PlanBenefit, Long> implements PlanBenefitRepositoryInternal {

    private static final String SELECT_WITH_RELATIONS_SQL =
        "SELECT e.* FROM plan_benefit e" +
        " LEFT JOIN organization e_organization ON e.organization_id = e_organization.id" +
        " LEFT JOIN maintenance_plan_version planVersion ON e.plan_version_id = planVersion.id";

    // The clients sort by the related entity display field, with the entity id as secondary sort.
    private static final Map<String, String> SORT_COLUMNS = Map.ofEntries(
        Map.entry("id", "e.id"),
        Map.entry("organization.id", "e_organization.id"),
        Map.entry("organization.name", "e_organization.name"),
        Map.entry("planVersion.id", "planVersion.id"),
        Map.entry("planVersion.versionCode", "planVersion.version_code")
    );

    public PlanBenefitRepositoryInternalImpl(R2dbcEntityTemplate template, R2dbcConverter converter, R2dbcDialect dialect) {
        super(PlanBenefit.class, template, converter, dialect);
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
    protected Mono<List<PlanBenefit>> populateRelationships(List<PlanBenefit> entities) {
        return Mono.just(entities)
            .flatMap(populated ->
                populate(populated, PlanBenefit::getOrganizationId, Organization.class, Organization::getId, PlanBenefit::setOrganization)
            )
            .flatMap(populated ->
                populate(
                    populated,
                    PlanBenefit::getPlanVersionId,
                    MaintenancePlanVersion.class,
                    MaintenancePlanVersion::getId,
                    PlanBenefit::setPlanVersion
                )
            );
    }
}

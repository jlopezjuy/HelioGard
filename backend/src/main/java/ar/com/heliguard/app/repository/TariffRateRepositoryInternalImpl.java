package ar.com.heliguard.app.repository;

import ar.com.heliguard.app.domain.Organization;
import ar.com.heliguard.app.domain.TariffPlan;
import ar.com.heliguard.app.domain.TariffRate;
import java.util.List;
import java.util.Map;
import org.springframework.data.r2dbc.convert.R2dbcConverter;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.r2dbc.dialect.R2dbcDialect;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC custom repository implementation for the TariffRate entity.
 */
@SuppressWarnings("unused")
class TariffRateRepositoryInternalImpl extends AbstractR2dbcRepository<TariffRate, Long> implements TariffRateRepositoryInternal {

    private static final String SELECT_WITH_RELATIONS_SQL =
        "SELECT e.* FROM tariff_rate e" +
        " LEFT JOIN organization e_organization ON e.organization_id = e_organization.id" +
        " LEFT JOIN tariff_plan tariffPlan ON e.tariff_plan_id = tariffPlan.id";

    // The clients sort by the related entity display field, with the entity id as secondary sort.
    private static final Map<String, String> SORT_COLUMNS = Map.ofEntries(
        Map.entry("id", "e.id"),
        Map.entry("organization.id", "e_organization.id"),
        Map.entry("organization.name", "e_organization.name"),
        Map.entry("tariffPlan.id", "tariffPlan.id"),
        Map.entry("tariffPlan.name", "tariffPlan.name")
    );

    public TariffRateRepositoryInternalImpl(R2dbcEntityTemplate template, R2dbcConverter converter, R2dbcDialect dialect) {
        super(TariffRate.class, template, converter, dialect);
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
    protected Mono<List<TariffRate>> populateRelationships(List<TariffRate> entities) {
        return Mono.just(entities)
            .flatMap(populated ->
                populate(populated, TariffRate::getOrganizationId, Organization.class, Organization::getId, TariffRate::setOrganization)
            )
            .flatMap(populated ->
                populate(populated, TariffRate::getTariffPlanId, TariffPlan.class, TariffPlan::getId, TariffRate::setTariffPlan)
            );
    }
}

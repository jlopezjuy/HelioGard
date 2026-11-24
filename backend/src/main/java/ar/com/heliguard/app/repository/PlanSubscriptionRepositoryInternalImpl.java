package ar.com.heliguard.app.repository;

import ar.com.heliguard.app.domain.Installation;
import ar.com.heliguard.app.domain.MaintenancePlanVersion;
import ar.com.heliguard.app.domain.Organization;
import ar.com.heliguard.app.domain.PlanSubscription;
import ar.com.heliguard.app.domain.ServiceAgreement;
import java.util.List;
import java.util.Map;
import org.springframework.data.r2dbc.convert.R2dbcConverter;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.r2dbc.dialect.R2dbcDialect;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC custom repository implementation for the PlanSubscription entity.
 */
@SuppressWarnings("unused")
class PlanSubscriptionRepositoryInternalImpl
    extends AbstractR2dbcRepository<PlanSubscription, Long>
    implements PlanSubscriptionRepositoryInternal
{

    private static final String SELECT_WITH_RELATIONS_SQL =
        "SELECT e.* FROM plan_subscription e" +
        " LEFT JOIN organization e_organization ON e.organization_id = e_organization.id" +
        " LEFT JOIN installation installation ON e.installation_id = installation.id" +
        " LEFT JOIN maintenance_plan_version planVersion ON e.plan_version_id = planVersion.id";

    // The clients sort by the related entity display field, with the entity id as secondary sort.
    private static final Map<String, String> SORT_COLUMNS = Map.ofEntries(
        Map.entry("id", "e.id"),
        Map.entry("organization.id", "e_organization.id"),
        Map.entry("organization.name", "e_organization.name"),
        Map.entry("installation.id", "installation.id"),
        Map.entry("installation.name", "installation.name"),
        Map.entry("planVersion.id", "planVersion.id"),
        Map.entry("planVersion.versionCode", "planVersion.version_code"),
        Map.entry("serviceAgreement.id", "e.service_agreement_id")
    );

    public PlanSubscriptionRepositoryInternalImpl(R2dbcEntityTemplate template, R2dbcConverter converter, R2dbcDialect dialect) {
        super(PlanSubscription.class, template, converter, dialect);
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
    protected Mono<List<PlanSubscription>> populateRelationships(List<PlanSubscription> entities) {
        reference(
            entities,
            PlanSubscription::getServiceAgreementId,
            ServiceAgreement::new,
            ServiceAgreement::setId,
            PlanSubscription::setServiceAgreement
        );
        return Mono.just(entities)
            .flatMap(populated ->
                populate(
                    populated,
                    PlanSubscription::getOrganizationId,
                    Organization.class,
                    Organization::getId,
                    PlanSubscription::setOrganization
                )
            )
            .flatMap(populated ->
                populate(
                    populated,
                    PlanSubscription::getInstallationId,
                    Installation.class,
                    Installation::getId,
                    PlanSubscription::setInstallation
                )
            )
            .flatMap(populated ->
                populate(
                    populated,
                    PlanSubscription::getPlanVersionId,
                    MaintenancePlanVersion.class,
                    MaintenancePlanVersion::getId,
                    PlanSubscription::setPlanVersion
                )
            );
    }
}

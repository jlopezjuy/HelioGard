package ar.com.heliguard.app.repository;

import ar.com.heliguard.app.domain.Installation;
import ar.com.heliguard.app.domain.Organization;
import ar.com.heliguard.app.domain.ServiceAgreement;
import java.util.List;
import java.util.Map;
import org.springframework.data.r2dbc.convert.R2dbcConverter;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.r2dbc.dialect.R2dbcDialect;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC custom repository implementation for the ServiceAgreement entity.
 */
@SuppressWarnings("unused")
class ServiceAgreementRepositoryInternalImpl
    extends AbstractR2dbcRepository<ServiceAgreement, Long>
    implements ServiceAgreementRepositoryInternal
{

    private static final String SELECT_WITH_RELATIONS_SQL =
        "SELECT e.* FROM service_agreement e" +
        " LEFT JOIN organization e_organization ON e.organization_id = e_organization.id" +
        " LEFT JOIN organization providerOrganization ON e.provider_organization_id = providerOrganization.id" +
        " LEFT JOIN installation installation ON e.installation_id = installation.id";

    // The clients sort by the related entity display field, with the entity id as secondary sort.
    private static final Map<String, String> SORT_COLUMNS = Map.ofEntries(
        Map.entry("id", "e.id"),
        Map.entry("organization.id", "e_organization.id"),
        Map.entry("organization.name", "e_organization.name"),
        Map.entry("providerOrganization.id", "providerOrganization.id"),
        Map.entry("providerOrganization.name", "providerOrganization.name"),
        Map.entry("installation.id", "installation.id"),
        Map.entry("installation.name", "installation.name")
    );

    public ServiceAgreementRepositoryInternalImpl(R2dbcEntityTemplate template, R2dbcConverter converter, R2dbcDialect dialect) {
        super(ServiceAgreement.class, template, converter, dialect);
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
    protected Mono<List<ServiceAgreement>> populateRelationships(List<ServiceAgreement> entities) {
        return Mono.just(entities)
            .flatMap(populated ->
                populate(
                    populated,
                    ServiceAgreement::getOrganizationId,
                    Organization.class,
                    Organization::getId,
                    ServiceAgreement::setOrganization
                )
            )
            .flatMap(populated ->
                populate(
                    populated,
                    ServiceAgreement::getProviderOrganizationId,
                    Organization.class,
                    Organization::getId,
                    ServiceAgreement::setProviderOrganization
                )
            )
            .flatMap(populated ->
                populate(
                    populated,
                    ServiceAgreement::getInstallationId,
                    Installation.class,
                    Installation::getId,
                    ServiceAgreement::setInstallation
                )
            );
    }
}

package ar.com.heliguard.app.repository;

import ar.com.heliguard.app.domain.Installation;
import ar.com.heliguard.app.domain.IntegrityAssessment;
import ar.com.heliguard.app.domain.Organization;
import ar.com.heliguard.app.domain.SecurityIncident;
import java.util.List;
import java.util.Map;
import org.springframework.data.r2dbc.convert.R2dbcConverter;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.r2dbc.dialect.R2dbcDialect;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC custom repository implementation for the SecurityIncident entity.
 */
@SuppressWarnings("unused")
class SecurityIncidentRepositoryInternalImpl
    extends AbstractR2dbcRepository<SecurityIncident, Long>
    implements SecurityIncidentRepositoryInternal
{

    private static final String SELECT_WITH_RELATIONS_SQL =
        "SELECT e.* FROM security_incident e" +
        " LEFT JOIN organization e_organization ON e.organization_id = e_organization.id" +
        " LEFT JOIN installation installation ON e.installation_id = installation.id";

    // The clients sort by the related entity display field, with the entity id as secondary sort.
    private static final Map<String, String> SORT_COLUMNS = Map.ofEntries(
        Map.entry("id", "e.id"),
        Map.entry("organization.id", "e_organization.id"),
        Map.entry("organization.name", "e_organization.name"),
        Map.entry("installation.id", "installation.id"),
        Map.entry("installation.name", "installation.name"),
        Map.entry("integrityAssessment.id", "e.integrity_assessment_id")
    );

    public SecurityIncidentRepositoryInternalImpl(R2dbcEntityTemplate template, R2dbcConverter converter, R2dbcDialect dialect) {
        super(SecurityIncident.class, template, converter, dialect);
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
    protected Mono<List<SecurityIncident>> populateRelationships(List<SecurityIncident> entities) {
        reference(
            entities,
            SecurityIncident::getIntegrityAssessmentId,
            IntegrityAssessment::new,
            IntegrityAssessment::setId,
            SecurityIncident::setIntegrityAssessment
        );
        return Mono.just(entities)
            .flatMap(populated ->
                populate(
                    populated,
                    SecurityIncident::getOrganizationId,
                    Organization.class,
                    Organization::getId,
                    SecurityIncident::setOrganization
                )
            )
            .flatMap(populated ->
                populate(
                    populated,
                    SecurityIncident::getInstallationId,
                    Installation.class,
                    Installation::getId,
                    SecurityIncident::setInstallation
                )
            );
    }
}

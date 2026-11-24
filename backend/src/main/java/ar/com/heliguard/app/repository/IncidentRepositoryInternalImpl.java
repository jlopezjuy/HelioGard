package ar.com.heliguard.app.repository;

import ar.com.heliguard.app.domain.AlertRuleVersion;
import ar.com.heliguard.app.domain.Incident;
import ar.com.heliguard.app.domain.Installation;
import ar.com.heliguard.app.domain.Organization;
import java.util.List;
import java.util.Map;
import org.springframework.data.r2dbc.convert.R2dbcConverter;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.r2dbc.dialect.R2dbcDialect;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC custom repository implementation for the Incident entity.
 */
@SuppressWarnings("unused")
class IncidentRepositoryInternalImpl extends AbstractR2dbcRepository<Incident, Long> implements IncidentRepositoryInternal {

    private static final String SELECT_WITH_RELATIONS_SQL =
        "SELECT e.* FROM incident e" +
        " LEFT JOIN organization e_organization ON e.organization_id = e_organization.id" +
        " LEFT JOIN installation installation ON e.installation_id = installation.id" +
        " LEFT JOIN alert_rule_version alertRuleVersion ON e.alert_rule_version_id = alertRuleVersion.id";

    // The clients sort by the related entity display field, with the entity id as secondary sort.
    private static final Map<String, String> SORT_COLUMNS = Map.ofEntries(
        Map.entry("id", "e.id"),
        Map.entry("organization.id", "e_organization.id"),
        Map.entry("organization.name", "e_organization.name"),
        Map.entry("installation.id", "installation.id"),
        Map.entry("installation.name", "installation.name"),
        Map.entry("alertRuleVersion.id", "alertRuleVersion.id"),
        Map.entry("alertRuleVersion.versionCode", "alertRuleVersion.version_code")
    );

    public IncidentRepositoryInternalImpl(R2dbcEntityTemplate template, R2dbcConverter converter, R2dbcDialect dialect) {
        super(Incident.class, template, converter, dialect);
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
    protected Mono<List<Incident>> populateRelationships(List<Incident> entities) {
        return Mono.just(entities)
            .flatMap(populated ->
                populate(populated, Incident::getOrganizationId, Organization.class, Organization::getId, Incident::setOrganization)
            )
            .flatMap(populated ->
                populate(populated, Incident::getInstallationId, Installation.class, Installation::getId, Incident::setInstallation)
            )
            .flatMap(populated ->
                populate(
                    populated,
                    Incident::getAlertRuleVersionId,
                    AlertRuleVersion.class,
                    AlertRuleVersion::getId,
                    Incident::setAlertRuleVersion
                )
            );
    }
}

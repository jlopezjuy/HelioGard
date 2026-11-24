package ar.com.heliguard.app.repository;

import ar.com.heliguard.app.domain.AlertRule;
import ar.com.heliguard.app.domain.AlertRuleVersion;
import ar.com.heliguard.app.domain.Organization;
import java.util.List;
import java.util.Map;
import org.springframework.data.r2dbc.convert.R2dbcConverter;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.r2dbc.dialect.R2dbcDialect;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC custom repository implementation for the AlertRuleVersion entity.
 */
@SuppressWarnings("unused")
class AlertRuleVersionRepositoryInternalImpl
    extends AbstractR2dbcRepository<AlertRuleVersion, Long>
    implements AlertRuleVersionRepositoryInternal
{

    private static final String SELECT_WITH_RELATIONS_SQL =
        "SELECT e.* FROM alert_rule_version e" +
        " LEFT JOIN organization e_organization ON e.organization_id = e_organization.id" +
        " LEFT JOIN alert_rule alertRule ON e.alert_rule_id = alertRule.id";

    // The clients sort by the related entity display field, with the entity id as secondary sort.
    private static final Map<String, String> SORT_COLUMNS = Map.ofEntries(
        Map.entry("id", "e.id"),
        Map.entry("organization.id", "e_organization.id"),
        Map.entry("organization.name", "e_organization.name"),
        Map.entry("alertRule.id", "alertRule.id"),
        Map.entry("alertRule.ruleCode", "alertRule.rule_code")
    );

    public AlertRuleVersionRepositoryInternalImpl(R2dbcEntityTemplate template, R2dbcConverter converter, R2dbcDialect dialect) {
        super(AlertRuleVersion.class, template, converter, dialect);
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
    protected Mono<List<AlertRuleVersion>> populateRelationships(List<AlertRuleVersion> entities) {
        return Mono.just(entities)
            .flatMap(populated ->
                populate(
                    populated,
                    AlertRuleVersion::getOrganizationId,
                    Organization.class,
                    Organization::getId,
                    AlertRuleVersion::setOrganization
                )
            )
            .flatMap(populated ->
                populate(populated, AlertRuleVersion::getAlertRuleId, AlertRule.class, AlertRule::getId, AlertRuleVersion::setAlertRule)
            );
    }
}

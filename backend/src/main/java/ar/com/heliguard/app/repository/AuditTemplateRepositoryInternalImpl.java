package ar.com.heliguard.app.repository;

import ar.com.heliguard.app.domain.AuditTemplate;
import ar.com.heliguard.app.domain.Organization;
import java.util.List;
import java.util.Map;
import org.springframework.data.r2dbc.convert.R2dbcConverter;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.r2dbc.dialect.R2dbcDialect;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC custom repository implementation for the AuditTemplate entity.
 */
@SuppressWarnings("unused")
class AuditTemplateRepositoryInternalImpl extends AbstractR2dbcRepository<AuditTemplate, Long> implements AuditTemplateRepositoryInternal {

    private static final String SELECT_WITH_RELATIONS_SQL =
        "SELECT e.* FROM audit_template e" + " LEFT JOIN organization e_organization ON e.organization_id = e_organization.id";

    // The clients sort by the related entity display field, with the entity id as secondary sort.
    private static final Map<String, String> SORT_COLUMNS = Map.ofEntries(
        Map.entry("id", "e.id"),
        Map.entry("organization.id", "e_organization.id"),
        Map.entry("organization.name", "e_organization.name")
    );

    public AuditTemplateRepositoryInternalImpl(R2dbcEntityTemplate template, R2dbcConverter converter, R2dbcDialect dialect) {
        super(AuditTemplate.class, template, converter, dialect);
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
    protected Mono<List<AuditTemplate>> populateRelationships(List<AuditTemplate> entities) {
        return Mono.just(entities).flatMap(populated ->
            populate(populated, AuditTemplate::getOrganizationId, Organization.class, Organization::getId, AuditTemplate::setOrganization)
        );
    }
}

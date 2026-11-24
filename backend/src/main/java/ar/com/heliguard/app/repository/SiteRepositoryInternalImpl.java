package ar.com.heliguard.app.repository;

import ar.com.heliguard.app.domain.Organization;
import ar.com.heliguard.app.domain.Site;
import java.util.List;
import java.util.Map;
import org.springframework.data.r2dbc.convert.R2dbcConverter;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.r2dbc.dialect.R2dbcDialect;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC custom repository implementation for the Site entity.
 */
@SuppressWarnings("unused")
class SiteRepositoryInternalImpl extends AbstractR2dbcRepository<Site, Long> implements SiteRepositoryInternal {

    private static final String SELECT_WITH_RELATIONS_SQL =
        "SELECT e.* FROM site e" + " LEFT JOIN organization e_organization ON e.organization_id = e_organization.id";

    // The clients sort by the related entity display field, with the entity id as secondary sort.
    private static final Map<String, String> SORT_COLUMNS = Map.ofEntries(
        Map.entry("id", "e.id"),
        Map.entry("organization.id", "e_organization.id"),
        Map.entry("organization.name", "e_organization.name")
    );

    public SiteRepositoryInternalImpl(R2dbcEntityTemplate template, R2dbcConverter converter, R2dbcDialect dialect) {
        super(Site.class, template, converter, dialect);
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
    protected Mono<List<Site>> populateRelationships(List<Site> entities) {
        return Mono.just(entities).flatMap(populated ->
            populate(populated, Site::getOrganizationId, Organization.class, Organization::getId, Site::setOrganization)
        );
    }
}

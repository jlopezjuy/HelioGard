package ar.com.heliguard.app.repository;

import ar.com.heliguard.app.domain.ClimateRiskProfile;
import ar.com.heliguard.app.domain.Installation;
import ar.com.heliguard.app.domain.Organization;
import ar.com.heliguard.app.domain.Site;
import java.util.List;
import java.util.Map;
import org.springframework.data.r2dbc.convert.R2dbcConverter;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.r2dbc.dialect.R2dbcDialect;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC custom repository implementation for the Installation entity.
 */
@SuppressWarnings("unused")
class InstallationRepositoryInternalImpl extends AbstractR2dbcRepository<Installation, Long> implements InstallationRepositoryInternal {

    private static final String SELECT_WITH_RELATIONS_SQL =
        "SELECT e.* FROM installation e" +
        " LEFT JOIN organization e_organization ON e.organization_id = e_organization.id" +
        " LEFT JOIN site site ON e.site_id = site.id" +
        " LEFT JOIN climate_risk_profile climateRiskProfile ON e.climate_risk_profile_id = climateRiskProfile.id";

    // The clients sort by the related entity display field, with the entity id as secondary sort.
    private static final Map<String, String> SORT_COLUMNS = Map.ofEntries(
        Map.entry("id", "e.id"),
        Map.entry("organization.id", "e_organization.id"),
        Map.entry("organization.name", "e_organization.name"),
        Map.entry("site.id", "site.id"),
        Map.entry("site.name", "site.name"),
        Map.entry("climateRiskProfile.id", "climateRiskProfile.id"),
        Map.entry("climateRiskProfile.code", "climateRiskProfile.code")
    );

    public InstallationRepositoryInternalImpl(R2dbcEntityTemplate template, R2dbcConverter converter, R2dbcDialect dialect) {
        super(Installation.class, template, converter, dialect);
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
    protected Mono<List<Installation>> populateRelationships(List<Installation> entities) {
        return Mono.just(entities)
            .flatMap(populated ->
                populate(populated, Installation::getOrganizationId, Organization.class, Organization::getId, Installation::setOrganization)
            )
            .flatMap(populated -> populate(populated, Installation::getSiteId, Site.class, Site::getId, Installation::setSite))
            .flatMap(populated ->
                populate(
                    populated,
                    Installation::getClimateRiskProfileId,
                    ClimateRiskProfile.class,
                    ClimateRiskProfile::getId,
                    Installation::setClimateRiskProfile
                )
            );
    }
}

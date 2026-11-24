package ar.com.heliguard.app.repository;

import ar.com.heliguard.app.domain.EvidenceSource;
import ar.com.heliguard.app.domain.Installation;
import ar.com.heliguard.app.domain.Organization;
import ar.com.heliguard.app.domain.WeatherIngestionBatch;
import java.util.List;
import java.util.Map;
import org.springframework.data.r2dbc.convert.R2dbcConverter;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.r2dbc.dialect.R2dbcDialect;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC custom repository implementation for the WeatherIngestionBatch entity.
 */
@SuppressWarnings("unused")
class WeatherIngestionBatchRepositoryInternalImpl
    extends AbstractR2dbcRepository<WeatherIngestionBatch, Long>
    implements WeatherIngestionBatchRepositoryInternal
{

    private static final String SELECT_WITH_RELATIONS_SQL =
        "SELECT e.* FROM weather_ingestion_batch e" +
        " LEFT JOIN organization e_organization ON e.organization_id = e_organization.id" +
        " LEFT JOIN installation installation ON e.installation_id = installation.id" +
        " LEFT JOIN evidence_source evidenceSource ON e.evidence_source_id = evidenceSource.id";

    // The clients sort by the related entity display field, with the entity id as secondary sort.
    private static final Map<String, String> SORT_COLUMNS = Map.ofEntries(
        Map.entry("id", "e.id"),
        Map.entry("organization.id", "e_organization.id"),
        Map.entry("organization.name", "e_organization.name"),
        Map.entry("installation.id", "installation.id"),
        Map.entry("installation.name", "installation.name"),
        Map.entry("evidenceSource.id", "evidenceSource.id"),
        Map.entry("evidenceSource.code", "evidenceSource.code")
    );

    public WeatherIngestionBatchRepositoryInternalImpl(R2dbcEntityTemplate template, R2dbcConverter converter, R2dbcDialect dialect) {
        super(WeatherIngestionBatch.class, template, converter, dialect);
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
    protected Mono<List<WeatherIngestionBatch>> populateRelationships(List<WeatherIngestionBatch> entities) {
        return Mono.just(entities)
            .flatMap(populated ->
                populate(
                    populated,
                    WeatherIngestionBatch::getOrganizationId,
                    Organization.class,
                    Organization::getId,
                    WeatherIngestionBatch::setOrganization
                )
            )
            .flatMap(populated ->
                populate(
                    populated,
                    WeatherIngestionBatch::getInstallationId,
                    Installation.class,
                    Installation::getId,
                    WeatherIngestionBatch::setInstallation
                )
            )
            .flatMap(populated ->
                populate(
                    populated,
                    WeatherIngestionBatch::getEvidenceSourceId,
                    EvidenceSource.class,
                    EvidenceSource::getId,
                    WeatherIngestionBatch::setEvidenceSource
                )
            );
    }
}

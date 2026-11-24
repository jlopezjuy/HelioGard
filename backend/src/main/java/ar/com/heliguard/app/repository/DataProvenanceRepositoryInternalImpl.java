package ar.com.heliguard.app.repository;

import ar.com.heliguard.app.domain.DataProvenance;
import ar.com.heliguard.app.domain.EnergyModelRun;
import ar.com.heliguard.app.domain.EvidenceSource;
import ar.com.heliguard.app.domain.Installation;
import ar.com.heliguard.app.domain.Organization;
import java.util.List;
import java.util.Map;
import org.springframework.data.r2dbc.convert.R2dbcConverter;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.r2dbc.dialect.R2dbcDialect;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC custom repository implementation for the DataProvenance entity.
 */
@SuppressWarnings("unused")
class DataProvenanceRepositoryInternalImpl
    extends AbstractR2dbcRepository<DataProvenance, Long>
    implements DataProvenanceRepositoryInternal
{

    private static final String SELECT_WITH_RELATIONS_SQL =
        "SELECT e.* FROM data_provenance e" +
        " LEFT JOIN organization e_organization ON e.organization_id = e_organization.id" +
        " LEFT JOIN installation installation ON e.installation_id = installation.id" +
        " LEFT JOIN evidence_source evidenceSource ON e.evidence_source_id = evidenceSource.id" +
        " LEFT JOIN energy_model_run modelRun ON e.model_run_id = modelRun.id";

    // The clients sort by the related entity display field, with the entity id as secondary sort.
    private static final Map<String, String> SORT_COLUMNS = Map.ofEntries(
        Map.entry("id", "e.id"),
        Map.entry("organization.id", "e_organization.id"),
        Map.entry("organization.name", "e_organization.name"),
        Map.entry("installation.id", "installation.id"),
        Map.entry("installation.name", "installation.name"),
        Map.entry("evidenceSource.id", "evidenceSource.id"),
        Map.entry("evidenceSource.code", "evidenceSource.code"),
        Map.entry("modelRun.id", "modelRun.id"),
        Map.entry("modelRun.modelName", "modelRun.model_name")
    );

    public DataProvenanceRepositoryInternalImpl(R2dbcEntityTemplate template, R2dbcConverter converter, R2dbcDialect dialect) {
        super(DataProvenance.class, template, converter, dialect);
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
    protected Mono<List<DataProvenance>> populateRelationships(List<DataProvenance> entities) {
        return Mono.just(entities)
            .flatMap(populated ->
                populate(
                    populated,
                    DataProvenance::getOrganizationId,
                    Organization.class,
                    Organization::getId,
                    DataProvenance::setOrganization
                )
            )
            .flatMap(populated ->
                populate(
                    populated,
                    DataProvenance::getInstallationId,
                    Installation.class,
                    Installation::getId,
                    DataProvenance::setInstallation
                )
            )
            .flatMap(populated ->
                populate(
                    populated,
                    DataProvenance::getEvidenceSourceId,
                    EvidenceSource.class,
                    EvidenceSource::getId,
                    DataProvenance::setEvidenceSource
                )
            )
            .flatMap(populated ->
                populate(populated, DataProvenance::getModelRunId, EnergyModelRun.class, EnergyModelRun::getId, DataProvenance::setModelRun)
            );
    }
}

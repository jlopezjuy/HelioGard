package ar.com.heliguard.app.repository;

import ar.com.heliguard.app.domain.EnergyModelRun;
import ar.com.heliguard.app.domain.Installation;
import ar.com.heliguard.app.domain.IntegrityAssessment;
import ar.com.heliguard.app.domain.Organization;
import ar.com.heliguard.app.domain.TelemetryIntegrityEvent;
import java.util.List;
import java.util.Map;
import org.springframework.data.r2dbc.convert.R2dbcConverter;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.r2dbc.dialect.R2dbcDialect;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC custom repository implementation for the IntegrityAssessment entity.
 */
@SuppressWarnings("unused")
class IntegrityAssessmentRepositoryInternalImpl
    extends AbstractR2dbcRepository<IntegrityAssessment, Long>
    implements IntegrityAssessmentRepositoryInternal
{

    private static final String SELECT_WITH_RELATIONS_SQL =
        "SELECT e.* FROM integrity_assessment e" +
        " LEFT JOIN organization e_organization ON e.organization_id = e_organization.id" +
        " LEFT JOIN installation installation ON e.installation_id = installation.id" +
        " LEFT JOIN telemetry_integrity_event integrityEvent ON e.integrity_event_id = integrityEvent.id" +
        " LEFT JOIN energy_model_run modelRun ON e.model_run_id = modelRun.id";

    // The clients sort by the related entity display field, with the entity id as secondary sort.
    private static final Map<String, String> SORT_COLUMNS = Map.ofEntries(
        Map.entry("id", "e.id"),
        Map.entry("organization.id", "e_organization.id"),
        Map.entry("organization.name", "e_organization.name"),
        Map.entry("installation.id", "installation.id"),
        Map.entry("installation.name", "installation.name"),
        Map.entry("integrityEvent.id", "integrityEvent.id"),
        Map.entry("integrityEvent.eventUid", "integrityEvent.event_uid"),
        Map.entry("modelRun.id", "modelRun.id"),
        Map.entry("modelRun.modelName", "modelRun.model_name")
    );

    public IntegrityAssessmentRepositoryInternalImpl(R2dbcEntityTemplate template, R2dbcConverter converter, R2dbcDialect dialect) {
        super(IntegrityAssessment.class, template, converter, dialect);
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
    protected Mono<List<IntegrityAssessment>> populateRelationships(List<IntegrityAssessment> entities) {
        return Mono.just(entities)
            .flatMap(populated ->
                populate(
                    populated,
                    IntegrityAssessment::getOrganizationId,
                    Organization.class,
                    Organization::getId,
                    IntegrityAssessment::setOrganization
                )
            )
            .flatMap(populated ->
                populate(
                    populated,
                    IntegrityAssessment::getInstallationId,
                    Installation.class,
                    Installation::getId,
                    IntegrityAssessment::setInstallation
                )
            )
            .flatMap(populated ->
                populate(
                    populated,
                    IntegrityAssessment::getIntegrityEventId,
                    TelemetryIntegrityEvent.class,
                    TelemetryIntegrityEvent::getId,
                    IntegrityAssessment::setIntegrityEvent
                )
            )
            .flatMap(populated ->
                populate(
                    populated,
                    IntegrityAssessment::getModelRunId,
                    EnergyModelRun.class,
                    EnergyModelRun::getId,
                    IntegrityAssessment::setModelRun
                )
            );
    }
}

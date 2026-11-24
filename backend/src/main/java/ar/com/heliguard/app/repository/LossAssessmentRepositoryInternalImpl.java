package ar.com.heliguard.app.repository;

import ar.com.heliguard.app.domain.EnergyModelRun;
import ar.com.heliguard.app.domain.LossAssessment;
import ar.com.heliguard.app.domain.Organization;
import java.util.List;
import java.util.Map;
import org.springframework.data.r2dbc.convert.R2dbcConverter;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.r2dbc.dialect.R2dbcDialect;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC custom repository implementation for the LossAssessment entity.
 */
@SuppressWarnings("unused")
class LossAssessmentRepositoryInternalImpl
    extends AbstractR2dbcRepository<LossAssessment, Long>
    implements LossAssessmentRepositoryInternal
{

    private static final String SELECT_WITH_RELATIONS_SQL =
        "SELECT e.* FROM loss_assessment e" +
        " LEFT JOIN organization e_organization ON e.organization_id = e_organization.id" +
        " LEFT JOIN energy_model_run modelRun ON e.model_run_id = modelRun.id";

    // The clients sort by the related entity display field, with the entity id as secondary sort.
    private static final Map<String, String> SORT_COLUMNS = Map.ofEntries(
        Map.entry("id", "e.id"),
        Map.entry("organization.id", "e_organization.id"),
        Map.entry("organization.name", "e_organization.name"),
        Map.entry("modelRun.id", "modelRun.id"),
        Map.entry("modelRun.modelName", "modelRun.model_name")
    );

    public LossAssessmentRepositoryInternalImpl(R2dbcEntityTemplate template, R2dbcConverter converter, R2dbcDialect dialect) {
        super(LossAssessment.class, template, converter, dialect);
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
    protected Mono<List<LossAssessment>> populateRelationships(List<LossAssessment> entities) {
        return Mono.just(entities)
            .flatMap(populated ->
                populate(
                    populated,
                    LossAssessment::getOrganizationId,
                    Organization.class,
                    Organization::getId,
                    LossAssessment::setOrganization
                )
            )
            .flatMap(populated ->
                populate(populated, LossAssessment::getModelRunId, EnergyModelRun.class, EnergyModelRun::getId, LossAssessment::setModelRun)
            );
    }
}

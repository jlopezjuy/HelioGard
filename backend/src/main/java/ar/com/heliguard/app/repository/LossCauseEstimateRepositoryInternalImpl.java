package ar.com.heliguard.app.repository;

import ar.com.heliguard.app.domain.LossAssessment;
import ar.com.heliguard.app.domain.LossCauseEstimate;
import ar.com.heliguard.app.domain.Organization;
import java.util.List;
import java.util.Map;
import org.springframework.data.r2dbc.convert.R2dbcConverter;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.r2dbc.dialect.R2dbcDialect;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC custom repository implementation for the LossCauseEstimate entity.
 */
@SuppressWarnings("unused")
class LossCauseEstimateRepositoryInternalImpl
    extends AbstractR2dbcRepository<LossCauseEstimate, Long>
    implements LossCauseEstimateRepositoryInternal
{

    private static final String SELECT_WITH_RELATIONS_SQL =
        "SELECT e.* FROM loss_cause_estimate e" +
        " LEFT JOIN organization e_organization ON e.organization_id = e_organization.id" +
        " LEFT JOIN loss_assessment lossAssessment ON e.loss_assessment_id = lossAssessment.id";

    // The clients sort by the related entity display field, with the entity id as secondary sort.
    private static final Map<String, String> SORT_COLUMNS = Map.ofEntries(
        Map.entry("id", "e.id"),
        Map.entry("organization.id", "e_organization.id"),
        Map.entry("organization.name", "e_organization.name"),
        Map.entry("lossAssessment.id", "lossAssessment.id"),
        Map.entry("lossAssessment.methodology", "lossAssessment.methodology")
    );

    public LossCauseEstimateRepositoryInternalImpl(R2dbcEntityTemplate template, R2dbcConverter converter, R2dbcDialect dialect) {
        super(LossCauseEstimate.class, template, converter, dialect);
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
    protected Mono<List<LossCauseEstimate>> populateRelationships(List<LossCauseEstimate> entities) {
        return Mono.just(entities)
            .flatMap(populated ->
                populate(
                    populated,
                    LossCauseEstimate::getOrganizationId,
                    Organization.class,
                    Organization::getId,
                    LossCauseEstimate::setOrganization
                )
            )
            .flatMap(populated ->
                populate(
                    populated,
                    LossCauseEstimate::getLossAssessmentId,
                    LossAssessment.class,
                    LossAssessment::getId,
                    LossCauseEstimate::setLossAssessment
                )
            );
    }
}

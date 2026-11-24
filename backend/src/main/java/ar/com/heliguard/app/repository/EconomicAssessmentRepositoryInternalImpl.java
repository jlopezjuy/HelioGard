package ar.com.heliguard.app.repository;

import ar.com.heliguard.app.domain.EconomicAssessment;
import ar.com.heliguard.app.domain.LossAssessment;
import ar.com.heliguard.app.domain.Organization;
import ar.com.heliguard.app.domain.TariffRate;
import java.util.List;
import java.util.Map;
import org.springframework.data.r2dbc.convert.R2dbcConverter;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.r2dbc.dialect.R2dbcDialect;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC custom repository implementation for the EconomicAssessment entity.
 */
@SuppressWarnings("unused")
class EconomicAssessmentRepositoryInternalImpl
    extends AbstractR2dbcRepository<EconomicAssessment, Long>
    implements EconomicAssessmentRepositoryInternal
{

    private static final String SELECT_WITH_RELATIONS_SQL =
        "SELECT e.* FROM economic_assessment e" +
        " LEFT JOIN organization e_organization ON e.organization_id = e_organization.id" +
        " LEFT JOIN loss_assessment lossAssessment ON e.loss_assessment_id = lossAssessment.id";

    // The clients sort by the related entity display field, with the entity id as secondary sort.
    private static final Map<String, String> SORT_COLUMNS = Map.ofEntries(
        Map.entry("id", "e.id"),
        Map.entry("organization.id", "e_organization.id"),
        Map.entry("organization.name", "e_organization.name"),
        Map.entry("lossAssessment.id", "lossAssessment.id"),
        Map.entry("lossAssessment.methodology", "lossAssessment.methodology"),
        Map.entry("tariffRate.id", "e.tariff_rate_id")
    );

    public EconomicAssessmentRepositoryInternalImpl(R2dbcEntityTemplate template, R2dbcConverter converter, R2dbcDialect dialect) {
        super(EconomicAssessment.class, template, converter, dialect);
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
    protected Mono<List<EconomicAssessment>> populateRelationships(List<EconomicAssessment> entities) {
        reference(entities, EconomicAssessment::getTariffRateId, TariffRate::new, TariffRate::setId, EconomicAssessment::setTariffRate);
        return Mono.just(entities)
            .flatMap(populated ->
                populate(
                    populated,
                    EconomicAssessment::getOrganizationId,
                    Organization.class,
                    Organization::getId,
                    EconomicAssessment::setOrganization
                )
            )
            .flatMap(populated ->
                populate(
                    populated,
                    EconomicAssessment::getLossAssessmentId,
                    LossAssessment.class,
                    LossAssessment::getId,
                    EconomicAssessment::setLossAssessment
                )
            );
    }
}

package ar.com.heliguard.app.repository;

import ar.com.heliguard.app.domain.Equipment;
import ar.com.heliguard.app.domain.InverterMpptInput;
import ar.com.heliguard.app.domain.Organization;
import java.util.List;
import java.util.Map;
import org.springframework.data.r2dbc.convert.R2dbcConverter;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.r2dbc.dialect.R2dbcDialect;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC custom repository implementation for the InverterMpptInput entity.
 */
@SuppressWarnings("unused")
class InverterMpptInputRepositoryInternalImpl
    extends AbstractR2dbcRepository<InverterMpptInput, Long>
    implements InverterMpptInputRepositoryInternal
{

    private static final String SELECT_WITH_RELATIONS_SQL =
        "SELECT e.* FROM inverter_mppt_input e" +
        " LEFT JOIN organization e_organization ON e.organization_id = e_organization.id" +
        " LEFT JOIN equipment inverter ON e.inverter_id = inverter.id";

    // The clients sort by the related entity display field, with the entity id as secondary sort.
    private static final Map<String, String> SORT_COLUMNS = Map.ofEntries(
        Map.entry("id", "e.id"),
        Map.entry("organization.id", "e_organization.id"),
        Map.entry("organization.name", "e_organization.name"),
        Map.entry("inverter.id", "inverter.id"),
        Map.entry("inverter.tag", "inverter.tag")
    );

    public InverterMpptInputRepositoryInternalImpl(R2dbcEntityTemplate template, R2dbcConverter converter, R2dbcDialect dialect) {
        super(InverterMpptInput.class, template, converter, dialect);
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
    protected Mono<List<InverterMpptInput>> populateRelationships(List<InverterMpptInput> entities) {
        return Mono.just(entities)
            .flatMap(populated ->
                populate(
                    populated,
                    InverterMpptInput::getOrganizationId,
                    Organization.class,
                    Organization::getId,
                    InverterMpptInput::setOrganization
                )
            )
            .flatMap(populated ->
                populate(populated, InverterMpptInput::getInverterId, Equipment.class, Equipment::getId, InverterMpptInput::setInverter)
            );
    }
}

package ar.com.heliguard.app.repository;

import ar.com.heliguard.app.domain.InverterMpptInput;
import ar.com.heliguard.app.domain.Organization;
import ar.com.heliguard.app.domain.SolarString;
import ar.com.heliguard.app.domain.StringInputAssignment;
import java.util.List;
import java.util.Map;
import org.springframework.data.r2dbc.convert.R2dbcConverter;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.r2dbc.dialect.R2dbcDialect;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC custom repository implementation for the StringInputAssignment entity.
 */
@SuppressWarnings("unused")
class StringInputAssignmentRepositoryInternalImpl
    extends AbstractR2dbcRepository<StringInputAssignment, Long>
    implements StringInputAssignmentRepositoryInternal
{

    private static final String SELECT_WITH_RELATIONS_SQL =
        "SELECT e.* FROM string_input_assignment e" +
        " LEFT JOIN organization e_organization ON e.organization_id = e_organization.id" +
        " LEFT JOIN solar_string solarString ON e.solar_string_id = solarString.id" +
        " LEFT JOIN inverter_mppt_input mpptInput ON e.mppt_input_id = mpptInput.id";

    // The clients sort by the related entity display field, with the entity id as secondary sort.
    private static final Map<String, String> SORT_COLUMNS = Map.ofEntries(
        Map.entry("id", "e.id"),
        Map.entry("organization.id", "e_organization.id"),
        Map.entry("organization.name", "e_organization.name"),
        Map.entry("solarString.id", "solarString.id"),
        Map.entry("solarString.stringCode", "solarString.string_code"),
        Map.entry("mpptInput.id", "mpptInput.id"),
        Map.entry("mpptInput.inputCode", "mpptInput.input_code")
    );

    public StringInputAssignmentRepositoryInternalImpl(R2dbcEntityTemplate template, R2dbcConverter converter, R2dbcDialect dialect) {
        super(StringInputAssignment.class, template, converter, dialect);
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
    protected Mono<List<StringInputAssignment>> populateRelationships(List<StringInputAssignment> entities) {
        return Mono.just(entities)
            .flatMap(populated ->
                populate(
                    populated,
                    StringInputAssignment::getOrganizationId,
                    Organization.class,
                    Organization::getId,
                    StringInputAssignment::setOrganization
                )
            )
            .flatMap(populated ->
                populate(
                    populated,
                    StringInputAssignment::getSolarStringId,
                    SolarString.class,
                    SolarString::getId,
                    StringInputAssignment::setSolarString
                )
            )
            .flatMap(populated ->
                populate(
                    populated,
                    StringInputAssignment::getMpptInputId,
                    InverterMpptInput.class,
                    InverterMpptInput::getId,
                    StringInputAssignment::setMpptInput
                )
            );
    }
}

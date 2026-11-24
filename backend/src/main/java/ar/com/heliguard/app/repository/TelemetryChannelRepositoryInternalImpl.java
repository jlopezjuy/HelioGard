package ar.com.heliguard.app.repository;

import ar.com.heliguard.app.domain.Equipment;
import ar.com.heliguard.app.domain.MetricDefinition;
import ar.com.heliguard.app.domain.Organization;
import ar.com.heliguard.app.domain.TelemetryChannel;
import java.util.List;
import java.util.Map;
import org.springframework.data.r2dbc.convert.R2dbcConverter;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.r2dbc.dialect.R2dbcDialect;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC custom repository implementation for the TelemetryChannel entity.
 */
@SuppressWarnings("unused")
class TelemetryChannelRepositoryInternalImpl
    extends AbstractR2dbcRepository<TelemetryChannel, Long>
    implements TelemetryChannelRepositoryInternal
{

    private static final String SELECT_WITH_RELATIONS_SQL =
        "SELECT e.* FROM telemetry_channel e" +
        " LEFT JOIN organization e_organization ON e.organization_id = e_organization.id" +
        " LEFT JOIN equipment equipment ON e.equipment_id = equipment.id" +
        " LEFT JOIN metric_definition metricDefinition ON e.metric_definition_id = metricDefinition.id";

    // The clients sort by the related entity display field, with the entity id as secondary sort.
    private static final Map<String, String> SORT_COLUMNS = Map.ofEntries(
        Map.entry("id", "e.id"),
        Map.entry("organization.id", "e_organization.id"),
        Map.entry("organization.name", "e_organization.name"),
        Map.entry("equipment.id", "equipment.id"),
        Map.entry("equipment.tag", "equipment.tag"),
        Map.entry("metricDefinition.id", "metricDefinition.id"),
        Map.entry("metricDefinition.code", "metricDefinition.code")
    );

    public TelemetryChannelRepositoryInternalImpl(R2dbcEntityTemplate template, R2dbcConverter converter, R2dbcDialect dialect) {
        super(TelemetryChannel.class, template, converter, dialect);
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
    protected Mono<List<TelemetryChannel>> populateRelationships(List<TelemetryChannel> entities) {
        return Mono.just(entities)
            .flatMap(populated ->
                populate(
                    populated,
                    TelemetryChannel::getOrganizationId,
                    Organization.class,
                    Organization::getId,
                    TelemetryChannel::setOrganization
                )
            )
            .flatMap(populated ->
                populate(populated, TelemetryChannel::getEquipmentId, Equipment.class, Equipment::getId, TelemetryChannel::setEquipment)
            )
            .flatMap(populated ->
                populate(
                    populated,
                    TelemetryChannel::getMetricDefinitionId,
                    MetricDefinition.class,
                    MetricDefinition::getId,
                    TelemetryChannel::setMetricDefinition
                )
            );
    }
}

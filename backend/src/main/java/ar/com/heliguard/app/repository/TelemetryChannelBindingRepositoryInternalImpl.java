package ar.com.heliguard.app.repository;

import ar.com.heliguard.app.domain.Gateway;
import ar.com.heliguard.app.domain.Organization;
import ar.com.heliguard.app.domain.TelemetryChannel;
import ar.com.heliguard.app.domain.TelemetryChannelBinding;
import java.util.List;
import java.util.Map;
import org.springframework.data.r2dbc.convert.R2dbcConverter;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.r2dbc.dialect.R2dbcDialect;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC custom repository implementation for the TelemetryChannelBinding entity.
 */
@SuppressWarnings("unused")
class TelemetryChannelBindingRepositoryInternalImpl
    extends AbstractR2dbcRepository<TelemetryChannelBinding, Long>
    implements TelemetryChannelBindingRepositoryInternal
{

    private static final String SELECT_WITH_RELATIONS_SQL =
        "SELECT e.* FROM telemetry_channel_binding e" +
        " LEFT JOIN organization e_organization ON e.organization_id = e_organization.id" +
        " LEFT JOIN telemetry_channel telemetryChannel ON e.telemetry_channel_id = telemetryChannel.id" +
        " LEFT JOIN gateway gateway ON e.gateway_id = gateway.id";

    // The clients sort by the related entity display field, with the entity id as secondary sort.
    private static final Map<String, String> SORT_COLUMNS = Map.ofEntries(
        Map.entry("id", "e.id"),
        Map.entry("organization.id", "e_organization.id"),
        Map.entry("organization.name", "e_organization.name"),
        Map.entry("telemetryChannel.id", "telemetryChannel.id"),
        Map.entry("telemetryChannel.externalChannelCode", "telemetryChannel.external_channel_code"),
        Map.entry("gateway.id", "gateway.id"),
        Map.entry("gateway.hardwareId", "gateway.hardware_id")
    );

    public TelemetryChannelBindingRepositoryInternalImpl(R2dbcEntityTemplate template, R2dbcConverter converter, R2dbcDialect dialect) {
        super(TelemetryChannelBinding.class, template, converter, dialect);
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
    protected Mono<List<TelemetryChannelBinding>> populateRelationships(List<TelemetryChannelBinding> entities) {
        return Mono.just(entities)
            .flatMap(populated ->
                populate(
                    populated,
                    TelemetryChannelBinding::getOrganizationId,
                    Organization.class,
                    Organization::getId,
                    TelemetryChannelBinding::setOrganization
                )
            )
            .flatMap(populated ->
                populate(
                    populated,
                    TelemetryChannelBinding::getTelemetryChannelId,
                    TelemetryChannel.class,
                    TelemetryChannel::getId,
                    TelemetryChannelBinding::setTelemetryChannel
                )
            )
            .flatMap(populated ->
                populate(
                    populated,
                    TelemetryChannelBinding::getGatewayId,
                    Gateway.class,
                    Gateway::getId,
                    TelemetryChannelBinding::setGateway
                )
            );
    }
}

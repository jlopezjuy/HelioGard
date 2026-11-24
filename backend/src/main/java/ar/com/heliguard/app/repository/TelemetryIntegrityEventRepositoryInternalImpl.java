package ar.com.heliguard.app.repository;

import ar.com.heliguard.app.domain.DeviceIdentity;
import ar.com.heliguard.app.domain.Installation;
import ar.com.heliguard.app.domain.Organization;
import ar.com.heliguard.app.domain.TelemetryChannel;
import ar.com.heliguard.app.domain.TelemetryIntegrityEvent;
import java.util.List;
import java.util.Map;
import org.springframework.data.r2dbc.convert.R2dbcConverter;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.r2dbc.dialect.R2dbcDialect;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC custom repository implementation for the TelemetryIntegrityEvent entity.
 */
@SuppressWarnings("unused")
class TelemetryIntegrityEventRepositoryInternalImpl
    extends AbstractR2dbcRepository<TelemetryIntegrityEvent, Long>
    implements TelemetryIntegrityEventRepositoryInternal
{

    private static final String SELECT_WITH_RELATIONS_SQL =
        "SELECT e.* FROM telemetry_integrity_event e" +
        " LEFT JOIN organization e_organization ON e.organization_id = e_organization.id" +
        " LEFT JOIN installation installation ON e.installation_id = installation.id" +
        " LEFT JOIN device_identity deviceIdentity ON e.device_identity_id = deviceIdentity.id" +
        " LEFT JOIN telemetry_channel telemetryChannel ON e.telemetry_channel_id = telemetryChannel.id";

    // The clients sort by the related entity display field, with the entity id as secondary sort.
    private static final Map<String, String> SORT_COLUMNS = Map.ofEntries(
        Map.entry("id", "e.id"),
        Map.entry("organization.id", "e_organization.id"),
        Map.entry("organization.name", "e_organization.name"),
        Map.entry("installation.id", "installation.id"),
        Map.entry("installation.name", "installation.name"),
        Map.entry("deviceIdentity.id", "deviceIdentity.id"),
        Map.entry("deviceIdentity.deviceUid", "deviceIdentity.device_uid"),
        Map.entry("telemetryChannel.id", "telemetryChannel.id"),
        Map.entry("telemetryChannel.externalChannelCode", "telemetryChannel.external_channel_code")
    );

    public TelemetryIntegrityEventRepositoryInternalImpl(R2dbcEntityTemplate template, R2dbcConverter converter, R2dbcDialect dialect) {
        super(TelemetryIntegrityEvent.class, template, converter, dialect);
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
    protected Mono<List<TelemetryIntegrityEvent>> populateRelationships(List<TelemetryIntegrityEvent> entities) {
        return Mono.just(entities)
            .flatMap(populated ->
                populate(
                    populated,
                    TelemetryIntegrityEvent::getOrganizationId,
                    Organization.class,
                    Organization::getId,
                    TelemetryIntegrityEvent::setOrganization
                )
            )
            .flatMap(populated ->
                populate(
                    populated,
                    TelemetryIntegrityEvent::getInstallationId,
                    Installation.class,
                    Installation::getId,
                    TelemetryIntegrityEvent::setInstallation
                )
            )
            .flatMap(populated ->
                populate(
                    populated,
                    TelemetryIntegrityEvent::getDeviceIdentityId,
                    DeviceIdentity.class,
                    DeviceIdentity::getId,
                    TelemetryIntegrityEvent::setDeviceIdentity
                )
            )
            .flatMap(populated ->
                populate(
                    populated,
                    TelemetryIntegrityEvent::getTelemetryChannelId,
                    TelemetryChannel.class,
                    TelemetryChannel::getId,
                    TelemetryIntegrityEvent::setTelemetryChannel
                )
            );
    }
}

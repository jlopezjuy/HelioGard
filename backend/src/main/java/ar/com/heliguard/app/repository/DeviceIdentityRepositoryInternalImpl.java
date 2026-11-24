package ar.com.heliguard.app.repository;

import ar.com.heliguard.app.domain.DeviceIdentity;
import ar.com.heliguard.app.domain.Equipment;
import ar.com.heliguard.app.domain.Gateway;
import ar.com.heliguard.app.domain.Organization;
import java.util.List;
import java.util.Map;
import org.springframework.data.r2dbc.convert.R2dbcConverter;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.r2dbc.dialect.R2dbcDialect;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC custom repository implementation for the DeviceIdentity entity.
 */
@SuppressWarnings("unused")
class DeviceIdentityRepositoryInternalImpl
    extends AbstractR2dbcRepository<DeviceIdentity, Long>
    implements DeviceIdentityRepositoryInternal
{

    private static final String SELECT_WITH_RELATIONS_SQL =
        "SELECT e.* FROM device_identity e" +
        " LEFT JOIN organization e_organization ON e.organization_id = e_organization.id" +
        " LEFT JOIN gateway gateway ON e.gateway_id = gateway.id" +
        " LEFT JOIN equipment equipment ON e.equipment_id = equipment.id";

    // The clients sort by the related entity display field, with the entity id as secondary sort.
    private static final Map<String, String> SORT_COLUMNS = Map.ofEntries(
        Map.entry("id", "e.id"),
        Map.entry("organization.id", "e_organization.id"),
        Map.entry("organization.name", "e_organization.name"),
        Map.entry("gateway.id", "gateway.id"),
        Map.entry("gateway.hardwareId", "gateway.hardware_id"),
        Map.entry("equipment.id", "equipment.id"),
        Map.entry("equipment.tag", "equipment.tag")
    );

    public DeviceIdentityRepositoryInternalImpl(R2dbcEntityTemplate template, R2dbcConverter converter, R2dbcDialect dialect) {
        super(DeviceIdentity.class, template, converter, dialect);
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
    protected Mono<List<DeviceIdentity>> populateRelationships(List<DeviceIdentity> entities) {
        return Mono.just(entities)
            .flatMap(populated ->
                populate(
                    populated,
                    DeviceIdentity::getOrganizationId,
                    Organization.class,
                    Organization::getId,
                    DeviceIdentity::setOrganization
                )
            )
            .flatMap(populated ->
                populate(populated, DeviceIdentity::getGatewayId, Gateway.class, Gateway::getId, DeviceIdentity::setGateway)
            )
            .flatMap(populated ->
                populate(populated, DeviceIdentity::getEquipmentId, Equipment.class, Equipment::getId, DeviceIdentity::setEquipment)
            );
    }
}

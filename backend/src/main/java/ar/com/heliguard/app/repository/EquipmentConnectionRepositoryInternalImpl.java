package ar.com.heliguard.app.repository;

import ar.com.heliguard.app.domain.Equipment;
import ar.com.heliguard.app.domain.EquipmentConnection;
import ar.com.heliguard.app.domain.Organization;
import java.util.List;
import java.util.Map;
import org.springframework.data.r2dbc.convert.R2dbcConverter;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.r2dbc.dialect.R2dbcDialect;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC custom repository implementation for the EquipmentConnection entity.
 */
@SuppressWarnings("unused")
class EquipmentConnectionRepositoryInternalImpl
    extends AbstractR2dbcRepository<EquipmentConnection, Long>
    implements EquipmentConnectionRepositoryInternal
{

    private static final String SELECT_WITH_RELATIONS_SQL =
        "SELECT e.* FROM equipment_connection e" +
        " LEFT JOIN organization e_organization ON e.organization_id = e_organization.id" +
        " LEFT JOIN equipment fromEquipment ON e.from_equipment_id = fromEquipment.id" +
        " LEFT JOIN equipment toEquipment ON e.to_equipment_id = toEquipment.id";

    // The clients sort by the related entity display field, with the entity id as secondary sort.
    private static final Map<String, String> SORT_COLUMNS = Map.ofEntries(
        Map.entry("id", "e.id"),
        Map.entry("organization.id", "e_organization.id"),
        Map.entry("organization.name", "e_organization.name"),
        Map.entry("fromEquipment.id", "fromEquipment.id"),
        Map.entry("fromEquipment.tag", "fromEquipment.tag"),
        Map.entry("toEquipment.id", "toEquipment.id"),
        Map.entry("toEquipment.tag", "toEquipment.tag")
    );

    public EquipmentConnectionRepositoryInternalImpl(R2dbcEntityTemplate template, R2dbcConverter converter, R2dbcDialect dialect) {
        super(EquipmentConnection.class, template, converter, dialect);
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
    protected Mono<List<EquipmentConnection>> populateRelationships(List<EquipmentConnection> entities) {
        return Mono.just(entities)
            .flatMap(populated ->
                populate(
                    populated,
                    EquipmentConnection::getOrganizationId,
                    Organization.class,
                    Organization::getId,
                    EquipmentConnection::setOrganization
                )
            )
            .flatMap(populated ->
                populate(
                    populated,
                    EquipmentConnection::getFromEquipmentId,
                    Equipment.class,
                    Equipment::getId,
                    EquipmentConnection::setFromEquipment
                )
            )
            .flatMap(populated ->
                populate(
                    populated,
                    EquipmentConnection::getToEquipmentId,
                    Equipment.class,
                    Equipment::getId,
                    EquipmentConnection::setToEquipment
                )
            );
    }
}

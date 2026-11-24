package ar.com.heliguard.app.repository;

import ar.com.heliguard.app.domain.Equipment;
import ar.com.heliguard.app.domain.EquipmentModel;
import ar.com.heliguard.app.domain.Installation;
import ar.com.heliguard.app.domain.Organization;
import ar.com.heliguard.app.domain.SolarString;
import java.util.List;
import java.util.Map;
import org.springframework.data.r2dbc.convert.R2dbcConverter;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.r2dbc.dialect.R2dbcDialect;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC custom repository implementation for the Equipment entity.
 */
@SuppressWarnings("unused")
class EquipmentRepositoryInternalImpl extends AbstractR2dbcRepository<Equipment, Long> implements EquipmentRepositoryInternal {

    private static final String SELECT_WITH_RELATIONS_SQL =
        "SELECT e.* FROM equipment e" +
        " LEFT JOIN organization e_organization ON e.organization_id = e_organization.id" +
        " LEFT JOIN installation installation ON e.installation_id = installation.id" +
        " LEFT JOIN equipment_model equipmentModel ON e.equipment_model_id = equipmentModel.id" +
        " LEFT JOIN solar_string solarString ON e.solar_string_id = solarString.id";

    // The clients sort by the related entity display field, with the entity id as secondary sort.
    private static final Map<String, String> SORT_COLUMNS = Map.ofEntries(
        Map.entry("id", "e.id"),
        Map.entry("organization.id", "e_organization.id"),
        Map.entry("organization.name", "e_organization.name"),
        Map.entry("installation.id", "installation.id"),
        Map.entry("installation.name", "installation.name"),
        Map.entry("equipmentModel.id", "equipmentModel.id"),
        Map.entry("equipmentModel.modelCode", "equipmentModel.model_code"),
        Map.entry("solarString.id", "solarString.id"),
        Map.entry("solarString.stringCode", "solarString.string_code")
    );

    public EquipmentRepositoryInternalImpl(R2dbcEntityTemplate template, R2dbcConverter converter, R2dbcDialect dialect) {
        super(Equipment.class, template, converter, dialect);
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
    protected Mono<List<Equipment>> populateRelationships(List<Equipment> entities) {
        return Mono.just(entities)
            .flatMap(populated ->
                populate(populated, Equipment::getOrganizationId, Organization.class, Organization::getId, Equipment::setOrganization)
            )
            .flatMap(populated ->
                populate(populated, Equipment::getInstallationId, Installation.class, Installation::getId, Equipment::setInstallation)
            )
            .flatMap(populated ->
                populate(
                    populated,
                    Equipment::getEquipmentModelId,
                    EquipmentModel.class,
                    EquipmentModel::getId,
                    Equipment::setEquipmentModel
                )
            )
            .flatMap(populated ->
                populate(populated, Equipment::getSolarStringId, SolarString.class, SolarString::getId, Equipment::setSolarString)
            );
    }
}

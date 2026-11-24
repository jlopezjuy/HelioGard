package ar.com.heliguard.app.repository;

import ar.com.heliguard.app.domain.DroneMission;
import ar.com.heliguard.app.domain.DroneMissionTarget;
import ar.com.heliguard.app.domain.Equipment;
import ar.com.heliguard.app.domain.Organization;
import ar.com.heliguard.app.domain.SolarSector;
import ar.com.heliguard.app.domain.SolarString;
import java.util.List;
import java.util.Map;
import org.springframework.data.r2dbc.convert.R2dbcConverter;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.r2dbc.dialect.R2dbcDialect;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC custom repository implementation for the DroneMissionTarget entity.
 */
@SuppressWarnings("unused")
class DroneMissionTargetRepositoryInternalImpl
    extends AbstractR2dbcRepository<DroneMissionTarget, Long>
    implements DroneMissionTargetRepositoryInternal
{

    private static final String SELECT_WITH_RELATIONS_SQL =
        "SELECT e.* FROM drone_mission_target e" +
        " LEFT JOIN organization e_organization ON e.organization_id = e_organization.id" +
        " LEFT JOIN drone_mission mission ON e.mission_id = mission.id" +
        " LEFT JOIN solar_sector solarSector ON e.solar_sector_id = solarSector.id" +
        " LEFT JOIN solar_string solarString ON e.solar_string_id = solarString.id" +
        " LEFT JOIN equipment equipment ON e.equipment_id = equipment.id";

    // The clients sort by the related entity display field, with the entity id as secondary sort.
    private static final Map<String, String> SORT_COLUMNS = Map.ofEntries(
        Map.entry("id", "e.id"),
        Map.entry("organization.id", "e_organization.id"),
        Map.entry("organization.name", "e_organization.name"),
        Map.entry("mission.id", "mission.id"),
        Map.entry("mission.missionCode", "mission.mission_code"),
        Map.entry("solarSector.id", "solarSector.id"),
        Map.entry("solarSector.sectorCode", "solarSector.sector_code"),
        Map.entry("solarString.id", "solarString.id"),
        Map.entry("solarString.stringCode", "solarString.string_code"),
        Map.entry("equipment.id", "equipment.id"),
        Map.entry("equipment.tag", "equipment.tag")
    );

    public DroneMissionTargetRepositoryInternalImpl(R2dbcEntityTemplate template, R2dbcConverter converter, R2dbcDialect dialect) {
        super(DroneMissionTarget.class, template, converter, dialect);
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
    protected Mono<List<DroneMissionTarget>> populateRelationships(List<DroneMissionTarget> entities) {
        return Mono.just(entities)
            .flatMap(populated ->
                populate(
                    populated,
                    DroneMissionTarget::getOrganizationId,
                    Organization.class,
                    Organization::getId,
                    DroneMissionTarget::setOrganization
                )
            )
            .flatMap(populated ->
                populate(
                    populated,
                    DroneMissionTarget::getMissionId,
                    DroneMission.class,
                    DroneMission::getId,
                    DroneMissionTarget::setMission
                )
            )
            .flatMap(populated ->
                populate(
                    populated,
                    DroneMissionTarget::getSolarSectorId,
                    SolarSector.class,
                    SolarSector::getId,
                    DroneMissionTarget::setSolarSector
                )
            )
            .flatMap(populated ->
                populate(
                    populated,
                    DroneMissionTarget::getSolarStringId,
                    SolarString.class,
                    SolarString::getId,
                    DroneMissionTarget::setSolarString
                )
            )
            .flatMap(populated ->
                populate(populated, DroneMissionTarget::getEquipmentId, Equipment.class, Equipment::getId, DroneMissionTarget::setEquipment)
            );
    }
}

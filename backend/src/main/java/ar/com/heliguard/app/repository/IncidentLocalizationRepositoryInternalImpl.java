package ar.com.heliguard.app.repository;

import ar.com.heliguard.app.domain.Equipment;
import ar.com.heliguard.app.domain.Incident;
import ar.com.heliguard.app.domain.IncidentLocalization;
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
 * Spring Data R2DBC custom repository implementation for the IncidentLocalization entity.
 */
@SuppressWarnings("unused")
class IncidentLocalizationRepositoryInternalImpl
    extends AbstractR2dbcRepository<IncidentLocalization, Long>
    implements IncidentLocalizationRepositoryInternal
{

    private static final String SELECT_WITH_RELATIONS_SQL =
        "SELECT e.* FROM incident_localization e" +
        " LEFT JOIN organization e_organization ON e.organization_id = e_organization.id" +
        " LEFT JOIN incident incident ON e.incident_id = incident.id" +
        " LEFT JOIN solar_sector solarSector ON e.solar_sector_id = solarSector.id" +
        " LEFT JOIN solar_string solarString ON e.solar_string_id = solarString.id" +
        " LEFT JOIN equipment equipment ON e.equipment_id = equipment.id";

    // The clients sort by the related entity display field, with the entity id as secondary sort.
    private static final Map<String, String> SORT_COLUMNS = Map.ofEntries(
        Map.entry("id", "e.id"),
        Map.entry("organization.id", "e_organization.id"),
        Map.entry("organization.name", "e_organization.name"),
        Map.entry("incident.id", "incident.id"),
        Map.entry("incident.title", "incident.title"),
        Map.entry("solarSector.id", "solarSector.id"),
        Map.entry("solarSector.sectorCode", "solarSector.sector_code"),
        Map.entry("solarString.id", "solarString.id"),
        Map.entry("solarString.stringCode", "solarString.string_code"),
        Map.entry("equipment.id", "equipment.id"),
        Map.entry("equipment.tag", "equipment.tag")
    );

    public IncidentLocalizationRepositoryInternalImpl(R2dbcEntityTemplate template, R2dbcConverter converter, R2dbcDialect dialect) {
        super(IncidentLocalization.class, template, converter, dialect);
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
    protected Mono<List<IncidentLocalization>> populateRelationships(List<IncidentLocalization> entities) {
        return Mono.just(entities)
            .flatMap(populated ->
                populate(
                    populated,
                    IncidentLocalization::getOrganizationId,
                    Organization.class,
                    Organization::getId,
                    IncidentLocalization::setOrganization
                )
            )
            .flatMap(populated ->
                populate(populated, IncidentLocalization::getIncidentId, Incident.class, Incident::getId, IncidentLocalization::setIncident)
            )
            .flatMap(populated ->
                populate(
                    populated,
                    IncidentLocalization::getSolarSectorId,
                    SolarSector.class,
                    SolarSector::getId,
                    IncidentLocalization::setSolarSector
                )
            )
            .flatMap(populated ->
                populate(
                    populated,
                    IncidentLocalization::getSolarStringId,
                    SolarString.class,
                    SolarString::getId,
                    IncidentLocalization::setSolarString
                )
            )
            .flatMap(populated ->
                populate(
                    populated,
                    IncidentLocalization::getEquipmentId,
                    Equipment.class,
                    Equipment::getId,
                    IncidentLocalization::setEquipment
                )
            );
    }
}

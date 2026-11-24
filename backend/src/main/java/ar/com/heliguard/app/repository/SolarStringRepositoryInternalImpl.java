package ar.com.heliguard.app.repository;

import ar.com.heliguard.app.domain.EquipmentModel;
import ar.com.heliguard.app.domain.Organization;
import ar.com.heliguard.app.domain.SolarArray;
import ar.com.heliguard.app.domain.SolarString;
import java.util.List;
import java.util.Map;
import org.springframework.data.r2dbc.convert.R2dbcConverter;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.r2dbc.dialect.R2dbcDialect;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC custom repository implementation for the SolarString entity.
 */
@SuppressWarnings("unused")
class SolarStringRepositoryInternalImpl extends AbstractR2dbcRepository<SolarString, Long> implements SolarStringRepositoryInternal {

    private static final String SELECT_WITH_RELATIONS_SQL =
        "SELECT e.* FROM solar_string e" +
        " LEFT JOIN organization e_organization ON e.organization_id = e_organization.id" +
        " LEFT JOIN solar_array solarArray ON e.solar_array_id = solarArray.id" +
        " LEFT JOIN equipment_model moduleModel ON e.module_model_id = moduleModel.id";

    // The clients sort by the related entity display field, with the entity id as secondary sort.
    private static final Map<String, String> SORT_COLUMNS = Map.ofEntries(
        Map.entry("id", "e.id"),
        Map.entry("organization.id", "e_organization.id"),
        Map.entry("organization.name", "e_organization.name"),
        Map.entry("solarArray.id", "solarArray.id"),
        Map.entry("solarArray.name", "solarArray.name"),
        Map.entry("moduleModel.id", "moduleModel.id"),
        Map.entry("moduleModel.modelCode", "moduleModel.model_code")
    );

    public SolarStringRepositoryInternalImpl(R2dbcEntityTemplate template, R2dbcConverter converter, R2dbcDialect dialect) {
        super(SolarString.class, template, converter, dialect);
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
    protected Mono<List<SolarString>> populateRelationships(List<SolarString> entities) {
        return Mono.just(entities)
            .flatMap(populated ->
                populate(populated, SolarString::getOrganizationId, Organization.class, Organization::getId, SolarString::setOrganization)
            )
            .flatMap(populated ->
                populate(populated, SolarString::getSolarArrayId, SolarArray.class, SolarArray::getId, SolarString::setSolarArray)
            )
            .flatMap(populated ->
                populate(populated, SolarString::getModuleModelId, EquipmentModel.class, EquipmentModel::getId, SolarString::setModuleModel)
            );
    }
}

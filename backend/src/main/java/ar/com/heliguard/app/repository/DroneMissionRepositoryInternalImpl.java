package ar.com.heliguard.app.repository;

import ar.com.heliguard.app.domain.DroneAsset;
import ar.com.heliguard.app.domain.DroneMission;
import ar.com.heliguard.app.domain.Installation;
import ar.com.heliguard.app.domain.Organization;
import ar.com.heliguard.app.domain.WorkOrder;
import java.util.List;
import java.util.Map;
import org.springframework.data.r2dbc.convert.R2dbcConverter;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.r2dbc.dialect.R2dbcDialect;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC custom repository implementation for the DroneMission entity.
 */
@SuppressWarnings("unused")
class DroneMissionRepositoryInternalImpl extends AbstractR2dbcRepository<DroneMission, Long> implements DroneMissionRepositoryInternal {

    private static final String SELECT_WITH_RELATIONS_SQL =
        "SELECT e.* FROM drone_mission e" +
        " LEFT JOIN organization e_organization ON e.organization_id = e_organization.id" +
        " LEFT JOIN installation installation ON e.installation_id = installation.id" +
        " LEFT JOIN work_order workOrder ON e.work_order_id = workOrder.id" +
        " LEFT JOIN drone_asset droneAsset ON e.drone_asset_id = droneAsset.id" +
        " LEFT JOIN organization providerOrganization ON e.provider_organization_id = providerOrganization.id";

    // The clients sort by the related entity display field, with the entity id as secondary sort.
    private static final Map<String, String> SORT_COLUMNS = Map.ofEntries(
        Map.entry("id", "e.id"),
        Map.entry("organization.id", "e_organization.id"),
        Map.entry("organization.name", "e_organization.name"),
        Map.entry("installation.id", "installation.id"),
        Map.entry("installation.name", "installation.name"),
        Map.entry("workOrder.id", "workOrder.id"),
        Map.entry("workOrder.referenceCode", "workOrder.reference_code"),
        Map.entry("droneAsset.id", "droneAsset.id"),
        Map.entry("droneAsset.assetCode", "droneAsset.asset_code"),
        Map.entry("providerOrganization.id", "providerOrganization.id"),
        Map.entry("providerOrganization.name", "providerOrganization.name")
    );

    public DroneMissionRepositoryInternalImpl(R2dbcEntityTemplate template, R2dbcConverter converter, R2dbcDialect dialect) {
        super(DroneMission.class, template, converter, dialect);
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
    protected Mono<List<DroneMission>> populateRelationships(List<DroneMission> entities) {
        return Mono.just(entities)
            .flatMap(populated ->
                populate(populated, DroneMission::getOrganizationId, Organization.class, Organization::getId, DroneMission::setOrganization)
            )
            .flatMap(populated ->
                populate(populated, DroneMission::getInstallationId, Installation.class, Installation::getId, DroneMission::setInstallation)
            )
            .flatMap(populated ->
                populate(populated, DroneMission::getWorkOrderId, WorkOrder.class, WorkOrder::getId, DroneMission::setWorkOrder)
            )
            .flatMap(populated ->
                populate(populated, DroneMission::getDroneAssetId, DroneAsset.class, DroneAsset::getId, DroneMission::setDroneAsset)
            )
            .flatMap(populated ->
                populate(
                    populated,
                    DroneMission::getProviderOrganizationId,
                    Organization.class,
                    Organization::getId,
                    DroneMission::setProviderOrganization
                )
            );
    }
}

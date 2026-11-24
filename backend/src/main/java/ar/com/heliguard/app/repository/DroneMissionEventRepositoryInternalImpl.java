package ar.com.heliguard.app.repository;

import ar.com.heliguard.app.domain.DroneMission;
import ar.com.heliguard.app.domain.DroneMissionEvent;
import ar.com.heliguard.app.domain.Organization;
import ar.com.heliguard.app.domain.OrganizationMembership;
import java.util.List;
import java.util.Map;
import org.springframework.data.r2dbc.convert.R2dbcConverter;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.r2dbc.dialect.R2dbcDialect;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC custom repository implementation for the DroneMissionEvent entity.
 */
@SuppressWarnings("unused")
class DroneMissionEventRepositoryInternalImpl
    extends AbstractR2dbcRepository<DroneMissionEvent, Long>
    implements DroneMissionEventRepositoryInternal
{

    private static final String SELECT_WITH_RELATIONS_SQL =
        "SELECT e.* FROM drone_mission_event e" +
        " LEFT JOIN organization e_organization ON e.organization_id = e_organization.id" +
        " LEFT JOIN drone_mission mission ON e.mission_id = mission.id";

    // The clients sort by the related entity display field, with the entity id as secondary sort.
    private static final Map<String, String> SORT_COLUMNS = Map.ofEntries(
        Map.entry("id", "e.id"),
        Map.entry("organization.id", "e_organization.id"),
        Map.entry("organization.name", "e_organization.name"),
        Map.entry("mission.id", "mission.id"),
        Map.entry("mission.missionCode", "mission.mission_code"),
        Map.entry("operatorMembership.id", "e.operator_membership_id")
    );

    public DroneMissionEventRepositoryInternalImpl(R2dbcEntityTemplate template, R2dbcConverter converter, R2dbcDialect dialect) {
        super(DroneMissionEvent.class, template, converter, dialect);
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
    protected Mono<List<DroneMissionEvent>> populateRelationships(List<DroneMissionEvent> entities) {
        reference(
            entities,
            DroneMissionEvent::getOperatorMembershipId,
            OrganizationMembership::new,
            OrganizationMembership::setId,
            DroneMissionEvent::setOperatorMembership
        );
        return Mono.just(entities)
            .flatMap(populated ->
                populate(
                    populated,
                    DroneMissionEvent::getOrganizationId,
                    Organization.class,
                    Organization::getId,
                    DroneMissionEvent::setOrganization
                )
            )
            .flatMap(populated ->
                populate(populated, DroneMissionEvent::getMissionId, DroneMission.class, DroneMission::getId, DroneMissionEvent::setMission)
            );
    }
}

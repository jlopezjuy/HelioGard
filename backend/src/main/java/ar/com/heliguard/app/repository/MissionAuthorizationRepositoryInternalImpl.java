package ar.com.heliguard.app.repository;

import ar.com.heliguard.app.domain.DroneMission;
import ar.com.heliguard.app.domain.MissionAuthorization;
import ar.com.heliguard.app.domain.Organization;
import ar.com.heliguard.app.domain.OrganizationMembership;
import java.util.List;
import java.util.Map;
import org.springframework.data.r2dbc.convert.R2dbcConverter;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.r2dbc.dialect.R2dbcDialect;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC custom repository implementation for the MissionAuthorization entity.
 */
@SuppressWarnings("unused")
class MissionAuthorizationRepositoryInternalImpl
    extends AbstractR2dbcRepository<MissionAuthorization, Long>
    implements MissionAuthorizationRepositoryInternal
{

    private static final String SELECT_WITH_RELATIONS_SQL =
        "SELECT e.* FROM mission_authorization e" +
        " LEFT JOIN organization e_organization ON e.organization_id = e_organization.id" +
        " LEFT JOIN drone_mission mission ON e.mission_id = mission.id";

    // The clients sort by the related entity display field, with the entity id as secondary sort.
    private static final Map<String, String> SORT_COLUMNS = Map.ofEntries(
        Map.entry("id", "e.id"),
        Map.entry("organization.id", "e_organization.id"),
        Map.entry("organization.name", "e_organization.name"),
        Map.entry("mission.id", "mission.id"),
        Map.entry("mission.missionCode", "mission.mission_code"),
        Map.entry("authorizedByMembership.id", "e.authorized_by_membership_id")
    );

    public MissionAuthorizationRepositoryInternalImpl(R2dbcEntityTemplate template, R2dbcConverter converter, R2dbcDialect dialect) {
        super(MissionAuthorization.class, template, converter, dialect);
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
    protected Mono<List<MissionAuthorization>> populateRelationships(List<MissionAuthorization> entities) {
        reference(
            entities,
            MissionAuthorization::getAuthorizedByMembershipId,
            OrganizationMembership::new,
            OrganizationMembership::setId,
            MissionAuthorization::setAuthorizedByMembership
        );
        return Mono.just(entities)
            .flatMap(populated ->
                populate(
                    populated,
                    MissionAuthorization::getOrganizationId,
                    Organization.class,
                    Organization::getId,
                    MissionAuthorization::setOrganization
                )
            )
            .flatMap(populated ->
                populate(
                    populated,
                    MissionAuthorization::getMissionId,
                    DroneMission.class,
                    DroneMission::getId,
                    MissionAuthorization::setMission
                )
            );
    }
}

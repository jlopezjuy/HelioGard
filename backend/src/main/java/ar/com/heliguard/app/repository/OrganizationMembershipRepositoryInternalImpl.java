package ar.com.heliguard.app.repository;

import ar.com.heliguard.app.domain.Organization;
import ar.com.heliguard.app.domain.OrganizationMembership;
import ar.com.heliguard.app.domain.User;
import java.util.List;
import java.util.Map;
import org.springframework.data.r2dbc.convert.R2dbcConverter;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.r2dbc.dialect.R2dbcDialect;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC custom repository implementation for the OrganizationMembership entity.
 */
@SuppressWarnings("unused")
class OrganizationMembershipRepositoryInternalImpl
    extends AbstractR2dbcRepository<OrganizationMembership, Long>
    implements OrganizationMembershipRepositoryInternal
{

    private static final String SELECT_WITH_RELATIONS_SQL =
        "SELECT e.* FROM organization_membership e" +
        " LEFT JOIN organization e_organization ON e.organization_id = e_organization.id" +
        " LEFT JOIN jhi_user e_member ON e.member_id = e_member.id";

    // The clients sort by the related entity display field, with the entity id as secondary sort.
    private static final Map<String, String> SORT_COLUMNS = Map.ofEntries(
        Map.entry("id", "e.id"),
        Map.entry("organization.id", "e_organization.id"),
        Map.entry("organization.name", "e_organization.name"),
        Map.entry("member.id", "e_member.id"),
        Map.entry("member.login", "e_member.login")
    );

    public OrganizationMembershipRepositoryInternalImpl(R2dbcEntityTemplate template, R2dbcConverter converter, R2dbcDialect dialect) {
        super(OrganizationMembership.class, template, converter, dialect);
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
    protected Mono<List<OrganizationMembership>> populateRelationships(List<OrganizationMembership> entities) {
        return Mono.just(entities)
            .flatMap(populated ->
                populate(
                    populated,
                    OrganizationMembership::getOrganizationId,
                    Organization.class,
                    Organization::getId,
                    OrganizationMembership::setOrganization
                )
            )
            .flatMap(populated ->
                populate(populated, OrganizationMembership::getMemberId, User.class, User::getId, OrganizationMembership::setMember)
            );
    }
}

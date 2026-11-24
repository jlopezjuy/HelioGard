package ar.com.heliguard.app.repository;

import ar.com.heliguard.app.domain.Organization;
import ar.com.heliguard.app.domain.OrganizationMembership;
import ar.com.heliguard.app.domain.WorkOrder;
import ar.com.heliguard.app.domain.WorkOrderAssignment;
import java.util.List;
import java.util.Map;
import org.springframework.data.r2dbc.convert.R2dbcConverter;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.r2dbc.dialect.R2dbcDialect;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC custom repository implementation for the WorkOrderAssignment entity.
 */
@SuppressWarnings("unused")
class WorkOrderAssignmentRepositoryInternalImpl
    extends AbstractR2dbcRepository<WorkOrderAssignment, Long>
    implements WorkOrderAssignmentRepositoryInternal
{

    private static final String SELECT_WITH_RELATIONS_SQL =
        "SELECT e.* FROM work_order_assignment e" +
        " LEFT JOIN organization e_organization ON e.organization_id = e_organization.id" +
        " LEFT JOIN work_order workOrder ON e.work_order_id = workOrder.id" +
        " LEFT JOIN organization providerOrganization ON e.provider_organization_id = providerOrganization.id";

    // The clients sort by the related entity display field, with the entity id as secondary sort.
    private static final Map<String, String> SORT_COLUMNS = Map.ofEntries(
        Map.entry("id", "e.id"),
        Map.entry("organization.id", "e_organization.id"),
        Map.entry("organization.name", "e_organization.name"),
        Map.entry("workOrder.id", "workOrder.id"),
        Map.entry("workOrder.referenceCode", "workOrder.reference_code"),
        Map.entry("providerOrganization.id", "providerOrganization.id"),
        Map.entry("providerOrganization.name", "providerOrganization.name"),
        Map.entry("assigneeMembership.id", "e.assignee_membership_id")
    );

    public WorkOrderAssignmentRepositoryInternalImpl(R2dbcEntityTemplate template, R2dbcConverter converter, R2dbcDialect dialect) {
        super(WorkOrderAssignment.class, template, converter, dialect);
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
    protected Mono<List<WorkOrderAssignment>> populateRelationships(List<WorkOrderAssignment> entities) {
        reference(
            entities,
            WorkOrderAssignment::getAssigneeMembershipId,
            OrganizationMembership::new,
            OrganizationMembership::setId,
            WorkOrderAssignment::setAssigneeMembership
        );
        return Mono.just(entities)
            .flatMap(populated ->
                populate(
                    populated,
                    WorkOrderAssignment::getOrganizationId,
                    Organization.class,
                    Organization::getId,
                    WorkOrderAssignment::setOrganization
                )
            )
            .flatMap(populated ->
                populate(
                    populated,
                    WorkOrderAssignment::getWorkOrderId,
                    WorkOrder.class,
                    WorkOrder::getId,
                    WorkOrderAssignment::setWorkOrder
                )
            )
            .flatMap(populated ->
                populate(
                    populated,
                    WorkOrderAssignment::getProviderOrganizationId,
                    Organization.class,
                    Organization::getId,
                    WorkOrderAssignment::setProviderOrganization
                )
            );
    }
}

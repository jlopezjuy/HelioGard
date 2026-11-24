package ar.com.heliguard.app.repository;

import ar.com.heliguard.app.domain.Organization;
import ar.com.heliguard.app.domain.OrganizationMembership;
import ar.com.heliguard.app.domain.WorkOrder;
import ar.com.heliguard.app.domain.WorkOrderApproval;
import java.util.List;
import java.util.Map;
import org.springframework.data.r2dbc.convert.R2dbcConverter;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.r2dbc.dialect.R2dbcDialect;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC custom repository implementation for the WorkOrderApproval entity.
 */
@SuppressWarnings("unused")
class WorkOrderApprovalRepositoryInternalImpl
    extends AbstractR2dbcRepository<WorkOrderApproval, Long>
    implements WorkOrderApprovalRepositoryInternal
{

    private static final String SELECT_WITH_RELATIONS_SQL =
        "SELECT e.* FROM work_order_approval e" +
        " LEFT JOIN organization e_organization ON e.organization_id = e_organization.id" +
        " LEFT JOIN work_order workOrder ON e.work_order_id = workOrder.id";

    // The clients sort by the related entity display field, with the entity id as secondary sort.
    private static final Map<String, String> SORT_COLUMNS = Map.ofEntries(
        Map.entry("id", "e.id"),
        Map.entry("organization.id", "e_organization.id"),
        Map.entry("organization.name", "e_organization.name"),
        Map.entry("workOrder.id", "workOrder.id"),
        Map.entry("workOrder.referenceCode", "workOrder.reference_code"),
        Map.entry("decidedByMembership.id", "e.decided_by_membership_id")
    );

    public WorkOrderApprovalRepositoryInternalImpl(R2dbcEntityTemplate template, R2dbcConverter converter, R2dbcDialect dialect) {
        super(WorkOrderApproval.class, template, converter, dialect);
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
    protected Mono<List<WorkOrderApproval>> populateRelationships(List<WorkOrderApproval> entities) {
        reference(
            entities,
            WorkOrderApproval::getDecidedByMembershipId,
            OrganizationMembership::new,
            OrganizationMembership::setId,
            WorkOrderApproval::setDecidedByMembership
        );
        return Mono.just(entities)
            .flatMap(populated ->
                populate(
                    populated,
                    WorkOrderApproval::getOrganizationId,
                    Organization.class,
                    Organization::getId,
                    WorkOrderApproval::setOrganization
                )
            )
            .flatMap(populated ->
                populate(populated, WorkOrderApproval::getWorkOrderId, WorkOrder.class, WorkOrder::getId, WorkOrderApproval::setWorkOrder)
            );
    }
}

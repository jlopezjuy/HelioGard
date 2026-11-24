package ar.com.heliguard.app.repository;

import ar.com.heliguard.app.domain.AuditFinding;
import ar.com.heliguard.app.domain.AuditReport;
import ar.com.heliguard.app.domain.Equipment;
import ar.com.heliguard.app.domain.Organization;
import java.util.List;
import java.util.Map;
import org.springframework.data.r2dbc.convert.R2dbcConverter;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.r2dbc.dialect.R2dbcDialect;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC custom repository implementation for the AuditFinding entity.
 */
@SuppressWarnings("unused")
class AuditFindingRepositoryInternalImpl extends AbstractR2dbcRepository<AuditFinding, Long> implements AuditFindingRepositoryInternal {

    private static final String SELECT_WITH_RELATIONS_SQL =
        "SELECT e.* FROM audit_finding e" +
        " LEFT JOIN organization e_organization ON e.organization_id = e_organization.id" +
        " LEFT JOIN equipment equipment ON e.equipment_id = equipment.id";

    // The clients sort by the related entity display field, with the entity id as secondary sort.
    private static final Map<String, String> SORT_COLUMNS = Map.ofEntries(
        Map.entry("id", "e.id"),
        Map.entry("organization.id", "e_organization.id"),
        Map.entry("organization.name", "e_organization.name"),
        Map.entry("auditReport.id", "e.audit_report_id"),
        Map.entry("equipment.id", "equipment.id"),
        Map.entry("equipment.tag", "equipment.tag")
    );

    public AuditFindingRepositoryInternalImpl(R2dbcEntityTemplate template, R2dbcConverter converter, R2dbcDialect dialect) {
        super(AuditFinding.class, template, converter, dialect);
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
    protected Mono<List<AuditFinding>> populateRelationships(List<AuditFinding> entities) {
        reference(entities, AuditFinding::getAuditReportId, AuditReport::new, AuditReport::setId, AuditFinding::setAuditReport);
        return Mono.just(entities)
            .flatMap(populated ->
                populate(populated, AuditFinding::getOrganizationId, Organization.class, Organization::getId, AuditFinding::setOrganization)
            )
            .flatMap(populated ->
                populate(populated, AuditFinding::getEquipmentId, Equipment.class, Equipment::getId, AuditFinding::setEquipment)
            );
    }
}

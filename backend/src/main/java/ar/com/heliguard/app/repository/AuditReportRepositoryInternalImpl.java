package ar.com.heliguard.app.repository;

import ar.com.heliguard.app.domain.AuditReport;
import ar.com.heliguard.app.domain.AuditTemplate;
import ar.com.heliguard.app.domain.Installation;
import ar.com.heliguard.app.domain.Organization;
import ar.com.heliguard.app.domain.OrganizationMembership;
import java.util.List;
import java.util.Map;
import org.springframework.data.r2dbc.convert.R2dbcConverter;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.r2dbc.dialect.R2dbcDialect;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC custom repository implementation for the AuditReport entity.
 */
@SuppressWarnings("unused")
class AuditReportRepositoryInternalImpl extends AbstractR2dbcRepository<AuditReport, Long> implements AuditReportRepositoryInternal {

    private static final String SELECT_WITH_RELATIONS_SQL =
        "SELECT e.* FROM audit_report e" +
        " LEFT JOIN organization e_organization ON e.organization_id = e_organization.id" +
        " LEFT JOIN installation installation ON e.installation_id = installation.id" +
        " LEFT JOIN audit_template auditTemplate ON e.audit_template_id = auditTemplate.id";

    // The clients sort by the related entity display field, with the entity id as secondary sort.
    private static final Map<String, String> SORT_COLUMNS = Map.ofEntries(
        Map.entry("id", "e.id"),
        Map.entry("organization.id", "e_organization.id"),
        Map.entry("organization.name", "e_organization.name"),
        Map.entry("installation.id", "installation.id"),
        Map.entry("installation.name", "installation.name"),
        Map.entry("auditTemplate.id", "auditTemplate.id"),
        Map.entry("auditTemplate.title", "auditTemplate.title"),
        Map.entry("auditorMembership.id", "e.auditor_membership_id")
    );

    public AuditReportRepositoryInternalImpl(R2dbcEntityTemplate template, R2dbcConverter converter, R2dbcDialect dialect) {
        super(AuditReport.class, template, converter, dialect);
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
    protected Mono<List<AuditReport>> populateRelationships(List<AuditReport> entities) {
        reference(
            entities,
            AuditReport::getAuditorMembershipId,
            OrganizationMembership::new,
            OrganizationMembership::setId,
            AuditReport::setAuditorMembership
        );
        return Mono.just(entities)
            .flatMap(populated ->
                populate(populated, AuditReport::getOrganizationId, Organization.class, Organization::getId, AuditReport::setOrganization)
            )
            .flatMap(populated ->
                populate(populated, AuditReport::getInstallationId, Installation.class, Installation::getId, AuditReport::setInstallation)
            )
            .flatMap(populated ->
                populate(
                    populated,
                    AuditReport::getAuditTemplateId,
                    AuditTemplate.class,
                    AuditTemplate::getId,
                    AuditReport::setAuditTemplate
                )
            );
    }
}

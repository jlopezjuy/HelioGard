package ar.com.heliguard.app.repository;

import ar.com.heliguard.app.domain.AuditReport;
import ar.com.heliguard.app.domain.AuditReportSignature;
import ar.com.heliguard.app.domain.Organization;
import ar.com.heliguard.app.domain.OrganizationMembership;
import java.util.List;
import java.util.Map;
import org.springframework.data.r2dbc.convert.R2dbcConverter;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.r2dbc.dialect.R2dbcDialect;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC custom repository implementation for the AuditReportSignature entity.
 */
@SuppressWarnings("unused")
class AuditReportSignatureRepositoryInternalImpl
    extends AbstractR2dbcRepository<AuditReportSignature, Long>
    implements AuditReportSignatureRepositoryInternal
{

    private static final String SELECT_WITH_RELATIONS_SQL =
        "SELECT e.* FROM audit_report_signature e" + " LEFT JOIN organization e_organization ON e.organization_id = e_organization.id";

    // The clients sort by the related entity display field, with the entity id as secondary sort.
    private static final Map<String, String> SORT_COLUMNS = Map.ofEntries(
        Map.entry("id", "e.id"),
        Map.entry("organization.id", "e_organization.id"),
        Map.entry("organization.name", "e_organization.name"),
        Map.entry("auditReport.id", "e.audit_report_id"),
        Map.entry("signerMembership.id", "e.signer_membership_id")
    );

    public AuditReportSignatureRepositoryInternalImpl(R2dbcEntityTemplate template, R2dbcConverter converter, R2dbcDialect dialect) {
        super(AuditReportSignature.class, template, converter, dialect);
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
    protected Mono<List<AuditReportSignature>> populateRelationships(List<AuditReportSignature> entities) {
        reference(
            entities,
            AuditReportSignature::getAuditReportId,
            AuditReport::new,
            AuditReport::setId,
            AuditReportSignature::setAuditReport
        );
        reference(
            entities,
            AuditReportSignature::getSignerMembershipId,
            OrganizationMembership::new,
            OrganizationMembership::setId,
            AuditReportSignature::setSignerMembership
        );
        return Mono.just(entities).flatMap(populated ->
            populate(
                populated,
                AuditReportSignature::getOrganizationId,
                Organization.class,
                Organization::getId,
                AuditReportSignature::setOrganization
            )
        );
    }
}

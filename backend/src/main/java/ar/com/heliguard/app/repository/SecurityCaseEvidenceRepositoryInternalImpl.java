package ar.com.heliguard.app.repository;

import ar.com.heliguard.app.domain.DataProvenance;
import ar.com.heliguard.app.domain.IntegrityAssessment;
import ar.com.heliguard.app.domain.Organization;
import ar.com.heliguard.app.domain.SecurityCaseEvidence;
import ar.com.heliguard.app.domain.SecurityIncident;
import java.util.List;
import java.util.Map;
import org.springframework.data.r2dbc.convert.R2dbcConverter;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.r2dbc.dialect.R2dbcDialect;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC custom repository implementation for the SecurityCaseEvidence entity.
 */
@SuppressWarnings("unused")
class SecurityCaseEvidenceRepositoryInternalImpl
    extends AbstractR2dbcRepository<SecurityCaseEvidence, Long>
    implements SecurityCaseEvidenceRepositoryInternal
{

    private static final String SELECT_WITH_RELATIONS_SQL =
        "SELECT e.* FROM security_case_evidence e" +
        " LEFT JOIN organization e_organization ON e.organization_id = e_organization.id" +
        " LEFT JOIN security_incident securityIncident ON e.security_incident_id = securityIncident.id";

    // The clients sort by the related entity display field, with the entity id as secondary sort.
    private static final Map<String, String> SORT_COLUMNS = Map.ofEntries(
        Map.entry("id", "e.id"),
        Map.entry("organization.id", "e_organization.id"),
        Map.entry("organization.name", "e_organization.name"),
        Map.entry("securityIncident.id", "securityIncident.id"),
        Map.entry("securityIncident.referenceCode", "securityIncident.reference_code"),
        Map.entry("integrityAssessment.id", "e.integrity_assessment_id"),
        Map.entry("dataProvenance.id", "e.data_provenance_id")
    );

    public SecurityCaseEvidenceRepositoryInternalImpl(R2dbcEntityTemplate template, R2dbcConverter converter, R2dbcDialect dialect) {
        super(SecurityCaseEvidence.class, template, converter, dialect);
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
    protected Mono<List<SecurityCaseEvidence>> populateRelationships(List<SecurityCaseEvidence> entities) {
        reference(
            entities,
            SecurityCaseEvidence::getIntegrityAssessmentId,
            IntegrityAssessment::new,
            IntegrityAssessment::setId,
            SecurityCaseEvidence::setIntegrityAssessment
        );
        reference(
            entities,
            SecurityCaseEvidence::getDataProvenanceId,
            DataProvenance::new,
            DataProvenance::setId,
            SecurityCaseEvidence::setDataProvenance
        );
        return Mono.just(entities)
            .flatMap(populated ->
                populate(
                    populated,
                    SecurityCaseEvidence::getOrganizationId,
                    Organization.class,
                    Organization::getId,
                    SecurityCaseEvidence::setOrganization
                )
            )
            .flatMap(populated ->
                populate(
                    populated,
                    SecurityCaseEvidence::getSecurityIncidentId,
                    SecurityIncident.class,
                    SecurityIncident::getId,
                    SecurityCaseEvidence::setSecurityIncident
                )
            );
    }
}

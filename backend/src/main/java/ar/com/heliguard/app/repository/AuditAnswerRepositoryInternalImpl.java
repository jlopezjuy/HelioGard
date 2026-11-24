package ar.com.heliguard.app.repository;

import ar.com.heliguard.app.domain.AuditAnswer;
import ar.com.heliguard.app.domain.AuditQuestion;
import ar.com.heliguard.app.domain.AuditReport;
import ar.com.heliguard.app.domain.Organization;
import java.util.List;
import java.util.Map;
import org.springframework.data.r2dbc.convert.R2dbcConverter;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.r2dbc.dialect.R2dbcDialect;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC custom repository implementation for the AuditAnswer entity.
 */
@SuppressWarnings("unused")
class AuditAnswerRepositoryInternalImpl extends AbstractR2dbcRepository<AuditAnswer, Long> implements AuditAnswerRepositoryInternal {

    private static final String SELECT_WITH_RELATIONS_SQL =
        "SELECT e.* FROM audit_answer e" +
        " LEFT JOIN organization e_organization ON e.organization_id = e_organization.id" +
        " LEFT JOIN audit_question auditQuestion ON e.audit_question_id = auditQuestion.id";

    // The clients sort by the related entity display field, with the entity id as secondary sort.
    private static final Map<String, String> SORT_COLUMNS = Map.ofEntries(
        Map.entry("id", "e.id"),
        Map.entry("organization.id", "e_organization.id"),
        Map.entry("organization.name", "e_organization.name"),
        Map.entry("auditReport.id", "e.audit_report_id"),
        Map.entry("auditQuestion.id", "auditQuestion.id"),
        Map.entry("auditQuestion.questionCode", "auditQuestion.question_code")
    );

    public AuditAnswerRepositoryInternalImpl(R2dbcEntityTemplate template, R2dbcConverter converter, R2dbcDialect dialect) {
        super(AuditAnswer.class, template, converter, dialect);
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
    protected Mono<List<AuditAnswer>> populateRelationships(List<AuditAnswer> entities) {
        reference(entities, AuditAnswer::getAuditReportId, AuditReport::new, AuditReport::setId, AuditAnswer::setAuditReport);
        return Mono.just(entities)
            .flatMap(populated ->
                populate(populated, AuditAnswer::getOrganizationId, Organization.class, Organization::getId, AuditAnswer::setOrganization)
            )
            .flatMap(populated ->
                populate(
                    populated,
                    AuditAnswer::getAuditQuestionId,
                    AuditQuestion.class,
                    AuditQuestion::getId,
                    AuditAnswer::setAuditQuestion
                )
            );
    }
}

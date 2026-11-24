package ar.com.heliguard.app.repository;

import ar.com.heliguard.app.domain.DroneMission;
import ar.com.heliguard.app.domain.Installation;
import ar.com.heliguard.app.domain.Organization;
import ar.com.heliguard.app.domain.RecoveryAssessment;
import ar.com.heliguard.app.domain.VerificationResult;
import java.util.List;
import java.util.Map;
import org.springframework.data.r2dbc.convert.R2dbcConverter;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.r2dbc.dialect.R2dbcDialect;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC custom repository implementation for the VerificationResult entity.
 */
@SuppressWarnings("unused")
class VerificationResultRepositoryInternalImpl
    extends AbstractR2dbcRepository<VerificationResult, Long>
    implements VerificationResultRepositoryInternal
{

    private static final String SELECT_WITH_RELATIONS_SQL =
        "SELECT e.* FROM verification_result e" +
        " LEFT JOIN organization e_organization ON e.organization_id = e_organization.id" +
        " LEFT JOIN installation installation ON e.installation_id = installation.id" +
        " LEFT JOIN drone_mission droneMission ON e.drone_mission_id = droneMission.id";

    // The clients sort by the related entity display field, with the entity id as secondary sort.
    private static final Map<String, String> SORT_COLUMNS = Map.ofEntries(
        Map.entry("id", "e.id"),
        Map.entry("organization.id", "e_organization.id"),
        Map.entry("organization.name", "e_organization.name"),
        Map.entry("installation.id", "installation.id"),
        Map.entry("installation.name", "installation.name"),
        Map.entry("droneMission.id", "droneMission.id"),
        Map.entry("droneMission.missionCode", "droneMission.mission_code"),
        Map.entry("recoveryAssessment.id", "e.recovery_assessment_id")
    );

    public VerificationResultRepositoryInternalImpl(R2dbcEntityTemplate template, R2dbcConverter converter, R2dbcDialect dialect) {
        super(VerificationResult.class, template, converter, dialect);
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
    protected Mono<List<VerificationResult>> populateRelationships(List<VerificationResult> entities) {
        reference(
            entities,
            VerificationResult::getRecoveryAssessmentId,
            RecoveryAssessment::new,
            RecoveryAssessment::setId,
            VerificationResult::setRecoveryAssessment
        );
        return Mono.just(entities)
            .flatMap(populated ->
                populate(
                    populated,
                    VerificationResult::getOrganizationId,
                    Organization.class,
                    Organization::getId,
                    VerificationResult::setOrganization
                )
            )
            .flatMap(populated ->
                populate(
                    populated,
                    VerificationResult::getInstallationId,
                    Installation.class,
                    Installation::getId,
                    VerificationResult::setInstallation
                )
            )
            .flatMap(populated ->
                populate(
                    populated,
                    VerificationResult::getDroneMissionId,
                    DroneMission.class,
                    DroneMission::getId,
                    VerificationResult::setDroneMission
                )
            );
    }
}

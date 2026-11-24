package ar.com.heliguard.app.repository;

import ar.com.heliguard.app.domain.PlanEligibilityAssessment;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the PlanEligibilityAssessment entity.
 */
@SuppressWarnings("unused")
@Repository
public interface PlanEligibilityAssessmentRepository
    extends ReactiveCrudRepository<PlanEligibilityAssessment, Long>, PlanEligibilityAssessmentRepositoryInternal
{
    @Override
    Mono<PlanEligibilityAssessment> findOneWithEagerRelationships(Long id);

    @Override
    Flux<PlanEligibilityAssessment> findAllWithEagerRelationships();

    @Override
    Flux<PlanEligibilityAssessment> findAllWithEagerRelationships(Pageable page);

    @Query("SELECT * FROM plan_eligibility_assessment entity WHERE entity.organization_id = :id")
    Flux<PlanEligibilityAssessment> findByOrganization(Long id);

    @Query("SELECT * FROM plan_eligibility_assessment entity WHERE entity.organization_id IS NULL")
    Flux<PlanEligibilityAssessment> findAllWhereOrganizationIsNull();

    @Query("SELECT * FROM plan_eligibility_assessment entity WHERE entity.installation_id = :id")
    Flux<PlanEligibilityAssessment> findByInstallation(Long id);

    @Query("SELECT * FROM plan_eligibility_assessment entity WHERE entity.installation_id IS NULL")
    Flux<PlanEligibilityAssessment> findAllWhereInstallationIsNull();

    @Query("SELECT * FROM plan_eligibility_assessment entity WHERE entity.audit_report_id = :id")
    Flux<PlanEligibilityAssessment> findByAuditReport(Long id);

    @Query("SELECT * FROM plan_eligibility_assessment entity WHERE entity.audit_report_id IS NULL")
    Flux<PlanEligibilityAssessment> findAllWhereAuditReportIsNull();

    @Query("SELECT * FROM plan_eligibility_assessment entity WHERE entity.plan_version_id = :id")
    Flux<PlanEligibilityAssessment> findByPlanVersion(Long id);

    @Query("SELECT * FROM plan_eligibility_assessment entity WHERE entity.plan_version_id IS NULL")
    Flux<PlanEligibilityAssessment> findAllWherePlanVersionIsNull();

    @Override
    <S extends PlanEligibilityAssessment> Mono<S> save(S entity);

    @Override
    Flux<PlanEligibilityAssessment> findAll();

    @Override
    Mono<PlanEligibilityAssessment> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface PlanEligibilityAssessmentRepositoryInternal {
    Flux<PlanEligibilityAssessment> findAllBy(Pageable pageable);

    Flux<PlanEligibilityAssessment> findAll();

    Mono<PlanEligibilityAssessment> findById(Long id);

    Mono<PlanEligibilityAssessment> findOneWithEagerRelationships(Long id);

    Flux<PlanEligibilityAssessment> findAllWithEagerRelationships();

    Flux<PlanEligibilityAssessment> findAllWithEagerRelationships(Pageable page);
}

package ar.com.heliguard.app.repository;

import ar.com.heliguard.app.domain.EconomicAssessment;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the EconomicAssessment entity.
 */
@SuppressWarnings("unused")
@Repository
public interface EconomicAssessmentRepository
    extends ReactiveCrudRepository<EconomicAssessment, Long>, EconomicAssessmentRepositoryInternal
{
    @Override
    Mono<EconomicAssessment> findOneWithEagerRelationships(Long id);

    @Override
    Flux<EconomicAssessment> findAllWithEagerRelationships();

    @Override
    Flux<EconomicAssessment> findAllWithEagerRelationships(Pageable page);

    @Query("SELECT * FROM economic_assessment entity WHERE entity.organization_id = :id")
    Flux<EconomicAssessment> findByOrganization(Long id);

    @Query("SELECT * FROM economic_assessment entity WHERE entity.organization_id IS NULL")
    Flux<EconomicAssessment> findAllWhereOrganizationIsNull();

    @Query("SELECT * FROM economic_assessment entity WHERE entity.loss_assessment_id = :id")
    Flux<EconomicAssessment> findByLossAssessment(Long id);

    @Query("SELECT * FROM economic_assessment entity WHERE entity.loss_assessment_id IS NULL")
    Flux<EconomicAssessment> findAllWhereLossAssessmentIsNull();

    @Query("SELECT * FROM economic_assessment entity WHERE entity.tariff_rate_id = :id")
    Flux<EconomicAssessment> findByTariffRate(Long id);

    @Query("SELECT * FROM economic_assessment entity WHERE entity.tariff_rate_id IS NULL")
    Flux<EconomicAssessment> findAllWhereTariffRateIsNull();

    @Override
    <S extends EconomicAssessment> Mono<S> save(S entity);

    @Override
    Flux<EconomicAssessment> findAll();

    @Override
    Mono<EconomicAssessment> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface EconomicAssessmentRepositoryInternal {
    Flux<EconomicAssessment> findAllBy(Pageable pageable);

    Flux<EconomicAssessment> findAll();

    Mono<EconomicAssessment> findById(Long id);

    Mono<EconomicAssessment> findOneWithEagerRelationships(Long id);

    Flux<EconomicAssessment> findAllWithEagerRelationships();

    Flux<EconomicAssessment> findAllWithEagerRelationships(Pageable page);
}

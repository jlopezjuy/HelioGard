package ar.com.heliguard.app.repository;

import ar.com.heliguard.app.domain.InverterMpptInput;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the InverterMpptInput entity.
 */
@SuppressWarnings("unused")
@Repository
public interface InverterMpptInputRepository extends ReactiveCrudRepository<InverterMpptInput, Long>, InverterMpptInputRepositoryInternal {
    @Override
    Mono<InverterMpptInput> findOneWithEagerRelationships(Long id);

    @Override
    Flux<InverterMpptInput> findAllWithEagerRelationships();

    @Override
    Flux<InverterMpptInput> findAllWithEagerRelationships(Pageable page);

    @Query("SELECT * FROM inverter_mppt_input entity WHERE entity.organization_id = :id")
    Flux<InverterMpptInput> findByOrganization(Long id);

    @Query("SELECT * FROM inverter_mppt_input entity WHERE entity.organization_id IS NULL")
    Flux<InverterMpptInput> findAllWhereOrganizationIsNull();

    @Query("SELECT * FROM inverter_mppt_input entity WHERE entity.inverter_id = :id")
    Flux<InverterMpptInput> findByInverter(Long id);

    @Query("SELECT * FROM inverter_mppt_input entity WHERE entity.inverter_id IS NULL")
    Flux<InverterMpptInput> findAllWhereInverterIsNull();

    @Override
    <S extends InverterMpptInput> Mono<S> save(S entity);

    @Override
    Flux<InverterMpptInput> findAll();

    @Override
    Mono<InverterMpptInput> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface InverterMpptInputRepositoryInternal {
    Flux<InverterMpptInput> findAllBy(Pageable pageable);

    Flux<InverterMpptInput> findAll();

    Mono<InverterMpptInput> findById(Long id);

    Mono<InverterMpptInput> findOneWithEagerRelationships(Long id);

    Flux<InverterMpptInput> findAllWithEagerRelationships();

    Flux<InverterMpptInput> findAllWithEagerRelationships(Pageable page);
}

package ar.com.heliguard.app.repository;

import ar.com.heliguard.app.domain.StringInputAssignment;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the StringInputAssignment entity.
 */
@SuppressWarnings("unused")
@Repository
public interface StringInputAssignmentRepository
    extends ReactiveCrudRepository<StringInputAssignment, Long>, StringInputAssignmentRepositoryInternal
{
    @Override
    Mono<StringInputAssignment> findOneWithEagerRelationships(Long id);

    @Override
    Flux<StringInputAssignment> findAllWithEagerRelationships();

    @Override
    Flux<StringInputAssignment> findAllWithEagerRelationships(Pageable page);

    @Query("SELECT * FROM string_input_assignment entity WHERE entity.organization_id = :id")
    Flux<StringInputAssignment> findByOrganization(Long id);

    @Query("SELECT * FROM string_input_assignment entity WHERE entity.organization_id IS NULL")
    Flux<StringInputAssignment> findAllWhereOrganizationIsNull();

    @Query("SELECT * FROM string_input_assignment entity WHERE entity.solar_string_id = :id")
    Flux<StringInputAssignment> findBySolarString(Long id);

    @Query("SELECT * FROM string_input_assignment entity WHERE entity.solar_string_id IS NULL")
    Flux<StringInputAssignment> findAllWhereSolarStringIsNull();

    @Query("SELECT * FROM string_input_assignment entity WHERE entity.mppt_input_id = :id")
    Flux<StringInputAssignment> findByMpptInput(Long id);

    @Query("SELECT * FROM string_input_assignment entity WHERE entity.mppt_input_id IS NULL")
    Flux<StringInputAssignment> findAllWhereMpptInputIsNull();

    @Override
    <S extends StringInputAssignment> Mono<S> save(S entity);

    @Override
    Flux<StringInputAssignment> findAll();

    @Override
    Mono<StringInputAssignment> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface StringInputAssignmentRepositoryInternal {
    Flux<StringInputAssignment> findAllBy(Pageable pageable);

    Flux<StringInputAssignment> findAll();

    Mono<StringInputAssignment> findById(Long id);

    Mono<StringInputAssignment> findOneWithEagerRelationships(Long id);

    Flux<StringInputAssignment> findAllWithEagerRelationships();

    Flux<StringInputAssignment> findAllWithEagerRelationships(Pageable page);
}

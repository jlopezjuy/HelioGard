package ar.com.heliguard.app.repository;

import ar.com.heliguard.app.domain.IncidentEvent;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the IncidentEvent entity.
 */
@SuppressWarnings("unused")
@Repository
public interface IncidentEventRepository extends ReactiveCrudRepository<IncidentEvent, Long>, IncidentEventRepositoryInternal {
    @Override
    Mono<IncidentEvent> findOneWithEagerRelationships(Long id);

    @Override
    Flux<IncidentEvent> findAllWithEagerRelationships();

    @Override
    Flux<IncidentEvent> findAllWithEagerRelationships(Pageable page);

    @Query("SELECT * FROM incident_event entity WHERE entity.organization_id = :id")
    Flux<IncidentEvent> findByOrganization(Long id);

    @Query("SELECT * FROM incident_event entity WHERE entity.organization_id IS NULL")
    Flux<IncidentEvent> findAllWhereOrganizationIsNull();

    @Query("SELECT * FROM incident_event entity WHERE entity.incident_id = :id")
    Flux<IncidentEvent> findByIncident(Long id);

    @Query("SELECT * FROM incident_event entity WHERE entity.incident_id IS NULL")
    Flux<IncidentEvent> findAllWhereIncidentIsNull();

    @Override
    <S extends IncidentEvent> Mono<S> save(S entity);

    @Override
    Flux<IncidentEvent> findAll();

    @Override
    Mono<IncidentEvent> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface IncidentEventRepositoryInternal {
    Flux<IncidentEvent> findAllBy(Pageable pageable);

    Flux<IncidentEvent> findAll();

    Mono<IncidentEvent> findById(Long id);

    Mono<IncidentEvent> findOneWithEagerRelationships(Long id);

    Flux<IncidentEvent> findAllWithEagerRelationships();

    Flux<IncidentEvent> findAllWithEagerRelationships(Pageable page);
}

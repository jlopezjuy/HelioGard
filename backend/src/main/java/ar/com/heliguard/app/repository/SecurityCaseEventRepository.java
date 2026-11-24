package ar.com.heliguard.app.repository;

import ar.com.heliguard.app.domain.SecurityCaseEvent;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the SecurityCaseEvent entity.
 */
@SuppressWarnings("unused")
@Repository
public interface SecurityCaseEventRepository extends ReactiveCrudRepository<SecurityCaseEvent, Long>, SecurityCaseEventRepositoryInternal {
    @Override
    Mono<SecurityCaseEvent> findOneWithEagerRelationships(Long id);

    @Override
    Flux<SecurityCaseEvent> findAllWithEagerRelationships();

    @Override
    Flux<SecurityCaseEvent> findAllWithEagerRelationships(Pageable page);

    @Query("SELECT * FROM security_case_event entity WHERE entity.organization_id = :id")
    Flux<SecurityCaseEvent> findByOrganization(Long id);

    @Query("SELECT * FROM security_case_event entity WHERE entity.organization_id IS NULL")
    Flux<SecurityCaseEvent> findAllWhereOrganizationIsNull();

    @Query("SELECT * FROM security_case_event entity WHERE entity.security_incident_id = :id")
    Flux<SecurityCaseEvent> findBySecurityIncident(Long id);

    @Query("SELECT * FROM security_case_event entity WHERE entity.security_incident_id IS NULL")
    Flux<SecurityCaseEvent> findAllWhereSecurityIncidentIsNull();

    @Query("SELECT * FROM security_case_event entity WHERE entity.actor_membership_id = :id")
    Flux<SecurityCaseEvent> findByActorMembership(Long id);

    @Query("SELECT * FROM security_case_event entity WHERE entity.actor_membership_id IS NULL")
    Flux<SecurityCaseEvent> findAllWhereActorMembershipIsNull();

    @Override
    <S extends SecurityCaseEvent> Mono<S> save(S entity);

    @Override
    Flux<SecurityCaseEvent> findAll();

    @Override
    Mono<SecurityCaseEvent> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface SecurityCaseEventRepositoryInternal {
    Flux<SecurityCaseEvent> findAllBy(Pageable pageable);

    Flux<SecurityCaseEvent> findAll();

    Mono<SecurityCaseEvent> findById(Long id);

    Mono<SecurityCaseEvent> findOneWithEagerRelationships(Long id);

    Flux<SecurityCaseEvent> findAllWithEagerRelationships();

    Flux<SecurityCaseEvent> findAllWithEagerRelationships(Pageable page);
}

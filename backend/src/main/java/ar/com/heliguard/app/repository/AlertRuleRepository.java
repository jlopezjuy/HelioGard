package ar.com.heliguard.app.repository;

import ar.com.heliguard.app.domain.AlertRule;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the AlertRule entity.
 */
@SuppressWarnings("unused")
@Repository
public interface AlertRuleRepository extends ReactiveCrudRepository<AlertRule, Long>, AlertRuleRepositoryInternal {
    @Override
    Mono<AlertRule> findOneWithEagerRelationships(Long id);

    @Override
    Flux<AlertRule> findAllWithEagerRelationships();

    @Override
    Flux<AlertRule> findAllWithEagerRelationships(Pageable page);

    @Query("SELECT * FROM alert_rule entity WHERE entity.organization_id = :id")
    Flux<AlertRule> findByOrganization(Long id);

    @Query("SELECT * FROM alert_rule entity WHERE entity.organization_id IS NULL")
    Flux<AlertRule> findAllWhereOrganizationIsNull();

    @Query("SELECT * FROM alert_rule entity WHERE entity.installation_id = :id")
    Flux<AlertRule> findByInstallation(Long id);

    @Query("SELECT * FROM alert_rule entity WHERE entity.installation_id IS NULL")
    Flux<AlertRule> findAllWhereInstallationIsNull();

    @Override
    <S extends AlertRule> Mono<S> save(S entity);

    @Override
    Flux<AlertRule> findAll();

    @Override
    Mono<AlertRule> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface AlertRuleRepositoryInternal {
    Flux<AlertRule> findAllBy(Pageable pageable);

    Flux<AlertRule> findAll();

    Mono<AlertRule> findById(Long id);

    Mono<AlertRule> findOneWithEagerRelationships(Long id);

    Flux<AlertRule> findAllWithEagerRelationships();

    Flux<AlertRule> findAllWithEagerRelationships(Pageable page);
}

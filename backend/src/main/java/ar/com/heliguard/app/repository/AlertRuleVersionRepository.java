package ar.com.heliguard.app.repository;

import ar.com.heliguard.app.domain.AlertRuleVersion;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the AlertRuleVersion entity.
 */
@SuppressWarnings("unused")
@Repository
public interface AlertRuleVersionRepository extends ReactiveCrudRepository<AlertRuleVersion, Long>, AlertRuleVersionRepositoryInternal {
    @Override
    Mono<AlertRuleVersion> findOneWithEagerRelationships(Long id);

    @Override
    Flux<AlertRuleVersion> findAllWithEagerRelationships();

    @Override
    Flux<AlertRuleVersion> findAllWithEagerRelationships(Pageable page);

    @Query("SELECT * FROM alert_rule_version entity WHERE entity.organization_id = :id")
    Flux<AlertRuleVersion> findByOrganization(Long id);

    @Query("SELECT * FROM alert_rule_version entity WHERE entity.organization_id IS NULL")
    Flux<AlertRuleVersion> findAllWhereOrganizationIsNull();

    @Query("SELECT * FROM alert_rule_version entity WHERE entity.alert_rule_id = :id")
    Flux<AlertRuleVersion> findByAlertRule(Long id);

    @Query("SELECT * FROM alert_rule_version entity WHERE entity.alert_rule_id IS NULL")
    Flux<AlertRuleVersion> findAllWhereAlertRuleIsNull();

    @Override
    <S extends AlertRuleVersion> Mono<S> save(S entity);

    @Override
    Flux<AlertRuleVersion> findAll();

    @Override
    Mono<AlertRuleVersion> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface AlertRuleVersionRepositoryInternal {
    Flux<AlertRuleVersion> findAllBy(Pageable pageable);

    Flux<AlertRuleVersion> findAll();

    Mono<AlertRuleVersion> findById(Long id);

    Mono<AlertRuleVersion> findOneWithEagerRelationships(Long id);

    Flux<AlertRuleVersion> findAllWithEagerRelationships();

    Flux<AlertRuleVersion> findAllWithEagerRelationships(Pageable page);
}

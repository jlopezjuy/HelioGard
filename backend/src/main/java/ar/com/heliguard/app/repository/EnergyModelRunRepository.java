package ar.com.heliguard.app.repository;

import ar.com.heliguard.app.domain.EnergyModelRun;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the EnergyModelRun entity.
 */
@SuppressWarnings("unused")
@Repository
public interface EnergyModelRunRepository extends ReactiveCrudRepository<EnergyModelRun, Long>, EnergyModelRunRepositoryInternal {
    @Override
    Mono<EnergyModelRun> findOneWithEagerRelationships(Long id);

    @Override
    Flux<EnergyModelRun> findAllWithEagerRelationships();

    @Override
    Flux<EnergyModelRun> findAllWithEagerRelationships(Pageable page);

    @Query("SELECT * FROM energy_model_run entity WHERE entity.organization_id = :id")
    Flux<EnergyModelRun> findByOrganization(Long id);

    @Query("SELECT * FROM energy_model_run entity WHERE entity.organization_id IS NULL")
    Flux<EnergyModelRun> findAllWhereOrganizationIsNull();

    @Query("SELECT * FROM energy_model_run entity WHERE entity.installation_id = :id")
    Flux<EnergyModelRun> findByInstallation(Long id);

    @Query("SELECT * FROM energy_model_run entity WHERE entity.installation_id IS NULL")
    Flux<EnergyModelRun> findAllWhereInstallationIsNull();

    @Query("SELECT * FROM energy_model_run entity WHERE entity.model_configuration_id = :id")
    Flux<EnergyModelRun> findByModelConfiguration(Long id);

    @Query("SELECT * FROM energy_model_run entity WHERE entity.model_configuration_id IS NULL")
    Flux<EnergyModelRun> findAllWhereModelConfigurationIsNull();

    @Query("SELECT * FROM energy_model_run entity WHERE entity.weather_batch_id = :id")
    Flux<EnergyModelRun> findByWeatherBatch(Long id);

    @Query("SELECT * FROM energy_model_run entity WHERE entity.weather_batch_id IS NULL")
    Flux<EnergyModelRun> findAllWhereWeatherBatchIsNull();

    @Override
    <S extends EnergyModelRun> Mono<S> save(S entity);

    @Override
    Flux<EnergyModelRun> findAll();

    @Override
    Mono<EnergyModelRun> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface EnergyModelRunRepositoryInternal {
    Flux<EnergyModelRun> findAllBy(Pageable pageable);

    Flux<EnergyModelRun> findAll();

    Mono<EnergyModelRun> findById(Long id);

    Mono<EnergyModelRun> findOneWithEagerRelationships(Long id);

    Flux<EnergyModelRun> findAllWithEagerRelationships();

    Flux<EnergyModelRun> findAllWithEagerRelationships(Pageable page);
}

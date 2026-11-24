package ar.com.heliguard.app.repository;

import ar.com.heliguard.app.domain.EnergyModelConfiguration;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the EnergyModelConfiguration entity.
 */
@SuppressWarnings("unused")
@Repository
public interface EnergyModelConfigurationRepository
    extends ReactiveCrudRepository<EnergyModelConfiguration, Long>, EnergyModelConfigurationRepositoryInternal
{
    @Override
    Mono<EnergyModelConfiguration> findOneWithEagerRelationships(Long id);

    @Override
    Flux<EnergyModelConfiguration> findAllWithEagerRelationships();

    @Override
    Flux<EnergyModelConfiguration> findAllWithEagerRelationships(Pageable page);

    @Query("SELECT * FROM energy_model_configuration entity WHERE entity.organization_id = :id")
    Flux<EnergyModelConfiguration> findByOrganization(Long id);

    @Query("SELECT * FROM energy_model_configuration entity WHERE entity.organization_id IS NULL")
    Flux<EnergyModelConfiguration> findAllWhereOrganizationIsNull();

    @Query("SELECT * FROM energy_model_configuration entity WHERE entity.installation_id = :id")
    Flux<EnergyModelConfiguration> findByInstallation(Long id);

    @Query("SELECT * FROM energy_model_configuration entity WHERE entity.installation_id IS NULL")
    Flux<EnergyModelConfiguration> findAllWhereInstallationIsNull();

    @Override
    <S extends EnergyModelConfiguration> Mono<S> save(S entity);

    @Override
    Flux<EnergyModelConfiguration> findAll();

    @Override
    Mono<EnergyModelConfiguration> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface EnergyModelConfigurationRepositoryInternal {
    Flux<EnergyModelConfiguration> findAllBy(Pageable pageable);

    Flux<EnergyModelConfiguration> findAll();

    Mono<EnergyModelConfiguration> findById(Long id);

    Mono<EnergyModelConfiguration> findOneWithEagerRelationships(Long id);

    Flux<EnergyModelConfiguration> findAllWithEagerRelationships();

    Flux<EnergyModelConfiguration> findAllWithEagerRelationships(Pageable page);
}

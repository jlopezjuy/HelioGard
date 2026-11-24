package ar.com.heliguard.app.repository;

import ar.com.heliguard.app.domain.SolarSector;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the SolarSector entity.
 */
@SuppressWarnings("unused")
@Repository
public interface SolarSectorRepository extends ReactiveCrudRepository<SolarSector, Long>, SolarSectorRepositoryInternal {
    @Override
    Mono<SolarSector> findOneWithEagerRelationships(Long id);

    @Override
    Flux<SolarSector> findAllWithEagerRelationships();

    @Override
    Flux<SolarSector> findAllWithEagerRelationships(Pageable page);

    @Query("SELECT * FROM solar_sector entity WHERE entity.organization_id = :id")
    Flux<SolarSector> findByOrganization(Long id);

    @Query("SELECT * FROM solar_sector entity WHERE entity.organization_id IS NULL")
    Flux<SolarSector> findAllWhereOrganizationIsNull();

    @Query("SELECT * FROM solar_sector entity WHERE entity.installation_id = :id")
    Flux<SolarSector> findByInstallation(Long id);

    @Query("SELECT * FROM solar_sector entity WHERE entity.installation_id IS NULL")
    Flux<SolarSector> findAllWhereInstallationIsNull();

    @Override
    <S extends SolarSector> Mono<S> save(S entity);

    @Override
    Flux<SolarSector> findAll();

    @Override
    Mono<SolarSector> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface SolarSectorRepositoryInternal {
    Flux<SolarSector> findAllBy(Pageable pageable);

    Flux<SolarSector> findAll();

    Mono<SolarSector> findById(Long id);

    Mono<SolarSector> findOneWithEagerRelationships(Long id);

    Flux<SolarSector> findAllWithEagerRelationships();

    Flux<SolarSector> findAllWithEagerRelationships(Pageable page);
}

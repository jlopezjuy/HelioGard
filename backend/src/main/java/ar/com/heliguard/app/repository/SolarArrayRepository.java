package ar.com.heliguard.app.repository;

import ar.com.heliguard.app.domain.SolarArray;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the SolarArray entity.
 */
@SuppressWarnings("unused")
@Repository
public interface SolarArrayRepository extends ReactiveCrudRepository<SolarArray, Long>, SolarArrayRepositoryInternal {
    @Override
    Mono<SolarArray> findOneWithEagerRelationships(Long id);

    @Override
    Flux<SolarArray> findAllWithEagerRelationships();

    @Override
    Flux<SolarArray> findAllWithEagerRelationships(Pageable page);

    @Query("SELECT * FROM solar_array entity WHERE entity.organization_id = :id")
    Flux<SolarArray> findByOrganization(Long id);

    @Query("SELECT * FROM solar_array entity WHERE entity.organization_id IS NULL")
    Flux<SolarArray> findAllWhereOrganizationIsNull();

    @Query("SELECT * FROM solar_array entity WHERE entity.installation_id = :id")
    Flux<SolarArray> findByInstallation(Long id);

    @Query("SELECT * FROM solar_array entity WHERE entity.installation_id IS NULL")
    Flux<SolarArray> findAllWhereInstallationIsNull();

    @Query("SELECT * FROM solar_array entity WHERE entity.solar_sector_id = :id")
    Flux<SolarArray> findBySolarSector(Long id);

    @Query("SELECT * FROM solar_array entity WHERE entity.solar_sector_id IS NULL")
    Flux<SolarArray> findAllWhereSolarSectorIsNull();

    @Override
    <S extends SolarArray> Mono<S> save(S entity);

    @Override
    Flux<SolarArray> findAll();

    @Override
    Mono<SolarArray> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface SolarArrayRepositoryInternal {
    Flux<SolarArray> findAllBy(Pageable pageable);

    Flux<SolarArray> findAll();

    Mono<SolarArray> findById(Long id);

    Mono<SolarArray> findOneWithEagerRelationships(Long id);

    Flux<SolarArray> findAllWithEagerRelationships();

    Flux<SolarArray> findAllWithEagerRelationships(Pageable page);
}

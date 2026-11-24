package ar.com.heliguard.app.repository;

import ar.com.heliguard.app.domain.SolarString;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the SolarString entity.
 */
@SuppressWarnings("unused")
@Repository
public interface SolarStringRepository extends ReactiveCrudRepository<SolarString, Long>, SolarStringRepositoryInternal {
    @Override
    Mono<SolarString> findOneWithEagerRelationships(Long id);

    @Override
    Flux<SolarString> findAllWithEagerRelationships();

    @Override
    Flux<SolarString> findAllWithEagerRelationships(Pageable page);

    @Query("SELECT * FROM solar_string entity WHERE entity.organization_id = :id")
    Flux<SolarString> findByOrganization(Long id);

    @Query("SELECT * FROM solar_string entity WHERE entity.organization_id IS NULL")
    Flux<SolarString> findAllWhereOrganizationIsNull();

    @Query("SELECT * FROM solar_string entity WHERE entity.solar_array_id = :id")
    Flux<SolarString> findBySolarArray(Long id);

    @Query("SELECT * FROM solar_string entity WHERE entity.solar_array_id IS NULL")
    Flux<SolarString> findAllWhereSolarArrayIsNull();

    @Query("SELECT * FROM solar_string entity WHERE entity.module_model_id = :id")
    Flux<SolarString> findByModuleModel(Long id);

    @Query("SELECT * FROM solar_string entity WHERE entity.module_model_id IS NULL")
    Flux<SolarString> findAllWhereModuleModelIsNull();

    @Override
    <S extends SolarString> Mono<S> save(S entity);

    @Override
    Flux<SolarString> findAll();

    @Override
    Mono<SolarString> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface SolarStringRepositoryInternal {
    Flux<SolarString> findAllBy(Pageable pageable);

    Flux<SolarString> findAll();

    Mono<SolarString> findById(Long id);

    Mono<SolarString> findOneWithEagerRelationships(Long id);

    Flux<SolarString> findAllWithEagerRelationships();

    Flux<SolarString> findAllWithEagerRelationships(Pageable page);
}

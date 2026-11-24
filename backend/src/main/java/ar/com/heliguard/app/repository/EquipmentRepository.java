package ar.com.heliguard.app.repository;

import ar.com.heliguard.app.domain.Equipment;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the Equipment entity.
 */
@SuppressWarnings("unused")
@Repository
public interface EquipmentRepository extends ReactiveCrudRepository<Equipment, Long>, EquipmentRepositoryInternal {
    Flux<Equipment> findAllBy(Pageable pageable);

    @Override
    Mono<Equipment> findOneWithEagerRelationships(Long id);

    @Override
    Flux<Equipment> findAllWithEagerRelationships();

    @Override
    Flux<Equipment> findAllWithEagerRelationships(Pageable page);

    @Query("SELECT * FROM equipment entity WHERE entity.organization_id = :id")
    Flux<Equipment> findByOrganization(Long id);

    @Query("SELECT * FROM equipment entity WHERE entity.organization_id IS NULL")
    Flux<Equipment> findAllWhereOrganizationIsNull();

    @Query("SELECT * FROM equipment entity WHERE entity.installation_id = :id")
    Flux<Equipment> findByInstallation(Long id);

    @Query("SELECT * FROM equipment entity WHERE entity.installation_id IS NULL")
    Flux<Equipment> findAllWhereInstallationIsNull();

    @Query("SELECT * FROM equipment entity WHERE entity.equipment_model_id = :id")
    Flux<Equipment> findByEquipmentModel(Long id);

    @Query("SELECT * FROM equipment entity WHERE entity.equipment_model_id IS NULL")
    Flux<Equipment> findAllWhereEquipmentModelIsNull();

    @Query("SELECT * FROM equipment entity WHERE entity.solar_string_id = :id")
    Flux<Equipment> findBySolarString(Long id);

    @Query("SELECT * FROM equipment entity WHERE entity.solar_string_id IS NULL")
    Flux<Equipment> findAllWhereSolarStringIsNull();

    @Override
    <S extends Equipment> Mono<S> save(S entity);

    @Override
    Flux<Equipment> findAll();

    @Override
    Mono<Equipment> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface EquipmentRepositoryInternal {
    Flux<Equipment> findAllBy(Pageable pageable);

    Flux<Equipment> findAll();

    Mono<Equipment> findById(Long id);

    Mono<Equipment> findOneWithEagerRelationships(Long id);

    Flux<Equipment> findAllWithEagerRelationships();

    Flux<Equipment> findAllWithEagerRelationships(Pageable page);
}

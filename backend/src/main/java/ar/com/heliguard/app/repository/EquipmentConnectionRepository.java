package ar.com.heliguard.app.repository;

import ar.com.heliguard.app.domain.EquipmentConnection;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the EquipmentConnection entity.
 */
@SuppressWarnings("unused")
@Repository
public interface EquipmentConnectionRepository
    extends ReactiveCrudRepository<EquipmentConnection, Long>, EquipmentConnectionRepositoryInternal
{
    @Override
    Mono<EquipmentConnection> findOneWithEagerRelationships(Long id);

    @Override
    Flux<EquipmentConnection> findAllWithEagerRelationships();

    @Override
    Flux<EquipmentConnection> findAllWithEagerRelationships(Pageable page);

    @Query("SELECT * FROM equipment_connection entity WHERE entity.organization_id = :id")
    Flux<EquipmentConnection> findByOrganization(Long id);

    @Query("SELECT * FROM equipment_connection entity WHERE entity.organization_id IS NULL")
    Flux<EquipmentConnection> findAllWhereOrganizationIsNull();

    @Query("SELECT * FROM equipment_connection entity WHERE entity.from_equipment_id = :id")
    Flux<EquipmentConnection> findByFromEquipment(Long id);

    @Query("SELECT * FROM equipment_connection entity WHERE entity.from_equipment_id IS NULL")
    Flux<EquipmentConnection> findAllWhereFromEquipmentIsNull();

    @Query("SELECT * FROM equipment_connection entity WHERE entity.to_equipment_id = :id")
    Flux<EquipmentConnection> findByToEquipment(Long id);

    @Query("SELECT * FROM equipment_connection entity WHERE entity.to_equipment_id IS NULL")
    Flux<EquipmentConnection> findAllWhereToEquipmentIsNull();

    @Override
    <S extends EquipmentConnection> Mono<S> save(S entity);

    @Override
    Flux<EquipmentConnection> findAll();

    @Override
    Mono<EquipmentConnection> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface EquipmentConnectionRepositoryInternal {
    Flux<EquipmentConnection> findAllBy(Pageable pageable);

    Flux<EquipmentConnection> findAll();

    Mono<EquipmentConnection> findById(Long id);

    Mono<EquipmentConnection> findOneWithEagerRelationships(Long id);

    Flux<EquipmentConnection> findAllWithEagerRelationships();

    Flux<EquipmentConnection> findAllWithEagerRelationships(Pageable page);
}

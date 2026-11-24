package ar.com.heliguard.app.repository;

import ar.com.heliguard.app.domain.DeviceIdentity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the DeviceIdentity entity.
 */
@SuppressWarnings("unused")
@Repository
public interface DeviceIdentityRepository extends ReactiveCrudRepository<DeviceIdentity, Long>, DeviceIdentityRepositoryInternal {
    @Override
    Mono<DeviceIdentity> findOneWithEagerRelationships(Long id);

    @Override
    Flux<DeviceIdentity> findAllWithEagerRelationships();

    @Override
    Flux<DeviceIdentity> findAllWithEagerRelationships(Pageable page);

    @Query("SELECT * FROM device_identity entity WHERE entity.organization_id = :id")
    Flux<DeviceIdentity> findByOrganization(Long id);

    @Query("SELECT * FROM device_identity entity WHERE entity.organization_id IS NULL")
    Flux<DeviceIdentity> findAllWhereOrganizationIsNull();

    @Query("SELECT * FROM device_identity entity WHERE entity.gateway_id = :id")
    Flux<DeviceIdentity> findByGateway(Long id);

    @Query("SELECT * FROM device_identity entity WHERE entity.gateway_id IS NULL")
    Flux<DeviceIdentity> findAllWhereGatewayIsNull();

    @Query("SELECT * FROM device_identity entity WHERE entity.equipment_id = :id")
    Flux<DeviceIdentity> findByEquipment(Long id);

    @Query("SELECT * FROM device_identity entity WHERE entity.equipment_id IS NULL")
    Flux<DeviceIdentity> findAllWhereEquipmentIsNull();

    @Override
    <S extends DeviceIdentity> Mono<S> save(S entity);

    @Override
    Flux<DeviceIdentity> findAll();

    @Override
    Mono<DeviceIdentity> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface DeviceIdentityRepositoryInternal {
    Flux<DeviceIdentity> findAllBy(Pageable pageable);

    Flux<DeviceIdentity> findAll();

    Mono<DeviceIdentity> findById(Long id);

    Mono<DeviceIdentity> findOneWithEagerRelationships(Long id);

    Flux<DeviceIdentity> findAllWithEagerRelationships();

    Flux<DeviceIdentity> findAllWithEagerRelationships(Pageable page);
}

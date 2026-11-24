package ar.com.heliguard.app.repository;

import ar.com.heliguard.app.domain.IncidentLocalization;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the IncidentLocalization entity.
 */
@SuppressWarnings("unused")
@Repository
public interface IncidentLocalizationRepository
    extends ReactiveCrudRepository<IncidentLocalization, Long>, IncidentLocalizationRepositoryInternal
{
    @Override
    Mono<IncidentLocalization> findOneWithEagerRelationships(Long id);

    @Override
    Flux<IncidentLocalization> findAllWithEagerRelationships();

    @Override
    Flux<IncidentLocalization> findAllWithEagerRelationships(Pageable page);

    @Query("SELECT * FROM incident_localization entity WHERE entity.organization_id = :id")
    Flux<IncidentLocalization> findByOrganization(Long id);

    @Query("SELECT * FROM incident_localization entity WHERE entity.organization_id IS NULL")
    Flux<IncidentLocalization> findAllWhereOrganizationIsNull();

    @Query("SELECT * FROM incident_localization entity WHERE entity.incident_id = :id")
    Flux<IncidentLocalization> findByIncident(Long id);

    @Query("SELECT * FROM incident_localization entity WHERE entity.incident_id IS NULL")
    Flux<IncidentLocalization> findAllWhereIncidentIsNull();

    @Query("SELECT * FROM incident_localization entity WHERE entity.solar_sector_id = :id")
    Flux<IncidentLocalization> findBySolarSector(Long id);

    @Query("SELECT * FROM incident_localization entity WHERE entity.solar_sector_id IS NULL")
    Flux<IncidentLocalization> findAllWhereSolarSectorIsNull();

    @Query("SELECT * FROM incident_localization entity WHERE entity.solar_string_id = :id")
    Flux<IncidentLocalization> findBySolarString(Long id);

    @Query("SELECT * FROM incident_localization entity WHERE entity.solar_string_id IS NULL")
    Flux<IncidentLocalization> findAllWhereSolarStringIsNull();

    @Query("SELECT * FROM incident_localization entity WHERE entity.equipment_id = :id")
    Flux<IncidentLocalization> findByEquipment(Long id);

    @Query("SELECT * FROM incident_localization entity WHERE entity.equipment_id IS NULL")
    Flux<IncidentLocalization> findAllWhereEquipmentIsNull();

    @Override
    <S extends IncidentLocalization> Mono<S> save(S entity);

    @Override
    Flux<IncidentLocalization> findAll();

    @Override
    Mono<IncidentLocalization> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface IncidentLocalizationRepositoryInternal {
    Flux<IncidentLocalization> findAllBy(Pageable pageable);

    Flux<IncidentLocalization> findAll();

    Mono<IncidentLocalization> findById(Long id);

    Mono<IncidentLocalization> findOneWithEagerRelationships(Long id);

    Flux<IncidentLocalization> findAllWithEagerRelationships();

    Flux<IncidentLocalization> findAllWithEagerRelationships(Pageable page);
}

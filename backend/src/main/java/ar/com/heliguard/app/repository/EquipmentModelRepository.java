package ar.com.heliguard.app.repository;

import ar.com.heliguard.app.domain.EquipmentModel;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data R2DBC repository for the EquipmentModel entity.
 */
@SuppressWarnings("unused")
@Repository
public interface EquipmentModelRepository extends R2dbcRepository<EquipmentModel, Long> {}

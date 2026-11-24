package ar.com.heliguard.app.repository;

import ar.com.heliguard.app.domain.MetricDefinition;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data R2DBC repository for the MetricDefinition entity.
 */
@SuppressWarnings("unused")
@Repository
public interface MetricDefinitionRepository extends R2dbcRepository<MetricDefinition, Long> {}

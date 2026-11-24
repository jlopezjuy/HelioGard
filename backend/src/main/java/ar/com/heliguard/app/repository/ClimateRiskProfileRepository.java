package ar.com.heliguard.app.repository;

import ar.com.heliguard.app.domain.ClimateRiskProfile;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data R2DBC repository for the ClimateRiskProfile entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ClimateRiskProfileRepository extends R2dbcRepository<ClimateRiskProfile, Long> {}

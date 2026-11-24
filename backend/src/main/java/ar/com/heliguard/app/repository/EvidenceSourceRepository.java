package ar.com.heliguard.app.repository;

import ar.com.heliguard.app.domain.EvidenceSource;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data R2DBC repository for the EvidenceSource entity.
 */
@SuppressWarnings("unused")
@Repository
public interface EvidenceSourceRepository extends R2dbcRepository<EvidenceSource, Long> {}

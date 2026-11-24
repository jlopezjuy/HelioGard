package ar.com.heliguard.app.repository;

import ar.com.heliguard.app.domain.EnergyIntervalResult;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

/**
 * Spring Data R2DBC repository for {@link EnergyIntervalResult}.
 */
@Repository
public interface EnergyIntervalResultRepository extends ReactiveCrudRepository<EnergyIntervalResult, Long> {
    Flux<EnergyIntervalResult> findByModelRunId(Long modelRunId);
}

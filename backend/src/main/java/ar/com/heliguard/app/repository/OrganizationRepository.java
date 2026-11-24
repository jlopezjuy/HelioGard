package ar.com.heliguard.app.repository;

import ar.com.heliguard.app.domain.Organization;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data R2DBC repository for the Organization entity.
 */
@SuppressWarnings("unused")
@Repository
public interface OrganizationRepository extends R2dbcRepository<Organization, Long> {}

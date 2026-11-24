package ar.com.heliguard.app.service.mapper;

import ar.com.heliguard.app.domain.ClimateRiskProfile;
import ar.com.heliguard.app.service.dto.ClimateRiskProfileDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link ClimateRiskProfile} and its DTO {@link ClimateRiskProfileDTO}.
 */
@Mapper(componentModel = "spring")
public interface ClimateRiskProfileMapper extends EntityMapper<ClimateRiskProfileDTO, ClimateRiskProfile> {}

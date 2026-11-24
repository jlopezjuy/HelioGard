package ar.com.heliguard.app.service.mapper;

import ar.com.heliguard.app.domain.LossAssessment;
import ar.com.heliguard.app.domain.LossCauseEstimate;
import ar.com.heliguard.app.domain.Organization;
import ar.com.heliguard.app.service.dto.LossAssessmentDTO;
import ar.com.heliguard.app.service.dto.LossCauseEstimateDTO;
import ar.com.heliguard.app.service.dto.OrganizationDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link LossCauseEstimate} and its DTO {@link LossCauseEstimateDTO}.
 */
@Mapper(componentModel = "spring")
public interface LossCauseEstimateMapper extends EntityMapper<LossCauseEstimateDTO, LossCauseEstimate> {
    @Mapping(target = "organization", source = "organization", qualifiedByName = "organizationName")
    @Mapping(target = "lossAssessment", source = "lossAssessment", qualifiedByName = "lossAssessmentMethodology")
    LossCauseEstimateDTO toDto(LossCauseEstimate s);

    @Named("organizationName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    OrganizationDTO toDtoOrganizationName(Organization organization);

    @Named("lossAssessmentMethodology")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "methodology", source = "methodology")
    LossAssessmentDTO toDtoLossAssessmentMethodology(LossAssessment lossAssessment);
}

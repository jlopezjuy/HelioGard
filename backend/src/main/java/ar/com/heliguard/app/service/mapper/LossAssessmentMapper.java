package ar.com.heliguard.app.service.mapper;

import ar.com.heliguard.app.domain.EnergyModelRun;
import ar.com.heliguard.app.domain.LossAssessment;
import ar.com.heliguard.app.domain.Organization;
import ar.com.heliguard.app.service.dto.EnergyModelRunDTO;
import ar.com.heliguard.app.service.dto.LossAssessmentDTO;
import ar.com.heliguard.app.service.dto.OrganizationDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link LossAssessment} and its DTO {@link LossAssessmentDTO}.
 */
@Mapper(componentModel = "spring")
public interface LossAssessmentMapper extends EntityMapper<LossAssessmentDTO, LossAssessment> {
    @Mapping(target = "organization", source = "organization", qualifiedByName = "organizationName")
    @Mapping(target = "modelRun", source = "modelRun", qualifiedByName = "energyModelRunModelName")
    LossAssessmentDTO toDto(LossAssessment s);

    @Named("organizationName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    OrganizationDTO toDtoOrganizationName(Organization organization);

    @Named("energyModelRunModelName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "modelName", source = "modelName")
    EnergyModelRunDTO toDtoEnergyModelRunModelName(EnergyModelRun energyModelRun);
}

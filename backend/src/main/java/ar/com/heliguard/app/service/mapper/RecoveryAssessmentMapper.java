package ar.com.heliguard.app.service.mapper;

import ar.com.heliguard.app.domain.EnergyModelRun;
import ar.com.heliguard.app.domain.MaintenanceIntervention;
import ar.com.heliguard.app.domain.Organization;
import ar.com.heliguard.app.domain.RecoveryAssessment;
import ar.com.heliguard.app.service.dto.EnergyModelRunDTO;
import ar.com.heliguard.app.service.dto.MaintenanceInterventionDTO;
import ar.com.heliguard.app.service.dto.OrganizationDTO;
import ar.com.heliguard.app.service.dto.RecoveryAssessmentDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link RecoveryAssessment} and its DTO {@link RecoveryAssessmentDTO}.
 */
@Mapper(componentModel = "spring")
public interface RecoveryAssessmentMapper extends EntityMapper<RecoveryAssessmentDTO, RecoveryAssessment> {
    @Mapping(target = "organization", source = "organization", qualifiedByName = "organizationName")
    @Mapping(target = "intervention", source = "intervention", qualifiedByName = "maintenanceInterventionId")
    @Mapping(target = "modelRun", source = "modelRun", qualifiedByName = "energyModelRunModelName")
    RecoveryAssessmentDTO toDto(RecoveryAssessment s);

    @Named("organizationName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    OrganizationDTO toDtoOrganizationName(Organization organization);

    @Named("maintenanceInterventionId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    MaintenanceInterventionDTO toDtoMaintenanceInterventionId(MaintenanceIntervention maintenanceIntervention);

    @Named("energyModelRunModelName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "modelName", source = "modelName")
    EnergyModelRunDTO toDtoEnergyModelRunModelName(EnergyModelRun energyModelRun);
}

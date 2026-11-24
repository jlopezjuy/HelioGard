package ar.com.heliguard.app.service.mapper;

import ar.com.heliguard.app.domain.InterventionEvidence;
import ar.com.heliguard.app.domain.MaintenanceIntervention;
import ar.com.heliguard.app.domain.Organization;
import ar.com.heliguard.app.service.dto.InterventionEvidenceDTO;
import ar.com.heliguard.app.service.dto.MaintenanceInterventionDTO;
import ar.com.heliguard.app.service.dto.OrganizationDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link InterventionEvidence} and its DTO {@link InterventionEvidenceDTO}.
 */
@Mapper(componentModel = "spring")
public interface InterventionEvidenceMapper extends EntityMapper<InterventionEvidenceDTO, InterventionEvidence> {
    @Mapping(target = "organization", source = "organization", qualifiedByName = "organizationName")
    @Mapping(target = "intervention", source = "intervention", qualifiedByName = "maintenanceInterventionId")
    InterventionEvidenceDTO toDto(InterventionEvidence s);

    @Named("organizationName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    OrganizationDTO toDtoOrganizationName(Organization organization);

    @Named("maintenanceInterventionId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    MaintenanceInterventionDTO toDtoMaintenanceInterventionId(MaintenanceIntervention maintenanceIntervention);
}

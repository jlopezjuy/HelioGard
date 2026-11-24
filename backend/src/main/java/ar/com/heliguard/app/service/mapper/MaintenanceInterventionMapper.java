package ar.com.heliguard.app.service.mapper;

import ar.com.heliguard.app.domain.MaintenanceIntervention;
import ar.com.heliguard.app.domain.Organization;
import ar.com.heliguard.app.domain.OrganizationMembership;
import ar.com.heliguard.app.domain.WorkOrder;
import ar.com.heliguard.app.service.dto.MaintenanceInterventionDTO;
import ar.com.heliguard.app.service.dto.OrganizationDTO;
import ar.com.heliguard.app.service.dto.OrganizationMembershipDTO;
import ar.com.heliguard.app.service.dto.WorkOrderDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link MaintenanceIntervention} and its DTO {@link MaintenanceInterventionDTO}.
 */
@Mapper(componentModel = "spring")
public interface MaintenanceInterventionMapper extends EntityMapper<MaintenanceInterventionDTO, MaintenanceIntervention> {
    @Mapping(target = "organization", source = "organization", qualifiedByName = "organizationName")
    @Mapping(target = "workOrder", source = "workOrder", qualifiedByName = "workOrderReferenceCode")
    @Mapping(target = "performedByMembership", source = "performedByMembership", qualifiedByName = "organizationMembershipId")
    MaintenanceInterventionDTO toDto(MaintenanceIntervention s);

    @Named("organizationName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    OrganizationDTO toDtoOrganizationName(Organization organization);

    @Named("workOrderReferenceCode")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "referenceCode", source = "referenceCode")
    WorkOrderDTO toDtoWorkOrderReferenceCode(WorkOrder workOrder);

    @Named("organizationMembershipId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    OrganizationMembershipDTO toDtoOrganizationMembershipId(OrganizationMembership organizationMembership);
}

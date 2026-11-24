package ar.com.heliguard.app.service.mapper;

import ar.com.heliguard.app.domain.Organization;
import ar.com.heliguard.app.domain.OrganizationMembership;
import ar.com.heliguard.app.domain.WorkOrder;
import ar.com.heliguard.app.domain.WorkOrderAssignment;
import ar.com.heliguard.app.service.dto.OrganizationDTO;
import ar.com.heliguard.app.service.dto.OrganizationMembershipDTO;
import ar.com.heliguard.app.service.dto.WorkOrderAssignmentDTO;
import ar.com.heliguard.app.service.dto.WorkOrderDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link WorkOrderAssignment} and its DTO {@link WorkOrderAssignmentDTO}.
 */
@Mapper(componentModel = "spring")
public interface WorkOrderAssignmentMapper extends EntityMapper<WorkOrderAssignmentDTO, WorkOrderAssignment> {
    @Mapping(target = "organization", source = "organization", qualifiedByName = "organizationName")
    @Mapping(target = "workOrder", source = "workOrder", qualifiedByName = "workOrderReferenceCode")
    @Mapping(target = "providerOrganization", source = "providerOrganization", qualifiedByName = "organizationName")
    @Mapping(target = "assigneeMembership", source = "assigneeMembership", qualifiedByName = "organizationMembershipId")
    WorkOrderAssignmentDTO toDto(WorkOrderAssignment s);

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

package ar.com.heliguard.app.service.mapper;

import ar.com.heliguard.app.domain.Organization;
import ar.com.heliguard.app.domain.WorkOrder;
import ar.com.heliguard.app.domain.WorkOrderTask;
import ar.com.heliguard.app.service.dto.OrganizationDTO;
import ar.com.heliguard.app.service.dto.WorkOrderDTO;
import ar.com.heliguard.app.service.dto.WorkOrderTaskDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link WorkOrderTask} and its DTO {@link WorkOrderTaskDTO}.
 */
@Mapper(componentModel = "spring")
public interface WorkOrderTaskMapper extends EntityMapper<WorkOrderTaskDTO, WorkOrderTask> {
    @Mapping(target = "organization", source = "organization", qualifiedByName = "organizationName")
    @Mapping(target = "workOrder", source = "workOrder", qualifiedByName = "workOrderReferenceCode")
    WorkOrderTaskDTO toDto(WorkOrderTask s);

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
}

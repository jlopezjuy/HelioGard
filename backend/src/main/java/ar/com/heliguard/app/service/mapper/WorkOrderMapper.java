package ar.com.heliguard.app.service.mapper;

import ar.com.heliguard.app.domain.Incident;
import ar.com.heliguard.app.domain.Installation;
import ar.com.heliguard.app.domain.Organization;
import ar.com.heliguard.app.domain.PlanSubscription;
import ar.com.heliguard.app.domain.WorkOrder;
import ar.com.heliguard.app.service.dto.IncidentDTO;
import ar.com.heliguard.app.service.dto.InstallationDTO;
import ar.com.heliguard.app.service.dto.OrganizationDTO;
import ar.com.heliguard.app.service.dto.PlanSubscriptionDTO;
import ar.com.heliguard.app.service.dto.WorkOrderDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link WorkOrder} and its DTO {@link WorkOrderDTO}.
 */
@Mapper(componentModel = "spring")
public interface WorkOrderMapper extends EntityMapper<WorkOrderDTO, WorkOrder> {
    @Mapping(target = "organization", source = "organization", qualifiedByName = "organizationName")
    @Mapping(target = "installation", source = "installation", qualifiedByName = "installationName")
    @Mapping(target = "incident", source = "incident", qualifiedByName = "incidentTitle")
    @Mapping(target = "subscription", source = "subscription", qualifiedByName = "planSubscriptionId")
    WorkOrderDTO toDto(WorkOrder s);

    @Named("organizationName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    OrganizationDTO toDtoOrganizationName(Organization organization);

    @Named("installationName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    InstallationDTO toDtoInstallationName(Installation installation);

    @Named("incidentTitle")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "title", source = "title")
    IncidentDTO toDtoIncidentTitle(Incident incident);

    @Named("planSubscriptionId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    PlanSubscriptionDTO toDtoPlanSubscriptionId(PlanSubscription planSubscription);
}

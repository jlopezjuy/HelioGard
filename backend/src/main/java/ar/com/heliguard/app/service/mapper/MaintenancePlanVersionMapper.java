package ar.com.heliguard.app.service.mapper;

import ar.com.heliguard.app.domain.MaintenancePlan;
import ar.com.heliguard.app.domain.MaintenancePlanVersion;
import ar.com.heliguard.app.domain.Organization;
import ar.com.heliguard.app.service.dto.MaintenancePlanDTO;
import ar.com.heliguard.app.service.dto.MaintenancePlanVersionDTO;
import ar.com.heliguard.app.service.dto.OrganizationDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link MaintenancePlanVersion} and its DTO {@link MaintenancePlanVersionDTO}.
 */
@Mapper(componentModel = "spring")
public interface MaintenancePlanVersionMapper extends EntityMapper<MaintenancePlanVersionDTO, MaintenancePlanVersion> {
    @Mapping(target = "organization", source = "organization", qualifiedByName = "organizationName")
    @Mapping(target = "maintenancePlan", source = "maintenancePlan", qualifiedByName = "maintenancePlanName")
    MaintenancePlanVersionDTO toDto(MaintenancePlanVersion s);

    @Named("organizationName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    OrganizationDTO toDtoOrganizationName(Organization organization);

    @Named("maintenancePlanName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    MaintenancePlanDTO toDtoMaintenancePlanName(MaintenancePlan maintenancePlan);
}

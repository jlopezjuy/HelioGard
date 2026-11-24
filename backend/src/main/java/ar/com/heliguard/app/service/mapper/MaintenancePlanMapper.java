package ar.com.heliguard.app.service.mapper;

import ar.com.heliguard.app.domain.MaintenancePlan;
import ar.com.heliguard.app.domain.Organization;
import ar.com.heliguard.app.service.dto.MaintenancePlanDTO;
import ar.com.heliguard.app.service.dto.OrganizationDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link MaintenancePlan} and its DTO {@link MaintenancePlanDTO}.
 */
@Mapper(componentModel = "spring")
public interface MaintenancePlanMapper extends EntityMapper<MaintenancePlanDTO, MaintenancePlan> {
    @Mapping(target = "organization", source = "organization", qualifiedByName = "organizationName")
    MaintenancePlanDTO toDto(MaintenancePlan s);

    @Named("organizationName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    OrganizationDTO toDtoOrganizationName(Organization organization);
}

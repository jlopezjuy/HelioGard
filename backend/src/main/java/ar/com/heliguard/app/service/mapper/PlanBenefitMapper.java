package ar.com.heliguard.app.service.mapper;

import ar.com.heliguard.app.domain.MaintenancePlanVersion;
import ar.com.heliguard.app.domain.Organization;
import ar.com.heliguard.app.domain.PlanBenefit;
import ar.com.heliguard.app.service.dto.MaintenancePlanVersionDTO;
import ar.com.heliguard.app.service.dto.OrganizationDTO;
import ar.com.heliguard.app.service.dto.PlanBenefitDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link PlanBenefit} and its DTO {@link PlanBenefitDTO}.
 */
@Mapper(componentModel = "spring")
public interface PlanBenefitMapper extends EntityMapper<PlanBenefitDTO, PlanBenefit> {
    @Mapping(target = "organization", source = "organization", qualifiedByName = "organizationName")
    @Mapping(target = "planVersion", source = "planVersion", qualifiedByName = "maintenancePlanVersionVersionCode")
    PlanBenefitDTO toDto(PlanBenefit s);

    @Named("organizationName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    OrganizationDTO toDtoOrganizationName(Organization organization);

    @Named("maintenancePlanVersionVersionCode")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "versionCode", source = "versionCode")
    MaintenancePlanVersionDTO toDtoMaintenancePlanVersionVersionCode(MaintenancePlanVersion maintenancePlanVersion);
}

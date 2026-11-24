package ar.com.heliguard.app.service.mapper;

import ar.com.heliguard.app.domain.Installation;
import ar.com.heliguard.app.domain.Organization;
import ar.com.heliguard.app.domain.TariffPlan;
import ar.com.heliguard.app.service.dto.InstallationDTO;
import ar.com.heliguard.app.service.dto.OrganizationDTO;
import ar.com.heliguard.app.service.dto.TariffPlanDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link TariffPlan} and its DTO {@link TariffPlanDTO}.
 */
@Mapper(componentModel = "spring")
public interface TariffPlanMapper extends EntityMapper<TariffPlanDTO, TariffPlan> {
    @Mapping(target = "organization", source = "organization", qualifiedByName = "organizationName")
    @Mapping(target = "installation", source = "installation", qualifiedByName = "installationName")
    TariffPlanDTO toDto(TariffPlan s);

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
}

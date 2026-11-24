package ar.com.heliguard.app.service.mapper;

import ar.com.heliguard.app.domain.EnergyModelConfiguration;
import ar.com.heliguard.app.domain.Installation;
import ar.com.heliguard.app.domain.Organization;
import ar.com.heliguard.app.service.dto.EnergyModelConfigurationDTO;
import ar.com.heliguard.app.service.dto.InstallationDTO;
import ar.com.heliguard.app.service.dto.OrganizationDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link EnergyModelConfiguration} and its DTO {@link EnergyModelConfigurationDTO}.
 */
@Mapper(componentModel = "spring")
public interface EnergyModelConfigurationMapper extends EntityMapper<EnergyModelConfigurationDTO, EnergyModelConfiguration> {
    @Mapping(target = "organization", source = "organization", qualifiedByName = "organizationName")
    @Mapping(target = "installation", source = "installation", qualifiedByName = "installationName")
    EnergyModelConfigurationDTO toDto(EnergyModelConfiguration s);

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

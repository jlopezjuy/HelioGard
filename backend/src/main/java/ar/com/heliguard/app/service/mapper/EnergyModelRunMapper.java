package ar.com.heliguard.app.service.mapper;

import ar.com.heliguard.app.domain.EnergyModelConfiguration;
import ar.com.heliguard.app.domain.EnergyModelRun;
import ar.com.heliguard.app.domain.Installation;
import ar.com.heliguard.app.domain.Organization;
import ar.com.heliguard.app.domain.WeatherIngestionBatch;
import ar.com.heliguard.app.service.dto.EnergyModelConfigurationDTO;
import ar.com.heliguard.app.service.dto.EnergyModelRunDTO;
import ar.com.heliguard.app.service.dto.InstallationDTO;
import ar.com.heliguard.app.service.dto.OrganizationDTO;
import ar.com.heliguard.app.service.dto.WeatherIngestionBatchDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link EnergyModelRun} and its DTO {@link EnergyModelRunDTO}.
 */
@Mapper(componentModel = "spring")
public interface EnergyModelRunMapper extends EntityMapper<EnergyModelRunDTO, EnergyModelRun> {
    @Mapping(target = "organization", source = "organization", qualifiedByName = "organizationName")
    @Mapping(target = "installation", source = "installation", qualifiedByName = "installationName")
    @Mapping(target = "modelConfiguration", source = "modelConfiguration", qualifiedByName = "energyModelConfigurationConfigVersion")
    @Mapping(target = "weatherBatch", source = "weatherBatch", qualifiedByName = "weatherIngestionBatchProviderBatchCode")
    EnergyModelRunDTO toDto(EnergyModelRun s);

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

    @Named("energyModelConfigurationConfigVersion")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "configVersion", source = "configVersion")
    EnergyModelConfigurationDTO toDtoEnergyModelConfigurationConfigVersion(EnergyModelConfiguration energyModelConfiguration);

    @Named("weatherIngestionBatchProviderBatchCode")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "providerBatchCode", source = "providerBatchCode")
    WeatherIngestionBatchDTO toDtoWeatherIngestionBatchProviderBatchCode(WeatherIngestionBatch weatherIngestionBatch);
}

package ar.com.heliguard.app.service.mapper;

import ar.com.heliguard.app.domain.Installation;
import ar.com.heliguard.app.domain.Organization;
import ar.com.heliguard.app.domain.SolarArray;
import ar.com.heliguard.app.domain.SolarSector;
import ar.com.heliguard.app.service.dto.InstallationDTO;
import ar.com.heliguard.app.service.dto.OrganizationDTO;
import ar.com.heliguard.app.service.dto.SolarArrayDTO;
import ar.com.heliguard.app.service.dto.SolarSectorDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link SolarArray} and its DTO {@link SolarArrayDTO}.
 */
@Mapper(componentModel = "spring")
public interface SolarArrayMapper extends EntityMapper<SolarArrayDTO, SolarArray> {
    @Mapping(target = "organization", source = "organization", qualifiedByName = "organizationName")
    @Mapping(target = "installation", source = "installation", qualifiedByName = "installationName")
    @Mapping(target = "solarSector", source = "solarSector", qualifiedByName = "solarSectorSectorCode")
    SolarArrayDTO toDto(SolarArray s);

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

    @Named("solarSectorSectorCode")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "sectorCode", source = "sectorCode")
    SolarSectorDTO toDtoSolarSectorSectorCode(SolarSector solarSector);
}

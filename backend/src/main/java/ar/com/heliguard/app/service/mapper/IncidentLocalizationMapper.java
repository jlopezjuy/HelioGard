package ar.com.heliguard.app.service.mapper;

import ar.com.heliguard.app.domain.Equipment;
import ar.com.heliguard.app.domain.Incident;
import ar.com.heliguard.app.domain.IncidentLocalization;
import ar.com.heliguard.app.domain.Organization;
import ar.com.heliguard.app.domain.SolarSector;
import ar.com.heliguard.app.domain.SolarString;
import ar.com.heliguard.app.service.dto.EquipmentDTO;
import ar.com.heliguard.app.service.dto.IncidentDTO;
import ar.com.heliguard.app.service.dto.IncidentLocalizationDTO;
import ar.com.heliguard.app.service.dto.OrganizationDTO;
import ar.com.heliguard.app.service.dto.SolarSectorDTO;
import ar.com.heliguard.app.service.dto.SolarStringDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link IncidentLocalization} and its DTO {@link IncidentLocalizationDTO}.
 */
@Mapper(componentModel = "spring")
public interface IncidentLocalizationMapper extends EntityMapper<IncidentLocalizationDTO, IncidentLocalization> {
    @Mapping(target = "organization", source = "organization", qualifiedByName = "organizationName")
    @Mapping(target = "incident", source = "incident", qualifiedByName = "incidentTitle")
    @Mapping(target = "solarSector", source = "solarSector", qualifiedByName = "solarSectorSectorCode")
    @Mapping(target = "solarString", source = "solarString", qualifiedByName = "solarStringStringCode")
    @Mapping(target = "equipment", source = "equipment", qualifiedByName = "equipmentTag")
    IncidentLocalizationDTO toDto(IncidentLocalization s);

    @Named("organizationName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    OrganizationDTO toDtoOrganizationName(Organization organization);

    @Named("incidentTitle")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "title", source = "title")
    IncidentDTO toDtoIncidentTitle(Incident incident);

    @Named("solarSectorSectorCode")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "sectorCode", source = "sectorCode")
    SolarSectorDTO toDtoSolarSectorSectorCode(SolarSector solarSector);

    @Named("solarStringStringCode")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "stringCode", source = "stringCode")
    SolarStringDTO toDtoSolarStringStringCode(SolarString solarString);

    @Named("equipmentTag")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "tag", source = "tag")
    EquipmentDTO toDtoEquipmentTag(Equipment equipment);
}

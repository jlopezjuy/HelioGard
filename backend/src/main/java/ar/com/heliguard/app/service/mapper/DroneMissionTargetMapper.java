package ar.com.heliguard.app.service.mapper;

import ar.com.heliguard.app.domain.DroneMission;
import ar.com.heliguard.app.domain.DroneMissionTarget;
import ar.com.heliguard.app.domain.Equipment;
import ar.com.heliguard.app.domain.Organization;
import ar.com.heliguard.app.domain.SolarSector;
import ar.com.heliguard.app.domain.SolarString;
import ar.com.heliguard.app.service.dto.DroneMissionDTO;
import ar.com.heliguard.app.service.dto.DroneMissionTargetDTO;
import ar.com.heliguard.app.service.dto.EquipmentDTO;
import ar.com.heliguard.app.service.dto.OrganizationDTO;
import ar.com.heliguard.app.service.dto.SolarSectorDTO;
import ar.com.heliguard.app.service.dto.SolarStringDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link DroneMissionTarget} and its DTO {@link DroneMissionTargetDTO}.
 */
@Mapper(componentModel = "spring")
public interface DroneMissionTargetMapper extends EntityMapper<DroneMissionTargetDTO, DroneMissionTarget> {
    @Mapping(target = "organization", source = "organization", qualifiedByName = "organizationName")
    @Mapping(target = "mission", source = "mission", qualifiedByName = "droneMissionMissionCode")
    @Mapping(target = "solarSector", source = "solarSector", qualifiedByName = "solarSectorSectorCode")
    @Mapping(target = "solarString", source = "solarString", qualifiedByName = "solarStringStringCode")
    @Mapping(target = "equipment", source = "equipment", qualifiedByName = "equipmentTag")
    DroneMissionTargetDTO toDto(DroneMissionTarget s);

    @Named("organizationName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    OrganizationDTO toDtoOrganizationName(Organization organization);

    @Named("droneMissionMissionCode")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "missionCode", source = "missionCode")
    DroneMissionDTO toDtoDroneMissionMissionCode(DroneMission droneMission);

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

package ar.com.heliguard.app.service.mapper;

import ar.com.heliguard.app.domain.Equipment;
import ar.com.heliguard.app.domain.EquipmentModel;
import ar.com.heliguard.app.domain.Installation;
import ar.com.heliguard.app.domain.Organization;
import ar.com.heliguard.app.domain.SolarString;
import ar.com.heliguard.app.service.dto.EquipmentDTO;
import ar.com.heliguard.app.service.dto.EquipmentModelDTO;
import ar.com.heliguard.app.service.dto.InstallationDTO;
import ar.com.heliguard.app.service.dto.OrganizationDTO;
import ar.com.heliguard.app.service.dto.SolarStringDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Equipment} and its DTO {@link EquipmentDTO}.
 */
@Mapper(componentModel = "spring")
public interface EquipmentMapper extends EntityMapper<EquipmentDTO, Equipment> {
    @Mapping(target = "organization", source = "organization", qualifiedByName = "organizationName")
    @Mapping(target = "installation", source = "installation", qualifiedByName = "installationName")
    @Mapping(target = "equipmentModel", source = "equipmentModel", qualifiedByName = "equipmentModelModelCode")
    @Mapping(target = "solarString", source = "solarString", qualifiedByName = "solarStringStringCode")
    EquipmentDTO toDto(Equipment s);

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

    @Named("equipmentModelModelCode")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "modelCode", source = "modelCode")
    EquipmentModelDTO toDtoEquipmentModelModelCode(EquipmentModel equipmentModel);

    @Named("solarStringStringCode")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "stringCode", source = "stringCode")
    SolarStringDTO toDtoSolarStringStringCode(SolarString solarString);
}

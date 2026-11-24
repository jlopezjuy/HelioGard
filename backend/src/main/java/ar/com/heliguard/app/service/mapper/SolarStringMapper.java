package ar.com.heliguard.app.service.mapper;

import ar.com.heliguard.app.domain.EquipmentModel;
import ar.com.heliguard.app.domain.Organization;
import ar.com.heliguard.app.domain.SolarArray;
import ar.com.heliguard.app.domain.SolarString;
import ar.com.heliguard.app.service.dto.EquipmentModelDTO;
import ar.com.heliguard.app.service.dto.OrganizationDTO;
import ar.com.heliguard.app.service.dto.SolarArrayDTO;
import ar.com.heliguard.app.service.dto.SolarStringDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link SolarString} and its DTO {@link SolarStringDTO}.
 */
@Mapper(componentModel = "spring")
public interface SolarStringMapper extends EntityMapper<SolarStringDTO, SolarString> {
    @Mapping(target = "organization", source = "organization", qualifiedByName = "organizationName")
    @Mapping(target = "solarArray", source = "solarArray", qualifiedByName = "solarArrayName")
    @Mapping(target = "moduleModel", source = "moduleModel", qualifiedByName = "equipmentModelModelCode")
    SolarStringDTO toDto(SolarString s);

    @Named("organizationName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    OrganizationDTO toDtoOrganizationName(Organization organization);

    @Named("solarArrayName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    SolarArrayDTO toDtoSolarArrayName(SolarArray solarArray);

    @Named("equipmentModelModelCode")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "modelCode", source = "modelCode")
    EquipmentModelDTO toDtoEquipmentModelModelCode(EquipmentModel equipmentModel);
}

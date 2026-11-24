package ar.com.heliguard.app.service.mapper;

import ar.com.heliguard.app.domain.Equipment;
import ar.com.heliguard.app.domain.EquipmentConnection;
import ar.com.heliguard.app.domain.Organization;
import ar.com.heliguard.app.service.dto.EquipmentConnectionDTO;
import ar.com.heliguard.app.service.dto.EquipmentDTO;
import ar.com.heliguard.app.service.dto.OrganizationDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link EquipmentConnection} and its DTO {@link EquipmentConnectionDTO}.
 */
@Mapper(componentModel = "spring")
public interface EquipmentConnectionMapper extends EntityMapper<EquipmentConnectionDTO, EquipmentConnection> {
    @Mapping(target = "organization", source = "organization", qualifiedByName = "organizationName")
    @Mapping(target = "fromEquipment", source = "fromEquipment", qualifiedByName = "equipmentTag")
    @Mapping(target = "toEquipment", source = "toEquipment", qualifiedByName = "equipmentTag")
    EquipmentConnectionDTO toDto(EquipmentConnection s);

    @Named("organizationName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    OrganizationDTO toDtoOrganizationName(Organization organization);

    @Named("equipmentTag")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "tag", source = "tag")
    EquipmentDTO toDtoEquipmentTag(Equipment equipment);
}

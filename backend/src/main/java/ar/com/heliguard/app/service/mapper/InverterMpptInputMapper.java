package ar.com.heliguard.app.service.mapper;

import ar.com.heliguard.app.domain.Equipment;
import ar.com.heliguard.app.domain.InverterMpptInput;
import ar.com.heliguard.app.domain.Organization;
import ar.com.heliguard.app.service.dto.EquipmentDTO;
import ar.com.heliguard.app.service.dto.InverterMpptInputDTO;
import ar.com.heliguard.app.service.dto.OrganizationDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link InverterMpptInput} and its DTO {@link InverterMpptInputDTO}.
 */
@Mapper(componentModel = "spring")
public interface InverterMpptInputMapper extends EntityMapper<InverterMpptInputDTO, InverterMpptInput> {
    @Mapping(target = "organization", source = "organization", qualifiedByName = "organizationName")
    @Mapping(target = "inverter", source = "inverter", qualifiedByName = "equipmentTag")
    InverterMpptInputDTO toDto(InverterMpptInput s);

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

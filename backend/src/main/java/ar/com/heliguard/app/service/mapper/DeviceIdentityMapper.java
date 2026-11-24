package ar.com.heliguard.app.service.mapper;

import ar.com.heliguard.app.domain.DeviceIdentity;
import ar.com.heliguard.app.domain.Equipment;
import ar.com.heliguard.app.domain.Gateway;
import ar.com.heliguard.app.domain.Organization;
import ar.com.heliguard.app.service.dto.DeviceIdentityDTO;
import ar.com.heliguard.app.service.dto.EquipmentDTO;
import ar.com.heliguard.app.service.dto.GatewayDTO;
import ar.com.heliguard.app.service.dto.OrganizationDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link DeviceIdentity} and its DTO {@link DeviceIdentityDTO}.
 */
@Mapper(componentModel = "spring")
public interface DeviceIdentityMapper extends EntityMapper<DeviceIdentityDTO, DeviceIdentity> {
    @Mapping(target = "organization", source = "organization", qualifiedByName = "organizationName")
    @Mapping(target = "gateway", source = "gateway", qualifiedByName = "gatewayHardwareId")
    @Mapping(target = "equipment", source = "equipment", qualifiedByName = "equipmentTag")
    DeviceIdentityDTO toDto(DeviceIdentity s);

    @Named("organizationName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    OrganizationDTO toDtoOrganizationName(Organization organization);

    @Named("gatewayHardwareId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "hardwareId", source = "hardwareId")
    GatewayDTO toDtoGatewayHardwareId(Gateway gateway);

    @Named("equipmentTag")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "tag", source = "tag")
    EquipmentDTO toDtoEquipmentTag(Equipment equipment);
}

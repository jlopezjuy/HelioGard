package ar.com.heliguard.app.service.mapper;

import ar.com.heliguard.app.domain.Gateway;
import ar.com.heliguard.app.domain.Organization;
import ar.com.heliguard.app.domain.TelemetryChannel;
import ar.com.heliguard.app.domain.TelemetryChannelBinding;
import ar.com.heliguard.app.service.dto.GatewayDTO;
import ar.com.heliguard.app.service.dto.OrganizationDTO;
import ar.com.heliguard.app.service.dto.TelemetryChannelBindingDTO;
import ar.com.heliguard.app.service.dto.TelemetryChannelDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link TelemetryChannelBinding} and its DTO {@link TelemetryChannelBindingDTO}.
 */
@Mapper(componentModel = "spring")
public interface TelemetryChannelBindingMapper extends EntityMapper<TelemetryChannelBindingDTO, TelemetryChannelBinding> {
    @Mapping(target = "organization", source = "organization", qualifiedByName = "organizationName")
    @Mapping(target = "telemetryChannel", source = "telemetryChannel", qualifiedByName = "telemetryChannelExternalChannelCode")
    @Mapping(target = "gateway", source = "gateway", qualifiedByName = "gatewayHardwareId")
    TelemetryChannelBindingDTO toDto(TelemetryChannelBinding s);

    @Named("organizationName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    OrganizationDTO toDtoOrganizationName(Organization organization);

    @Named("telemetryChannelExternalChannelCode")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "externalChannelCode", source = "externalChannelCode")
    TelemetryChannelDTO toDtoTelemetryChannelExternalChannelCode(TelemetryChannel telemetryChannel);

    @Named("gatewayHardwareId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "hardwareId", source = "hardwareId")
    GatewayDTO toDtoGatewayHardwareId(Gateway gateway);
}

package ar.com.heliguard.app.service.mapper;

import ar.com.heliguard.app.domain.DeviceIdentity;
import ar.com.heliguard.app.domain.Installation;
import ar.com.heliguard.app.domain.Organization;
import ar.com.heliguard.app.domain.TelemetryChannel;
import ar.com.heliguard.app.domain.TelemetryIntegrityEvent;
import ar.com.heliguard.app.service.dto.DeviceIdentityDTO;
import ar.com.heliguard.app.service.dto.InstallationDTO;
import ar.com.heliguard.app.service.dto.OrganizationDTO;
import ar.com.heliguard.app.service.dto.TelemetryChannelDTO;
import ar.com.heliguard.app.service.dto.TelemetryIntegrityEventDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link TelemetryIntegrityEvent} and its DTO {@link TelemetryIntegrityEventDTO}.
 */
@Mapper(componentModel = "spring")
public interface TelemetryIntegrityEventMapper extends EntityMapper<TelemetryIntegrityEventDTO, TelemetryIntegrityEvent> {
    @Mapping(target = "organization", source = "organization", qualifiedByName = "organizationName")
    @Mapping(target = "installation", source = "installation", qualifiedByName = "installationName")
    @Mapping(target = "deviceIdentity", source = "deviceIdentity", qualifiedByName = "deviceIdentityDeviceUid")
    @Mapping(target = "telemetryChannel", source = "telemetryChannel", qualifiedByName = "telemetryChannelExternalChannelCode")
    TelemetryIntegrityEventDTO toDto(TelemetryIntegrityEvent s);

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

    @Named("deviceIdentityDeviceUid")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "deviceUid", source = "deviceUid")
    DeviceIdentityDTO toDtoDeviceIdentityDeviceUid(DeviceIdentity deviceIdentity);

    @Named("telemetryChannelExternalChannelCode")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "externalChannelCode", source = "externalChannelCode")
    TelemetryChannelDTO toDtoTelemetryChannelExternalChannelCode(TelemetryChannel telemetryChannel);
}

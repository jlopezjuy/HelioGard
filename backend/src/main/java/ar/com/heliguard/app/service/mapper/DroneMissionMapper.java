package ar.com.heliguard.app.service.mapper;

import ar.com.heliguard.app.domain.DroneAsset;
import ar.com.heliguard.app.domain.DroneMission;
import ar.com.heliguard.app.domain.Installation;
import ar.com.heliguard.app.domain.Organization;
import ar.com.heliguard.app.domain.WorkOrder;
import ar.com.heliguard.app.service.dto.DroneAssetDTO;
import ar.com.heliguard.app.service.dto.DroneMissionDTO;
import ar.com.heliguard.app.service.dto.InstallationDTO;
import ar.com.heliguard.app.service.dto.OrganizationDTO;
import ar.com.heliguard.app.service.dto.WorkOrderDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link DroneMission} and its DTO {@link DroneMissionDTO}.
 */
@Mapper(componentModel = "spring")
public interface DroneMissionMapper extends EntityMapper<DroneMissionDTO, DroneMission> {
    @Mapping(target = "organization", source = "organization", qualifiedByName = "organizationName")
    @Mapping(target = "installation", source = "installation", qualifiedByName = "installationName")
    @Mapping(target = "workOrder", source = "workOrder", qualifiedByName = "workOrderReferenceCode")
    @Mapping(target = "droneAsset", source = "droneAsset", qualifiedByName = "droneAssetAssetCode")
    @Mapping(target = "providerOrganization", source = "providerOrganization", qualifiedByName = "organizationName")
    DroneMissionDTO toDto(DroneMission s);

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

    @Named("workOrderReferenceCode")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "referenceCode", source = "referenceCode")
    WorkOrderDTO toDtoWorkOrderReferenceCode(WorkOrder workOrder);

    @Named("droneAssetAssetCode")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "assetCode", source = "assetCode")
    DroneAssetDTO toDtoDroneAssetAssetCode(DroneAsset droneAsset);
}

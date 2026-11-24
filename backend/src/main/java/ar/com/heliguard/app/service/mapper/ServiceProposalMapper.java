package ar.com.heliguard.app.service.mapper;

import ar.com.heliguard.app.domain.Installation;
import ar.com.heliguard.app.domain.MaintenancePlanVersion;
import ar.com.heliguard.app.domain.Organization;
import ar.com.heliguard.app.domain.ServiceProposal;
import ar.com.heliguard.app.service.dto.InstallationDTO;
import ar.com.heliguard.app.service.dto.MaintenancePlanVersionDTO;
import ar.com.heliguard.app.service.dto.OrganizationDTO;
import ar.com.heliguard.app.service.dto.ServiceProposalDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link ServiceProposal} and its DTO {@link ServiceProposalDTO}.
 */
@Mapper(componentModel = "spring")
public interface ServiceProposalMapper extends EntityMapper<ServiceProposalDTO, ServiceProposal> {
    @Mapping(target = "organization", source = "organization", qualifiedByName = "organizationName")
    @Mapping(target = "installation", source = "installation", qualifiedByName = "installationName")
    @Mapping(target = "planVersion", source = "planVersion", qualifiedByName = "maintenancePlanVersionVersionCode")
    ServiceProposalDTO toDto(ServiceProposal s);

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

    @Named("maintenancePlanVersionVersionCode")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "versionCode", source = "versionCode")
    MaintenancePlanVersionDTO toDtoMaintenancePlanVersionVersionCode(MaintenancePlanVersion maintenancePlanVersion);
}

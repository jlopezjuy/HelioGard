package ar.com.heliguard.app.service.mapper;

import ar.com.heliguard.app.domain.Installation;
import ar.com.heliguard.app.domain.InstallationAccessGrant;
import ar.com.heliguard.app.domain.Organization;
import ar.com.heliguard.app.service.dto.InstallationAccessGrantDTO;
import ar.com.heliguard.app.service.dto.InstallationDTO;
import ar.com.heliguard.app.service.dto.OrganizationDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link InstallationAccessGrant} and its DTO {@link InstallationAccessGrantDTO}.
 */
@Mapper(componentModel = "spring")
public interface InstallationAccessGrantMapper extends EntityMapper<InstallationAccessGrantDTO, InstallationAccessGrant> {
    @Mapping(target = "organization", source = "organization", qualifiedByName = "organizationName")
    @Mapping(target = "installation", source = "installation", qualifiedByName = "installationName")
    @Mapping(target = "granteeOrganization", source = "granteeOrganization", qualifiedByName = "organizationName")
    InstallationAccessGrantDTO toDto(InstallationAccessGrant s);

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
}

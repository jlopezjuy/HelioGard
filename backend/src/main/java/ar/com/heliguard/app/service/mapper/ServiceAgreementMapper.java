package ar.com.heliguard.app.service.mapper;

import ar.com.heliguard.app.domain.Installation;
import ar.com.heliguard.app.domain.Organization;
import ar.com.heliguard.app.domain.ServiceAgreement;
import ar.com.heliguard.app.service.dto.InstallationDTO;
import ar.com.heliguard.app.service.dto.OrganizationDTO;
import ar.com.heliguard.app.service.dto.ServiceAgreementDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link ServiceAgreement} and its DTO {@link ServiceAgreementDTO}.
 */
@Mapper(componentModel = "spring")
public interface ServiceAgreementMapper extends EntityMapper<ServiceAgreementDTO, ServiceAgreement> {
    @Mapping(target = "organization", source = "organization", qualifiedByName = "organizationName")
    @Mapping(target = "providerOrganization", source = "providerOrganization", qualifiedByName = "organizationName")
    @Mapping(target = "installation", source = "installation", qualifiedByName = "installationName")
    ServiceAgreementDTO toDto(ServiceAgreement s);

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

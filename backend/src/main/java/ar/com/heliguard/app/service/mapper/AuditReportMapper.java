package ar.com.heliguard.app.service.mapper;

import ar.com.heliguard.app.domain.AuditReport;
import ar.com.heliguard.app.domain.AuditTemplate;
import ar.com.heliguard.app.domain.Installation;
import ar.com.heliguard.app.domain.Organization;
import ar.com.heliguard.app.domain.OrganizationMembership;
import ar.com.heliguard.app.service.dto.AuditReportDTO;
import ar.com.heliguard.app.service.dto.AuditTemplateDTO;
import ar.com.heliguard.app.service.dto.InstallationDTO;
import ar.com.heliguard.app.service.dto.OrganizationDTO;
import ar.com.heliguard.app.service.dto.OrganizationMembershipDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link AuditReport} and its DTO {@link AuditReportDTO}.
 */
@Mapper(componentModel = "spring")
public interface AuditReportMapper extends EntityMapper<AuditReportDTO, AuditReport> {
    @Mapping(target = "organization", source = "organization", qualifiedByName = "organizationName")
    @Mapping(target = "installation", source = "installation", qualifiedByName = "installationName")
    @Mapping(target = "auditTemplate", source = "auditTemplate", qualifiedByName = "auditTemplateTitle")
    @Mapping(target = "auditorMembership", source = "auditorMembership", qualifiedByName = "organizationMembershipId")
    AuditReportDTO toDto(AuditReport s);

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

    @Named("auditTemplateTitle")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "title", source = "title")
    AuditTemplateDTO toDtoAuditTemplateTitle(AuditTemplate auditTemplate);

    @Named("organizationMembershipId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    OrganizationMembershipDTO toDtoOrganizationMembershipId(OrganizationMembership organizationMembership);
}

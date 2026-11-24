package ar.com.heliguard.app.service.mapper;

import ar.com.heliguard.app.domain.AuditReport;
import ar.com.heliguard.app.domain.AuditReportSignature;
import ar.com.heliguard.app.domain.Organization;
import ar.com.heliguard.app.domain.OrganizationMembership;
import ar.com.heliguard.app.service.dto.AuditReportDTO;
import ar.com.heliguard.app.service.dto.AuditReportSignatureDTO;
import ar.com.heliguard.app.service.dto.OrganizationDTO;
import ar.com.heliguard.app.service.dto.OrganizationMembershipDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link AuditReportSignature} and its DTO {@link AuditReportSignatureDTO}.
 */
@Mapper(componentModel = "spring")
public interface AuditReportSignatureMapper extends EntityMapper<AuditReportSignatureDTO, AuditReportSignature> {
    @Mapping(target = "organization", source = "organization", qualifiedByName = "organizationName")
    @Mapping(target = "auditReport", source = "auditReport", qualifiedByName = "auditReportId")
    @Mapping(target = "signerMembership", source = "signerMembership", qualifiedByName = "organizationMembershipId")
    AuditReportSignatureDTO toDto(AuditReportSignature s);

    @Named("organizationName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    OrganizationDTO toDtoOrganizationName(Organization organization);

    @Named("auditReportId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    AuditReportDTO toDtoAuditReportId(AuditReport auditReport);

    @Named("organizationMembershipId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    OrganizationMembershipDTO toDtoOrganizationMembershipId(OrganizationMembership organizationMembership);
}

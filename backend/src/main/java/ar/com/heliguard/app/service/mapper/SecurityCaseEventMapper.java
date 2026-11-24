package ar.com.heliguard.app.service.mapper;

import ar.com.heliguard.app.domain.Organization;
import ar.com.heliguard.app.domain.OrganizationMembership;
import ar.com.heliguard.app.domain.SecurityCaseEvent;
import ar.com.heliguard.app.domain.SecurityIncident;
import ar.com.heliguard.app.service.dto.OrganizationDTO;
import ar.com.heliguard.app.service.dto.OrganizationMembershipDTO;
import ar.com.heliguard.app.service.dto.SecurityCaseEventDTO;
import ar.com.heliguard.app.service.dto.SecurityIncidentDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link SecurityCaseEvent} and its DTO {@link SecurityCaseEventDTO}.
 */
@Mapper(componentModel = "spring")
public interface SecurityCaseEventMapper extends EntityMapper<SecurityCaseEventDTO, SecurityCaseEvent> {
    @Mapping(target = "organization", source = "organization", qualifiedByName = "organizationName")
    @Mapping(target = "securityIncident", source = "securityIncident", qualifiedByName = "securityIncidentReferenceCode")
    @Mapping(target = "actorMembership", source = "actorMembership", qualifiedByName = "organizationMembershipId")
    SecurityCaseEventDTO toDto(SecurityCaseEvent s);

    @Named("organizationName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    OrganizationDTO toDtoOrganizationName(Organization organization);

    @Named("securityIncidentReferenceCode")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "referenceCode", source = "referenceCode")
    SecurityIncidentDTO toDtoSecurityIncidentReferenceCode(SecurityIncident securityIncident);

    @Named("organizationMembershipId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    OrganizationMembershipDTO toDtoOrganizationMembershipId(OrganizationMembership organizationMembership);
}

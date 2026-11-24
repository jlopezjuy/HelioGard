package ar.com.heliguard.app.service.mapper;

import ar.com.heliguard.app.domain.Installation;
import ar.com.heliguard.app.domain.IntegrityAssessment;
import ar.com.heliguard.app.domain.Organization;
import ar.com.heliguard.app.domain.SecurityIncident;
import ar.com.heliguard.app.service.dto.InstallationDTO;
import ar.com.heliguard.app.service.dto.IntegrityAssessmentDTO;
import ar.com.heliguard.app.service.dto.OrganizationDTO;
import ar.com.heliguard.app.service.dto.SecurityIncidentDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link SecurityIncident} and its DTO {@link SecurityIncidentDTO}.
 */
@Mapper(componentModel = "spring")
public interface SecurityIncidentMapper extends EntityMapper<SecurityIncidentDTO, SecurityIncident> {
    @Mapping(target = "organization", source = "organization", qualifiedByName = "organizationName")
    @Mapping(target = "installation", source = "installation", qualifiedByName = "installationName")
    @Mapping(target = "integrityAssessment", source = "integrityAssessment", qualifiedByName = "integrityAssessmentId")
    SecurityIncidentDTO toDto(SecurityIncident s);

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

    @Named("integrityAssessmentId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    IntegrityAssessmentDTO toDtoIntegrityAssessmentId(IntegrityAssessment integrityAssessment);
}

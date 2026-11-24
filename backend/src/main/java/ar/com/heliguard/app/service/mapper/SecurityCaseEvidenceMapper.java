package ar.com.heliguard.app.service.mapper;

import ar.com.heliguard.app.domain.DataProvenance;
import ar.com.heliguard.app.domain.IntegrityAssessment;
import ar.com.heliguard.app.domain.Organization;
import ar.com.heliguard.app.domain.SecurityCaseEvidence;
import ar.com.heliguard.app.domain.SecurityIncident;
import ar.com.heliguard.app.service.dto.DataProvenanceDTO;
import ar.com.heliguard.app.service.dto.IntegrityAssessmentDTO;
import ar.com.heliguard.app.service.dto.OrganizationDTO;
import ar.com.heliguard.app.service.dto.SecurityCaseEvidenceDTO;
import ar.com.heliguard.app.service.dto.SecurityIncidentDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link SecurityCaseEvidence} and its DTO {@link SecurityCaseEvidenceDTO}.
 */
@Mapper(componentModel = "spring")
public interface SecurityCaseEvidenceMapper extends EntityMapper<SecurityCaseEvidenceDTO, SecurityCaseEvidence> {
    @Mapping(target = "organization", source = "organization", qualifiedByName = "organizationName")
    @Mapping(target = "securityIncident", source = "securityIncident", qualifiedByName = "securityIncidentReferenceCode")
    @Mapping(target = "integrityAssessment", source = "integrityAssessment", qualifiedByName = "integrityAssessmentId")
    @Mapping(target = "dataProvenance", source = "dataProvenance", qualifiedByName = "dataProvenanceId")
    SecurityCaseEvidenceDTO toDto(SecurityCaseEvidence s);

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

    @Named("integrityAssessmentId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    IntegrityAssessmentDTO toDtoIntegrityAssessmentId(IntegrityAssessment integrityAssessment);

    @Named("dataProvenanceId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    DataProvenanceDTO toDtoDataProvenanceId(DataProvenance dataProvenance);
}

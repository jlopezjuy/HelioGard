package ar.com.heliguard.app.service.mapper;

import ar.com.heliguard.app.domain.AuditEvidence;
import ar.com.heliguard.app.domain.AuditFinding;
import ar.com.heliguard.app.domain.Organization;
import ar.com.heliguard.app.service.dto.AuditEvidenceDTO;
import ar.com.heliguard.app.service.dto.AuditFindingDTO;
import ar.com.heliguard.app.service.dto.OrganizationDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link AuditEvidence} and its DTO {@link AuditEvidenceDTO}.
 */
@Mapper(componentModel = "spring")
public interface AuditEvidenceMapper extends EntityMapper<AuditEvidenceDTO, AuditEvidence> {
    @Mapping(target = "organization", source = "organization", qualifiedByName = "organizationName")
    @Mapping(target = "finding", source = "finding", qualifiedByName = "auditFindingId")
    AuditEvidenceDTO toDto(AuditEvidence s);

    @Named("organizationName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    OrganizationDTO toDtoOrganizationName(Organization organization);

    @Named("auditFindingId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    AuditFindingDTO toDtoAuditFindingId(AuditFinding auditFinding);
}

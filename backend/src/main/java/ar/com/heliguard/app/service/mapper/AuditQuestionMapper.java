package ar.com.heliguard.app.service.mapper;

import ar.com.heliguard.app.domain.AuditQuestion;
import ar.com.heliguard.app.domain.AuditTemplate;
import ar.com.heliguard.app.domain.Organization;
import ar.com.heliguard.app.service.dto.AuditQuestionDTO;
import ar.com.heliguard.app.service.dto.AuditTemplateDTO;
import ar.com.heliguard.app.service.dto.OrganizationDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link AuditQuestion} and its DTO {@link AuditQuestionDTO}.
 */
@Mapper(componentModel = "spring")
public interface AuditQuestionMapper extends EntityMapper<AuditQuestionDTO, AuditQuestion> {
    @Mapping(target = "organization", source = "organization", qualifiedByName = "organizationName")
    @Mapping(target = "auditTemplate", source = "auditTemplate", qualifiedByName = "auditTemplateTitle")
    AuditQuestionDTO toDto(AuditQuestion s);

    @Named("organizationName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    OrganizationDTO toDtoOrganizationName(Organization organization);

    @Named("auditTemplateTitle")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "title", source = "title")
    AuditTemplateDTO toDtoAuditTemplateTitle(AuditTemplate auditTemplate);
}

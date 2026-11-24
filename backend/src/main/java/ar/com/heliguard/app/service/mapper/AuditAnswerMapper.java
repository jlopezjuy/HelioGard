package ar.com.heliguard.app.service.mapper;

import ar.com.heliguard.app.domain.AuditAnswer;
import ar.com.heliguard.app.domain.AuditQuestion;
import ar.com.heliguard.app.domain.AuditReport;
import ar.com.heliguard.app.domain.Organization;
import ar.com.heliguard.app.service.dto.AuditAnswerDTO;
import ar.com.heliguard.app.service.dto.AuditQuestionDTO;
import ar.com.heliguard.app.service.dto.AuditReportDTO;
import ar.com.heliguard.app.service.dto.OrganizationDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link AuditAnswer} and its DTO {@link AuditAnswerDTO}.
 */
@Mapper(componentModel = "spring")
public interface AuditAnswerMapper extends EntityMapper<AuditAnswerDTO, AuditAnswer> {
    @Mapping(target = "organization", source = "organization", qualifiedByName = "organizationName")
    @Mapping(target = "auditReport", source = "auditReport", qualifiedByName = "auditReportId")
    @Mapping(target = "auditQuestion", source = "auditQuestion", qualifiedByName = "auditQuestionQuestionCode")
    AuditAnswerDTO toDto(AuditAnswer s);

    @Named("organizationName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    OrganizationDTO toDtoOrganizationName(Organization organization);

    @Named("auditReportId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    AuditReportDTO toDtoAuditReportId(AuditReport auditReport);

    @Named("auditQuestionQuestionCode")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "questionCode", source = "questionCode")
    AuditQuestionDTO toDtoAuditQuestionQuestionCode(AuditQuestion auditQuestion);
}

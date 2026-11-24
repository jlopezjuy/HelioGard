package ar.com.heliguard.app.service.mapper;

import ar.com.heliguard.app.domain.AuditTemplate;
import ar.com.heliguard.app.domain.Organization;
import ar.com.heliguard.app.service.dto.AuditTemplateDTO;
import ar.com.heliguard.app.service.dto.OrganizationDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link AuditTemplate} and its DTO {@link AuditTemplateDTO}.
 */
@Mapper(componentModel = "spring")
public interface AuditTemplateMapper extends EntityMapper<AuditTemplateDTO, AuditTemplate> {
    @Mapping(target = "organization", source = "organization", qualifiedByName = "organizationName")
    AuditTemplateDTO toDto(AuditTemplate s);

    @Named("organizationName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    OrganizationDTO toDtoOrganizationName(Organization organization);
}

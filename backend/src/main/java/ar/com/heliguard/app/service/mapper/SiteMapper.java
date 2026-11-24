package ar.com.heliguard.app.service.mapper;

import ar.com.heliguard.app.domain.Organization;
import ar.com.heliguard.app.domain.Site;
import ar.com.heliguard.app.service.dto.OrganizationDTO;
import ar.com.heliguard.app.service.dto.SiteDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Site} and its DTO {@link SiteDTO}.
 */
@Mapper(componentModel = "spring")
public interface SiteMapper extends EntityMapper<SiteDTO, Site> {
    @Mapping(target = "organization", source = "organization", qualifiedByName = "organizationName")
    SiteDTO toDto(Site s);

    @Named("organizationName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    OrganizationDTO toDtoOrganizationName(Organization organization);
}

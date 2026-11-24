package ar.com.heliguard.app.service.mapper;

import ar.com.heliguard.app.domain.ClimateRiskProfile;
import ar.com.heliguard.app.domain.Installation;
import ar.com.heliguard.app.domain.Organization;
import ar.com.heliguard.app.domain.Site;
import ar.com.heliguard.app.service.dto.ClimateRiskProfileDTO;
import ar.com.heliguard.app.service.dto.InstallationDTO;
import ar.com.heliguard.app.service.dto.OrganizationDTO;
import ar.com.heliguard.app.service.dto.SiteDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Installation} and its DTO {@link InstallationDTO}.
 */
@Mapper(componentModel = "spring")
public interface InstallationMapper extends EntityMapper<InstallationDTO, Installation> {
    @Mapping(target = "organization", source = "organization", qualifiedByName = "organizationName")
    @Mapping(target = "site", source = "site", qualifiedByName = "siteName")
    @Mapping(target = "climateRiskProfile", source = "climateRiskProfile", qualifiedByName = "climateRiskProfileCode")
    InstallationDTO toDto(Installation s);

    @Named("organizationName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    OrganizationDTO toDtoOrganizationName(Organization organization);

    @Named("siteName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    SiteDTO toDtoSiteName(Site site);

    @Named("climateRiskProfileCode")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "code", source = "code")
    ClimateRiskProfileDTO toDtoClimateRiskProfileCode(ClimateRiskProfile climateRiskProfile);
}

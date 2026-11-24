package ar.com.heliguard.app.service.mapper;

import ar.com.heliguard.app.domain.AlertRuleVersion;
import ar.com.heliguard.app.domain.Incident;
import ar.com.heliguard.app.domain.Installation;
import ar.com.heliguard.app.domain.Organization;
import ar.com.heliguard.app.service.dto.AlertRuleVersionDTO;
import ar.com.heliguard.app.service.dto.IncidentDTO;
import ar.com.heliguard.app.service.dto.InstallationDTO;
import ar.com.heliguard.app.service.dto.OrganizationDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Incident} and its DTO {@link IncidentDTO}.
 */
@Mapper(componentModel = "spring")
public interface IncidentMapper extends EntityMapper<IncidentDTO, Incident> {
    @Mapping(target = "organization", source = "organization", qualifiedByName = "organizationName")
    @Mapping(target = "installation", source = "installation", qualifiedByName = "installationName")
    @Mapping(target = "alertRuleVersion", source = "alertRuleVersion", qualifiedByName = "alertRuleVersionVersionCode")
    IncidentDTO toDto(Incident s);

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

    @Named("alertRuleVersionVersionCode")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "versionCode", source = "versionCode")
    AlertRuleVersionDTO toDtoAlertRuleVersionVersionCode(AlertRuleVersion alertRuleVersion);
}

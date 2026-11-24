package ar.com.heliguard.app.service.mapper;

import ar.com.heliguard.app.domain.AlertRule;
import ar.com.heliguard.app.domain.AlertRuleVersion;
import ar.com.heliguard.app.domain.Organization;
import ar.com.heliguard.app.service.dto.AlertRuleDTO;
import ar.com.heliguard.app.service.dto.AlertRuleVersionDTO;
import ar.com.heliguard.app.service.dto.OrganizationDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link AlertRuleVersion} and its DTO {@link AlertRuleVersionDTO}.
 */
@Mapper(componentModel = "spring")
public interface AlertRuleVersionMapper extends EntityMapper<AlertRuleVersionDTO, AlertRuleVersion> {
    @Mapping(target = "organization", source = "organization", qualifiedByName = "organizationName")
    @Mapping(target = "alertRule", source = "alertRule", qualifiedByName = "alertRuleRuleCode")
    AlertRuleVersionDTO toDto(AlertRuleVersion s);

    @Named("organizationName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    OrganizationDTO toDtoOrganizationName(Organization organization);

    @Named("alertRuleRuleCode")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "ruleCode", source = "ruleCode")
    AlertRuleDTO toDtoAlertRuleRuleCode(AlertRule alertRule);
}

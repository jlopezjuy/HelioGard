package ar.com.heliguard.app.service.mapper;

import ar.com.heliguard.app.domain.Organization;
import ar.com.heliguard.app.domain.TariffPlan;
import ar.com.heliguard.app.domain.TariffRate;
import ar.com.heliguard.app.service.dto.OrganizationDTO;
import ar.com.heliguard.app.service.dto.TariffPlanDTO;
import ar.com.heliguard.app.service.dto.TariffRateDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link TariffRate} and its DTO {@link TariffRateDTO}.
 */
@Mapper(componentModel = "spring")
public interface TariffRateMapper extends EntityMapper<TariffRateDTO, TariffRate> {
    @Mapping(target = "organization", source = "organization", qualifiedByName = "organizationName")
    @Mapping(target = "tariffPlan", source = "tariffPlan", qualifiedByName = "tariffPlanName")
    TariffRateDTO toDto(TariffRate s);

    @Named("organizationName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    OrganizationDTO toDtoOrganizationName(Organization organization);

    @Named("tariffPlanName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    TariffPlanDTO toDtoTariffPlanName(TariffPlan tariffPlan);
}

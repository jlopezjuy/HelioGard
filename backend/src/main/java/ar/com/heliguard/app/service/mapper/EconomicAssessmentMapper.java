package ar.com.heliguard.app.service.mapper;

import ar.com.heliguard.app.domain.EconomicAssessment;
import ar.com.heliguard.app.domain.LossAssessment;
import ar.com.heliguard.app.domain.Organization;
import ar.com.heliguard.app.domain.TariffRate;
import ar.com.heliguard.app.service.dto.EconomicAssessmentDTO;
import ar.com.heliguard.app.service.dto.LossAssessmentDTO;
import ar.com.heliguard.app.service.dto.OrganizationDTO;
import ar.com.heliguard.app.service.dto.TariffRateDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link EconomicAssessment} and its DTO {@link EconomicAssessmentDTO}.
 */
@Mapper(componentModel = "spring")
public interface EconomicAssessmentMapper extends EntityMapper<EconomicAssessmentDTO, EconomicAssessment> {
    @Mapping(target = "organization", source = "organization", qualifiedByName = "organizationName")
    @Mapping(target = "lossAssessment", source = "lossAssessment", qualifiedByName = "lossAssessmentMethodology")
    @Mapping(target = "tariffRate", source = "tariffRate", qualifiedByName = "tariffRateId")
    EconomicAssessmentDTO toDto(EconomicAssessment s);

    @Named("organizationName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    OrganizationDTO toDtoOrganizationName(Organization organization);

    @Named("lossAssessmentMethodology")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "methodology", source = "methodology")
    LossAssessmentDTO toDtoLossAssessmentMethodology(LossAssessment lossAssessment);

    @Named("tariffRateId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    TariffRateDTO toDtoTariffRateId(TariffRate tariffRate);
}

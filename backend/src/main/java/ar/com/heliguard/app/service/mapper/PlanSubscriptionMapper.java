package ar.com.heliguard.app.service.mapper;

import ar.com.heliguard.app.domain.Installation;
import ar.com.heliguard.app.domain.MaintenancePlanVersion;
import ar.com.heliguard.app.domain.Organization;
import ar.com.heliguard.app.domain.PlanSubscription;
import ar.com.heliguard.app.domain.ServiceAgreement;
import ar.com.heliguard.app.service.dto.InstallationDTO;
import ar.com.heliguard.app.service.dto.MaintenancePlanVersionDTO;
import ar.com.heliguard.app.service.dto.OrganizationDTO;
import ar.com.heliguard.app.service.dto.PlanSubscriptionDTO;
import ar.com.heliguard.app.service.dto.ServiceAgreementDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link PlanSubscription} and its DTO {@link PlanSubscriptionDTO}.
 */
@Mapper(componentModel = "spring")
public interface PlanSubscriptionMapper extends EntityMapper<PlanSubscriptionDTO, PlanSubscription> {
    @Mapping(target = "organization", source = "organization", qualifiedByName = "organizationName")
    @Mapping(target = "installation", source = "installation", qualifiedByName = "installationName")
    @Mapping(target = "planVersion", source = "planVersion", qualifiedByName = "maintenancePlanVersionVersionCode")
    @Mapping(target = "serviceAgreement", source = "serviceAgreement", qualifiedByName = "serviceAgreementId")
    PlanSubscriptionDTO toDto(PlanSubscription s);

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

    @Named("maintenancePlanVersionVersionCode")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "versionCode", source = "versionCode")
    MaintenancePlanVersionDTO toDtoMaintenancePlanVersionVersionCode(MaintenancePlanVersion maintenancePlanVersion);

    @Named("serviceAgreementId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    ServiceAgreementDTO toDtoServiceAgreementId(ServiceAgreement serviceAgreement);
}

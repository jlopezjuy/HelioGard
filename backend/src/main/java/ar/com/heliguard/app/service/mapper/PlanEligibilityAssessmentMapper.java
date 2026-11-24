package ar.com.heliguard.app.service.mapper;

import ar.com.heliguard.app.domain.AuditReport;
import ar.com.heliguard.app.domain.Installation;
import ar.com.heliguard.app.domain.MaintenancePlanVersion;
import ar.com.heliguard.app.domain.Organization;
import ar.com.heliguard.app.domain.PlanEligibilityAssessment;
import ar.com.heliguard.app.service.dto.AuditReportDTO;
import ar.com.heliguard.app.service.dto.InstallationDTO;
import ar.com.heliguard.app.service.dto.MaintenancePlanVersionDTO;
import ar.com.heliguard.app.service.dto.OrganizationDTO;
import ar.com.heliguard.app.service.dto.PlanEligibilityAssessmentDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link PlanEligibilityAssessment} and its DTO {@link PlanEligibilityAssessmentDTO}.
 */
@Mapper(componentModel = "spring")
public interface PlanEligibilityAssessmentMapper extends EntityMapper<PlanEligibilityAssessmentDTO, PlanEligibilityAssessment> {
    @Mapping(target = "organization", source = "organization", qualifiedByName = "organizationName")
    @Mapping(target = "installation", source = "installation", qualifiedByName = "installationName")
    @Mapping(target = "auditReport", source = "auditReport", qualifiedByName = "auditReportId")
    @Mapping(target = "planVersion", source = "planVersion", qualifiedByName = "maintenancePlanVersionVersionCode")
    PlanEligibilityAssessmentDTO toDto(PlanEligibilityAssessment s);

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

    @Named("auditReportId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    AuditReportDTO toDtoAuditReportId(AuditReport auditReport);

    @Named("maintenancePlanVersionVersionCode")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "versionCode", source = "versionCode")
    MaintenancePlanVersionDTO toDtoMaintenancePlanVersionVersionCode(MaintenancePlanVersion maintenancePlanVersion);
}

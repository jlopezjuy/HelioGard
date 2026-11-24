package ar.com.heliguard.app.service.mapper;

import ar.com.heliguard.app.domain.EnergyModelRun;
import ar.com.heliguard.app.domain.Installation;
import ar.com.heliguard.app.domain.IntegrityAssessment;
import ar.com.heliguard.app.domain.Organization;
import ar.com.heliguard.app.domain.TelemetryIntegrityEvent;
import ar.com.heliguard.app.service.dto.EnergyModelRunDTO;
import ar.com.heliguard.app.service.dto.InstallationDTO;
import ar.com.heliguard.app.service.dto.IntegrityAssessmentDTO;
import ar.com.heliguard.app.service.dto.OrganizationDTO;
import ar.com.heliguard.app.service.dto.TelemetryIntegrityEventDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link IntegrityAssessment} and its DTO {@link IntegrityAssessmentDTO}.
 */
@Mapper(componentModel = "spring")
public interface IntegrityAssessmentMapper extends EntityMapper<IntegrityAssessmentDTO, IntegrityAssessment> {
    @Mapping(target = "organization", source = "organization", qualifiedByName = "organizationName")
    @Mapping(target = "installation", source = "installation", qualifiedByName = "installationName")
    @Mapping(target = "integrityEvent", source = "integrityEvent", qualifiedByName = "telemetryIntegrityEventEventUid")
    @Mapping(target = "modelRun", source = "modelRun", qualifiedByName = "energyModelRunModelName")
    IntegrityAssessmentDTO toDto(IntegrityAssessment s);

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

    @Named("telemetryIntegrityEventEventUid")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "eventUid", source = "eventUid")
    TelemetryIntegrityEventDTO toDtoTelemetryIntegrityEventEventUid(TelemetryIntegrityEvent telemetryIntegrityEvent);

    @Named("energyModelRunModelName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "modelName", source = "modelName")
    EnergyModelRunDTO toDtoEnergyModelRunModelName(EnergyModelRun energyModelRun);
}

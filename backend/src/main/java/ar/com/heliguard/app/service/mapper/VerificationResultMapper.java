package ar.com.heliguard.app.service.mapper;

import ar.com.heliguard.app.domain.DroneMission;
import ar.com.heliguard.app.domain.Installation;
import ar.com.heliguard.app.domain.Organization;
import ar.com.heliguard.app.domain.RecoveryAssessment;
import ar.com.heliguard.app.domain.VerificationResult;
import ar.com.heliguard.app.service.dto.DroneMissionDTO;
import ar.com.heliguard.app.service.dto.InstallationDTO;
import ar.com.heliguard.app.service.dto.OrganizationDTO;
import ar.com.heliguard.app.service.dto.RecoveryAssessmentDTO;
import ar.com.heliguard.app.service.dto.VerificationResultDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link VerificationResult} and its DTO {@link VerificationResultDTO}.
 */
@Mapper(componentModel = "spring")
public interface VerificationResultMapper extends EntityMapper<VerificationResultDTO, VerificationResult> {
    @Mapping(target = "organization", source = "organization", qualifiedByName = "organizationName")
    @Mapping(target = "installation", source = "installation", qualifiedByName = "installationName")
    @Mapping(target = "droneMission", source = "droneMission", qualifiedByName = "droneMissionMissionCode")
    @Mapping(target = "recoveryAssessment", source = "recoveryAssessment", qualifiedByName = "recoveryAssessmentId")
    VerificationResultDTO toDto(VerificationResult s);

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

    @Named("droneMissionMissionCode")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "missionCode", source = "missionCode")
    DroneMissionDTO toDtoDroneMissionMissionCode(DroneMission droneMission);

    @Named("recoveryAssessmentId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    RecoveryAssessmentDTO toDtoRecoveryAssessmentId(RecoveryAssessment recoveryAssessment);
}

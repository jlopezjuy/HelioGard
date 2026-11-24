package ar.com.heliguard.app.service.mapper;

import ar.com.heliguard.app.domain.DroneMission;
import ar.com.heliguard.app.domain.MissionSafetyCheck;
import ar.com.heliguard.app.domain.Organization;
import ar.com.heliguard.app.domain.OrganizationMembership;
import ar.com.heliguard.app.service.dto.DroneMissionDTO;
import ar.com.heliguard.app.service.dto.MissionSafetyCheckDTO;
import ar.com.heliguard.app.service.dto.OrganizationDTO;
import ar.com.heliguard.app.service.dto.OrganizationMembershipDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link MissionSafetyCheck} and its DTO {@link MissionSafetyCheckDTO}.
 */
@Mapper(componentModel = "spring")
public interface MissionSafetyCheckMapper extends EntityMapper<MissionSafetyCheckDTO, MissionSafetyCheck> {
    @Mapping(target = "organization", source = "organization", qualifiedByName = "organizationName")
    @Mapping(target = "mission", source = "mission", qualifiedByName = "droneMissionMissionCode")
    @Mapping(target = "checkedByMembership", source = "checkedByMembership", qualifiedByName = "organizationMembershipId")
    MissionSafetyCheckDTO toDto(MissionSafetyCheck s);

    @Named("organizationName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    OrganizationDTO toDtoOrganizationName(Organization organization);

    @Named("droneMissionMissionCode")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "missionCode", source = "missionCode")
    DroneMissionDTO toDtoDroneMissionMissionCode(DroneMission droneMission);

    @Named("organizationMembershipId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    OrganizationMembershipDTO toDtoOrganizationMembershipId(OrganizationMembership organizationMembership);
}

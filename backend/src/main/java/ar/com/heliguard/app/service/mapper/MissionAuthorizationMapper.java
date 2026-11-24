package ar.com.heliguard.app.service.mapper;

import ar.com.heliguard.app.domain.DroneMission;
import ar.com.heliguard.app.domain.MissionAuthorization;
import ar.com.heliguard.app.domain.Organization;
import ar.com.heliguard.app.domain.OrganizationMembership;
import ar.com.heliguard.app.service.dto.DroneMissionDTO;
import ar.com.heliguard.app.service.dto.MissionAuthorizationDTO;
import ar.com.heliguard.app.service.dto.OrganizationDTO;
import ar.com.heliguard.app.service.dto.OrganizationMembershipDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link MissionAuthorization} and its DTO {@link MissionAuthorizationDTO}.
 */
@Mapper(componentModel = "spring")
public interface MissionAuthorizationMapper extends EntityMapper<MissionAuthorizationDTO, MissionAuthorization> {
    @Mapping(target = "organization", source = "organization", qualifiedByName = "organizationName")
    @Mapping(target = "mission", source = "mission", qualifiedByName = "droneMissionMissionCode")
    @Mapping(target = "authorizedByMembership", source = "authorizedByMembership", qualifiedByName = "organizationMembershipId")
    MissionAuthorizationDTO toDto(MissionAuthorization s);

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

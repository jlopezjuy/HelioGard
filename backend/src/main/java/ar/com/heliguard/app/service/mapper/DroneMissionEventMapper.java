package ar.com.heliguard.app.service.mapper;

import ar.com.heliguard.app.domain.DroneMission;
import ar.com.heliguard.app.domain.DroneMissionEvent;
import ar.com.heliguard.app.domain.Organization;
import ar.com.heliguard.app.domain.OrganizationMembership;
import ar.com.heliguard.app.service.dto.DroneMissionDTO;
import ar.com.heliguard.app.service.dto.DroneMissionEventDTO;
import ar.com.heliguard.app.service.dto.OrganizationDTO;
import ar.com.heliguard.app.service.dto.OrganizationMembershipDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link DroneMissionEvent} and its DTO {@link DroneMissionEventDTO}.
 */
@Mapper(componentModel = "spring")
public interface DroneMissionEventMapper extends EntityMapper<DroneMissionEventDTO, DroneMissionEvent> {
    @Mapping(target = "organization", source = "organization", qualifiedByName = "organizationName")
    @Mapping(target = "mission", source = "mission", qualifiedByName = "droneMissionMissionCode")
    @Mapping(target = "operatorMembership", source = "operatorMembership", qualifiedByName = "organizationMembershipId")
    DroneMissionEventDTO toDto(DroneMissionEvent s);

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

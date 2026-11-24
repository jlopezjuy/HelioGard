package ar.com.heliguard.app.service.mapper;

import ar.com.heliguard.app.domain.Organization;
import ar.com.heliguard.app.domain.OrganizationMembership;
import ar.com.heliguard.app.domain.User;
import ar.com.heliguard.app.service.dto.OrganizationDTO;
import ar.com.heliguard.app.service.dto.OrganizationMembershipDTO;
import ar.com.heliguard.app.service.dto.UserDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link OrganizationMembership} and its DTO {@link OrganizationMembershipDTO}.
 */
@Mapper(componentModel = "spring")
public interface OrganizationMembershipMapper extends EntityMapper<OrganizationMembershipDTO, OrganizationMembership> {
    @Mapping(target = "organization", source = "organization", qualifiedByName = "organizationName")
    @Mapping(target = "member", source = "member", qualifiedByName = "userLogin")
    OrganizationMembershipDTO toDto(OrganizationMembership s);

    @Named("organizationName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    OrganizationDTO toDtoOrganizationName(Organization organization);

    @Named("userLogin")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "login", source = "login")
    UserDTO toDtoUserLogin(User user);
}

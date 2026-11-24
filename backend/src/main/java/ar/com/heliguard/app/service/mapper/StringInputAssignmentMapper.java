package ar.com.heliguard.app.service.mapper;

import ar.com.heliguard.app.domain.InverterMpptInput;
import ar.com.heliguard.app.domain.Organization;
import ar.com.heliguard.app.domain.SolarString;
import ar.com.heliguard.app.domain.StringInputAssignment;
import ar.com.heliguard.app.service.dto.InverterMpptInputDTO;
import ar.com.heliguard.app.service.dto.OrganizationDTO;
import ar.com.heliguard.app.service.dto.SolarStringDTO;
import ar.com.heliguard.app.service.dto.StringInputAssignmentDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link StringInputAssignment} and its DTO {@link StringInputAssignmentDTO}.
 */
@Mapper(componentModel = "spring")
public interface StringInputAssignmentMapper extends EntityMapper<StringInputAssignmentDTO, StringInputAssignment> {
    @Mapping(target = "organization", source = "organization", qualifiedByName = "organizationName")
    @Mapping(target = "solarString", source = "solarString", qualifiedByName = "solarStringStringCode")
    @Mapping(target = "mpptInput", source = "mpptInput", qualifiedByName = "inverterMpptInputInputCode")
    StringInputAssignmentDTO toDto(StringInputAssignment s);

    @Named("organizationName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    OrganizationDTO toDtoOrganizationName(Organization organization);

    @Named("solarStringStringCode")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "stringCode", source = "stringCode")
    SolarStringDTO toDtoSolarStringStringCode(SolarString solarString);

    @Named("inverterMpptInputInputCode")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "inputCode", source = "inputCode")
    InverterMpptInputDTO toDtoInverterMpptInputInputCode(InverterMpptInput inverterMpptInput);
}

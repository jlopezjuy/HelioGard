package ar.com.heliguard.app.service.mapper;

import ar.com.heliguard.app.domain.Incident;
import ar.com.heliguard.app.domain.IncidentEvent;
import ar.com.heliguard.app.domain.Organization;
import ar.com.heliguard.app.service.dto.IncidentDTO;
import ar.com.heliguard.app.service.dto.IncidentEventDTO;
import ar.com.heliguard.app.service.dto.OrganizationDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link IncidentEvent} and its DTO {@link IncidentEventDTO}.
 */
@Mapper(componentModel = "spring")
public interface IncidentEventMapper extends EntityMapper<IncidentEventDTO, IncidentEvent> {
    @Mapping(target = "organization", source = "organization", qualifiedByName = "organizationName")
    @Mapping(target = "incident", source = "incident", qualifiedByName = "incidentTitle")
    IncidentEventDTO toDto(IncidentEvent s);

    @Named("organizationName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    OrganizationDTO toDtoOrganizationName(Organization organization);

    @Named("incidentTitle")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "title", source = "title")
    IncidentDTO toDtoIncidentTitle(Incident incident);
}

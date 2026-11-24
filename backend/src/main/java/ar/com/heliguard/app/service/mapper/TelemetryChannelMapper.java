package ar.com.heliguard.app.service.mapper;

import ar.com.heliguard.app.domain.Equipment;
import ar.com.heliguard.app.domain.MetricDefinition;
import ar.com.heliguard.app.domain.Organization;
import ar.com.heliguard.app.domain.TelemetryChannel;
import ar.com.heliguard.app.service.dto.EquipmentDTO;
import ar.com.heliguard.app.service.dto.MetricDefinitionDTO;
import ar.com.heliguard.app.service.dto.OrganizationDTO;
import ar.com.heliguard.app.service.dto.TelemetryChannelDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link TelemetryChannel} and its DTO {@link TelemetryChannelDTO}.
 */
@Mapper(componentModel = "spring")
public interface TelemetryChannelMapper extends EntityMapper<TelemetryChannelDTO, TelemetryChannel> {
    @Mapping(target = "organization", source = "organization", qualifiedByName = "organizationName")
    @Mapping(target = "equipment", source = "equipment", qualifiedByName = "equipmentTag")
    @Mapping(target = "metricDefinition", source = "metricDefinition", qualifiedByName = "metricDefinitionCode")
    TelemetryChannelDTO toDto(TelemetryChannel s);

    @Named("organizationName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    OrganizationDTO toDtoOrganizationName(Organization organization);

    @Named("equipmentTag")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "tag", source = "tag")
    EquipmentDTO toDtoEquipmentTag(Equipment equipment);

    @Named("metricDefinitionCode")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "code", source = "code")
    MetricDefinitionDTO toDtoMetricDefinitionCode(MetricDefinition metricDefinition);
}

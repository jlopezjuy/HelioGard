package ar.com.heliguard.app.service.mapper;

import ar.com.heliguard.app.domain.MetricDefinition;
import ar.com.heliguard.app.service.dto.MetricDefinitionDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link MetricDefinition} and its DTO {@link MetricDefinitionDTO}.
 */
@Mapper(componentModel = "spring")
public interface MetricDefinitionMapper extends EntityMapper<MetricDefinitionDTO, MetricDefinition> {}

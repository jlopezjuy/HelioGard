package ar.com.heliguard.app.service.mapper;

import ar.com.heliguard.app.domain.EquipmentModel;
import ar.com.heliguard.app.service.dto.EquipmentModelDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link EquipmentModel} and its DTO {@link EquipmentModelDTO}.
 */
@Mapper(componentModel = "spring")
public interface EquipmentModelMapper extends EntityMapper<EquipmentModelDTO, EquipmentModel> {}

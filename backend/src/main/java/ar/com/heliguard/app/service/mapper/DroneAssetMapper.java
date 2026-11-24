package ar.com.heliguard.app.service.mapper;

import ar.com.heliguard.app.domain.DroneAsset;
import ar.com.heliguard.app.domain.Organization;
import ar.com.heliguard.app.service.dto.DroneAssetDTO;
import ar.com.heliguard.app.service.dto.OrganizationDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link DroneAsset} and its DTO {@link DroneAssetDTO}.
 */
@Mapper(componentModel = "spring")
public interface DroneAssetMapper extends EntityMapper<DroneAssetDTO, DroneAsset> {
    @Mapping(target = "organization", source = "organization", qualifiedByName = "organizationName")
    DroneAssetDTO toDto(DroneAsset s);

    @Named("organizationName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    OrganizationDTO toDtoOrganizationName(Organization organization);
}

package ar.com.heliguard.app.service.mapper;

import ar.com.heliguard.app.domain.EvidenceSource;
import ar.com.heliguard.app.domain.Installation;
import ar.com.heliguard.app.domain.Organization;
import ar.com.heliguard.app.domain.WeatherIngestionBatch;
import ar.com.heliguard.app.service.dto.EvidenceSourceDTO;
import ar.com.heliguard.app.service.dto.InstallationDTO;
import ar.com.heliguard.app.service.dto.OrganizationDTO;
import ar.com.heliguard.app.service.dto.WeatherIngestionBatchDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link WeatherIngestionBatch} and its DTO {@link WeatherIngestionBatchDTO}.
 */
@Mapper(componentModel = "spring")
public interface WeatherIngestionBatchMapper extends EntityMapper<WeatherIngestionBatchDTO, WeatherIngestionBatch> {
    @Mapping(target = "organization", source = "organization", qualifiedByName = "organizationName")
    @Mapping(target = "installation", source = "installation", qualifiedByName = "installationName")
    @Mapping(target = "evidenceSource", source = "evidenceSource", qualifiedByName = "evidenceSourceCode")
    WeatherIngestionBatchDTO toDto(WeatherIngestionBatch s);

    @Named("organizationName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    OrganizationDTO toDtoOrganizationName(Organization organization);

    @Named("installationName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    InstallationDTO toDtoInstallationName(Installation installation);

    @Named("evidenceSourceCode")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "code", source = "code")
    EvidenceSourceDTO toDtoEvidenceSourceCode(EvidenceSource evidenceSource);
}

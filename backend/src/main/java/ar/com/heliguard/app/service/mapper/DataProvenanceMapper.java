package ar.com.heliguard.app.service.mapper;

import ar.com.heliguard.app.domain.DataProvenance;
import ar.com.heliguard.app.domain.EnergyModelRun;
import ar.com.heliguard.app.domain.EvidenceSource;
import ar.com.heliguard.app.domain.Installation;
import ar.com.heliguard.app.domain.Organization;
import ar.com.heliguard.app.service.dto.DataProvenanceDTO;
import ar.com.heliguard.app.service.dto.EnergyModelRunDTO;
import ar.com.heliguard.app.service.dto.EvidenceSourceDTO;
import ar.com.heliguard.app.service.dto.InstallationDTO;
import ar.com.heliguard.app.service.dto.OrganizationDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link DataProvenance} and its DTO {@link DataProvenanceDTO}.
 */
@Mapper(componentModel = "spring")
public interface DataProvenanceMapper extends EntityMapper<DataProvenanceDTO, DataProvenance> {
    @Mapping(target = "organization", source = "organization", qualifiedByName = "organizationName")
    @Mapping(target = "installation", source = "installation", qualifiedByName = "installationName")
    @Mapping(target = "evidenceSource", source = "evidenceSource", qualifiedByName = "evidenceSourceCode")
    @Mapping(target = "modelRun", source = "modelRun", qualifiedByName = "energyModelRunModelName")
    DataProvenanceDTO toDto(DataProvenance s);

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

    @Named("energyModelRunModelName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "modelName", source = "modelName")
    EnergyModelRunDTO toDtoEnergyModelRunModelName(EnergyModelRun energyModelRun);
}

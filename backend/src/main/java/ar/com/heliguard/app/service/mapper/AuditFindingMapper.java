package ar.com.heliguard.app.service.mapper;

import ar.com.heliguard.app.domain.AuditFinding;
import ar.com.heliguard.app.domain.AuditReport;
import ar.com.heliguard.app.domain.Equipment;
import ar.com.heliguard.app.domain.Organization;
import ar.com.heliguard.app.service.dto.AuditFindingDTO;
import ar.com.heliguard.app.service.dto.AuditReportDTO;
import ar.com.heliguard.app.service.dto.EquipmentDTO;
import ar.com.heliguard.app.service.dto.OrganizationDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link AuditFinding} and its DTO {@link AuditFindingDTO}.
 */
@Mapper(componentModel = "spring")
public interface AuditFindingMapper extends EntityMapper<AuditFindingDTO, AuditFinding> {
    @Mapping(target = "organization", source = "organization", qualifiedByName = "organizationName")
    @Mapping(target = "auditReport", source = "auditReport", qualifiedByName = "auditReportId")
    @Mapping(target = "equipment", source = "equipment", qualifiedByName = "equipmentTag")
    AuditFindingDTO toDto(AuditFinding s);

    @Named("organizationName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    OrganizationDTO toDtoOrganizationName(Organization organization);

    @Named("auditReportId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    AuditReportDTO toDtoAuditReportId(AuditReport auditReport);

    @Named("equipmentTag")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "tag", source = "tag")
    EquipmentDTO toDtoEquipmentTag(Equipment equipment);
}

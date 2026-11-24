package ar.com.heliguard.app.service.mapper;

import ar.com.heliguard.app.domain.EvidenceSource;
import ar.com.heliguard.app.service.dto.EvidenceSourceDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link EvidenceSource} and its DTO {@link EvidenceSourceDTO}.
 */
@Mapper(componentModel = "spring")
public interface EvidenceSourceMapper extends EntityMapper<EvidenceSourceDTO, EvidenceSource> {}

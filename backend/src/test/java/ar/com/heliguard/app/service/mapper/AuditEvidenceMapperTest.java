package ar.com.heliguard.app.service.mapper;

import static ar.com.heliguard.app.domain.AuditEvidenceAsserts.*;
import static ar.com.heliguard.app.domain.AuditEvidenceTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AuditEvidenceMapperTest {

    private AuditEvidenceMapper auditEvidenceMapper;

    @BeforeEach
    void setUp() {
        auditEvidenceMapper = new AuditEvidenceMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getAuditEvidenceSample1();
        var actual = auditEvidenceMapper.toEntity(auditEvidenceMapper.toDto(expected));
        assertAuditEvidenceAllPropertiesEquals(expected, actual);
    }
}

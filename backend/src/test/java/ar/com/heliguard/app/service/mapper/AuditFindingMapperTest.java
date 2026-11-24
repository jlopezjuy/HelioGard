package ar.com.heliguard.app.service.mapper;

import static ar.com.heliguard.app.domain.AuditFindingAsserts.*;
import static ar.com.heliguard.app.domain.AuditFindingTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AuditFindingMapperTest {

    private AuditFindingMapper auditFindingMapper;

    @BeforeEach
    void setUp() {
        auditFindingMapper = new AuditFindingMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getAuditFindingSample1();
        var actual = auditFindingMapper.toEntity(auditFindingMapper.toDto(expected));
        assertAuditFindingAllPropertiesEquals(expected, actual);
    }
}

package ar.com.heliguard.app.service.mapper;

import static ar.com.heliguard.app.domain.AuditReportAsserts.*;
import static ar.com.heliguard.app.domain.AuditReportTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AuditReportMapperTest {

    private AuditReportMapper auditReportMapper;

    @BeforeEach
    void setUp() {
        auditReportMapper = new AuditReportMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getAuditReportSample1();
        var actual = auditReportMapper.toEntity(auditReportMapper.toDto(expected));
        assertAuditReportAllPropertiesEquals(expected, actual);
    }
}

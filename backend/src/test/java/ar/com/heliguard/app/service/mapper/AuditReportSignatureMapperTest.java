package ar.com.heliguard.app.service.mapper;

import static ar.com.heliguard.app.domain.AuditReportSignatureAsserts.*;
import static ar.com.heliguard.app.domain.AuditReportSignatureTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AuditReportSignatureMapperTest {

    private AuditReportSignatureMapper auditReportSignatureMapper;

    @BeforeEach
    void setUp() {
        auditReportSignatureMapper = new AuditReportSignatureMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getAuditReportSignatureSample1();
        var actual = auditReportSignatureMapper.toEntity(auditReportSignatureMapper.toDto(expected));
        assertAuditReportSignatureAllPropertiesEquals(expected, actual);
    }
}

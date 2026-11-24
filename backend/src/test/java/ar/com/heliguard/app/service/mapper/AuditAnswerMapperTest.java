package ar.com.heliguard.app.service.mapper;

import static ar.com.heliguard.app.domain.AuditAnswerAsserts.*;
import static ar.com.heliguard.app.domain.AuditAnswerTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AuditAnswerMapperTest {

    private AuditAnswerMapper auditAnswerMapper;

    @BeforeEach
    void setUp() {
        auditAnswerMapper = new AuditAnswerMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getAuditAnswerSample1();
        var actual = auditAnswerMapper.toEntity(auditAnswerMapper.toDto(expected));
        assertAuditAnswerAllPropertiesEquals(expected, actual);
    }
}

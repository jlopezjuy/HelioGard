package ar.com.heliguard.app.service.mapper;

import static ar.com.heliguard.app.domain.AuditQuestionAsserts.*;
import static ar.com.heliguard.app.domain.AuditQuestionTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AuditQuestionMapperTest {

    private AuditQuestionMapper auditQuestionMapper;

    @BeforeEach
    void setUp() {
        auditQuestionMapper = new AuditQuestionMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getAuditQuestionSample1();
        var actual = auditQuestionMapper.toEntity(auditQuestionMapper.toDto(expected));
        assertAuditQuestionAllPropertiesEquals(expected, actual);
    }
}

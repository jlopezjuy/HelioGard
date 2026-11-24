package ar.com.heliguard.app.service.mapper;

import static ar.com.heliguard.app.domain.AuditTemplateAsserts.*;
import static ar.com.heliguard.app.domain.AuditTemplateTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AuditTemplateMapperTest {

    private AuditTemplateMapper auditTemplateMapper;

    @BeforeEach
    void setUp() {
        auditTemplateMapper = new AuditTemplateMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getAuditTemplateSample1();
        var actual = auditTemplateMapper.toEntity(auditTemplateMapper.toDto(expected));
        assertAuditTemplateAllPropertiesEquals(expected, actual);
    }
}

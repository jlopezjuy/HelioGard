package ar.com.heliguard.app.service.mapper;

import static ar.com.heliguard.app.domain.SecurityCaseEvidenceAsserts.*;
import static ar.com.heliguard.app.domain.SecurityCaseEvidenceTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SecurityCaseEvidenceMapperTest {

    private SecurityCaseEvidenceMapper securityCaseEvidenceMapper;

    @BeforeEach
    void setUp() {
        securityCaseEvidenceMapper = new SecurityCaseEvidenceMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getSecurityCaseEvidenceSample1();
        var actual = securityCaseEvidenceMapper.toEntity(securityCaseEvidenceMapper.toDto(expected));
        assertSecurityCaseEvidenceAllPropertiesEquals(expected, actual);
    }
}

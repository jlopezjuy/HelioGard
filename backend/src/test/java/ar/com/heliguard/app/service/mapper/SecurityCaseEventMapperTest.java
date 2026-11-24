package ar.com.heliguard.app.service.mapper;

import static ar.com.heliguard.app.domain.SecurityCaseEventAsserts.*;
import static ar.com.heliguard.app.domain.SecurityCaseEventTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SecurityCaseEventMapperTest {

    private SecurityCaseEventMapper securityCaseEventMapper;

    @BeforeEach
    void setUp() {
        securityCaseEventMapper = new SecurityCaseEventMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getSecurityCaseEventSample1();
        var actual = securityCaseEventMapper.toEntity(securityCaseEventMapper.toDto(expected));
        assertSecurityCaseEventAllPropertiesEquals(expected, actual);
    }
}

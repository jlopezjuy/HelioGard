package ar.com.heliguard.app.service.mapper;

import static ar.com.heliguard.app.domain.SecurityIncidentAsserts.*;
import static ar.com.heliguard.app.domain.SecurityIncidentTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SecurityIncidentMapperTest {

    private SecurityIncidentMapper securityIncidentMapper;

    @BeforeEach
    void setUp() {
        securityIncidentMapper = new SecurityIncidentMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getSecurityIncidentSample1();
        var actual = securityIncidentMapper.toEntity(securityIncidentMapper.toDto(expected));
        assertSecurityIncidentAllPropertiesEquals(expected, actual);
    }
}

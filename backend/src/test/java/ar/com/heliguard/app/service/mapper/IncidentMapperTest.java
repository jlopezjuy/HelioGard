package ar.com.heliguard.app.service.mapper;

import static ar.com.heliguard.app.domain.IncidentAsserts.*;
import static ar.com.heliguard.app.domain.IncidentTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class IncidentMapperTest {

    private IncidentMapper incidentMapper;

    @BeforeEach
    void setUp() {
        incidentMapper = new IncidentMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getIncidentSample1();
        var actual = incidentMapper.toEntity(incidentMapper.toDto(expected));
        assertIncidentAllPropertiesEquals(expected, actual);
    }
}

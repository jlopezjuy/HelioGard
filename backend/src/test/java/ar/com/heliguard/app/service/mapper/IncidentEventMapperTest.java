package ar.com.heliguard.app.service.mapper;

import static ar.com.heliguard.app.domain.IncidentEventAsserts.*;
import static ar.com.heliguard.app.domain.IncidentEventTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class IncidentEventMapperTest {

    private IncidentEventMapper incidentEventMapper;

    @BeforeEach
    void setUp() {
        incidentEventMapper = new IncidentEventMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getIncidentEventSample1();
        var actual = incidentEventMapper.toEntity(incidentEventMapper.toDto(expected));
        assertIncidentEventAllPropertiesEquals(expected, actual);
    }
}

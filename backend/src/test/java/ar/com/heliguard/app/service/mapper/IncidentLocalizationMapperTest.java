package ar.com.heliguard.app.service.mapper;

import static ar.com.heliguard.app.domain.IncidentLocalizationAsserts.*;
import static ar.com.heliguard.app.domain.IncidentLocalizationTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class IncidentLocalizationMapperTest {

    private IncidentLocalizationMapper incidentLocalizationMapper;

    @BeforeEach
    void setUp() {
        incidentLocalizationMapper = new IncidentLocalizationMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getIncidentLocalizationSample1();
        var actual = incidentLocalizationMapper.toEntity(incidentLocalizationMapper.toDto(expected));
        assertIncidentLocalizationAllPropertiesEquals(expected, actual);
    }
}

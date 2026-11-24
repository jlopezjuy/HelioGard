package ar.com.heliguard.app.service.mapper;

import static ar.com.heliguard.app.domain.TelemetryIntegrityEventAsserts.*;
import static ar.com.heliguard.app.domain.TelemetryIntegrityEventTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TelemetryIntegrityEventMapperTest {

    private TelemetryIntegrityEventMapper telemetryIntegrityEventMapper;

    @BeforeEach
    void setUp() {
        telemetryIntegrityEventMapper = new TelemetryIntegrityEventMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getTelemetryIntegrityEventSample1();
        var actual = telemetryIntegrityEventMapper.toEntity(telemetryIntegrityEventMapper.toDto(expected));
        assertTelemetryIntegrityEventAllPropertiesEquals(expected, actual);
    }
}

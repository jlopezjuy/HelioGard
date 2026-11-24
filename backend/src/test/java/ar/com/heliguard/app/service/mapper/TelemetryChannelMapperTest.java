package ar.com.heliguard.app.service.mapper;

import static ar.com.heliguard.app.domain.TelemetryChannelAsserts.*;
import static ar.com.heliguard.app.domain.TelemetryChannelTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TelemetryChannelMapperTest {

    private TelemetryChannelMapper telemetryChannelMapper;

    @BeforeEach
    void setUp() {
        telemetryChannelMapper = new TelemetryChannelMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getTelemetryChannelSample1();
        var actual = telemetryChannelMapper.toEntity(telemetryChannelMapper.toDto(expected));
        assertTelemetryChannelAllPropertiesEquals(expected, actual);
    }
}

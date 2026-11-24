package ar.com.heliguard.app.service.mapper;

import static ar.com.heliguard.app.domain.TelemetryChannelBindingAsserts.*;
import static ar.com.heliguard.app.domain.TelemetryChannelBindingTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TelemetryChannelBindingMapperTest {

    private TelemetryChannelBindingMapper telemetryChannelBindingMapper;

    @BeforeEach
    void setUp() {
        telemetryChannelBindingMapper = new TelemetryChannelBindingMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getTelemetryChannelBindingSample1();
        var actual = telemetryChannelBindingMapper.toEntity(telemetryChannelBindingMapper.toDto(expected));
        assertTelemetryChannelBindingAllPropertiesEquals(expected, actual);
    }
}

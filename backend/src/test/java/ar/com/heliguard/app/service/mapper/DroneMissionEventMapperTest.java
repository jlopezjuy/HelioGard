package ar.com.heliguard.app.service.mapper;

import static ar.com.heliguard.app.domain.DroneMissionEventAsserts.*;
import static ar.com.heliguard.app.domain.DroneMissionEventTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DroneMissionEventMapperTest {

    private DroneMissionEventMapper droneMissionEventMapper;

    @BeforeEach
    void setUp() {
        droneMissionEventMapper = new DroneMissionEventMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getDroneMissionEventSample1();
        var actual = droneMissionEventMapper.toEntity(droneMissionEventMapper.toDto(expected));
        assertDroneMissionEventAllPropertiesEquals(expected, actual);
    }
}

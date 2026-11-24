package ar.com.heliguard.app.service.mapper;

import static ar.com.heliguard.app.domain.DroneMissionAsserts.*;
import static ar.com.heliguard.app.domain.DroneMissionTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DroneMissionMapperTest {

    private DroneMissionMapper droneMissionMapper;

    @BeforeEach
    void setUp() {
        droneMissionMapper = new DroneMissionMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getDroneMissionSample1();
        var actual = droneMissionMapper.toEntity(droneMissionMapper.toDto(expected));
        assertDroneMissionAllPropertiesEquals(expected, actual);
    }
}

package ar.com.heliguard.app.service.mapper;

import static ar.com.heliguard.app.domain.DroneMissionTargetAsserts.*;
import static ar.com.heliguard.app.domain.DroneMissionTargetTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DroneMissionTargetMapperTest {

    private DroneMissionTargetMapper droneMissionTargetMapper;

    @BeforeEach
    void setUp() {
        droneMissionTargetMapper = new DroneMissionTargetMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getDroneMissionTargetSample1();
        var actual = droneMissionTargetMapper.toEntity(droneMissionTargetMapper.toDto(expected));
        assertDroneMissionTargetAllPropertiesEquals(expected, actual);
    }
}

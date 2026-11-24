package ar.com.heliguard.app.service.mapper;

import static ar.com.heliguard.app.domain.MissionSafetyCheckAsserts.*;
import static ar.com.heliguard.app.domain.MissionSafetyCheckTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MissionSafetyCheckMapperTest {

    private MissionSafetyCheckMapper missionSafetyCheckMapper;

    @BeforeEach
    void setUp() {
        missionSafetyCheckMapper = new MissionSafetyCheckMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getMissionSafetyCheckSample1();
        var actual = missionSafetyCheckMapper.toEntity(missionSafetyCheckMapper.toDto(expected));
        assertMissionSafetyCheckAllPropertiesEquals(expected, actual);
    }
}

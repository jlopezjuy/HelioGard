package ar.com.heliguard.app.service.mapper;

import static ar.com.heliguard.app.domain.MissionAuthorizationAsserts.*;
import static ar.com.heliguard.app.domain.MissionAuthorizationTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MissionAuthorizationMapperTest {

    private MissionAuthorizationMapper missionAuthorizationMapper;

    @BeforeEach
    void setUp() {
        missionAuthorizationMapper = new MissionAuthorizationMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getMissionAuthorizationSample1();
        var actual = missionAuthorizationMapper.toEntity(missionAuthorizationMapper.toDto(expected));
        assertMissionAuthorizationAllPropertiesEquals(expected, actual);
    }
}

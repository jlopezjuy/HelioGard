package ar.com.heliguard.app.service.mapper;

import static ar.com.heliguard.app.domain.DroneAssetAsserts.*;
import static ar.com.heliguard.app.domain.DroneAssetTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DroneAssetMapperTest {

    private DroneAssetMapper droneAssetMapper;

    @BeforeEach
    void setUp() {
        droneAssetMapper = new DroneAssetMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getDroneAssetSample1();
        var actual = droneAssetMapper.toEntity(droneAssetMapper.toDto(expected));
        assertDroneAssetAllPropertiesEquals(expected, actual);
    }
}

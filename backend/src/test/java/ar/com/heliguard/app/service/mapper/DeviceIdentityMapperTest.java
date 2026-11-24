package ar.com.heliguard.app.service.mapper;

import static ar.com.heliguard.app.domain.DeviceIdentityAsserts.*;
import static ar.com.heliguard.app.domain.DeviceIdentityTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DeviceIdentityMapperTest {

    private DeviceIdentityMapper deviceIdentityMapper;

    @BeforeEach
    void setUp() {
        deviceIdentityMapper = new DeviceIdentityMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getDeviceIdentitySample1();
        var actual = deviceIdentityMapper.toEntity(deviceIdentityMapper.toDto(expected));
        assertDeviceIdentityAllPropertiesEquals(expected, actual);
    }
}

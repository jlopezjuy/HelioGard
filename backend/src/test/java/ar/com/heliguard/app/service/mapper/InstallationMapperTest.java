package ar.com.heliguard.app.service.mapper;

import static ar.com.heliguard.app.domain.InstallationAsserts.*;
import static ar.com.heliguard.app.domain.InstallationTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class InstallationMapperTest {

    private InstallationMapper installationMapper;

    @BeforeEach
    void setUp() {
        installationMapper = new InstallationMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getInstallationSample1();
        var actual = installationMapper.toEntity(installationMapper.toDto(expected));
        assertInstallationAllPropertiesEquals(expected, actual);
    }
}

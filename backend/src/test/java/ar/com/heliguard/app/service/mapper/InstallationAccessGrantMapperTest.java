package ar.com.heliguard.app.service.mapper;

import static ar.com.heliguard.app.domain.InstallationAccessGrantAsserts.*;
import static ar.com.heliguard.app.domain.InstallationAccessGrantTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class InstallationAccessGrantMapperTest {

    private InstallationAccessGrantMapper installationAccessGrantMapper;

    @BeforeEach
    void setUp() {
        installationAccessGrantMapper = new InstallationAccessGrantMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getInstallationAccessGrantSample1();
        var actual = installationAccessGrantMapper.toEntity(installationAccessGrantMapper.toDto(expected));
        assertInstallationAccessGrantAllPropertiesEquals(expected, actual);
    }
}

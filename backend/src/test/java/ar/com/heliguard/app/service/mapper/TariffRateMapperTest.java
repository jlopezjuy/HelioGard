package ar.com.heliguard.app.service.mapper;

import static ar.com.heliguard.app.domain.TariffRateAsserts.*;
import static ar.com.heliguard.app.domain.TariffRateTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TariffRateMapperTest {

    private TariffRateMapper tariffRateMapper;

    @BeforeEach
    void setUp() {
        tariffRateMapper = new TariffRateMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getTariffRateSample1();
        var actual = tariffRateMapper.toEntity(tariffRateMapper.toDto(expected));
        assertTariffRateAllPropertiesEquals(expected, actual);
    }
}

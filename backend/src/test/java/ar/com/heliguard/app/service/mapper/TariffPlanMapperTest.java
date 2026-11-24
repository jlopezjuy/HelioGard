package ar.com.heliguard.app.service.mapper;

import static ar.com.heliguard.app.domain.TariffPlanAsserts.*;
import static ar.com.heliguard.app.domain.TariffPlanTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TariffPlanMapperTest {

    private TariffPlanMapper tariffPlanMapper;

    @BeforeEach
    void setUp() {
        tariffPlanMapper = new TariffPlanMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getTariffPlanSample1();
        var actual = tariffPlanMapper.toEntity(tariffPlanMapper.toDto(expected));
        assertTariffPlanAllPropertiesEquals(expected, actual);
    }
}

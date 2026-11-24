package ar.com.heliguard.app.service.mapper;

import static ar.com.heliguard.app.domain.PlanBenefitAsserts.*;
import static ar.com.heliguard.app.domain.PlanBenefitTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PlanBenefitMapperTest {

    private PlanBenefitMapper planBenefitMapper;

    @BeforeEach
    void setUp() {
        planBenefitMapper = new PlanBenefitMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getPlanBenefitSample1();
        var actual = planBenefitMapper.toEntity(planBenefitMapper.toDto(expected));
        assertPlanBenefitAllPropertiesEquals(expected, actual);
    }
}

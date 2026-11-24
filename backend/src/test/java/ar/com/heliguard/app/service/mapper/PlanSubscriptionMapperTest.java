package ar.com.heliguard.app.service.mapper;

import static ar.com.heliguard.app.domain.PlanSubscriptionAsserts.*;
import static ar.com.heliguard.app.domain.PlanSubscriptionTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PlanSubscriptionMapperTest {

    private PlanSubscriptionMapper planSubscriptionMapper;

    @BeforeEach
    void setUp() {
        planSubscriptionMapper = new PlanSubscriptionMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getPlanSubscriptionSample1();
        var actual = planSubscriptionMapper.toEntity(planSubscriptionMapper.toDto(expected));
        assertPlanSubscriptionAllPropertiesEquals(expected, actual);
    }
}

package ar.com.heliguard.app.service.mapper;

import static ar.com.heliguard.app.domain.PlanEligibilityAssessmentAsserts.*;
import static ar.com.heliguard.app.domain.PlanEligibilityAssessmentTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PlanEligibilityAssessmentMapperTest {

    private PlanEligibilityAssessmentMapper planEligibilityAssessmentMapper;

    @BeforeEach
    void setUp() {
        planEligibilityAssessmentMapper = new PlanEligibilityAssessmentMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getPlanEligibilityAssessmentSample1();
        var actual = planEligibilityAssessmentMapper.toEntity(planEligibilityAssessmentMapper.toDto(expected));
        assertPlanEligibilityAssessmentAllPropertiesEquals(expected, actual);
    }
}

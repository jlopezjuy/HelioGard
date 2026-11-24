package ar.com.heliguard.app.service.mapper;

import static ar.com.heliguard.app.domain.RecoveryAssessmentAsserts.*;
import static ar.com.heliguard.app.domain.RecoveryAssessmentTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class RecoveryAssessmentMapperTest {

    private RecoveryAssessmentMapper recoveryAssessmentMapper;

    @BeforeEach
    void setUp() {
        recoveryAssessmentMapper = new RecoveryAssessmentMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getRecoveryAssessmentSample1();
        var actual = recoveryAssessmentMapper.toEntity(recoveryAssessmentMapper.toDto(expected));
        assertRecoveryAssessmentAllPropertiesEquals(expected, actual);
    }
}

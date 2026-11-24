package ar.com.heliguard.app.service.mapper;

import static ar.com.heliguard.app.domain.AlertRuleAsserts.*;
import static ar.com.heliguard.app.domain.AlertRuleTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AlertRuleMapperTest {

    private AlertRuleMapper alertRuleMapper;

    @BeforeEach
    void setUp() {
        alertRuleMapper = new AlertRuleMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getAlertRuleSample1();
        var actual = alertRuleMapper.toEntity(alertRuleMapper.toDto(expected));
        assertAlertRuleAllPropertiesEquals(expected, actual);
    }
}

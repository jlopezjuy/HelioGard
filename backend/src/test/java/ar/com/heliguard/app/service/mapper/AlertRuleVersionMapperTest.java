package ar.com.heliguard.app.service.mapper;

import static ar.com.heliguard.app.domain.AlertRuleVersionAsserts.*;
import static ar.com.heliguard.app.domain.AlertRuleVersionTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AlertRuleVersionMapperTest {

    private AlertRuleVersionMapper alertRuleVersionMapper;

    @BeforeEach
    void setUp() {
        alertRuleVersionMapper = new AlertRuleVersionMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getAlertRuleVersionSample1();
        var actual = alertRuleVersionMapper.toEntity(alertRuleVersionMapper.toDto(expected));
        assertAlertRuleVersionAllPropertiesEquals(expected, actual);
    }
}

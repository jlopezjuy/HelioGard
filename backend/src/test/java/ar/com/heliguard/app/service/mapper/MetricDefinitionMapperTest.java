package ar.com.heliguard.app.service.mapper;

import static ar.com.heliguard.app.domain.MetricDefinitionAsserts.*;
import static ar.com.heliguard.app.domain.MetricDefinitionTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MetricDefinitionMapperTest {

    private MetricDefinitionMapper metricDefinitionMapper;

    @BeforeEach
    void setUp() {
        metricDefinitionMapper = new MetricDefinitionMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getMetricDefinitionSample1();
        var actual = metricDefinitionMapper.toEntity(metricDefinitionMapper.toDto(expected));
        assertMetricDefinitionAllPropertiesEquals(expected, actual);
    }
}

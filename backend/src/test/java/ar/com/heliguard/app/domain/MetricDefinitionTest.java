package ar.com.heliguard.app.domain;

import static ar.com.heliguard.app.domain.MetricDefinitionTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import ar.com.heliguard.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class MetricDefinitionTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(MetricDefinition.class);
        MetricDefinition metricDefinition1 = getMetricDefinitionSample1();
        MetricDefinition metricDefinition2 = new MetricDefinition();
        assertThat(metricDefinition1).isNotEqualTo(metricDefinition2);

        metricDefinition2.setId(metricDefinition1.getId());
        assertThat(metricDefinition1).isEqualTo(metricDefinition2);

        metricDefinition2 = getMetricDefinitionSample2();
        assertThat(metricDefinition1).isNotEqualTo(metricDefinition2);
    }
}

package ar.com.heliguard.app.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import ar.com.heliguard.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class MetricDefinitionDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(MetricDefinitionDTO.class);
        MetricDefinitionDTO metricDefinitionDTO1 = new MetricDefinitionDTO();
        metricDefinitionDTO1.setId(1L);
        MetricDefinitionDTO metricDefinitionDTO2 = new MetricDefinitionDTO();
        assertThat(metricDefinitionDTO1).isNotEqualTo(metricDefinitionDTO2);
        metricDefinitionDTO2.setId(metricDefinitionDTO1.getId());
        assertThat(metricDefinitionDTO1).isEqualTo(metricDefinitionDTO2);
        metricDefinitionDTO2.setId(2L);
        assertThat(metricDefinitionDTO1).isNotEqualTo(metricDefinitionDTO2);
        metricDefinitionDTO1.setId(null);
        assertThat(metricDefinitionDTO1).isNotEqualTo(metricDefinitionDTO2);
    }
}

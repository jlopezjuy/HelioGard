package ar.com.heliguard.app.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import ar.com.heliguard.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class WeatherIngestionBatchDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(WeatherIngestionBatchDTO.class);
        WeatherIngestionBatchDTO weatherIngestionBatchDTO1 = new WeatherIngestionBatchDTO();
        weatherIngestionBatchDTO1.setId(1L);
        WeatherIngestionBatchDTO weatherIngestionBatchDTO2 = new WeatherIngestionBatchDTO();
        assertThat(weatherIngestionBatchDTO1).isNotEqualTo(weatherIngestionBatchDTO2);
        weatherIngestionBatchDTO2.setId(weatherIngestionBatchDTO1.getId());
        assertThat(weatherIngestionBatchDTO1).isEqualTo(weatherIngestionBatchDTO2);
        weatherIngestionBatchDTO2.setId(2L);
        assertThat(weatherIngestionBatchDTO1).isNotEqualTo(weatherIngestionBatchDTO2);
        weatherIngestionBatchDTO1.setId(null);
        assertThat(weatherIngestionBatchDTO1).isNotEqualTo(weatherIngestionBatchDTO2);
    }
}

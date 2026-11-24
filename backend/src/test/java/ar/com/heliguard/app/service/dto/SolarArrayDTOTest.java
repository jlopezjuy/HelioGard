package ar.com.heliguard.app.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import ar.com.heliguard.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class SolarArrayDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(SolarArrayDTO.class);
        SolarArrayDTO solarArrayDTO1 = new SolarArrayDTO();
        solarArrayDTO1.setId(1L);
        SolarArrayDTO solarArrayDTO2 = new SolarArrayDTO();
        assertThat(solarArrayDTO1).isNotEqualTo(solarArrayDTO2);
        solarArrayDTO2.setId(solarArrayDTO1.getId());
        assertThat(solarArrayDTO1).isEqualTo(solarArrayDTO2);
        solarArrayDTO2.setId(2L);
        assertThat(solarArrayDTO1).isNotEqualTo(solarArrayDTO2);
        solarArrayDTO1.setId(null);
        assertThat(solarArrayDTO1).isNotEqualTo(solarArrayDTO2);
    }
}

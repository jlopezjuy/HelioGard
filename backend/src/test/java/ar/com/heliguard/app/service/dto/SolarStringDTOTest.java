package ar.com.heliguard.app.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import ar.com.heliguard.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class SolarStringDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(SolarStringDTO.class);
        SolarStringDTO solarStringDTO1 = new SolarStringDTO();
        solarStringDTO1.setId(1L);
        SolarStringDTO solarStringDTO2 = new SolarStringDTO();
        assertThat(solarStringDTO1).isNotEqualTo(solarStringDTO2);
        solarStringDTO2.setId(solarStringDTO1.getId());
        assertThat(solarStringDTO1).isEqualTo(solarStringDTO2);
        solarStringDTO2.setId(2L);
        assertThat(solarStringDTO1).isNotEqualTo(solarStringDTO2);
        solarStringDTO1.setId(null);
        assertThat(solarStringDTO1).isNotEqualTo(solarStringDTO2);
    }
}

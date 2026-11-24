package ar.com.heliguard.app.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import ar.com.heliguard.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class SolarSectorDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(SolarSectorDTO.class);
        SolarSectorDTO solarSectorDTO1 = new SolarSectorDTO();
        solarSectorDTO1.setId(1L);
        SolarSectorDTO solarSectorDTO2 = new SolarSectorDTO();
        assertThat(solarSectorDTO1).isNotEqualTo(solarSectorDTO2);
        solarSectorDTO2.setId(solarSectorDTO1.getId());
        assertThat(solarSectorDTO1).isEqualTo(solarSectorDTO2);
        solarSectorDTO2.setId(2L);
        assertThat(solarSectorDTO1).isNotEqualTo(solarSectorDTO2);
        solarSectorDTO1.setId(null);
        assertThat(solarSectorDTO1).isNotEqualTo(solarSectorDTO2);
    }
}

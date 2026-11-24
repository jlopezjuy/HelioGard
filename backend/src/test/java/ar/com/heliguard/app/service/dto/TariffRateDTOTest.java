package ar.com.heliguard.app.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import ar.com.heliguard.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class TariffRateDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(TariffRateDTO.class);
        TariffRateDTO tariffRateDTO1 = new TariffRateDTO();
        tariffRateDTO1.setId(1L);
        TariffRateDTO tariffRateDTO2 = new TariffRateDTO();
        assertThat(tariffRateDTO1).isNotEqualTo(tariffRateDTO2);
        tariffRateDTO2.setId(tariffRateDTO1.getId());
        assertThat(tariffRateDTO1).isEqualTo(tariffRateDTO2);
        tariffRateDTO2.setId(2L);
        assertThat(tariffRateDTO1).isNotEqualTo(tariffRateDTO2);
        tariffRateDTO1.setId(null);
        assertThat(tariffRateDTO1).isNotEqualTo(tariffRateDTO2);
    }
}

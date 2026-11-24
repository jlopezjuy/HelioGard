package ar.com.heliguard.app.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import ar.com.heliguard.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class TariffPlanDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(TariffPlanDTO.class);
        TariffPlanDTO tariffPlanDTO1 = new TariffPlanDTO();
        tariffPlanDTO1.setId(1L);
        TariffPlanDTO tariffPlanDTO2 = new TariffPlanDTO();
        assertThat(tariffPlanDTO1).isNotEqualTo(tariffPlanDTO2);
        tariffPlanDTO2.setId(tariffPlanDTO1.getId());
        assertThat(tariffPlanDTO1).isEqualTo(tariffPlanDTO2);
        tariffPlanDTO2.setId(2L);
        assertThat(tariffPlanDTO1).isNotEqualTo(tariffPlanDTO2);
        tariffPlanDTO1.setId(null);
        assertThat(tariffPlanDTO1).isNotEqualTo(tariffPlanDTO2);
    }
}

package ar.com.heliguard.app.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import ar.com.heliguard.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class LossCauseEstimateDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(LossCauseEstimateDTO.class);
        LossCauseEstimateDTO lossCauseEstimateDTO1 = new LossCauseEstimateDTO();
        lossCauseEstimateDTO1.setId(1L);
        LossCauseEstimateDTO lossCauseEstimateDTO2 = new LossCauseEstimateDTO();
        assertThat(lossCauseEstimateDTO1).isNotEqualTo(lossCauseEstimateDTO2);
        lossCauseEstimateDTO2.setId(lossCauseEstimateDTO1.getId());
        assertThat(lossCauseEstimateDTO1).isEqualTo(lossCauseEstimateDTO2);
        lossCauseEstimateDTO2.setId(2L);
        assertThat(lossCauseEstimateDTO1).isNotEqualTo(lossCauseEstimateDTO2);
        lossCauseEstimateDTO1.setId(null);
        assertThat(lossCauseEstimateDTO1).isNotEqualTo(lossCauseEstimateDTO2);
    }
}

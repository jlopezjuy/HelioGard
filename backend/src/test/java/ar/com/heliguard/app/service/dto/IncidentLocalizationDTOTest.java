package ar.com.heliguard.app.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import ar.com.heliguard.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class IncidentLocalizationDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(IncidentLocalizationDTO.class);
        IncidentLocalizationDTO incidentLocalizationDTO1 = new IncidentLocalizationDTO();
        incidentLocalizationDTO1.setId(1L);
        IncidentLocalizationDTO incidentLocalizationDTO2 = new IncidentLocalizationDTO();
        assertThat(incidentLocalizationDTO1).isNotEqualTo(incidentLocalizationDTO2);
        incidentLocalizationDTO2.setId(incidentLocalizationDTO1.getId());
        assertThat(incidentLocalizationDTO1).isEqualTo(incidentLocalizationDTO2);
        incidentLocalizationDTO2.setId(2L);
        assertThat(incidentLocalizationDTO1).isNotEqualTo(incidentLocalizationDTO2);
        incidentLocalizationDTO1.setId(null);
        assertThat(incidentLocalizationDTO1).isNotEqualTo(incidentLocalizationDTO2);
    }
}

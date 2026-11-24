package ar.com.heliguard.app.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import ar.com.heliguard.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class IncidentEventDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(IncidentEventDTO.class);
        IncidentEventDTO incidentEventDTO1 = new IncidentEventDTO();
        incidentEventDTO1.setId(1L);
        IncidentEventDTO incidentEventDTO2 = new IncidentEventDTO();
        assertThat(incidentEventDTO1).isNotEqualTo(incidentEventDTO2);
        incidentEventDTO2.setId(incidentEventDTO1.getId());
        assertThat(incidentEventDTO1).isEqualTo(incidentEventDTO2);
        incidentEventDTO2.setId(2L);
        assertThat(incidentEventDTO1).isNotEqualTo(incidentEventDTO2);
        incidentEventDTO1.setId(null);
        assertThat(incidentEventDTO1).isNotEqualTo(incidentEventDTO2);
    }
}

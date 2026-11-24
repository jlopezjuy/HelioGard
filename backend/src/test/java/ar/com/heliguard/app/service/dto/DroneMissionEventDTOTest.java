package ar.com.heliguard.app.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import ar.com.heliguard.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class DroneMissionEventDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(DroneMissionEventDTO.class);
        DroneMissionEventDTO droneMissionEventDTO1 = new DroneMissionEventDTO();
        droneMissionEventDTO1.setId(1L);
        DroneMissionEventDTO droneMissionEventDTO2 = new DroneMissionEventDTO();
        assertThat(droneMissionEventDTO1).isNotEqualTo(droneMissionEventDTO2);
        droneMissionEventDTO2.setId(droneMissionEventDTO1.getId());
        assertThat(droneMissionEventDTO1).isEqualTo(droneMissionEventDTO2);
        droneMissionEventDTO2.setId(2L);
        assertThat(droneMissionEventDTO1).isNotEqualTo(droneMissionEventDTO2);
        droneMissionEventDTO1.setId(null);
        assertThat(droneMissionEventDTO1).isNotEqualTo(droneMissionEventDTO2);
    }
}

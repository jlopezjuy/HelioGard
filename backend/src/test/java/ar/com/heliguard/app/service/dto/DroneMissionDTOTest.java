package ar.com.heliguard.app.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import ar.com.heliguard.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class DroneMissionDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(DroneMissionDTO.class);
        DroneMissionDTO droneMissionDTO1 = new DroneMissionDTO();
        droneMissionDTO1.setId(1L);
        DroneMissionDTO droneMissionDTO2 = new DroneMissionDTO();
        assertThat(droneMissionDTO1).isNotEqualTo(droneMissionDTO2);
        droneMissionDTO2.setId(droneMissionDTO1.getId());
        assertThat(droneMissionDTO1).isEqualTo(droneMissionDTO2);
        droneMissionDTO2.setId(2L);
        assertThat(droneMissionDTO1).isNotEqualTo(droneMissionDTO2);
        droneMissionDTO1.setId(null);
        assertThat(droneMissionDTO1).isNotEqualTo(droneMissionDTO2);
    }
}

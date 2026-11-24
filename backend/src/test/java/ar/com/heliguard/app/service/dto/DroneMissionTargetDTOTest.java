package ar.com.heliguard.app.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import ar.com.heliguard.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class DroneMissionTargetDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(DroneMissionTargetDTO.class);
        DroneMissionTargetDTO droneMissionTargetDTO1 = new DroneMissionTargetDTO();
        droneMissionTargetDTO1.setId(1L);
        DroneMissionTargetDTO droneMissionTargetDTO2 = new DroneMissionTargetDTO();
        assertThat(droneMissionTargetDTO1).isNotEqualTo(droneMissionTargetDTO2);
        droneMissionTargetDTO2.setId(droneMissionTargetDTO1.getId());
        assertThat(droneMissionTargetDTO1).isEqualTo(droneMissionTargetDTO2);
        droneMissionTargetDTO2.setId(2L);
        assertThat(droneMissionTargetDTO1).isNotEqualTo(droneMissionTargetDTO2);
        droneMissionTargetDTO1.setId(null);
        assertThat(droneMissionTargetDTO1).isNotEqualTo(droneMissionTargetDTO2);
    }
}

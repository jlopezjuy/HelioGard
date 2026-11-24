package ar.com.heliguard.app.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import ar.com.heliguard.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class MissionSafetyCheckDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(MissionSafetyCheckDTO.class);
        MissionSafetyCheckDTO missionSafetyCheckDTO1 = new MissionSafetyCheckDTO();
        missionSafetyCheckDTO1.setId(1L);
        MissionSafetyCheckDTO missionSafetyCheckDTO2 = new MissionSafetyCheckDTO();
        assertThat(missionSafetyCheckDTO1).isNotEqualTo(missionSafetyCheckDTO2);
        missionSafetyCheckDTO2.setId(missionSafetyCheckDTO1.getId());
        assertThat(missionSafetyCheckDTO1).isEqualTo(missionSafetyCheckDTO2);
        missionSafetyCheckDTO2.setId(2L);
        assertThat(missionSafetyCheckDTO1).isNotEqualTo(missionSafetyCheckDTO2);
        missionSafetyCheckDTO1.setId(null);
        assertThat(missionSafetyCheckDTO1).isNotEqualTo(missionSafetyCheckDTO2);
    }
}

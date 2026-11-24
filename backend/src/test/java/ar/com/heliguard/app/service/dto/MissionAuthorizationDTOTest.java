package ar.com.heliguard.app.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import ar.com.heliguard.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class MissionAuthorizationDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(MissionAuthorizationDTO.class);
        MissionAuthorizationDTO missionAuthorizationDTO1 = new MissionAuthorizationDTO();
        missionAuthorizationDTO1.setId(1L);
        MissionAuthorizationDTO missionAuthorizationDTO2 = new MissionAuthorizationDTO();
        assertThat(missionAuthorizationDTO1).isNotEqualTo(missionAuthorizationDTO2);
        missionAuthorizationDTO2.setId(missionAuthorizationDTO1.getId());
        assertThat(missionAuthorizationDTO1).isEqualTo(missionAuthorizationDTO2);
        missionAuthorizationDTO2.setId(2L);
        assertThat(missionAuthorizationDTO1).isNotEqualTo(missionAuthorizationDTO2);
        missionAuthorizationDTO1.setId(null);
        assertThat(missionAuthorizationDTO1).isNotEqualTo(missionAuthorizationDTO2);
    }
}

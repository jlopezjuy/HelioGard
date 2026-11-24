package ar.com.heliguard.app.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import ar.com.heliguard.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class SecurityIncidentDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(SecurityIncidentDTO.class);
        SecurityIncidentDTO securityIncidentDTO1 = new SecurityIncidentDTO();
        securityIncidentDTO1.setId(1L);
        SecurityIncidentDTO securityIncidentDTO2 = new SecurityIncidentDTO();
        assertThat(securityIncidentDTO1).isNotEqualTo(securityIncidentDTO2);
        securityIncidentDTO2.setId(securityIncidentDTO1.getId());
        assertThat(securityIncidentDTO1).isEqualTo(securityIncidentDTO2);
        securityIncidentDTO2.setId(2L);
        assertThat(securityIncidentDTO1).isNotEqualTo(securityIncidentDTO2);
        securityIncidentDTO1.setId(null);
        assertThat(securityIncidentDTO1).isNotEqualTo(securityIncidentDTO2);
    }
}

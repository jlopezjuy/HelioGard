package ar.com.heliguard.app.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import ar.com.heliguard.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class SecurityCaseEventDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(SecurityCaseEventDTO.class);
        SecurityCaseEventDTO securityCaseEventDTO1 = new SecurityCaseEventDTO();
        securityCaseEventDTO1.setId(1L);
        SecurityCaseEventDTO securityCaseEventDTO2 = new SecurityCaseEventDTO();
        assertThat(securityCaseEventDTO1).isNotEqualTo(securityCaseEventDTO2);
        securityCaseEventDTO2.setId(securityCaseEventDTO1.getId());
        assertThat(securityCaseEventDTO1).isEqualTo(securityCaseEventDTO2);
        securityCaseEventDTO2.setId(2L);
        assertThat(securityCaseEventDTO1).isNotEqualTo(securityCaseEventDTO2);
        securityCaseEventDTO1.setId(null);
        assertThat(securityCaseEventDTO1).isNotEqualTo(securityCaseEventDTO2);
    }
}

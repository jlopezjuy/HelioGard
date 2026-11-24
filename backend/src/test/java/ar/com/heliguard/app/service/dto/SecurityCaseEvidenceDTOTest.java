package ar.com.heliguard.app.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import ar.com.heliguard.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class SecurityCaseEvidenceDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(SecurityCaseEvidenceDTO.class);
        SecurityCaseEvidenceDTO securityCaseEvidenceDTO1 = new SecurityCaseEvidenceDTO();
        securityCaseEvidenceDTO1.setId(1L);
        SecurityCaseEvidenceDTO securityCaseEvidenceDTO2 = new SecurityCaseEvidenceDTO();
        assertThat(securityCaseEvidenceDTO1).isNotEqualTo(securityCaseEvidenceDTO2);
        securityCaseEvidenceDTO2.setId(securityCaseEvidenceDTO1.getId());
        assertThat(securityCaseEvidenceDTO1).isEqualTo(securityCaseEvidenceDTO2);
        securityCaseEvidenceDTO2.setId(2L);
        assertThat(securityCaseEvidenceDTO1).isNotEqualTo(securityCaseEvidenceDTO2);
        securityCaseEvidenceDTO1.setId(null);
        assertThat(securityCaseEvidenceDTO1).isNotEqualTo(securityCaseEvidenceDTO2);
    }
}

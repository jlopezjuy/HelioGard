package ar.com.heliguard.app.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import ar.com.heliguard.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class VerificationResultDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(VerificationResultDTO.class);
        VerificationResultDTO verificationResultDTO1 = new VerificationResultDTO();
        verificationResultDTO1.setId(1L);
        VerificationResultDTO verificationResultDTO2 = new VerificationResultDTO();
        assertThat(verificationResultDTO1).isNotEqualTo(verificationResultDTO2);
        verificationResultDTO2.setId(verificationResultDTO1.getId());
        assertThat(verificationResultDTO1).isEqualTo(verificationResultDTO2);
        verificationResultDTO2.setId(2L);
        assertThat(verificationResultDTO1).isNotEqualTo(verificationResultDTO2);
        verificationResultDTO1.setId(null);
        assertThat(verificationResultDTO1).isNotEqualTo(verificationResultDTO2);
    }
}

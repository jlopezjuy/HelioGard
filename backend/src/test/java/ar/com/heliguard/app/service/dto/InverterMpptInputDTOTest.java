package ar.com.heliguard.app.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import ar.com.heliguard.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class InverterMpptInputDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(InverterMpptInputDTO.class);
        InverterMpptInputDTO inverterMpptInputDTO1 = new InverterMpptInputDTO();
        inverterMpptInputDTO1.setId(1L);
        InverterMpptInputDTO inverterMpptInputDTO2 = new InverterMpptInputDTO();
        assertThat(inverterMpptInputDTO1).isNotEqualTo(inverterMpptInputDTO2);
        inverterMpptInputDTO2.setId(inverterMpptInputDTO1.getId());
        assertThat(inverterMpptInputDTO1).isEqualTo(inverterMpptInputDTO2);
        inverterMpptInputDTO2.setId(2L);
        assertThat(inverterMpptInputDTO1).isNotEqualTo(inverterMpptInputDTO2);
        inverterMpptInputDTO1.setId(null);
        assertThat(inverterMpptInputDTO1).isNotEqualTo(inverterMpptInputDTO2);
    }
}

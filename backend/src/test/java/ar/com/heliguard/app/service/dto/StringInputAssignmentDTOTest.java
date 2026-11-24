package ar.com.heliguard.app.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import ar.com.heliguard.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class StringInputAssignmentDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(StringInputAssignmentDTO.class);
        StringInputAssignmentDTO stringInputAssignmentDTO1 = new StringInputAssignmentDTO();
        stringInputAssignmentDTO1.setId(1L);
        StringInputAssignmentDTO stringInputAssignmentDTO2 = new StringInputAssignmentDTO();
        assertThat(stringInputAssignmentDTO1).isNotEqualTo(stringInputAssignmentDTO2);
        stringInputAssignmentDTO2.setId(stringInputAssignmentDTO1.getId());
        assertThat(stringInputAssignmentDTO1).isEqualTo(stringInputAssignmentDTO2);
        stringInputAssignmentDTO2.setId(2L);
        assertThat(stringInputAssignmentDTO1).isNotEqualTo(stringInputAssignmentDTO2);
        stringInputAssignmentDTO1.setId(null);
        assertThat(stringInputAssignmentDTO1).isNotEqualTo(stringInputAssignmentDTO2);
    }
}

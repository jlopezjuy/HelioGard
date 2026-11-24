package ar.com.heliguard.app.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import ar.com.heliguard.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class AuditAnswerDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(AuditAnswerDTO.class);
        AuditAnswerDTO auditAnswerDTO1 = new AuditAnswerDTO();
        auditAnswerDTO1.setId(1L);
        AuditAnswerDTO auditAnswerDTO2 = new AuditAnswerDTO();
        assertThat(auditAnswerDTO1).isNotEqualTo(auditAnswerDTO2);
        auditAnswerDTO2.setId(auditAnswerDTO1.getId());
        assertThat(auditAnswerDTO1).isEqualTo(auditAnswerDTO2);
        auditAnswerDTO2.setId(2L);
        assertThat(auditAnswerDTO1).isNotEqualTo(auditAnswerDTO2);
        auditAnswerDTO1.setId(null);
        assertThat(auditAnswerDTO1).isNotEqualTo(auditAnswerDTO2);
    }
}

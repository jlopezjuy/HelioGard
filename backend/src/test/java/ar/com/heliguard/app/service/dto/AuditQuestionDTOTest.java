package ar.com.heliguard.app.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import ar.com.heliguard.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class AuditQuestionDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(AuditQuestionDTO.class);
        AuditQuestionDTO auditQuestionDTO1 = new AuditQuestionDTO();
        auditQuestionDTO1.setId(1L);
        AuditQuestionDTO auditQuestionDTO2 = new AuditQuestionDTO();
        assertThat(auditQuestionDTO1).isNotEqualTo(auditQuestionDTO2);
        auditQuestionDTO2.setId(auditQuestionDTO1.getId());
        assertThat(auditQuestionDTO1).isEqualTo(auditQuestionDTO2);
        auditQuestionDTO2.setId(2L);
        assertThat(auditQuestionDTO1).isNotEqualTo(auditQuestionDTO2);
        auditQuestionDTO1.setId(null);
        assertThat(auditQuestionDTO1).isNotEqualTo(auditQuestionDTO2);
    }
}

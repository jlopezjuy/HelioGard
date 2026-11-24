package ar.com.heliguard.app.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import ar.com.heliguard.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class AuditTemplateDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(AuditTemplateDTO.class);
        AuditTemplateDTO auditTemplateDTO1 = new AuditTemplateDTO();
        auditTemplateDTO1.setId(1L);
        AuditTemplateDTO auditTemplateDTO2 = new AuditTemplateDTO();
        assertThat(auditTemplateDTO1).isNotEqualTo(auditTemplateDTO2);
        auditTemplateDTO2.setId(auditTemplateDTO1.getId());
        assertThat(auditTemplateDTO1).isEqualTo(auditTemplateDTO2);
        auditTemplateDTO2.setId(2L);
        assertThat(auditTemplateDTO1).isNotEqualTo(auditTemplateDTO2);
        auditTemplateDTO1.setId(null);
        assertThat(auditTemplateDTO1).isNotEqualTo(auditTemplateDTO2);
    }
}

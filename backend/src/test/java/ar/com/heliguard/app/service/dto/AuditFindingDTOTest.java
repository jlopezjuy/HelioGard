package ar.com.heliguard.app.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import ar.com.heliguard.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class AuditFindingDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(AuditFindingDTO.class);
        AuditFindingDTO auditFindingDTO1 = new AuditFindingDTO();
        auditFindingDTO1.setId(1L);
        AuditFindingDTO auditFindingDTO2 = new AuditFindingDTO();
        assertThat(auditFindingDTO1).isNotEqualTo(auditFindingDTO2);
        auditFindingDTO2.setId(auditFindingDTO1.getId());
        assertThat(auditFindingDTO1).isEqualTo(auditFindingDTO2);
        auditFindingDTO2.setId(2L);
        assertThat(auditFindingDTO1).isNotEqualTo(auditFindingDTO2);
        auditFindingDTO1.setId(null);
        assertThat(auditFindingDTO1).isNotEqualTo(auditFindingDTO2);
    }
}

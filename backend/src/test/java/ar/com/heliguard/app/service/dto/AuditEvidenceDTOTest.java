package ar.com.heliguard.app.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import ar.com.heliguard.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class AuditEvidenceDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(AuditEvidenceDTO.class);
        AuditEvidenceDTO auditEvidenceDTO1 = new AuditEvidenceDTO();
        auditEvidenceDTO1.setId(1L);
        AuditEvidenceDTO auditEvidenceDTO2 = new AuditEvidenceDTO();
        assertThat(auditEvidenceDTO1).isNotEqualTo(auditEvidenceDTO2);
        auditEvidenceDTO2.setId(auditEvidenceDTO1.getId());
        assertThat(auditEvidenceDTO1).isEqualTo(auditEvidenceDTO2);
        auditEvidenceDTO2.setId(2L);
        assertThat(auditEvidenceDTO1).isNotEqualTo(auditEvidenceDTO2);
        auditEvidenceDTO1.setId(null);
        assertThat(auditEvidenceDTO1).isNotEqualTo(auditEvidenceDTO2);
    }
}

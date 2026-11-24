package ar.com.heliguard.app.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import ar.com.heliguard.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class AuditReportSignatureDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(AuditReportSignatureDTO.class);
        AuditReportSignatureDTO auditReportSignatureDTO1 = new AuditReportSignatureDTO();
        auditReportSignatureDTO1.setId(1L);
        AuditReportSignatureDTO auditReportSignatureDTO2 = new AuditReportSignatureDTO();
        assertThat(auditReportSignatureDTO1).isNotEqualTo(auditReportSignatureDTO2);
        auditReportSignatureDTO2.setId(auditReportSignatureDTO1.getId());
        assertThat(auditReportSignatureDTO1).isEqualTo(auditReportSignatureDTO2);
        auditReportSignatureDTO2.setId(2L);
        assertThat(auditReportSignatureDTO1).isNotEqualTo(auditReportSignatureDTO2);
        auditReportSignatureDTO1.setId(null);
        assertThat(auditReportSignatureDTO1).isNotEqualTo(auditReportSignatureDTO2);
    }
}

package ar.com.heliguard.app.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import ar.com.heliguard.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class AuditReportDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(AuditReportDTO.class);
        AuditReportDTO auditReportDTO1 = new AuditReportDTO();
        auditReportDTO1.setId(1L);
        AuditReportDTO auditReportDTO2 = new AuditReportDTO();
        assertThat(auditReportDTO1).isNotEqualTo(auditReportDTO2);
        auditReportDTO2.setId(auditReportDTO1.getId());
        assertThat(auditReportDTO1).isEqualTo(auditReportDTO2);
        auditReportDTO2.setId(2L);
        assertThat(auditReportDTO1).isNotEqualTo(auditReportDTO2);
        auditReportDTO1.setId(null);
        assertThat(auditReportDTO1).isNotEqualTo(auditReportDTO2);
    }
}

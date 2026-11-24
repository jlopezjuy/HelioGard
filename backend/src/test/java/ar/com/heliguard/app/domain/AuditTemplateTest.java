package ar.com.heliguard.app.domain;

import static ar.com.heliguard.app.domain.AuditTemplateTestSamples.*;
import static ar.com.heliguard.app.domain.OrganizationTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import ar.com.heliguard.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class AuditTemplateTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(AuditTemplate.class);
        AuditTemplate auditTemplate1 = getAuditTemplateSample1();
        AuditTemplate auditTemplate2 = new AuditTemplate();
        assertThat(auditTemplate1).isNotEqualTo(auditTemplate2);

        auditTemplate2.setId(auditTemplate1.getId());
        assertThat(auditTemplate1).isEqualTo(auditTemplate2);

        auditTemplate2 = getAuditTemplateSample2();
        assertThat(auditTemplate1).isNotEqualTo(auditTemplate2);
    }

    @Test
    void organizationTest() {
        AuditTemplate auditTemplate = getAuditTemplateRandomSampleGenerator();
        Organization organizationBack = getOrganizationRandomSampleGenerator();

        auditTemplate.setOrganization(organizationBack);
        assertThat(auditTemplate.getOrganization()).isEqualTo(organizationBack);

        auditTemplate.organization(null);
        assertThat(auditTemplate.getOrganization()).isNull();
    }
}

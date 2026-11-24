package ar.com.heliguard.app.domain;

import static ar.com.heliguard.app.domain.AuditQuestionTestSamples.*;
import static ar.com.heliguard.app.domain.AuditTemplateTestSamples.*;
import static ar.com.heliguard.app.domain.OrganizationTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import ar.com.heliguard.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class AuditQuestionTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(AuditQuestion.class);
        AuditQuestion auditQuestion1 = getAuditQuestionSample1();
        AuditQuestion auditQuestion2 = new AuditQuestion();
        assertThat(auditQuestion1).isNotEqualTo(auditQuestion2);

        auditQuestion2.setId(auditQuestion1.getId());
        assertThat(auditQuestion1).isEqualTo(auditQuestion2);

        auditQuestion2 = getAuditQuestionSample2();
        assertThat(auditQuestion1).isNotEqualTo(auditQuestion2);
    }

    @Test
    void organizationTest() {
        AuditQuestion auditQuestion = getAuditQuestionRandomSampleGenerator();
        Organization organizationBack = getOrganizationRandomSampleGenerator();

        auditQuestion.setOrganization(organizationBack);
        assertThat(auditQuestion.getOrganization()).isEqualTo(organizationBack);

        auditQuestion.organization(null);
        assertThat(auditQuestion.getOrganization()).isNull();
    }

    @Test
    void auditTemplateTest() {
        AuditQuestion auditQuestion = getAuditQuestionRandomSampleGenerator();
        AuditTemplate auditTemplateBack = getAuditTemplateRandomSampleGenerator();

        auditQuestion.setAuditTemplate(auditTemplateBack);
        assertThat(auditQuestion.getAuditTemplate()).isEqualTo(auditTemplateBack);

        auditQuestion.auditTemplate(null);
        assertThat(auditQuestion.getAuditTemplate()).isNull();
    }
}

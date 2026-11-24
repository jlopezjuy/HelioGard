package ar.com.heliguard.app.domain;

import static ar.com.heliguard.app.domain.AuditAnswerTestSamples.*;
import static ar.com.heliguard.app.domain.AuditQuestionTestSamples.*;
import static ar.com.heliguard.app.domain.AuditReportTestSamples.*;
import static ar.com.heliguard.app.domain.OrganizationTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import ar.com.heliguard.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class AuditAnswerTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(AuditAnswer.class);
        AuditAnswer auditAnswer1 = getAuditAnswerSample1();
        AuditAnswer auditAnswer2 = new AuditAnswer();
        assertThat(auditAnswer1).isNotEqualTo(auditAnswer2);

        auditAnswer2.setId(auditAnswer1.getId());
        assertThat(auditAnswer1).isEqualTo(auditAnswer2);

        auditAnswer2 = getAuditAnswerSample2();
        assertThat(auditAnswer1).isNotEqualTo(auditAnswer2);
    }

    @Test
    void organizationTest() {
        AuditAnswer auditAnswer = getAuditAnswerRandomSampleGenerator();
        Organization organizationBack = getOrganizationRandomSampleGenerator();

        auditAnswer.setOrganization(organizationBack);
        assertThat(auditAnswer.getOrganization()).isEqualTo(organizationBack);

        auditAnswer.organization(null);
        assertThat(auditAnswer.getOrganization()).isNull();
    }

    @Test
    void auditReportTest() {
        AuditAnswer auditAnswer = getAuditAnswerRandomSampleGenerator();
        AuditReport auditReportBack = getAuditReportRandomSampleGenerator();

        auditAnswer.setAuditReport(auditReportBack);
        assertThat(auditAnswer.getAuditReport()).isEqualTo(auditReportBack);

        auditAnswer.auditReport(null);
        assertThat(auditAnswer.getAuditReport()).isNull();
    }

    @Test
    void auditQuestionTest() {
        AuditAnswer auditAnswer = getAuditAnswerRandomSampleGenerator();
        AuditQuestion auditQuestionBack = getAuditQuestionRandomSampleGenerator();

        auditAnswer.setAuditQuestion(auditQuestionBack);
        assertThat(auditAnswer.getAuditQuestion()).isEqualTo(auditQuestionBack);

        auditAnswer.auditQuestion(null);
        assertThat(auditAnswer.getAuditQuestion()).isNull();
    }
}

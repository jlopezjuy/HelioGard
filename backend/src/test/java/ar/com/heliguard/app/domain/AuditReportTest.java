package ar.com.heliguard.app.domain;

import static ar.com.heliguard.app.domain.AuditReportTestSamples.*;
import static ar.com.heliguard.app.domain.AuditTemplateTestSamples.*;
import static ar.com.heliguard.app.domain.InstallationTestSamples.*;
import static ar.com.heliguard.app.domain.OrganizationMembershipTestSamples.*;
import static ar.com.heliguard.app.domain.OrganizationTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import ar.com.heliguard.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class AuditReportTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(AuditReport.class);
        AuditReport auditReport1 = getAuditReportSample1();
        AuditReport auditReport2 = new AuditReport();
        assertThat(auditReport1).isNotEqualTo(auditReport2);

        auditReport2.setId(auditReport1.getId());
        assertThat(auditReport1).isEqualTo(auditReport2);

        auditReport2 = getAuditReportSample2();
        assertThat(auditReport1).isNotEqualTo(auditReport2);
    }

    @Test
    void organizationTest() {
        AuditReport auditReport = getAuditReportRandomSampleGenerator();
        Organization organizationBack = getOrganizationRandomSampleGenerator();

        auditReport.setOrganization(organizationBack);
        assertThat(auditReport.getOrganization()).isEqualTo(organizationBack);

        auditReport.organization(null);
        assertThat(auditReport.getOrganization()).isNull();
    }

    @Test
    void installationTest() {
        AuditReport auditReport = getAuditReportRandomSampleGenerator();
        Installation installationBack = getInstallationRandomSampleGenerator();

        auditReport.setInstallation(installationBack);
        assertThat(auditReport.getInstallation()).isEqualTo(installationBack);

        auditReport.installation(null);
        assertThat(auditReport.getInstallation()).isNull();
    }

    @Test
    void auditTemplateTest() {
        AuditReport auditReport = getAuditReportRandomSampleGenerator();
        AuditTemplate auditTemplateBack = getAuditTemplateRandomSampleGenerator();

        auditReport.setAuditTemplate(auditTemplateBack);
        assertThat(auditReport.getAuditTemplate()).isEqualTo(auditTemplateBack);

        auditReport.auditTemplate(null);
        assertThat(auditReport.getAuditTemplate()).isNull();
    }

    @Test
    void auditorMembershipTest() {
        AuditReport auditReport = getAuditReportRandomSampleGenerator();
        OrganizationMembership organizationMembershipBack = getOrganizationMembershipRandomSampleGenerator();

        auditReport.setAuditorMembership(organizationMembershipBack);
        assertThat(auditReport.getAuditorMembership()).isEqualTo(organizationMembershipBack);

        auditReport.auditorMembership(null);
        assertThat(auditReport.getAuditorMembership()).isNull();
    }
}

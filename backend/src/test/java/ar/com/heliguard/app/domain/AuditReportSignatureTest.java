package ar.com.heliguard.app.domain;

import static ar.com.heliguard.app.domain.AuditReportSignatureTestSamples.*;
import static ar.com.heliguard.app.domain.AuditReportTestSamples.*;
import static ar.com.heliguard.app.domain.OrganizationMembershipTestSamples.*;
import static ar.com.heliguard.app.domain.OrganizationTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import ar.com.heliguard.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class AuditReportSignatureTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(AuditReportSignature.class);
        AuditReportSignature auditReportSignature1 = getAuditReportSignatureSample1();
        AuditReportSignature auditReportSignature2 = new AuditReportSignature();
        assertThat(auditReportSignature1).isNotEqualTo(auditReportSignature2);

        auditReportSignature2.setId(auditReportSignature1.getId());
        assertThat(auditReportSignature1).isEqualTo(auditReportSignature2);

        auditReportSignature2 = getAuditReportSignatureSample2();
        assertThat(auditReportSignature1).isNotEqualTo(auditReportSignature2);
    }

    @Test
    void organizationTest() {
        AuditReportSignature auditReportSignature = getAuditReportSignatureRandomSampleGenerator();
        Organization organizationBack = getOrganizationRandomSampleGenerator();

        auditReportSignature.setOrganization(organizationBack);
        assertThat(auditReportSignature.getOrganization()).isEqualTo(organizationBack);

        auditReportSignature.organization(null);
        assertThat(auditReportSignature.getOrganization()).isNull();
    }

    @Test
    void auditReportTest() {
        AuditReportSignature auditReportSignature = getAuditReportSignatureRandomSampleGenerator();
        AuditReport auditReportBack = getAuditReportRandomSampleGenerator();

        auditReportSignature.setAuditReport(auditReportBack);
        assertThat(auditReportSignature.getAuditReport()).isEqualTo(auditReportBack);

        auditReportSignature.auditReport(null);
        assertThat(auditReportSignature.getAuditReport()).isNull();
    }

    @Test
    void signerMembershipTest() {
        AuditReportSignature auditReportSignature = getAuditReportSignatureRandomSampleGenerator();
        OrganizationMembership organizationMembershipBack = getOrganizationMembershipRandomSampleGenerator();

        auditReportSignature.setSignerMembership(organizationMembershipBack);
        assertThat(auditReportSignature.getSignerMembership()).isEqualTo(organizationMembershipBack);

        auditReportSignature.signerMembership(null);
        assertThat(auditReportSignature.getSignerMembership()).isNull();
    }
}

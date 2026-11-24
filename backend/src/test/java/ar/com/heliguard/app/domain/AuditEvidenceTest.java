package ar.com.heliguard.app.domain;

import static ar.com.heliguard.app.domain.AuditEvidenceTestSamples.*;
import static ar.com.heliguard.app.domain.AuditFindingTestSamples.*;
import static ar.com.heliguard.app.domain.OrganizationTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import ar.com.heliguard.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class AuditEvidenceTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(AuditEvidence.class);
        AuditEvidence auditEvidence1 = getAuditEvidenceSample1();
        AuditEvidence auditEvidence2 = new AuditEvidence();
        assertThat(auditEvidence1).isNotEqualTo(auditEvidence2);

        auditEvidence2.setId(auditEvidence1.getId());
        assertThat(auditEvidence1).isEqualTo(auditEvidence2);

        auditEvidence2 = getAuditEvidenceSample2();
        assertThat(auditEvidence1).isNotEqualTo(auditEvidence2);
    }

    @Test
    void organizationTest() {
        AuditEvidence auditEvidence = getAuditEvidenceRandomSampleGenerator();
        Organization organizationBack = getOrganizationRandomSampleGenerator();

        auditEvidence.setOrganization(organizationBack);
        assertThat(auditEvidence.getOrganization()).isEqualTo(organizationBack);

        auditEvidence.organization(null);
        assertThat(auditEvidence.getOrganization()).isNull();
    }

    @Test
    void findingTest() {
        AuditEvidence auditEvidence = getAuditEvidenceRandomSampleGenerator();
        AuditFinding auditFindingBack = getAuditFindingRandomSampleGenerator();

        auditEvidence.setFinding(auditFindingBack);
        assertThat(auditEvidence.getFinding()).isEqualTo(auditFindingBack);

        auditEvidence.finding(null);
        assertThat(auditEvidence.getFinding()).isNull();
    }
}

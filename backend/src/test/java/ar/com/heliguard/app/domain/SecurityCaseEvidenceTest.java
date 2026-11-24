package ar.com.heliguard.app.domain;

import static ar.com.heliguard.app.domain.DataProvenanceTestSamples.*;
import static ar.com.heliguard.app.domain.IntegrityAssessmentTestSamples.*;
import static ar.com.heliguard.app.domain.OrganizationTestSamples.*;
import static ar.com.heliguard.app.domain.SecurityCaseEvidenceTestSamples.*;
import static ar.com.heliguard.app.domain.SecurityIncidentTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import ar.com.heliguard.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class SecurityCaseEvidenceTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(SecurityCaseEvidence.class);
        SecurityCaseEvidence securityCaseEvidence1 = getSecurityCaseEvidenceSample1();
        SecurityCaseEvidence securityCaseEvidence2 = new SecurityCaseEvidence();
        assertThat(securityCaseEvidence1).isNotEqualTo(securityCaseEvidence2);

        securityCaseEvidence2.setId(securityCaseEvidence1.getId());
        assertThat(securityCaseEvidence1).isEqualTo(securityCaseEvidence2);

        securityCaseEvidence2 = getSecurityCaseEvidenceSample2();
        assertThat(securityCaseEvidence1).isNotEqualTo(securityCaseEvidence2);
    }

    @Test
    void organizationTest() {
        SecurityCaseEvidence securityCaseEvidence = getSecurityCaseEvidenceRandomSampleGenerator();
        Organization organizationBack = getOrganizationRandomSampleGenerator();

        securityCaseEvidence.setOrganization(organizationBack);
        assertThat(securityCaseEvidence.getOrganization()).isEqualTo(organizationBack);

        securityCaseEvidence.organization(null);
        assertThat(securityCaseEvidence.getOrganization()).isNull();
    }

    @Test
    void securityIncidentTest() {
        SecurityCaseEvidence securityCaseEvidence = getSecurityCaseEvidenceRandomSampleGenerator();
        SecurityIncident securityIncidentBack = getSecurityIncidentRandomSampleGenerator();

        securityCaseEvidence.setSecurityIncident(securityIncidentBack);
        assertThat(securityCaseEvidence.getSecurityIncident()).isEqualTo(securityIncidentBack);

        securityCaseEvidence.securityIncident(null);
        assertThat(securityCaseEvidence.getSecurityIncident()).isNull();
    }

    @Test
    void integrityAssessmentTest() {
        SecurityCaseEvidence securityCaseEvidence = getSecurityCaseEvidenceRandomSampleGenerator();
        IntegrityAssessment integrityAssessmentBack = getIntegrityAssessmentRandomSampleGenerator();

        securityCaseEvidence.setIntegrityAssessment(integrityAssessmentBack);
        assertThat(securityCaseEvidence.getIntegrityAssessment()).isEqualTo(integrityAssessmentBack);

        securityCaseEvidence.integrityAssessment(null);
        assertThat(securityCaseEvidence.getIntegrityAssessment()).isNull();
    }

    @Test
    void dataProvenanceTest() {
        SecurityCaseEvidence securityCaseEvidence = getSecurityCaseEvidenceRandomSampleGenerator();
        DataProvenance dataProvenanceBack = getDataProvenanceRandomSampleGenerator();

        securityCaseEvidence.setDataProvenance(dataProvenanceBack);
        assertThat(securityCaseEvidence.getDataProvenance()).isEqualTo(dataProvenanceBack);

        securityCaseEvidence.dataProvenance(null);
        assertThat(securityCaseEvidence.getDataProvenance()).isNull();
    }
}

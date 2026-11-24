package ar.com.heliguard.app.domain;

import static ar.com.heliguard.app.domain.AuditReportTestSamples.*;
import static ar.com.heliguard.app.domain.InstallationTestSamples.*;
import static ar.com.heliguard.app.domain.MaintenancePlanVersionTestSamples.*;
import static ar.com.heliguard.app.domain.OrganizationTestSamples.*;
import static ar.com.heliguard.app.domain.PlanEligibilityAssessmentTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import ar.com.heliguard.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class PlanEligibilityAssessmentTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(PlanEligibilityAssessment.class);
        PlanEligibilityAssessment planEligibilityAssessment1 = getPlanEligibilityAssessmentSample1();
        PlanEligibilityAssessment planEligibilityAssessment2 = new PlanEligibilityAssessment();
        assertThat(planEligibilityAssessment1).isNotEqualTo(planEligibilityAssessment2);

        planEligibilityAssessment2.setId(planEligibilityAssessment1.getId());
        assertThat(planEligibilityAssessment1).isEqualTo(planEligibilityAssessment2);

        planEligibilityAssessment2 = getPlanEligibilityAssessmentSample2();
        assertThat(planEligibilityAssessment1).isNotEqualTo(planEligibilityAssessment2);
    }

    @Test
    void organizationTest() {
        PlanEligibilityAssessment planEligibilityAssessment = getPlanEligibilityAssessmentRandomSampleGenerator();
        Organization organizationBack = getOrganizationRandomSampleGenerator();

        planEligibilityAssessment.setOrganization(organizationBack);
        assertThat(planEligibilityAssessment.getOrganization()).isEqualTo(organizationBack);

        planEligibilityAssessment.organization(null);
        assertThat(planEligibilityAssessment.getOrganization()).isNull();
    }

    @Test
    void installationTest() {
        PlanEligibilityAssessment planEligibilityAssessment = getPlanEligibilityAssessmentRandomSampleGenerator();
        Installation installationBack = getInstallationRandomSampleGenerator();

        planEligibilityAssessment.setInstallation(installationBack);
        assertThat(planEligibilityAssessment.getInstallation()).isEqualTo(installationBack);

        planEligibilityAssessment.installation(null);
        assertThat(planEligibilityAssessment.getInstallation()).isNull();
    }

    @Test
    void auditReportTest() {
        PlanEligibilityAssessment planEligibilityAssessment = getPlanEligibilityAssessmentRandomSampleGenerator();
        AuditReport auditReportBack = getAuditReportRandomSampleGenerator();

        planEligibilityAssessment.setAuditReport(auditReportBack);
        assertThat(planEligibilityAssessment.getAuditReport()).isEqualTo(auditReportBack);

        planEligibilityAssessment.auditReport(null);
        assertThat(planEligibilityAssessment.getAuditReport()).isNull();
    }

    @Test
    void planVersionTest() {
        PlanEligibilityAssessment planEligibilityAssessment = getPlanEligibilityAssessmentRandomSampleGenerator();
        MaintenancePlanVersion maintenancePlanVersionBack = getMaintenancePlanVersionRandomSampleGenerator();

        planEligibilityAssessment.setPlanVersion(maintenancePlanVersionBack);
        assertThat(planEligibilityAssessment.getPlanVersion()).isEqualTo(maintenancePlanVersionBack);

        planEligibilityAssessment.planVersion(null);
        assertThat(planEligibilityAssessment.getPlanVersion()).isNull();
    }
}

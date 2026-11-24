package ar.com.heliguard.app.domain;

import static ar.com.heliguard.app.domain.InstallationTestSamples.*;
import static ar.com.heliguard.app.domain.MaintenancePlanVersionTestSamples.*;
import static ar.com.heliguard.app.domain.OrganizationTestSamples.*;
import static ar.com.heliguard.app.domain.PlanSubscriptionTestSamples.*;
import static ar.com.heliguard.app.domain.ServiceAgreementTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import ar.com.heliguard.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class PlanSubscriptionTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(PlanSubscription.class);
        PlanSubscription planSubscription1 = getPlanSubscriptionSample1();
        PlanSubscription planSubscription2 = new PlanSubscription();
        assertThat(planSubscription1).isNotEqualTo(planSubscription2);

        planSubscription2.setId(planSubscription1.getId());
        assertThat(planSubscription1).isEqualTo(planSubscription2);

        planSubscription2 = getPlanSubscriptionSample2();
        assertThat(planSubscription1).isNotEqualTo(planSubscription2);
    }

    @Test
    void organizationTest() {
        PlanSubscription planSubscription = getPlanSubscriptionRandomSampleGenerator();
        Organization organizationBack = getOrganizationRandomSampleGenerator();

        planSubscription.setOrganization(organizationBack);
        assertThat(planSubscription.getOrganization()).isEqualTo(organizationBack);

        planSubscription.organization(null);
        assertThat(planSubscription.getOrganization()).isNull();
    }

    @Test
    void installationTest() {
        PlanSubscription planSubscription = getPlanSubscriptionRandomSampleGenerator();
        Installation installationBack = getInstallationRandomSampleGenerator();

        planSubscription.setInstallation(installationBack);
        assertThat(planSubscription.getInstallation()).isEqualTo(installationBack);

        planSubscription.installation(null);
        assertThat(planSubscription.getInstallation()).isNull();
    }

    @Test
    void planVersionTest() {
        PlanSubscription planSubscription = getPlanSubscriptionRandomSampleGenerator();
        MaintenancePlanVersion maintenancePlanVersionBack = getMaintenancePlanVersionRandomSampleGenerator();

        planSubscription.setPlanVersion(maintenancePlanVersionBack);
        assertThat(planSubscription.getPlanVersion()).isEqualTo(maintenancePlanVersionBack);

        planSubscription.planVersion(null);
        assertThat(planSubscription.getPlanVersion()).isNull();
    }

    @Test
    void serviceAgreementTest() {
        PlanSubscription planSubscription = getPlanSubscriptionRandomSampleGenerator();
        ServiceAgreement serviceAgreementBack = getServiceAgreementRandomSampleGenerator();

        planSubscription.setServiceAgreement(serviceAgreementBack);
        assertThat(planSubscription.getServiceAgreement()).isEqualTo(serviceAgreementBack);

        planSubscription.serviceAgreement(null);
        assertThat(planSubscription.getServiceAgreement()).isNull();
    }
}

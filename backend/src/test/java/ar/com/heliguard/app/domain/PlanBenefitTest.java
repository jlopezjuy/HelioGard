package ar.com.heliguard.app.domain;

import static ar.com.heliguard.app.domain.MaintenancePlanVersionTestSamples.*;
import static ar.com.heliguard.app.domain.OrganizationTestSamples.*;
import static ar.com.heliguard.app.domain.PlanBenefitTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import ar.com.heliguard.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class PlanBenefitTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(PlanBenefit.class);
        PlanBenefit planBenefit1 = getPlanBenefitSample1();
        PlanBenefit planBenefit2 = new PlanBenefit();
        assertThat(planBenefit1).isNotEqualTo(planBenefit2);

        planBenefit2.setId(planBenefit1.getId());
        assertThat(planBenefit1).isEqualTo(planBenefit2);

        planBenefit2 = getPlanBenefitSample2();
        assertThat(planBenefit1).isNotEqualTo(planBenefit2);
    }

    @Test
    void organizationTest() {
        PlanBenefit planBenefit = getPlanBenefitRandomSampleGenerator();
        Organization organizationBack = getOrganizationRandomSampleGenerator();

        planBenefit.setOrganization(organizationBack);
        assertThat(planBenefit.getOrganization()).isEqualTo(organizationBack);

        planBenefit.organization(null);
        assertThat(planBenefit.getOrganization()).isNull();
    }

    @Test
    void planVersionTest() {
        PlanBenefit planBenefit = getPlanBenefitRandomSampleGenerator();
        MaintenancePlanVersion maintenancePlanVersionBack = getMaintenancePlanVersionRandomSampleGenerator();

        planBenefit.setPlanVersion(maintenancePlanVersionBack);
        assertThat(planBenefit.getPlanVersion()).isEqualTo(maintenancePlanVersionBack);

        planBenefit.planVersion(null);
        assertThat(planBenefit.getPlanVersion()).isNull();
    }
}

package ar.com.heliguard.app.domain;

import static ar.com.heliguard.app.domain.MaintenancePlanTestSamples.*;
import static ar.com.heliguard.app.domain.OrganizationTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import ar.com.heliguard.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class MaintenancePlanTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(MaintenancePlan.class);
        MaintenancePlan maintenancePlan1 = getMaintenancePlanSample1();
        MaintenancePlan maintenancePlan2 = new MaintenancePlan();
        assertThat(maintenancePlan1).isNotEqualTo(maintenancePlan2);

        maintenancePlan2.setId(maintenancePlan1.getId());
        assertThat(maintenancePlan1).isEqualTo(maintenancePlan2);

        maintenancePlan2 = getMaintenancePlanSample2();
        assertThat(maintenancePlan1).isNotEqualTo(maintenancePlan2);
    }

    @Test
    void organizationTest() {
        MaintenancePlan maintenancePlan = getMaintenancePlanRandomSampleGenerator();
        Organization organizationBack = getOrganizationRandomSampleGenerator();

        maintenancePlan.setOrganization(organizationBack);
        assertThat(maintenancePlan.getOrganization()).isEqualTo(organizationBack);

        maintenancePlan.organization(null);
        assertThat(maintenancePlan.getOrganization()).isNull();
    }
}

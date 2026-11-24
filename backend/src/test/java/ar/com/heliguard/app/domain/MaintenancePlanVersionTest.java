package ar.com.heliguard.app.domain;

import static ar.com.heliguard.app.domain.MaintenancePlanTestSamples.*;
import static ar.com.heliguard.app.domain.MaintenancePlanVersionTestSamples.*;
import static ar.com.heliguard.app.domain.OrganizationTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import ar.com.heliguard.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class MaintenancePlanVersionTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(MaintenancePlanVersion.class);
        MaintenancePlanVersion maintenancePlanVersion1 = getMaintenancePlanVersionSample1();
        MaintenancePlanVersion maintenancePlanVersion2 = new MaintenancePlanVersion();
        assertThat(maintenancePlanVersion1).isNotEqualTo(maintenancePlanVersion2);

        maintenancePlanVersion2.setId(maintenancePlanVersion1.getId());
        assertThat(maintenancePlanVersion1).isEqualTo(maintenancePlanVersion2);

        maintenancePlanVersion2 = getMaintenancePlanVersionSample2();
        assertThat(maintenancePlanVersion1).isNotEqualTo(maintenancePlanVersion2);
    }

    @Test
    void organizationTest() {
        MaintenancePlanVersion maintenancePlanVersion = getMaintenancePlanVersionRandomSampleGenerator();
        Organization organizationBack = getOrganizationRandomSampleGenerator();

        maintenancePlanVersion.setOrganization(organizationBack);
        assertThat(maintenancePlanVersion.getOrganization()).isEqualTo(organizationBack);

        maintenancePlanVersion.organization(null);
        assertThat(maintenancePlanVersion.getOrganization()).isNull();
    }

    @Test
    void maintenancePlanTest() {
        MaintenancePlanVersion maintenancePlanVersion = getMaintenancePlanVersionRandomSampleGenerator();
        MaintenancePlan maintenancePlanBack = getMaintenancePlanRandomSampleGenerator();

        maintenancePlanVersion.setMaintenancePlan(maintenancePlanBack);
        assertThat(maintenancePlanVersion.getMaintenancePlan()).isEqualTo(maintenancePlanBack);

        maintenancePlanVersion.maintenancePlan(null);
        assertThat(maintenancePlanVersion.getMaintenancePlan()).isNull();
    }
}

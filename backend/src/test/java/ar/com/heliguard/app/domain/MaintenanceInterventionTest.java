package ar.com.heliguard.app.domain;

import static ar.com.heliguard.app.domain.MaintenanceInterventionTestSamples.*;
import static ar.com.heliguard.app.domain.OrganizationMembershipTestSamples.*;
import static ar.com.heliguard.app.domain.OrganizationTestSamples.*;
import static ar.com.heliguard.app.domain.WorkOrderTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import ar.com.heliguard.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class MaintenanceInterventionTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(MaintenanceIntervention.class);
        MaintenanceIntervention maintenanceIntervention1 = getMaintenanceInterventionSample1();
        MaintenanceIntervention maintenanceIntervention2 = new MaintenanceIntervention();
        assertThat(maintenanceIntervention1).isNotEqualTo(maintenanceIntervention2);

        maintenanceIntervention2.setId(maintenanceIntervention1.getId());
        assertThat(maintenanceIntervention1).isEqualTo(maintenanceIntervention2);

        maintenanceIntervention2 = getMaintenanceInterventionSample2();
        assertThat(maintenanceIntervention1).isNotEqualTo(maintenanceIntervention2);
    }

    @Test
    void organizationTest() {
        MaintenanceIntervention maintenanceIntervention = getMaintenanceInterventionRandomSampleGenerator();
        Organization organizationBack = getOrganizationRandomSampleGenerator();

        maintenanceIntervention.setOrganization(organizationBack);
        assertThat(maintenanceIntervention.getOrganization()).isEqualTo(organizationBack);

        maintenanceIntervention.organization(null);
        assertThat(maintenanceIntervention.getOrganization()).isNull();
    }

    @Test
    void workOrderTest() {
        MaintenanceIntervention maintenanceIntervention = getMaintenanceInterventionRandomSampleGenerator();
        WorkOrder workOrderBack = getWorkOrderRandomSampleGenerator();

        maintenanceIntervention.setWorkOrder(workOrderBack);
        assertThat(maintenanceIntervention.getWorkOrder()).isEqualTo(workOrderBack);

        maintenanceIntervention.workOrder(null);
        assertThat(maintenanceIntervention.getWorkOrder()).isNull();
    }

    @Test
    void performedByMembershipTest() {
        MaintenanceIntervention maintenanceIntervention = getMaintenanceInterventionRandomSampleGenerator();
        OrganizationMembership organizationMembershipBack = getOrganizationMembershipRandomSampleGenerator();

        maintenanceIntervention.setPerformedByMembership(organizationMembershipBack);
        assertThat(maintenanceIntervention.getPerformedByMembership()).isEqualTo(organizationMembershipBack);

        maintenanceIntervention.performedByMembership(null);
        assertThat(maintenanceIntervention.getPerformedByMembership()).isNull();
    }
}

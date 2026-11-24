package ar.com.heliguard.app.domain;

import static ar.com.heliguard.app.domain.IncidentTestSamples.*;
import static ar.com.heliguard.app.domain.InstallationTestSamples.*;
import static ar.com.heliguard.app.domain.OrganizationTestSamples.*;
import static ar.com.heliguard.app.domain.PlanSubscriptionTestSamples.*;
import static ar.com.heliguard.app.domain.WorkOrderTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import ar.com.heliguard.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class WorkOrderTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(WorkOrder.class);
        WorkOrder workOrder1 = getWorkOrderSample1();
        WorkOrder workOrder2 = new WorkOrder();
        assertThat(workOrder1).isNotEqualTo(workOrder2);

        workOrder2.setId(workOrder1.getId());
        assertThat(workOrder1).isEqualTo(workOrder2);

        workOrder2 = getWorkOrderSample2();
        assertThat(workOrder1).isNotEqualTo(workOrder2);
    }

    @Test
    void organizationTest() {
        WorkOrder workOrder = getWorkOrderRandomSampleGenerator();
        Organization organizationBack = getOrganizationRandomSampleGenerator();

        workOrder.setOrganization(organizationBack);
        assertThat(workOrder.getOrganization()).isEqualTo(organizationBack);

        workOrder.organization(null);
        assertThat(workOrder.getOrganization()).isNull();
    }

    @Test
    void installationTest() {
        WorkOrder workOrder = getWorkOrderRandomSampleGenerator();
        Installation installationBack = getInstallationRandomSampleGenerator();

        workOrder.setInstallation(installationBack);
        assertThat(workOrder.getInstallation()).isEqualTo(installationBack);

        workOrder.installation(null);
        assertThat(workOrder.getInstallation()).isNull();
    }

    @Test
    void incidentTest() {
        WorkOrder workOrder = getWorkOrderRandomSampleGenerator();
        Incident incidentBack = getIncidentRandomSampleGenerator();

        workOrder.setIncident(incidentBack);
        assertThat(workOrder.getIncident()).isEqualTo(incidentBack);

        workOrder.incident(null);
        assertThat(workOrder.getIncident()).isNull();
    }

    @Test
    void subscriptionTest() {
        WorkOrder workOrder = getWorkOrderRandomSampleGenerator();
        PlanSubscription planSubscriptionBack = getPlanSubscriptionRandomSampleGenerator();

        workOrder.setSubscription(planSubscriptionBack);
        assertThat(workOrder.getSubscription()).isEqualTo(planSubscriptionBack);

        workOrder.subscription(null);
        assertThat(workOrder.getSubscription()).isNull();
    }
}

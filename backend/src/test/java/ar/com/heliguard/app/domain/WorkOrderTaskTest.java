package ar.com.heliguard.app.domain;

import static ar.com.heliguard.app.domain.OrganizationTestSamples.*;
import static ar.com.heliguard.app.domain.WorkOrderTaskTestSamples.*;
import static ar.com.heliguard.app.domain.WorkOrderTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import ar.com.heliguard.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class WorkOrderTaskTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(WorkOrderTask.class);
        WorkOrderTask workOrderTask1 = getWorkOrderTaskSample1();
        WorkOrderTask workOrderTask2 = new WorkOrderTask();
        assertThat(workOrderTask1).isNotEqualTo(workOrderTask2);

        workOrderTask2.setId(workOrderTask1.getId());
        assertThat(workOrderTask1).isEqualTo(workOrderTask2);

        workOrderTask2 = getWorkOrderTaskSample2();
        assertThat(workOrderTask1).isNotEqualTo(workOrderTask2);
    }

    @Test
    void organizationTest() {
        WorkOrderTask workOrderTask = getWorkOrderTaskRandomSampleGenerator();
        Organization organizationBack = getOrganizationRandomSampleGenerator();

        workOrderTask.setOrganization(organizationBack);
        assertThat(workOrderTask.getOrganization()).isEqualTo(organizationBack);

        workOrderTask.organization(null);
        assertThat(workOrderTask.getOrganization()).isNull();
    }

    @Test
    void workOrderTest() {
        WorkOrderTask workOrderTask = getWorkOrderTaskRandomSampleGenerator();
        WorkOrder workOrderBack = getWorkOrderRandomSampleGenerator();

        workOrderTask.setWorkOrder(workOrderBack);
        assertThat(workOrderTask.getWorkOrder()).isEqualTo(workOrderBack);

        workOrderTask.workOrder(null);
        assertThat(workOrderTask.getWorkOrder()).isNull();
    }
}

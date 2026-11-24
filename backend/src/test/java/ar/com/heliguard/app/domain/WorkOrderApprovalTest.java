package ar.com.heliguard.app.domain;

import static ar.com.heliguard.app.domain.OrganizationMembershipTestSamples.*;
import static ar.com.heliguard.app.domain.OrganizationTestSamples.*;
import static ar.com.heliguard.app.domain.WorkOrderApprovalTestSamples.*;
import static ar.com.heliguard.app.domain.WorkOrderTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import ar.com.heliguard.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class WorkOrderApprovalTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(WorkOrderApproval.class);
        WorkOrderApproval workOrderApproval1 = getWorkOrderApprovalSample1();
        WorkOrderApproval workOrderApproval2 = new WorkOrderApproval();
        assertThat(workOrderApproval1).isNotEqualTo(workOrderApproval2);

        workOrderApproval2.setId(workOrderApproval1.getId());
        assertThat(workOrderApproval1).isEqualTo(workOrderApproval2);

        workOrderApproval2 = getWorkOrderApprovalSample2();
        assertThat(workOrderApproval1).isNotEqualTo(workOrderApproval2);
    }

    @Test
    void organizationTest() {
        WorkOrderApproval workOrderApproval = getWorkOrderApprovalRandomSampleGenerator();
        Organization organizationBack = getOrganizationRandomSampleGenerator();

        workOrderApproval.setOrganization(organizationBack);
        assertThat(workOrderApproval.getOrganization()).isEqualTo(organizationBack);

        workOrderApproval.organization(null);
        assertThat(workOrderApproval.getOrganization()).isNull();
    }

    @Test
    void workOrderTest() {
        WorkOrderApproval workOrderApproval = getWorkOrderApprovalRandomSampleGenerator();
        WorkOrder workOrderBack = getWorkOrderRandomSampleGenerator();

        workOrderApproval.setWorkOrder(workOrderBack);
        assertThat(workOrderApproval.getWorkOrder()).isEqualTo(workOrderBack);

        workOrderApproval.workOrder(null);
        assertThat(workOrderApproval.getWorkOrder()).isNull();
    }

    @Test
    void decidedByMembershipTest() {
        WorkOrderApproval workOrderApproval = getWorkOrderApprovalRandomSampleGenerator();
        OrganizationMembership organizationMembershipBack = getOrganizationMembershipRandomSampleGenerator();

        workOrderApproval.setDecidedByMembership(organizationMembershipBack);
        assertThat(workOrderApproval.getDecidedByMembership()).isEqualTo(organizationMembershipBack);

        workOrderApproval.decidedByMembership(null);
        assertThat(workOrderApproval.getDecidedByMembership()).isNull();
    }
}

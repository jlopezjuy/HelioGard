package ar.com.heliguard.app.domain;

import static ar.com.heliguard.app.domain.OrganizationMembershipTestSamples.*;
import static ar.com.heliguard.app.domain.OrganizationTestSamples.*;
import static ar.com.heliguard.app.domain.WorkOrderAssignmentTestSamples.*;
import static ar.com.heliguard.app.domain.WorkOrderTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import ar.com.heliguard.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class WorkOrderAssignmentTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(WorkOrderAssignment.class);
        WorkOrderAssignment workOrderAssignment1 = getWorkOrderAssignmentSample1();
        WorkOrderAssignment workOrderAssignment2 = new WorkOrderAssignment();
        assertThat(workOrderAssignment1).isNotEqualTo(workOrderAssignment2);

        workOrderAssignment2.setId(workOrderAssignment1.getId());
        assertThat(workOrderAssignment1).isEqualTo(workOrderAssignment2);

        workOrderAssignment2 = getWorkOrderAssignmentSample2();
        assertThat(workOrderAssignment1).isNotEqualTo(workOrderAssignment2);
    }

    @Test
    void organizationTest() {
        WorkOrderAssignment workOrderAssignment = getWorkOrderAssignmentRandomSampleGenerator();
        Organization organizationBack = getOrganizationRandomSampleGenerator();

        workOrderAssignment.setOrganization(organizationBack);
        assertThat(workOrderAssignment.getOrganization()).isEqualTo(organizationBack);

        workOrderAssignment.organization(null);
        assertThat(workOrderAssignment.getOrganization()).isNull();
    }

    @Test
    void workOrderTest() {
        WorkOrderAssignment workOrderAssignment = getWorkOrderAssignmentRandomSampleGenerator();
        WorkOrder workOrderBack = getWorkOrderRandomSampleGenerator();

        workOrderAssignment.setWorkOrder(workOrderBack);
        assertThat(workOrderAssignment.getWorkOrder()).isEqualTo(workOrderBack);

        workOrderAssignment.workOrder(null);
        assertThat(workOrderAssignment.getWorkOrder()).isNull();
    }

    @Test
    void providerOrganizationTest() {
        WorkOrderAssignment workOrderAssignment = getWorkOrderAssignmentRandomSampleGenerator();
        Organization organizationBack = getOrganizationRandomSampleGenerator();

        workOrderAssignment.setProviderOrganization(organizationBack);
        assertThat(workOrderAssignment.getProviderOrganization()).isEqualTo(organizationBack);

        workOrderAssignment.providerOrganization(null);
        assertThat(workOrderAssignment.getProviderOrganization()).isNull();
    }

    @Test
    void assigneeMembershipTest() {
        WorkOrderAssignment workOrderAssignment = getWorkOrderAssignmentRandomSampleGenerator();
        OrganizationMembership organizationMembershipBack = getOrganizationMembershipRandomSampleGenerator();

        workOrderAssignment.setAssigneeMembership(organizationMembershipBack);
        assertThat(workOrderAssignment.getAssigneeMembership()).isEqualTo(organizationMembershipBack);

        workOrderAssignment.assigneeMembership(null);
        assertThat(workOrderAssignment.getAssigneeMembership()).isNull();
    }
}

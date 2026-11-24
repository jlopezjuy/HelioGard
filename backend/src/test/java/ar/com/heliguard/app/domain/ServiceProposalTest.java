package ar.com.heliguard.app.domain;

import static ar.com.heliguard.app.domain.InstallationTestSamples.*;
import static ar.com.heliguard.app.domain.MaintenancePlanVersionTestSamples.*;
import static ar.com.heliguard.app.domain.OrganizationTestSamples.*;
import static ar.com.heliguard.app.domain.ServiceProposalTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import ar.com.heliguard.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ServiceProposalTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ServiceProposal.class);
        ServiceProposal serviceProposal1 = getServiceProposalSample1();
        ServiceProposal serviceProposal2 = new ServiceProposal();
        assertThat(serviceProposal1).isNotEqualTo(serviceProposal2);

        serviceProposal2.setId(serviceProposal1.getId());
        assertThat(serviceProposal1).isEqualTo(serviceProposal2);

        serviceProposal2 = getServiceProposalSample2();
        assertThat(serviceProposal1).isNotEqualTo(serviceProposal2);
    }

    @Test
    void organizationTest() {
        ServiceProposal serviceProposal = getServiceProposalRandomSampleGenerator();
        Organization organizationBack = getOrganizationRandomSampleGenerator();

        serviceProposal.setOrganization(organizationBack);
        assertThat(serviceProposal.getOrganization()).isEqualTo(organizationBack);

        serviceProposal.organization(null);
        assertThat(serviceProposal.getOrganization()).isNull();
    }

    @Test
    void installationTest() {
        ServiceProposal serviceProposal = getServiceProposalRandomSampleGenerator();
        Installation installationBack = getInstallationRandomSampleGenerator();

        serviceProposal.setInstallation(installationBack);
        assertThat(serviceProposal.getInstallation()).isEqualTo(installationBack);

        serviceProposal.installation(null);
        assertThat(serviceProposal.getInstallation()).isNull();
    }

    @Test
    void planVersionTest() {
        ServiceProposal serviceProposal = getServiceProposalRandomSampleGenerator();
        MaintenancePlanVersion maintenancePlanVersionBack = getMaintenancePlanVersionRandomSampleGenerator();

        serviceProposal.setPlanVersion(maintenancePlanVersionBack);
        assertThat(serviceProposal.getPlanVersion()).isEqualTo(maintenancePlanVersionBack);

        serviceProposal.planVersion(null);
        assertThat(serviceProposal.getPlanVersion()).isNull();
    }
}

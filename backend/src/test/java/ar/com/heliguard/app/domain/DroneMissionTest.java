package ar.com.heliguard.app.domain;

import static ar.com.heliguard.app.domain.DroneAssetTestSamples.*;
import static ar.com.heliguard.app.domain.DroneMissionTestSamples.*;
import static ar.com.heliguard.app.domain.InstallationTestSamples.*;
import static ar.com.heliguard.app.domain.OrganizationTestSamples.*;
import static ar.com.heliguard.app.domain.WorkOrderTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import ar.com.heliguard.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class DroneMissionTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(DroneMission.class);
        DroneMission droneMission1 = getDroneMissionSample1();
        DroneMission droneMission2 = new DroneMission();
        assertThat(droneMission1).isNotEqualTo(droneMission2);

        droneMission2.setId(droneMission1.getId());
        assertThat(droneMission1).isEqualTo(droneMission2);

        droneMission2 = getDroneMissionSample2();
        assertThat(droneMission1).isNotEqualTo(droneMission2);
    }

    @Test
    void organizationTest() {
        DroneMission droneMission = getDroneMissionRandomSampleGenerator();
        Organization organizationBack = getOrganizationRandomSampleGenerator();

        droneMission.setOrganization(organizationBack);
        assertThat(droneMission.getOrganization()).isEqualTo(organizationBack);

        droneMission.organization(null);
        assertThat(droneMission.getOrganization()).isNull();
    }

    @Test
    void installationTest() {
        DroneMission droneMission = getDroneMissionRandomSampleGenerator();
        Installation installationBack = getInstallationRandomSampleGenerator();

        droneMission.setInstallation(installationBack);
        assertThat(droneMission.getInstallation()).isEqualTo(installationBack);

        droneMission.installation(null);
        assertThat(droneMission.getInstallation()).isNull();
    }

    @Test
    void workOrderTest() {
        DroneMission droneMission = getDroneMissionRandomSampleGenerator();
        WorkOrder workOrderBack = getWorkOrderRandomSampleGenerator();

        droneMission.setWorkOrder(workOrderBack);
        assertThat(droneMission.getWorkOrder()).isEqualTo(workOrderBack);

        droneMission.workOrder(null);
        assertThat(droneMission.getWorkOrder()).isNull();
    }

    @Test
    void droneAssetTest() {
        DroneMission droneMission = getDroneMissionRandomSampleGenerator();
        DroneAsset droneAssetBack = getDroneAssetRandomSampleGenerator();

        droneMission.setDroneAsset(droneAssetBack);
        assertThat(droneMission.getDroneAsset()).isEqualTo(droneAssetBack);

        droneMission.droneAsset(null);
        assertThat(droneMission.getDroneAsset()).isNull();
    }

    @Test
    void providerOrganizationTest() {
        DroneMission droneMission = getDroneMissionRandomSampleGenerator();
        Organization organizationBack = getOrganizationRandomSampleGenerator();

        droneMission.setProviderOrganization(organizationBack);
        assertThat(droneMission.getProviderOrganization()).isEqualTo(organizationBack);

        droneMission.providerOrganization(null);
        assertThat(droneMission.getProviderOrganization()).isNull();
    }
}

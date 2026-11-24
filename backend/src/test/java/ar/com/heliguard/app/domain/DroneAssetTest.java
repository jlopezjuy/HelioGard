package ar.com.heliguard.app.domain;

import static ar.com.heliguard.app.domain.DroneAssetTestSamples.*;
import static ar.com.heliguard.app.domain.OrganizationTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import ar.com.heliguard.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class DroneAssetTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(DroneAsset.class);
        DroneAsset droneAsset1 = getDroneAssetSample1();
        DroneAsset droneAsset2 = new DroneAsset();
        assertThat(droneAsset1).isNotEqualTo(droneAsset2);

        droneAsset2.setId(droneAsset1.getId());
        assertThat(droneAsset1).isEqualTo(droneAsset2);

        droneAsset2 = getDroneAssetSample2();
        assertThat(droneAsset1).isNotEqualTo(droneAsset2);
    }

    @Test
    void organizationTest() {
        DroneAsset droneAsset = getDroneAssetRandomSampleGenerator();
        Organization organizationBack = getOrganizationRandomSampleGenerator();

        droneAsset.setOrganization(organizationBack);
        assertThat(droneAsset.getOrganization()).isEqualTo(organizationBack);

        droneAsset.organization(null);
        assertThat(droneAsset.getOrganization()).isNull();
    }
}

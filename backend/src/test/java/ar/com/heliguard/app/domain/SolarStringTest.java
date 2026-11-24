package ar.com.heliguard.app.domain;

import static ar.com.heliguard.app.domain.EquipmentModelTestSamples.*;
import static ar.com.heliguard.app.domain.OrganizationTestSamples.*;
import static ar.com.heliguard.app.domain.SolarArrayTestSamples.*;
import static ar.com.heliguard.app.domain.SolarStringTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import ar.com.heliguard.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class SolarStringTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(SolarString.class);
        SolarString solarString1 = getSolarStringSample1();
        SolarString solarString2 = new SolarString();
        assertThat(solarString1).isNotEqualTo(solarString2);

        solarString2.setId(solarString1.getId());
        assertThat(solarString1).isEqualTo(solarString2);

        solarString2 = getSolarStringSample2();
        assertThat(solarString1).isNotEqualTo(solarString2);
    }

    @Test
    void organizationTest() {
        SolarString solarString = getSolarStringRandomSampleGenerator();
        Organization organizationBack = getOrganizationRandomSampleGenerator();

        solarString.setOrganization(organizationBack);
        assertThat(solarString.getOrganization()).isEqualTo(organizationBack);

        solarString.organization(null);
        assertThat(solarString.getOrganization()).isNull();
    }

    @Test
    void solarArrayTest() {
        SolarString solarString = getSolarStringRandomSampleGenerator();
        SolarArray solarArrayBack = getSolarArrayRandomSampleGenerator();

        solarString.setSolarArray(solarArrayBack);
        assertThat(solarString.getSolarArray()).isEqualTo(solarArrayBack);

        solarString.solarArray(null);
        assertThat(solarString.getSolarArray()).isNull();
    }

    @Test
    void moduleModelTest() {
        SolarString solarString = getSolarStringRandomSampleGenerator();
        EquipmentModel equipmentModelBack = getEquipmentModelRandomSampleGenerator();

        solarString.setModuleModel(equipmentModelBack);
        assertThat(solarString.getModuleModel()).isEqualTo(equipmentModelBack);

        solarString.moduleModel(null);
        assertThat(solarString.getModuleModel()).isNull();
    }
}

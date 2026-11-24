package ar.com.heliguard.app.domain;

import static ar.com.heliguard.app.domain.EnergyModelRunTestSamples.*;
import static ar.com.heliguard.app.domain.LossAssessmentTestSamples.*;
import static ar.com.heliguard.app.domain.OrganizationTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import ar.com.heliguard.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class LossAssessmentTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(LossAssessment.class);
        LossAssessment lossAssessment1 = getLossAssessmentSample1();
        LossAssessment lossAssessment2 = new LossAssessment();
        assertThat(lossAssessment1).isNotEqualTo(lossAssessment2);

        lossAssessment2.setId(lossAssessment1.getId());
        assertThat(lossAssessment1).isEqualTo(lossAssessment2);

        lossAssessment2 = getLossAssessmentSample2();
        assertThat(lossAssessment1).isNotEqualTo(lossAssessment2);
    }

    @Test
    void organizationTest() {
        LossAssessment lossAssessment = getLossAssessmentRandomSampleGenerator();
        Organization organizationBack = getOrganizationRandomSampleGenerator();

        lossAssessment.setOrganization(organizationBack);
        assertThat(lossAssessment.getOrganization()).isEqualTo(organizationBack);

        lossAssessment.organization(null);
        assertThat(lossAssessment.getOrganization()).isNull();
    }

    @Test
    void modelRunTest() {
        LossAssessment lossAssessment = getLossAssessmentRandomSampleGenerator();
        EnergyModelRun energyModelRunBack = getEnergyModelRunRandomSampleGenerator();

        lossAssessment.setModelRun(energyModelRunBack);
        assertThat(lossAssessment.getModelRun()).isEqualTo(energyModelRunBack);

        lossAssessment.modelRun(null);
        assertThat(lossAssessment.getModelRun()).isNull();
    }
}

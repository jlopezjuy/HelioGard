package ar.com.heliguard.app.domain;

import static ar.com.heliguard.app.domain.LossAssessmentTestSamples.*;
import static ar.com.heliguard.app.domain.LossCauseEstimateTestSamples.*;
import static ar.com.heliguard.app.domain.OrganizationTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import ar.com.heliguard.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class LossCauseEstimateTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(LossCauseEstimate.class);
        LossCauseEstimate lossCauseEstimate1 = getLossCauseEstimateSample1();
        LossCauseEstimate lossCauseEstimate2 = new LossCauseEstimate();
        assertThat(lossCauseEstimate1).isNotEqualTo(lossCauseEstimate2);

        lossCauseEstimate2.setId(lossCauseEstimate1.getId());
        assertThat(lossCauseEstimate1).isEqualTo(lossCauseEstimate2);

        lossCauseEstimate2 = getLossCauseEstimateSample2();
        assertThat(lossCauseEstimate1).isNotEqualTo(lossCauseEstimate2);
    }

    @Test
    void organizationTest() {
        LossCauseEstimate lossCauseEstimate = getLossCauseEstimateRandomSampleGenerator();
        Organization organizationBack = getOrganizationRandomSampleGenerator();

        lossCauseEstimate.setOrganization(organizationBack);
        assertThat(lossCauseEstimate.getOrganization()).isEqualTo(organizationBack);

        lossCauseEstimate.organization(null);
        assertThat(lossCauseEstimate.getOrganization()).isNull();
    }

    @Test
    void lossAssessmentTest() {
        LossCauseEstimate lossCauseEstimate = getLossCauseEstimateRandomSampleGenerator();
        LossAssessment lossAssessmentBack = getLossAssessmentRandomSampleGenerator();

        lossCauseEstimate.setLossAssessment(lossAssessmentBack);
        assertThat(lossCauseEstimate.getLossAssessment()).isEqualTo(lossAssessmentBack);

        lossCauseEstimate.lossAssessment(null);
        assertThat(lossCauseEstimate.getLossAssessment()).isNull();
    }
}

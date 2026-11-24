package ar.com.heliguard.app.domain;

import static ar.com.heliguard.app.domain.EconomicAssessmentTestSamples.*;
import static ar.com.heliguard.app.domain.LossAssessmentTestSamples.*;
import static ar.com.heliguard.app.domain.OrganizationTestSamples.*;
import static ar.com.heliguard.app.domain.TariffRateTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import ar.com.heliguard.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class EconomicAssessmentTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(EconomicAssessment.class);
        EconomicAssessment economicAssessment1 = getEconomicAssessmentSample1();
        EconomicAssessment economicAssessment2 = new EconomicAssessment();
        assertThat(economicAssessment1).isNotEqualTo(economicAssessment2);

        economicAssessment2.setId(economicAssessment1.getId());
        assertThat(economicAssessment1).isEqualTo(economicAssessment2);

        economicAssessment2 = getEconomicAssessmentSample2();
        assertThat(economicAssessment1).isNotEqualTo(economicAssessment2);
    }

    @Test
    void organizationTest() {
        EconomicAssessment economicAssessment = getEconomicAssessmentRandomSampleGenerator();
        Organization organizationBack = getOrganizationRandomSampleGenerator();

        economicAssessment.setOrganization(organizationBack);
        assertThat(economicAssessment.getOrganization()).isEqualTo(organizationBack);

        economicAssessment.organization(null);
        assertThat(economicAssessment.getOrganization()).isNull();
    }

    @Test
    void lossAssessmentTest() {
        EconomicAssessment economicAssessment = getEconomicAssessmentRandomSampleGenerator();
        LossAssessment lossAssessmentBack = getLossAssessmentRandomSampleGenerator();

        economicAssessment.setLossAssessment(lossAssessmentBack);
        assertThat(economicAssessment.getLossAssessment()).isEqualTo(lossAssessmentBack);

        economicAssessment.lossAssessment(null);
        assertThat(economicAssessment.getLossAssessment()).isNull();
    }

    @Test
    void tariffRateTest() {
        EconomicAssessment economicAssessment = getEconomicAssessmentRandomSampleGenerator();
        TariffRate tariffRateBack = getTariffRateRandomSampleGenerator();

        economicAssessment.setTariffRate(tariffRateBack);
        assertThat(economicAssessment.getTariffRate()).isEqualTo(tariffRateBack);

        economicAssessment.tariffRate(null);
        assertThat(economicAssessment.getTariffRate()).isNull();
    }
}

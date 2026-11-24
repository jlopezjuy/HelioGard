package ar.com.heliguard.app.domain;

import static ar.com.heliguard.app.domain.AlertRuleTestSamples.*;
import static ar.com.heliguard.app.domain.InstallationTestSamples.*;
import static ar.com.heliguard.app.domain.OrganizationTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import ar.com.heliguard.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class AlertRuleTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(AlertRule.class);
        AlertRule alertRule1 = getAlertRuleSample1();
        AlertRule alertRule2 = new AlertRule();
        assertThat(alertRule1).isNotEqualTo(alertRule2);

        alertRule2.setId(alertRule1.getId());
        assertThat(alertRule1).isEqualTo(alertRule2);

        alertRule2 = getAlertRuleSample2();
        assertThat(alertRule1).isNotEqualTo(alertRule2);
    }

    @Test
    void organizationTest() {
        AlertRule alertRule = getAlertRuleRandomSampleGenerator();
        Organization organizationBack = getOrganizationRandomSampleGenerator();

        alertRule.setOrganization(organizationBack);
        assertThat(alertRule.getOrganization()).isEqualTo(organizationBack);

        alertRule.organization(null);
        assertThat(alertRule.getOrganization()).isNull();
    }

    @Test
    void installationTest() {
        AlertRule alertRule = getAlertRuleRandomSampleGenerator();
        Installation installationBack = getInstallationRandomSampleGenerator();

        alertRule.setInstallation(installationBack);
        assertThat(alertRule.getInstallation()).isEqualTo(installationBack);

        alertRule.installation(null);
        assertThat(alertRule.getInstallation()).isNull();
    }
}

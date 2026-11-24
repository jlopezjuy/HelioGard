package ar.com.heliguard.app.domain;

import static ar.com.heliguard.app.domain.AlertRuleTestSamples.*;
import static ar.com.heliguard.app.domain.AlertRuleVersionTestSamples.*;
import static ar.com.heliguard.app.domain.OrganizationTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import ar.com.heliguard.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class AlertRuleVersionTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(AlertRuleVersion.class);
        AlertRuleVersion alertRuleVersion1 = getAlertRuleVersionSample1();
        AlertRuleVersion alertRuleVersion2 = new AlertRuleVersion();
        assertThat(alertRuleVersion1).isNotEqualTo(alertRuleVersion2);

        alertRuleVersion2.setId(alertRuleVersion1.getId());
        assertThat(alertRuleVersion1).isEqualTo(alertRuleVersion2);

        alertRuleVersion2 = getAlertRuleVersionSample2();
        assertThat(alertRuleVersion1).isNotEqualTo(alertRuleVersion2);
    }

    @Test
    void organizationTest() {
        AlertRuleVersion alertRuleVersion = getAlertRuleVersionRandomSampleGenerator();
        Organization organizationBack = getOrganizationRandomSampleGenerator();

        alertRuleVersion.setOrganization(organizationBack);
        assertThat(alertRuleVersion.getOrganization()).isEqualTo(organizationBack);

        alertRuleVersion.organization(null);
        assertThat(alertRuleVersion.getOrganization()).isNull();
    }

    @Test
    void alertRuleTest() {
        AlertRuleVersion alertRuleVersion = getAlertRuleVersionRandomSampleGenerator();
        AlertRule alertRuleBack = getAlertRuleRandomSampleGenerator();

        alertRuleVersion.setAlertRule(alertRuleBack);
        assertThat(alertRuleVersion.getAlertRule()).isEqualTo(alertRuleBack);

        alertRuleVersion.alertRule(null);
        assertThat(alertRuleVersion.getAlertRule()).isNull();
    }
}

package ar.com.heliguard.app.domain;

import static ar.com.heliguard.app.domain.EnergyModelConfigurationTestSamples.*;
import static ar.com.heliguard.app.domain.EnergyModelRunTestSamples.*;
import static ar.com.heliguard.app.domain.InstallationTestSamples.*;
import static ar.com.heliguard.app.domain.OrganizationTestSamples.*;
import static ar.com.heliguard.app.domain.WeatherIngestionBatchTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import ar.com.heliguard.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class EnergyModelRunTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(EnergyModelRun.class);
        EnergyModelRun energyModelRun1 = getEnergyModelRunSample1();
        EnergyModelRun energyModelRun2 = new EnergyModelRun();
        assertThat(energyModelRun1).isNotEqualTo(energyModelRun2);

        energyModelRun2.setId(energyModelRun1.getId());
        assertThat(energyModelRun1).isEqualTo(energyModelRun2);

        energyModelRun2 = getEnergyModelRunSample2();
        assertThat(energyModelRun1).isNotEqualTo(energyModelRun2);
    }

    @Test
    void organizationTest() {
        EnergyModelRun energyModelRun = getEnergyModelRunRandomSampleGenerator();
        Organization organizationBack = getOrganizationRandomSampleGenerator();

        energyModelRun.setOrganization(organizationBack);
        assertThat(energyModelRun.getOrganization()).isEqualTo(organizationBack);

        energyModelRun.organization(null);
        assertThat(energyModelRun.getOrganization()).isNull();
    }

    @Test
    void installationTest() {
        EnergyModelRun energyModelRun = getEnergyModelRunRandomSampleGenerator();
        Installation installationBack = getInstallationRandomSampleGenerator();

        energyModelRun.setInstallation(installationBack);
        assertThat(energyModelRun.getInstallation()).isEqualTo(installationBack);

        energyModelRun.installation(null);
        assertThat(energyModelRun.getInstallation()).isNull();
    }

    @Test
    void modelConfigurationTest() {
        EnergyModelRun energyModelRun = getEnergyModelRunRandomSampleGenerator();
        EnergyModelConfiguration energyModelConfigurationBack = getEnergyModelConfigurationRandomSampleGenerator();

        energyModelRun.setModelConfiguration(energyModelConfigurationBack);
        assertThat(energyModelRun.getModelConfiguration()).isEqualTo(energyModelConfigurationBack);

        energyModelRun.modelConfiguration(null);
        assertThat(energyModelRun.getModelConfiguration()).isNull();
    }

    @Test
    void weatherBatchTest() {
        EnergyModelRun energyModelRun = getEnergyModelRunRandomSampleGenerator();
        WeatherIngestionBatch weatherIngestionBatchBack = getWeatherIngestionBatchRandomSampleGenerator();

        energyModelRun.setWeatherBatch(weatherIngestionBatchBack);
        assertThat(energyModelRun.getWeatherBatch()).isEqualTo(weatherIngestionBatchBack);

        energyModelRun.weatherBatch(null);
        assertThat(energyModelRun.getWeatherBatch()).isNull();
    }
}

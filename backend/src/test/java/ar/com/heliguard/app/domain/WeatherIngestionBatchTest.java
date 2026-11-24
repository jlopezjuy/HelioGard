package ar.com.heliguard.app.domain;

import static ar.com.heliguard.app.domain.EvidenceSourceTestSamples.*;
import static ar.com.heliguard.app.domain.InstallationTestSamples.*;
import static ar.com.heliguard.app.domain.OrganizationTestSamples.*;
import static ar.com.heliguard.app.domain.WeatherIngestionBatchTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import ar.com.heliguard.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class WeatherIngestionBatchTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(WeatherIngestionBatch.class);
        WeatherIngestionBatch weatherIngestionBatch1 = getWeatherIngestionBatchSample1();
        WeatherIngestionBatch weatherIngestionBatch2 = new WeatherIngestionBatch();
        assertThat(weatherIngestionBatch1).isNotEqualTo(weatherIngestionBatch2);

        weatherIngestionBatch2.setId(weatherIngestionBatch1.getId());
        assertThat(weatherIngestionBatch1).isEqualTo(weatherIngestionBatch2);

        weatherIngestionBatch2 = getWeatherIngestionBatchSample2();
        assertThat(weatherIngestionBatch1).isNotEqualTo(weatherIngestionBatch2);
    }

    @Test
    void organizationTest() {
        WeatherIngestionBatch weatherIngestionBatch = getWeatherIngestionBatchRandomSampleGenerator();
        Organization organizationBack = getOrganizationRandomSampleGenerator();

        weatherIngestionBatch.setOrganization(organizationBack);
        assertThat(weatherIngestionBatch.getOrganization()).isEqualTo(organizationBack);

        weatherIngestionBatch.organization(null);
        assertThat(weatherIngestionBatch.getOrganization()).isNull();
    }

    @Test
    void installationTest() {
        WeatherIngestionBatch weatherIngestionBatch = getWeatherIngestionBatchRandomSampleGenerator();
        Installation installationBack = getInstallationRandomSampleGenerator();

        weatherIngestionBatch.setInstallation(installationBack);
        assertThat(weatherIngestionBatch.getInstallation()).isEqualTo(installationBack);

        weatherIngestionBatch.installation(null);
        assertThat(weatherIngestionBatch.getInstallation()).isNull();
    }

    @Test
    void evidenceSourceTest() {
        WeatherIngestionBatch weatherIngestionBatch = getWeatherIngestionBatchRandomSampleGenerator();
        EvidenceSource evidenceSourceBack = getEvidenceSourceRandomSampleGenerator();

        weatherIngestionBatch.setEvidenceSource(evidenceSourceBack);
        assertThat(weatherIngestionBatch.getEvidenceSource()).isEqualTo(evidenceSourceBack);

        weatherIngestionBatch.evidenceSource(null);
        assertThat(weatherIngestionBatch.getEvidenceSource()).isNull();
    }
}

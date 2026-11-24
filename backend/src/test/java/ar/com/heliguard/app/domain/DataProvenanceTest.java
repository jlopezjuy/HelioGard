package ar.com.heliguard.app.domain;

import static ar.com.heliguard.app.domain.DataProvenanceTestSamples.*;
import static ar.com.heliguard.app.domain.EnergyModelRunTestSamples.*;
import static ar.com.heliguard.app.domain.EvidenceSourceTestSamples.*;
import static ar.com.heliguard.app.domain.InstallationTestSamples.*;
import static ar.com.heliguard.app.domain.OrganizationTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import ar.com.heliguard.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class DataProvenanceTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(DataProvenance.class);
        DataProvenance dataProvenance1 = getDataProvenanceSample1();
        DataProvenance dataProvenance2 = new DataProvenance();
        assertThat(dataProvenance1).isNotEqualTo(dataProvenance2);

        dataProvenance2.setId(dataProvenance1.getId());
        assertThat(dataProvenance1).isEqualTo(dataProvenance2);

        dataProvenance2 = getDataProvenanceSample2();
        assertThat(dataProvenance1).isNotEqualTo(dataProvenance2);
    }

    @Test
    void organizationTest() {
        DataProvenance dataProvenance = getDataProvenanceRandomSampleGenerator();
        Organization organizationBack = getOrganizationRandomSampleGenerator();

        dataProvenance.setOrganization(organizationBack);
        assertThat(dataProvenance.getOrganization()).isEqualTo(organizationBack);

        dataProvenance.organization(null);
        assertThat(dataProvenance.getOrganization()).isNull();
    }

    @Test
    void installationTest() {
        DataProvenance dataProvenance = getDataProvenanceRandomSampleGenerator();
        Installation installationBack = getInstallationRandomSampleGenerator();

        dataProvenance.setInstallation(installationBack);
        assertThat(dataProvenance.getInstallation()).isEqualTo(installationBack);

        dataProvenance.installation(null);
        assertThat(dataProvenance.getInstallation()).isNull();
    }

    @Test
    void evidenceSourceTest() {
        DataProvenance dataProvenance = getDataProvenanceRandomSampleGenerator();
        EvidenceSource evidenceSourceBack = getEvidenceSourceRandomSampleGenerator();

        dataProvenance.setEvidenceSource(evidenceSourceBack);
        assertThat(dataProvenance.getEvidenceSource()).isEqualTo(evidenceSourceBack);

        dataProvenance.evidenceSource(null);
        assertThat(dataProvenance.getEvidenceSource()).isNull();
    }

    @Test
    void modelRunTest() {
        DataProvenance dataProvenance = getDataProvenanceRandomSampleGenerator();
        EnergyModelRun energyModelRunBack = getEnergyModelRunRandomSampleGenerator();

        dataProvenance.setModelRun(energyModelRunBack);
        assertThat(dataProvenance.getModelRun()).isEqualTo(energyModelRunBack);

        dataProvenance.modelRun(null);
        assertThat(dataProvenance.getModelRun()).isNull();
    }
}

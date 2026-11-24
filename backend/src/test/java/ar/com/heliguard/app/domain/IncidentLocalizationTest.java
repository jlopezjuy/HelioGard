package ar.com.heliguard.app.domain;

import static ar.com.heliguard.app.domain.EquipmentTestSamples.*;
import static ar.com.heliguard.app.domain.IncidentLocalizationTestSamples.*;
import static ar.com.heliguard.app.domain.IncidentTestSamples.*;
import static ar.com.heliguard.app.domain.OrganizationTestSamples.*;
import static ar.com.heliguard.app.domain.SolarSectorTestSamples.*;
import static ar.com.heliguard.app.domain.SolarStringTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import ar.com.heliguard.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class IncidentLocalizationTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(IncidentLocalization.class);
        IncidentLocalization incidentLocalization1 = getIncidentLocalizationSample1();
        IncidentLocalization incidentLocalization2 = new IncidentLocalization();
        assertThat(incidentLocalization1).isNotEqualTo(incidentLocalization2);

        incidentLocalization2.setId(incidentLocalization1.getId());
        assertThat(incidentLocalization1).isEqualTo(incidentLocalization2);

        incidentLocalization2 = getIncidentLocalizationSample2();
        assertThat(incidentLocalization1).isNotEqualTo(incidentLocalization2);
    }

    @Test
    void organizationTest() {
        IncidentLocalization incidentLocalization = getIncidentLocalizationRandomSampleGenerator();
        Organization organizationBack = getOrganizationRandomSampleGenerator();

        incidentLocalization.setOrganization(organizationBack);
        assertThat(incidentLocalization.getOrganization()).isEqualTo(organizationBack);

        incidentLocalization.organization(null);
        assertThat(incidentLocalization.getOrganization()).isNull();
    }

    @Test
    void incidentTest() {
        IncidentLocalization incidentLocalization = getIncidentLocalizationRandomSampleGenerator();
        Incident incidentBack = getIncidentRandomSampleGenerator();

        incidentLocalization.setIncident(incidentBack);
        assertThat(incidentLocalization.getIncident()).isEqualTo(incidentBack);

        incidentLocalization.incident(null);
        assertThat(incidentLocalization.getIncident()).isNull();
    }

    @Test
    void solarSectorTest() {
        IncidentLocalization incidentLocalization = getIncidentLocalizationRandomSampleGenerator();
        SolarSector solarSectorBack = getSolarSectorRandomSampleGenerator();

        incidentLocalization.setSolarSector(solarSectorBack);
        assertThat(incidentLocalization.getSolarSector()).isEqualTo(solarSectorBack);

        incidentLocalization.solarSector(null);
        assertThat(incidentLocalization.getSolarSector()).isNull();
    }

    @Test
    void solarStringTest() {
        IncidentLocalization incidentLocalization = getIncidentLocalizationRandomSampleGenerator();
        SolarString solarStringBack = getSolarStringRandomSampleGenerator();

        incidentLocalization.setSolarString(solarStringBack);
        assertThat(incidentLocalization.getSolarString()).isEqualTo(solarStringBack);

        incidentLocalization.solarString(null);
        assertThat(incidentLocalization.getSolarString()).isNull();
    }

    @Test
    void equipmentTest() {
        IncidentLocalization incidentLocalization = getIncidentLocalizationRandomSampleGenerator();
        Equipment equipmentBack = getEquipmentRandomSampleGenerator();

        incidentLocalization.setEquipment(equipmentBack);
        assertThat(incidentLocalization.getEquipment()).isEqualTo(equipmentBack);

        incidentLocalization.equipment(null);
        assertThat(incidentLocalization.getEquipment()).isNull();
    }
}

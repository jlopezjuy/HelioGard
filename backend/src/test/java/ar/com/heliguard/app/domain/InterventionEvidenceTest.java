package ar.com.heliguard.app.domain;

import static ar.com.heliguard.app.domain.InterventionEvidenceTestSamples.*;
import static ar.com.heliguard.app.domain.MaintenanceInterventionTestSamples.*;
import static ar.com.heliguard.app.domain.OrganizationTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import ar.com.heliguard.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class InterventionEvidenceTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(InterventionEvidence.class);
        InterventionEvidence interventionEvidence1 = getInterventionEvidenceSample1();
        InterventionEvidence interventionEvidence2 = new InterventionEvidence();
        assertThat(interventionEvidence1).isNotEqualTo(interventionEvidence2);

        interventionEvidence2.setId(interventionEvidence1.getId());
        assertThat(interventionEvidence1).isEqualTo(interventionEvidence2);

        interventionEvidence2 = getInterventionEvidenceSample2();
        assertThat(interventionEvidence1).isNotEqualTo(interventionEvidence2);
    }

    @Test
    void organizationTest() {
        InterventionEvidence interventionEvidence = getInterventionEvidenceRandomSampleGenerator();
        Organization organizationBack = getOrganizationRandomSampleGenerator();

        interventionEvidence.setOrganization(organizationBack);
        assertThat(interventionEvidence.getOrganization()).isEqualTo(organizationBack);

        interventionEvidence.organization(null);
        assertThat(interventionEvidence.getOrganization()).isNull();
    }

    @Test
    void interventionTest() {
        InterventionEvidence interventionEvidence = getInterventionEvidenceRandomSampleGenerator();
        MaintenanceIntervention maintenanceInterventionBack = getMaintenanceInterventionRandomSampleGenerator();

        interventionEvidence.setIntervention(maintenanceInterventionBack);
        assertThat(interventionEvidence.getIntervention()).isEqualTo(maintenanceInterventionBack);

        interventionEvidence.intervention(null);
        assertThat(interventionEvidence.getIntervention()).isNull();
    }
}

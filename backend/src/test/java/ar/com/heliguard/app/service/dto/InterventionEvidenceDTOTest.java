package ar.com.heliguard.app.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import ar.com.heliguard.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class InterventionEvidenceDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(InterventionEvidenceDTO.class);
        InterventionEvidenceDTO interventionEvidenceDTO1 = new InterventionEvidenceDTO();
        interventionEvidenceDTO1.setId(1L);
        InterventionEvidenceDTO interventionEvidenceDTO2 = new InterventionEvidenceDTO();
        assertThat(interventionEvidenceDTO1).isNotEqualTo(interventionEvidenceDTO2);
        interventionEvidenceDTO2.setId(interventionEvidenceDTO1.getId());
        assertThat(interventionEvidenceDTO1).isEqualTo(interventionEvidenceDTO2);
        interventionEvidenceDTO2.setId(2L);
        assertThat(interventionEvidenceDTO1).isNotEqualTo(interventionEvidenceDTO2);
        interventionEvidenceDTO1.setId(null);
        assertThat(interventionEvidenceDTO1).isNotEqualTo(interventionEvidenceDTO2);
    }
}

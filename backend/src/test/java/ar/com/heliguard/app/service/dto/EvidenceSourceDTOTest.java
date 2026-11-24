package ar.com.heliguard.app.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import ar.com.heliguard.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class EvidenceSourceDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(EvidenceSourceDTO.class);
        EvidenceSourceDTO evidenceSourceDTO1 = new EvidenceSourceDTO();
        evidenceSourceDTO1.setId(1L);
        EvidenceSourceDTO evidenceSourceDTO2 = new EvidenceSourceDTO();
        assertThat(evidenceSourceDTO1).isNotEqualTo(evidenceSourceDTO2);
        evidenceSourceDTO2.setId(evidenceSourceDTO1.getId());
        assertThat(evidenceSourceDTO1).isEqualTo(evidenceSourceDTO2);
        evidenceSourceDTO2.setId(2L);
        assertThat(evidenceSourceDTO1).isNotEqualTo(evidenceSourceDTO2);
        evidenceSourceDTO1.setId(null);
        assertThat(evidenceSourceDTO1).isNotEqualTo(evidenceSourceDTO2);
    }
}

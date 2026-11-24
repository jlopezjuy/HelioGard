package ar.com.heliguard.app.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import ar.com.heliguard.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class DataProvenanceDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(DataProvenanceDTO.class);
        DataProvenanceDTO dataProvenanceDTO1 = new DataProvenanceDTO();
        dataProvenanceDTO1.setId(1L);
        DataProvenanceDTO dataProvenanceDTO2 = new DataProvenanceDTO();
        assertThat(dataProvenanceDTO1).isNotEqualTo(dataProvenanceDTO2);
        dataProvenanceDTO2.setId(dataProvenanceDTO1.getId());
        assertThat(dataProvenanceDTO1).isEqualTo(dataProvenanceDTO2);
        dataProvenanceDTO2.setId(2L);
        assertThat(dataProvenanceDTO1).isNotEqualTo(dataProvenanceDTO2);
        dataProvenanceDTO1.setId(null);
        assertThat(dataProvenanceDTO1).isNotEqualTo(dataProvenanceDTO2);
    }
}

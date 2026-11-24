package ar.com.heliguard.app.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import ar.com.heliguard.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class EnergyModelRunDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(EnergyModelRunDTO.class);
        EnergyModelRunDTO energyModelRunDTO1 = new EnergyModelRunDTO();
        energyModelRunDTO1.setId(1L);
        EnergyModelRunDTO energyModelRunDTO2 = new EnergyModelRunDTO();
        assertThat(energyModelRunDTO1).isNotEqualTo(energyModelRunDTO2);
        energyModelRunDTO2.setId(energyModelRunDTO1.getId());
        assertThat(energyModelRunDTO1).isEqualTo(energyModelRunDTO2);
        energyModelRunDTO2.setId(2L);
        assertThat(energyModelRunDTO1).isNotEqualTo(energyModelRunDTO2);
        energyModelRunDTO1.setId(null);
        assertThat(energyModelRunDTO1).isNotEqualTo(energyModelRunDTO2);
    }
}

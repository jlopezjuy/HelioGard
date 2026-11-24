package ar.com.heliguard.app.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import ar.com.heliguard.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class EnergyModelConfigurationDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(EnergyModelConfigurationDTO.class);
        EnergyModelConfigurationDTO energyModelConfigurationDTO1 = new EnergyModelConfigurationDTO();
        energyModelConfigurationDTO1.setId(1L);
        EnergyModelConfigurationDTO energyModelConfigurationDTO2 = new EnergyModelConfigurationDTO();
        assertThat(energyModelConfigurationDTO1).isNotEqualTo(energyModelConfigurationDTO2);
        energyModelConfigurationDTO2.setId(energyModelConfigurationDTO1.getId());
        assertThat(energyModelConfigurationDTO1).isEqualTo(energyModelConfigurationDTO2);
        energyModelConfigurationDTO2.setId(2L);
        assertThat(energyModelConfigurationDTO1).isNotEqualTo(energyModelConfigurationDTO2);
        energyModelConfigurationDTO1.setId(null);
        assertThat(energyModelConfigurationDTO1).isNotEqualTo(energyModelConfigurationDTO2);
    }
}

package ar.com.heliguard.app.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import ar.com.heliguard.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class DroneAssetDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(DroneAssetDTO.class);
        DroneAssetDTO droneAssetDTO1 = new DroneAssetDTO();
        droneAssetDTO1.setId(1L);
        DroneAssetDTO droneAssetDTO2 = new DroneAssetDTO();
        assertThat(droneAssetDTO1).isNotEqualTo(droneAssetDTO2);
        droneAssetDTO2.setId(droneAssetDTO1.getId());
        assertThat(droneAssetDTO1).isEqualTo(droneAssetDTO2);
        droneAssetDTO2.setId(2L);
        assertThat(droneAssetDTO1).isNotEqualTo(droneAssetDTO2);
        droneAssetDTO1.setId(null);
        assertThat(droneAssetDTO1).isNotEqualTo(droneAssetDTO2);
    }
}

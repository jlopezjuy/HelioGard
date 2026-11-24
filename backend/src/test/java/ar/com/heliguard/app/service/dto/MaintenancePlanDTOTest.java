package ar.com.heliguard.app.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import ar.com.heliguard.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class MaintenancePlanDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(MaintenancePlanDTO.class);
        MaintenancePlanDTO maintenancePlanDTO1 = new MaintenancePlanDTO();
        maintenancePlanDTO1.setId(1L);
        MaintenancePlanDTO maintenancePlanDTO2 = new MaintenancePlanDTO();
        assertThat(maintenancePlanDTO1).isNotEqualTo(maintenancePlanDTO2);
        maintenancePlanDTO2.setId(maintenancePlanDTO1.getId());
        assertThat(maintenancePlanDTO1).isEqualTo(maintenancePlanDTO2);
        maintenancePlanDTO2.setId(2L);
        assertThat(maintenancePlanDTO1).isNotEqualTo(maintenancePlanDTO2);
        maintenancePlanDTO1.setId(null);
        assertThat(maintenancePlanDTO1).isNotEqualTo(maintenancePlanDTO2);
    }
}

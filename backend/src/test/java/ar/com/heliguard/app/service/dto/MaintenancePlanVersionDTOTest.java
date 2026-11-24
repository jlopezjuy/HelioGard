package ar.com.heliguard.app.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import ar.com.heliguard.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class MaintenancePlanVersionDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(MaintenancePlanVersionDTO.class);
        MaintenancePlanVersionDTO maintenancePlanVersionDTO1 = new MaintenancePlanVersionDTO();
        maintenancePlanVersionDTO1.setId(1L);
        MaintenancePlanVersionDTO maintenancePlanVersionDTO2 = new MaintenancePlanVersionDTO();
        assertThat(maintenancePlanVersionDTO1).isNotEqualTo(maintenancePlanVersionDTO2);
        maintenancePlanVersionDTO2.setId(maintenancePlanVersionDTO1.getId());
        assertThat(maintenancePlanVersionDTO1).isEqualTo(maintenancePlanVersionDTO2);
        maintenancePlanVersionDTO2.setId(2L);
        assertThat(maintenancePlanVersionDTO1).isNotEqualTo(maintenancePlanVersionDTO2);
        maintenancePlanVersionDTO1.setId(null);
        assertThat(maintenancePlanVersionDTO1).isNotEqualTo(maintenancePlanVersionDTO2);
    }
}

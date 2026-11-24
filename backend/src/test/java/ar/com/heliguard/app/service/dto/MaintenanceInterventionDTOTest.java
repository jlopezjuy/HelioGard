package ar.com.heliguard.app.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import ar.com.heliguard.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class MaintenanceInterventionDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(MaintenanceInterventionDTO.class);
        MaintenanceInterventionDTO maintenanceInterventionDTO1 = new MaintenanceInterventionDTO();
        maintenanceInterventionDTO1.setId(1L);
        MaintenanceInterventionDTO maintenanceInterventionDTO2 = new MaintenanceInterventionDTO();
        assertThat(maintenanceInterventionDTO1).isNotEqualTo(maintenanceInterventionDTO2);
        maintenanceInterventionDTO2.setId(maintenanceInterventionDTO1.getId());
        assertThat(maintenanceInterventionDTO1).isEqualTo(maintenanceInterventionDTO2);
        maintenanceInterventionDTO2.setId(2L);
        assertThat(maintenanceInterventionDTO1).isNotEqualTo(maintenanceInterventionDTO2);
        maintenanceInterventionDTO1.setId(null);
        assertThat(maintenanceInterventionDTO1).isNotEqualTo(maintenanceInterventionDTO2);
    }
}

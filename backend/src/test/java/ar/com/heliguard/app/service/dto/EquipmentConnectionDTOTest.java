package ar.com.heliguard.app.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import ar.com.heliguard.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class EquipmentConnectionDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(EquipmentConnectionDTO.class);
        EquipmentConnectionDTO equipmentConnectionDTO1 = new EquipmentConnectionDTO();
        equipmentConnectionDTO1.setId(1L);
        EquipmentConnectionDTO equipmentConnectionDTO2 = new EquipmentConnectionDTO();
        assertThat(equipmentConnectionDTO1).isNotEqualTo(equipmentConnectionDTO2);
        equipmentConnectionDTO2.setId(equipmentConnectionDTO1.getId());
        assertThat(equipmentConnectionDTO1).isEqualTo(equipmentConnectionDTO2);
        equipmentConnectionDTO2.setId(2L);
        assertThat(equipmentConnectionDTO1).isNotEqualTo(equipmentConnectionDTO2);
        equipmentConnectionDTO1.setId(null);
        assertThat(equipmentConnectionDTO1).isNotEqualTo(equipmentConnectionDTO2);
    }
}

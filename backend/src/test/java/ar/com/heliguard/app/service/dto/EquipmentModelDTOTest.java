package ar.com.heliguard.app.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import ar.com.heliguard.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class EquipmentModelDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(EquipmentModelDTO.class);
        EquipmentModelDTO equipmentModelDTO1 = new EquipmentModelDTO();
        equipmentModelDTO1.setId(1L);
        EquipmentModelDTO equipmentModelDTO2 = new EquipmentModelDTO();
        assertThat(equipmentModelDTO1).isNotEqualTo(equipmentModelDTO2);
        equipmentModelDTO2.setId(equipmentModelDTO1.getId());
        assertThat(equipmentModelDTO1).isEqualTo(equipmentModelDTO2);
        equipmentModelDTO2.setId(2L);
        assertThat(equipmentModelDTO1).isNotEqualTo(equipmentModelDTO2);
        equipmentModelDTO1.setId(null);
        assertThat(equipmentModelDTO1).isNotEqualTo(equipmentModelDTO2);
    }
}

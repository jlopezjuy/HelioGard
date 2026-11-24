package ar.com.heliguard.app.domain;

import static ar.com.heliguard.app.domain.EquipmentModelTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import ar.com.heliguard.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class EquipmentModelTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(EquipmentModel.class);
        EquipmentModel equipmentModel1 = getEquipmentModelSample1();
        EquipmentModel equipmentModel2 = new EquipmentModel();
        assertThat(equipmentModel1).isNotEqualTo(equipmentModel2);

        equipmentModel2.setId(equipmentModel1.getId());
        assertThat(equipmentModel1).isEqualTo(equipmentModel2);

        equipmentModel2 = getEquipmentModelSample2();
        assertThat(equipmentModel1).isNotEqualTo(equipmentModel2);
    }
}

package ar.com.heliguard.app.domain;

import static ar.com.heliguard.app.domain.EquipmentTestSamples.*;
import static ar.com.heliguard.app.domain.InverterMpptInputTestSamples.*;
import static ar.com.heliguard.app.domain.OrganizationTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import ar.com.heliguard.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class InverterMpptInputTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(InverterMpptInput.class);
        InverterMpptInput inverterMpptInput1 = getInverterMpptInputSample1();
        InverterMpptInput inverterMpptInput2 = new InverterMpptInput();
        assertThat(inverterMpptInput1).isNotEqualTo(inverterMpptInput2);

        inverterMpptInput2.setId(inverterMpptInput1.getId());
        assertThat(inverterMpptInput1).isEqualTo(inverterMpptInput2);

        inverterMpptInput2 = getInverterMpptInputSample2();
        assertThat(inverterMpptInput1).isNotEqualTo(inverterMpptInput2);
    }

    @Test
    void organizationTest() {
        InverterMpptInput inverterMpptInput = getInverterMpptInputRandomSampleGenerator();
        Organization organizationBack = getOrganizationRandomSampleGenerator();

        inverterMpptInput.setOrganization(organizationBack);
        assertThat(inverterMpptInput.getOrganization()).isEqualTo(organizationBack);

        inverterMpptInput.organization(null);
        assertThat(inverterMpptInput.getOrganization()).isNull();
    }

    @Test
    void inverterTest() {
        InverterMpptInput inverterMpptInput = getInverterMpptInputRandomSampleGenerator();
        Equipment equipmentBack = getEquipmentRandomSampleGenerator();

        inverterMpptInput.setInverter(equipmentBack);
        assertThat(inverterMpptInput.getInverter()).isEqualTo(equipmentBack);

        inverterMpptInput.inverter(null);
        assertThat(inverterMpptInput.getInverter()).isNull();
    }
}

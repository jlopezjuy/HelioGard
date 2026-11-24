package ar.com.heliguard.app.domain;

import static ar.com.heliguard.app.domain.InverterMpptInputTestSamples.*;
import static ar.com.heliguard.app.domain.OrganizationTestSamples.*;
import static ar.com.heliguard.app.domain.SolarStringTestSamples.*;
import static ar.com.heliguard.app.domain.StringInputAssignmentTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import ar.com.heliguard.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class StringInputAssignmentTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(StringInputAssignment.class);
        StringInputAssignment stringInputAssignment1 = getStringInputAssignmentSample1();
        StringInputAssignment stringInputAssignment2 = new StringInputAssignment();
        assertThat(stringInputAssignment1).isNotEqualTo(stringInputAssignment2);

        stringInputAssignment2.setId(stringInputAssignment1.getId());
        assertThat(stringInputAssignment1).isEqualTo(stringInputAssignment2);

        stringInputAssignment2 = getStringInputAssignmentSample2();
        assertThat(stringInputAssignment1).isNotEqualTo(stringInputAssignment2);
    }

    @Test
    void organizationTest() {
        StringInputAssignment stringInputAssignment = getStringInputAssignmentRandomSampleGenerator();
        Organization organizationBack = getOrganizationRandomSampleGenerator();

        stringInputAssignment.setOrganization(organizationBack);
        assertThat(stringInputAssignment.getOrganization()).isEqualTo(organizationBack);

        stringInputAssignment.organization(null);
        assertThat(stringInputAssignment.getOrganization()).isNull();
    }

    @Test
    void solarStringTest() {
        StringInputAssignment stringInputAssignment = getStringInputAssignmentRandomSampleGenerator();
        SolarString solarStringBack = getSolarStringRandomSampleGenerator();

        stringInputAssignment.setSolarString(solarStringBack);
        assertThat(stringInputAssignment.getSolarString()).isEqualTo(solarStringBack);

        stringInputAssignment.solarString(null);
        assertThat(stringInputAssignment.getSolarString()).isNull();
    }

    @Test
    void mpptInputTest() {
        StringInputAssignment stringInputAssignment = getStringInputAssignmentRandomSampleGenerator();
        InverterMpptInput inverterMpptInputBack = getInverterMpptInputRandomSampleGenerator();

        stringInputAssignment.setMpptInput(inverterMpptInputBack);
        assertThat(stringInputAssignment.getMpptInput()).isEqualTo(inverterMpptInputBack);

        stringInputAssignment.mpptInput(null);
        assertThat(stringInputAssignment.getMpptInput()).isNull();
    }
}

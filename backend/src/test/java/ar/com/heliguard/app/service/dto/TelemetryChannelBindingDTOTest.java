package ar.com.heliguard.app.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import ar.com.heliguard.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class TelemetryChannelBindingDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(TelemetryChannelBindingDTO.class);
        TelemetryChannelBindingDTO telemetryChannelBindingDTO1 = new TelemetryChannelBindingDTO();
        telemetryChannelBindingDTO1.setId(1L);
        TelemetryChannelBindingDTO telemetryChannelBindingDTO2 = new TelemetryChannelBindingDTO();
        assertThat(telemetryChannelBindingDTO1).isNotEqualTo(telemetryChannelBindingDTO2);
        telemetryChannelBindingDTO2.setId(telemetryChannelBindingDTO1.getId());
        assertThat(telemetryChannelBindingDTO1).isEqualTo(telemetryChannelBindingDTO2);
        telemetryChannelBindingDTO2.setId(2L);
        assertThat(telemetryChannelBindingDTO1).isNotEqualTo(telemetryChannelBindingDTO2);
        telemetryChannelBindingDTO1.setId(null);
        assertThat(telemetryChannelBindingDTO1).isNotEqualTo(telemetryChannelBindingDTO2);
    }
}

package ar.com.heliguard.app.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import ar.com.heliguard.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class TelemetryChannelDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(TelemetryChannelDTO.class);
        TelemetryChannelDTO telemetryChannelDTO1 = new TelemetryChannelDTO();
        telemetryChannelDTO1.setId(1L);
        TelemetryChannelDTO telemetryChannelDTO2 = new TelemetryChannelDTO();
        assertThat(telemetryChannelDTO1).isNotEqualTo(telemetryChannelDTO2);
        telemetryChannelDTO2.setId(telemetryChannelDTO1.getId());
        assertThat(telemetryChannelDTO1).isEqualTo(telemetryChannelDTO2);
        telemetryChannelDTO2.setId(2L);
        assertThat(telemetryChannelDTO1).isNotEqualTo(telemetryChannelDTO2);
        telemetryChannelDTO1.setId(null);
        assertThat(telemetryChannelDTO1).isNotEqualTo(telemetryChannelDTO2);
    }
}

package ar.com.heliguard.app.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import ar.com.heliguard.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class TelemetryIntegrityEventDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(TelemetryIntegrityEventDTO.class);
        TelemetryIntegrityEventDTO telemetryIntegrityEventDTO1 = new TelemetryIntegrityEventDTO();
        telemetryIntegrityEventDTO1.setId(1L);
        TelemetryIntegrityEventDTO telemetryIntegrityEventDTO2 = new TelemetryIntegrityEventDTO();
        assertThat(telemetryIntegrityEventDTO1).isNotEqualTo(telemetryIntegrityEventDTO2);
        telemetryIntegrityEventDTO2.setId(telemetryIntegrityEventDTO1.getId());
        assertThat(telemetryIntegrityEventDTO1).isEqualTo(telemetryIntegrityEventDTO2);
        telemetryIntegrityEventDTO2.setId(2L);
        assertThat(telemetryIntegrityEventDTO1).isNotEqualTo(telemetryIntegrityEventDTO2);
        telemetryIntegrityEventDTO1.setId(null);
        assertThat(telemetryIntegrityEventDTO1).isNotEqualTo(telemetryIntegrityEventDTO2);
    }
}

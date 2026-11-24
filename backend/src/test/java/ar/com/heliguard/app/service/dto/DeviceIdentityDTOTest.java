package ar.com.heliguard.app.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import ar.com.heliguard.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class DeviceIdentityDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(DeviceIdentityDTO.class);
        DeviceIdentityDTO deviceIdentityDTO1 = new DeviceIdentityDTO();
        deviceIdentityDTO1.setId(1L);
        DeviceIdentityDTO deviceIdentityDTO2 = new DeviceIdentityDTO();
        assertThat(deviceIdentityDTO1).isNotEqualTo(deviceIdentityDTO2);
        deviceIdentityDTO2.setId(deviceIdentityDTO1.getId());
        assertThat(deviceIdentityDTO1).isEqualTo(deviceIdentityDTO2);
        deviceIdentityDTO2.setId(2L);
        assertThat(deviceIdentityDTO1).isNotEqualTo(deviceIdentityDTO2);
        deviceIdentityDTO1.setId(null);
        assertThat(deviceIdentityDTO1).isNotEqualTo(deviceIdentityDTO2);
    }
}

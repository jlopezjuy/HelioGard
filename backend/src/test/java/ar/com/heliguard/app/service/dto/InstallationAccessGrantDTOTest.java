package ar.com.heliguard.app.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import ar.com.heliguard.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class InstallationAccessGrantDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(InstallationAccessGrantDTO.class);
        InstallationAccessGrantDTO installationAccessGrantDTO1 = new InstallationAccessGrantDTO();
        installationAccessGrantDTO1.setId(1L);
        InstallationAccessGrantDTO installationAccessGrantDTO2 = new InstallationAccessGrantDTO();
        assertThat(installationAccessGrantDTO1).isNotEqualTo(installationAccessGrantDTO2);
        installationAccessGrantDTO2.setId(installationAccessGrantDTO1.getId());
        assertThat(installationAccessGrantDTO1).isEqualTo(installationAccessGrantDTO2);
        installationAccessGrantDTO2.setId(2L);
        assertThat(installationAccessGrantDTO1).isNotEqualTo(installationAccessGrantDTO2);
        installationAccessGrantDTO1.setId(null);
        assertThat(installationAccessGrantDTO1).isNotEqualTo(installationAccessGrantDTO2);
    }
}

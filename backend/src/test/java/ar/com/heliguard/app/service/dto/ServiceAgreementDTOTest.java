package ar.com.heliguard.app.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import ar.com.heliguard.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ServiceAgreementDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(ServiceAgreementDTO.class);
        ServiceAgreementDTO serviceAgreementDTO1 = new ServiceAgreementDTO();
        serviceAgreementDTO1.setId(1L);
        ServiceAgreementDTO serviceAgreementDTO2 = new ServiceAgreementDTO();
        assertThat(serviceAgreementDTO1).isNotEqualTo(serviceAgreementDTO2);
        serviceAgreementDTO2.setId(serviceAgreementDTO1.getId());
        assertThat(serviceAgreementDTO1).isEqualTo(serviceAgreementDTO2);
        serviceAgreementDTO2.setId(2L);
        assertThat(serviceAgreementDTO1).isNotEqualTo(serviceAgreementDTO2);
        serviceAgreementDTO1.setId(null);
        assertThat(serviceAgreementDTO1).isNotEqualTo(serviceAgreementDTO2);
    }
}

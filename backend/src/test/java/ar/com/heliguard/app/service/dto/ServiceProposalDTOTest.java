package ar.com.heliguard.app.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import ar.com.heliguard.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ServiceProposalDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(ServiceProposalDTO.class);
        ServiceProposalDTO serviceProposalDTO1 = new ServiceProposalDTO();
        serviceProposalDTO1.setId(1L);
        ServiceProposalDTO serviceProposalDTO2 = new ServiceProposalDTO();
        assertThat(serviceProposalDTO1).isNotEqualTo(serviceProposalDTO2);
        serviceProposalDTO2.setId(serviceProposalDTO1.getId());
        assertThat(serviceProposalDTO1).isEqualTo(serviceProposalDTO2);
        serviceProposalDTO2.setId(2L);
        assertThat(serviceProposalDTO1).isNotEqualTo(serviceProposalDTO2);
        serviceProposalDTO1.setId(null);
        assertThat(serviceProposalDTO1).isNotEqualTo(serviceProposalDTO2);
    }
}

package ar.com.heliguard.app.domain;

import static ar.com.heliguard.app.domain.EvidenceSourceTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import ar.com.heliguard.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class EvidenceSourceTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(EvidenceSource.class);
        EvidenceSource evidenceSource1 = getEvidenceSourceSample1();
        EvidenceSource evidenceSource2 = new EvidenceSource();
        assertThat(evidenceSource1).isNotEqualTo(evidenceSource2);

        evidenceSource2.setId(evidenceSource1.getId());
        assertThat(evidenceSource1).isEqualTo(evidenceSource2);

        evidenceSource2 = getEvidenceSourceSample2();
        assertThat(evidenceSource1).isNotEqualTo(evidenceSource2);
    }
}

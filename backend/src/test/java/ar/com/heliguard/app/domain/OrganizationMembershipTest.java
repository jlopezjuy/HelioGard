package ar.com.heliguard.app.domain;

import static ar.com.heliguard.app.domain.OrganizationMembershipTestSamples.*;
import static ar.com.heliguard.app.domain.OrganizationTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import ar.com.heliguard.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class OrganizationMembershipTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(OrganizationMembership.class);
        OrganizationMembership organizationMembership1 = getOrganizationMembershipSample1();
        OrganizationMembership organizationMembership2 = new OrganizationMembership();
        assertThat(organizationMembership1).isNotEqualTo(organizationMembership2);

        organizationMembership2.setId(organizationMembership1.getId());
        assertThat(organizationMembership1).isEqualTo(organizationMembership2);

        organizationMembership2 = getOrganizationMembershipSample2();
        assertThat(organizationMembership1).isNotEqualTo(organizationMembership2);
    }

    @Test
    void organizationTest() {
        OrganizationMembership organizationMembership = getOrganizationMembershipRandomSampleGenerator();
        Organization organizationBack = getOrganizationRandomSampleGenerator();

        organizationMembership.setOrganization(organizationBack);
        assertThat(organizationMembership.getOrganization()).isEqualTo(organizationBack);

        organizationMembership.organization(null);
        assertThat(organizationMembership.getOrganization()).isNull();
    }
}

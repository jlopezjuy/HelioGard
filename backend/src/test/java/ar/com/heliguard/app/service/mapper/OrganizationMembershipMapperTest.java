package ar.com.heliguard.app.service.mapper;

import static ar.com.heliguard.app.domain.OrganizationMembershipAsserts.*;
import static ar.com.heliguard.app.domain.OrganizationMembershipTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class OrganizationMembershipMapperTest {

    private OrganizationMembershipMapper organizationMembershipMapper;

    @BeforeEach
    void setUp() {
        organizationMembershipMapper = new OrganizationMembershipMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getOrganizationMembershipSample1();
        var actual = organizationMembershipMapper.toEntity(organizationMembershipMapper.toDto(expected));
        assertOrganizationMembershipAllPropertiesEquals(expected, actual);
    }
}

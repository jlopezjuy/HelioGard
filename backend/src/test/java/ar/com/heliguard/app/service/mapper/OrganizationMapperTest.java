package ar.com.heliguard.app.service.mapper;

import static ar.com.heliguard.app.domain.OrganizationAsserts.*;
import static ar.com.heliguard.app.domain.OrganizationTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class OrganizationMapperTest {

    private OrganizationMapper organizationMapper;

    @BeforeEach
    void setUp() {
        organizationMapper = new OrganizationMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getOrganizationSample1();
        var actual = organizationMapper.toEntity(organizationMapper.toDto(expected));
        assertOrganizationAllPropertiesEquals(expected, actual);
    }
}

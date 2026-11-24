package ar.com.heliguard.app.service.mapper;

import static ar.com.heliguard.app.domain.ServiceProposalAsserts.*;
import static ar.com.heliguard.app.domain.ServiceProposalTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ServiceProposalMapperTest {

    private ServiceProposalMapper serviceProposalMapper;

    @BeforeEach
    void setUp() {
        serviceProposalMapper = new ServiceProposalMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getServiceProposalSample1();
        var actual = serviceProposalMapper.toEntity(serviceProposalMapper.toDto(expected));
        assertServiceProposalAllPropertiesEquals(expected, actual);
    }
}

package ar.com.heliguard.app.service.mapper;

import static ar.com.heliguard.app.domain.ServiceAgreementAsserts.*;
import static ar.com.heliguard.app.domain.ServiceAgreementTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ServiceAgreementMapperTest {

    private ServiceAgreementMapper serviceAgreementMapper;

    @BeforeEach
    void setUp() {
        serviceAgreementMapper = new ServiceAgreementMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getServiceAgreementSample1();
        var actual = serviceAgreementMapper.toEntity(serviceAgreementMapper.toDto(expected));
        assertServiceAgreementAllPropertiesEquals(expected, actual);
    }
}

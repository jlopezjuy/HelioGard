package ar.com.heliguard.app.service.mapper;

import static ar.com.heliguard.app.domain.VerificationResultAsserts.*;
import static ar.com.heliguard.app.domain.VerificationResultTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class VerificationResultMapperTest {

    private VerificationResultMapper verificationResultMapper;

    @BeforeEach
    void setUp() {
        verificationResultMapper = new VerificationResultMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getVerificationResultSample1();
        var actual = verificationResultMapper.toEntity(verificationResultMapper.toDto(expected));
        assertVerificationResultAllPropertiesEquals(expected, actual);
    }
}

package ar.com.heliguard.app.service.mapper;

import static ar.com.heliguard.app.domain.InverterMpptInputAsserts.*;
import static ar.com.heliguard.app.domain.InverterMpptInputTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class InverterMpptInputMapperTest {

    private InverterMpptInputMapper inverterMpptInputMapper;

    @BeforeEach
    void setUp() {
        inverterMpptInputMapper = new InverterMpptInputMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getInverterMpptInputSample1();
        var actual = inverterMpptInputMapper.toEntity(inverterMpptInputMapper.toDto(expected));
        assertInverterMpptInputAllPropertiesEquals(expected, actual);
    }
}

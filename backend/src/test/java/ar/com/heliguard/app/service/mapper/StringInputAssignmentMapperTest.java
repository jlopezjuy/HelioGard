package ar.com.heliguard.app.service.mapper;

import static ar.com.heliguard.app.domain.StringInputAssignmentAsserts.*;
import static ar.com.heliguard.app.domain.StringInputAssignmentTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class StringInputAssignmentMapperTest {

    private StringInputAssignmentMapper stringInputAssignmentMapper;

    @BeforeEach
    void setUp() {
        stringInputAssignmentMapper = new StringInputAssignmentMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getStringInputAssignmentSample1();
        var actual = stringInputAssignmentMapper.toEntity(stringInputAssignmentMapper.toDto(expected));
        assertStringInputAssignmentAllPropertiesEquals(expected, actual);
    }
}

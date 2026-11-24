package ar.com.heliguard.app.service.mapper;

import static ar.com.heliguard.app.domain.WeatherIngestionBatchAsserts.*;
import static ar.com.heliguard.app.domain.WeatherIngestionBatchTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class WeatherIngestionBatchMapperTest {

    private WeatherIngestionBatchMapper weatherIngestionBatchMapper;

    @BeforeEach
    void setUp() {
        weatherIngestionBatchMapper = new WeatherIngestionBatchMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getWeatherIngestionBatchSample1();
        var actual = weatherIngestionBatchMapper.toEntity(weatherIngestionBatchMapper.toDto(expected));
        assertWeatherIngestionBatchAllPropertiesEquals(expected, actual);
    }
}

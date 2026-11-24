package ar.com.heliguard.app.service.mapper;

import static ar.com.heliguard.app.domain.SiteAsserts.*;
import static ar.com.heliguard.app.domain.SiteTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SiteMapperTest {

    private SiteMapper siteMapper;

    @BeforeEach
    void setUp() {
        siteMapper = new SiteMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getSiteSample1();
        var actual = siteMapper.toEntity(siteMapper.toDto(expected));
        assertSiteAllPropertiesEquals(expected, actual);
    }
}

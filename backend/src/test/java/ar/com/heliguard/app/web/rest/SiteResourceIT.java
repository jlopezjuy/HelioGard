package ar.com.heliguard.app.web.rest;

import static ar.com.heliguard.app.domain.SiteAsserts.*;
import static ar.com.heliguard.app.web.rest.TestUtil.createUpdateProxyForBean;
import static ar.com.heliguard.app.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

import ar.com.heliguard.app.IntegrationTest;
import ar.com.heliguard.app.domain.Organization;
import ar.com.heliguard.app.domain.Site;
import ar.com.heliguard.app.repository.SiteRepository;
import ar.com.heliguard.app.service.SiteService;
import ar.com.heliguard.app.service.dto.SiteDTO;
import ar.com.heliguard.app.service.mapper.SiteMapper;
import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import tools.jackson.databind.ObjectMapper;

/**
 * Integration tests for the {@link SiteResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class SiteResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_ADDRESS = "AAAAAAAAAA";
    private static final String UPDATED_ADDRESS = "BBBBBBBBBB";

    private static final BigDecimal DEFAULT_LATITUDE = new BigDecimal(-90);
    private static final BigDecimal UPDATED_LATITUDE = new BigDecimal(-89);

    private static final BigDecimal DEFAULT_LONGITUDE = new BigDecimal(-180);
    private static final BigDecimal UPDATED_LONGITUDE = new BigDecimal(-179);

    private static final BigDecimal DEFAULT_ALTITUDE_M = new BigDecimal(1);
    private static final BigDecimal UPDATED_ALTITUDE_M = new BigDecimal(2);

    private static final String DEFAULT_TIME_ZONE = "AAAAAAAAAA";
    private static final String UPDATED_TIME_ZONE = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/sites";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    @Autowired
    private ObjectMapper om;

    @Autowired
    private SiteRepository siteRepository;

    @Mock
    private SiteRepository siteRepositoryMock;

    @Autowired
    private SiteMapper siteMapper;

    @Mock
    private SiteService siteServiceMock;

    @Autowired
    private R2dbcEntityTemplate template;

    @Autowired
    private WebTestClient webTestClient;

    private Site site;

    private Site insertedSite;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Site createEntity(R2dbcEntityTemplate template) {
        Site site = new Site()
            .name(DEFAULT_NAME)
            .address(DEFAULT_ADDRESS)
            .latitude(DEFAULT_LATITUDE)
            .longitude(DEFAULT_LONGITUDE)
            .altitudeM(DEFAULT_ALTITUDE_M)
            .timeZone(DEFAULT_TIME_ZONE);
        // Add required entity
        Organization organization;
        organization = template.insert(OrganizationResourceIT.createEntity()).block();
        site.setOrganization(organization);
        return site;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Site createUpdatedEntity(R2dbcEntityTemplate template) {
        Site updatedSite = new Site()
            .name(UPDATED_NAME)
            .address(UPDATED_ADDRESS)
            .latitude(UPDATED_LATITUDE)
            .longitude(UPDATED_LONGITUDE)
            .altitudeM(UPDATED_ALTITUDE_M)
            .timeZone(UPDATED_TIME_ZONE);
        // Add required entity
        Organization organization;
        organization = template.insert(OrganizationResourceIT.createUpdatedEntity()).block();
        updatedSite.setOrganization(organization);
        return updatedSite;
    }

    public static void deleteEntities(R2dbcEntityTemplate template) {
        try {
            template.delete(Site.class).all().block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
        OrganizationResourceIT.deleteEntities(template);
    }

    @BeforeEach
    void initTest() {
        site = createEntity(template);
    }

    @AfterEach
    void cleanup() {
        if (insertedSite != null) {
            siteRepository.delete(insertedSite).block();
            insertedSite = null;
        }
        deleteEntities(template);
    }

    @Test
    void createSite() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Site
        SiteDTO siteDTO = siteMapper.toDto(site);
        var returnedSiteDTO = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(siteDTO))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(SiteDTO.class)
            .returnResult()
            .getResponseBody();

        // Validate the Site in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedSite = siteMapper.toEntity(returnedSiteDTO);
        assertSiteUpdatableFieldsEquals(returnedSite, getPersistedSite(returnedSite));

        insertedSite = returnedSite;
    }

    @Test
    void createSiteWithExistingId() throws Exception {
        // Create the Site with an existing ID
        site.setId(1L);
        SiteDTO siteDTO = siteMapper.toDto(site);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(siteDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Site in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    void checkNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        site.setName(null);

        // Create the Site, which fails.
        SiteDTO siteDTO = siteMapper.toDto(site);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(siteDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkTimeZoneIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        site.setTimeZone(null);

        // Create the Site, which fails.
        SiteDTO siteDTO = siteMapper.toDto(site);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(siteDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void getAllSitesAsStream() {
        // Initialize the database
        siteRepository.save(site).block();

        List<Site> siteList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(SiteDTO.class)
            .getResponseBody()
            .map(siteMapper::toEntity)
            .filter(site::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(siteList).isNotNull();
        assertThat(siteList).hasSize(1);
        Site testSite = siteList.getFirst();

        // Test fails because reactive api returns an empty object instead of null
        // assertSiteAllPropertiesEquals(site, testSite);
        assertSiteUpdatableFieldsEquals(site, testSite);
    }

    @Test
    void getAllSites() {
        // Initialize the database
        insertedSite = siteRepository.save(site).block();

        // Get all the siteList
        webTestClient
            .get()
            .uri(ENTITY_API_URL + "?sort=id,desc")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(site.getId().intValue()))
            .jsonPath("$.[*].name")
            .value(hasItem(DEFAULT_NAME))
            .jsonPath("$.[*].address")
            .value(hasItem(DEFAULT_ADDRESS))
            .jsonPath("$.[*].latitude")
            .value(hasItem(sameNumber(DEFAULT_LATITUDE)))
            .jsonPath("$.[*].longitude")
            .value(hasItem(sameNumber(DEFAULT_LONGITUDE)))
            .jsonPath("$.[*].altitudeM")
            .value(hasItem(sameNumber(DEFAULT_ALTITUDE_M)))
            .jsonPath("$.[*].timeZone")
            .value(hasItem(DEFAULT_TIME_ZONE));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllSitesWithEagerRelationshipsIsEnabled() {
        when(siteServiceMock.findAllWithEagerRelationships()).thenReturn(Flux.empty());

        webTestClient
            .get()
            .uri(ENTITY_API_URL + "?eagerload=true")
            .exchange()
            .expectStatus()
            .isOk();

        verify(siteServiceMock, times(1)).findAllWithEagerRelationships();
    }

    @SuppressWarnings({ "unchecked" })
    void getAllSitesWithEagerRelationshipsIsNotEnabled() {
        when(siteServiceMock.findAllWithEagerRelationships()).thenReturn(Flux.empty());

        webTestClient
            .get()
            .uri(ENTITY_API_URL + "?eagerload=false")
            .exchange()
            .expectStatus()
            .isOk();
        verify(siteRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    void getSite() {
        // Initialize the database
        insertedSite = siteRepository.save(site).block();

        // Get the site
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, site.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(site.getId().intValue()))
            .jsonPath("$.name")
            .value(is(DEFAULT_NAME))
            .jsonPath("$.address")
            .value(is(DEFAULT_ADDRESS))
            .jsonPath("$.latitude")
            .value(is(sameNumber(DEFAULT_LATITUDE)))
            .jsonPath("$.longitude")
            .value(is(sameNumber(DEFAULT_LONGITUDE)))
            .jsonPath("$.altitudeM")
            .value(is(sameNumber(DEFAULT_ALTITUDE_M)))
            .jsonPath("$.timeZone")
            .value(is(DEFAULT_TIME_ZONE));
    }

    @Test
    void getNonExistingSite() {
        // Get the site
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingSite() throws Exception {
        // Initialize the database
        insertedSite = siteRepository.save(site).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the site
        Site updatedSite = siteRepository.findById(site.getId()).block();
        updatedSite
            .name(UPDATED_NAME)
            .address(UPDATED_ADDRESS)
            .latitude(UPDATED_LATITUDE)
            .longitude(UPDATED_LONGITUDE)
            .altitudeM(UPDATED_ALTITUDE_M)
            .timeZone(UPDATED_TIME_ZONE);
        SiteDTO siteDTO = siteMapper.toDto(updatedSite);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, siteDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(siteDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Site in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedSiteToMatchAllProperties(updatedSite);
    }

    @Test
    void putNonExistingSite() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        site.setId(longCount.incrementAndGet());

        // Create the Site
        SiteDTO siteDTO = siteMapper.toDto(site);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, siteDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(siteDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Site in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchSite() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        site.setId(longCount.incrementAndGet());

        // Create the Site
        SiteDTO siteDTO = siteMapper.toDto(site);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(siteDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Site in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamSite() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        site.setId(longCount.incrementAndGet());

        // Create the Site
        SiteDTO siteDTO = siteMapper.toDto(site);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(siteDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Site in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateSiteWithPatch() throws Exception {
        // Initialize the database
        insertedSite = siteRepository.save(site).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the site using partial update
        Site partialUpdatedSite = new Site();
        partialUpdatedSite.setId(site.getId());

        partialUpdatedSite.latitude(UPDATED_LATITUDE).altitudeM(UPDATED_ALTITUDE_M).timeZone(UPDATED_TIME_ZONE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedSite.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedSite))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Site in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertSiteUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedSite, site), getPersistedSite(site));
    }

    @Test
    void fullUpdateSiteWithPatch() throws Exception {
        // Initialize the database
        insertedSite = siteRepository.save(site).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the site using partial update
        Site partialUpdatedSite = new Site();
        partialUpdatedSite.setId(site.getId());

        partialUpdatedSite
            .name(UPDATED_NAME)
            .address(UPDATED_ADDRESS)
            .latitude(UPDATED_LATITUDE)
            .longitude(UPDATED_LONGITUDE)
            .altitudeM(UPDATED_ALTITUDE_M)
            .timeZone(UPDATED_TIME_ZONE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedSite.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedSite))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Site in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertSiteUpdatableFieldsEquals(partialUpdatedSite, getPersistedSite(partialUpdatedSite));
    }

    @Test
    void patchNonExistingSite() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        site.setId(longCount.incrementAndGet());

        // Create the Site
        SiteDTO siteDTO = siteMapper.toDto(site);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, siteDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(siteDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Site in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchSite() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        site.setId(longCount.incrementAndGet());

        // Create the Site
        SiteDTO siteDTO = siteMapper.toDto(site);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(siteDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Site in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamSite() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        site.setId(longCount.incrementAndGet());

        // Create the Site
        SiteDTO siteDTO = siteMapper.toDto(site);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(siteDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Site in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteSite() {
        // Initialize the database
        insertedSite = siteRepository.save(site).block();

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the site
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, site.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return siteRepository.count().block();
    }

    protected void assertIncrementedRepositoryCount(long countBefore) {
        assertThat(countBefore + 1).isEqualTo(getRepositoryCount());
    }

    protected void assertDecrementedRepositoryCount(long countBefore) {
        assertThat(countBefore - 1).isEqualTo(getRepositoryCount());
    }

    protected void assertSameRepositoryCount(long countBefore) {
        assertThat(countBefore).isEqualTo(getRepositoryCount());
    }

    protected Site getPersistedSite(Site site) {
        return siteRepository.findById(site.getId()).block();
    }

    protected void assertPersistedSiteToMatchAllProperties(Site expectedSite) {
        // Test fails because reactive api returns an empty object instead of null
        // assertSiteAllPropertiesEquals(expectedSite, getPersistedSite(expectedSite));
        assertSiteUpdatableFieldsEquals(expectedSite, getPersistedSite(expectedSite));
    }

    protected void assertPersistedSiteToMatchUpdatableProperties(Site expectedSite) {
        // Test fails because reactive api returns an empty object instead of null
        // assertSiteAllUpdatablePropertiesEquals(expectedSite, getPersistedSite(expectedSite));
        assertSiteUpdatableFieldsEquals(expectedSite, getPersistedSite(expectedSite));
    }
}

package ar.com.heliguard.app.web.rest;

import static ar.com.heliguard.app.domain.SolarSectorAsserts.*;
import static ar.com.heliguard.app.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

import ar.com.heliguard.app.IntegrationTest;
import ar.com.heliguard.app.domain.Installation;
import ar.com.heliguard.app.domain.Organization;
import ar.com.heliguard.app.domain.SolarSector;
import ar.com.heliguard.app.repository.SolarSectorRepository;
import ar.com.heliguard.app.service.SolarSectorService;
import ar.com.heliguard.app.service.dto.SolarSectorDTO;
import ar.com.heliguard.app.service.mapper.SolarSectorMapper;
import java.time.Duration;
import java.time.Instant;
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
 * Integration tests for the {@link SolarSectorResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class SolarSectorResourceIT {

    private static final String DEFAULT_SECTOR_CODE = "AAAAAAAAAA";
    private static final String UPDATED_SECTOR_CODE = "BBBBBBBBBB";

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_GEOMETRY_REFERENCE = "AAAAAAAAAA";
    private static final String UPDATED_GEOMETRY_REFERENCE = "BBBBBBBBBB";

    private static final Instant DEFAULT_VALID_FROM = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_VALID_FROM = Instant.ofEpochMilli(1795370143700L);

    private static final Instant DEFAULT_VALID_UNTIL = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_VALID_UNTIL = Instant.ofEpochMilli(1795370143700L);

    private static final String ENTITY_API_URL = "/api/solar-sectors";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    @Autowired
    private ObjectMapper om;

    @Autowired
    private SolarSectorRepository solarSectorRepository;

    @Mock
    private SolarSectorRepository solarSectorRepositoryMock;

    @Autowired
    private SolarSectorMapper solarSectorMapper;

    @Mock
    private SolarSectorService solarSectorServiceMock;

    @Autowired
    private R2dbcEntityTemplate template;

    @Autowired
    private WebTestClient webTestClient;

    private SolarSector solarSector;

    private SolarSector insertedSolarSector;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static SolarSector createEntity(R2dbcEntityTemplate template) {
        SolarSector solarSector = new SolarSector()
            .sectorCode(DEFAULT_SECTOR_CODE)
            .name(DEFAULT_NAME)
            .geometryReference(DEFAULT_GEOMETRY_REFERENCE)
            .validFrom(DEFAULT_VALID_FROM)
            .validUntil(DEFAULT_VALID_UNTIL);
        // Add required entity
        Organization organization;
        organization = template.insert(OrganizationResourceIT.createEntity()).block();
        solarSector.setOrganization(organization);
        // Add required entity
        Installation installation;
        installation = template.insert(InstallationResourceIT.createEntity(template)).block();
        solarSector.setInstallation(installation);
        return solarSector;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static SolarSector createUpdatedEntity(R2dbcEntityTemplate template) {
        SolarSector updatedSolarSector = new SolarSector()
            .sectorCode(UPDATED_SECTOR_CODE)
            .name(UPDATED_NAME)
            .geometryReference(UPDATED_GEOMETRY_REFERENCE)
            .validFrom(UPDATED_VALID_FROM)
            .validUntil(UPDATED_VALID_UNTIL);
        // Add required entity
        Organization organization;
        organization = template.insert(OrganizationResourceIT.createUpdatedEntity()).block();
        updatedSolarSector.setOrganization(organization);
        // Add required entity
        Installation installation;
        installation = template.insert(InstallationResourceIT.createUpdatedEntity(template)).block();
        updatedSolarSector.setInstallation(installation);
        return updatedSolarSector;
    }

    public static void deleteEntities(R2dbcEntityTemplate template) {
        try {
            template.delete(SolarSector.class).all().block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
        OrganizationResourceIT.deleteEntities(template);
        InstallationResourceIT.deleteEntities(template);
    }

    @BeforeEach
    void initTest() {
        solarSector = createEntity(template);
    }

    @AfterEach
    void cleanup() {
        if (insertedSolarSector != null) {
            solarSectorRepository.delete(insertedSolarSector).block();
            insertedSolarSector = null;
        }
        deleteEntities(template);
    }

    @Test
    void createSolarSector() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the SolarSector
        SolarSectorDTO solarSectorDTO = solarSectorMapper.toDto(solarSector);
        var returnedSolarSectorDTO = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(solarSectorDTO))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(SolarSectorDTO.class)
            .returnResult()
            .getResponseBody();

        // Validate the SolarSector in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedSolarSector = solarSectorMapper.toEntity(returnedSolarSectorDTO);
        assertSolarSectorUpdatableFieldsEquals(returnedSolarSector, getPersistedSolarSector(returnedSolarSector));

        insertedSolarSector = returnedSolarSector;
    }

    @Test
    void createSolarSectorWithExistingId() throws Exception {
        // Create the SolarSector with an existing ID
        solarSector.setId(1L);
        SolarSectorDTO solarSectorDTO = solarSectorMapper.toDto(solarSector);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(solarSectorDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the SolarSector in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    void checkSectorCodeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        solarSector.setSectorCode(null);

        // Create the SolarSector, which fails.
        SolarSectorDTO solarSectorDTO = solarSectorMapper.toDto(solarSector);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(solarSectorDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        solarSector.setName(null);

        // Create the SolarSector, which fails.
        SolarSectorDTO solarSectorDTO = solarSectorMapper.toDto(solarSector);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(solarSectorDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkValidFromIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        solarSector.setValidFrom(null);

        // Create the SolarSector, which fails.
        SolarSectorDTO solarSectorDTO = solarSectorMapper.toDto(solarSector);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(solarSectorDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void getAllSolarSectorsAsStream() {
        // Initialize the database
        solarSectorRepository.save(solarSector).block();

        List<SolarSector> solarSectorList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(SolarSectorDTO.class)
            .getResponseBody()
            .map(solarSectorMapper::toEntity)
            .filter(solarSector::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(solarSectorList).isNotNull();
        assertThat(solarSectorList).hasSize(1);
        SolarSector testSolarSector = solarSectorList.getFirst();

        // Test fails because reactive api returns an empty object instead of null
        // assertSolarSectorAllPropertiesEquals(solarSector, testSolarSector);
        assertSolarSectorUpdatableFieldsEquals(solarSector, testSolarSector);
    }

    @Test
    void getAllSolarSectors() {
        // Initialize the database
        insertedSolarSector = solarSectorRepository.save(solarSector).block();

        // Get all the solarSectorList
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
            .value(hasItem(solarSector.getId().intValue()))
            .jsonPath("$.[*].sectorCode")
            .value(hasItem(DEFAULT_SECTOR_CODE))
            .jsonPath("$.[*].name")
            .value(hasItem(DEFAULT_NAME))
            .jsonPath("$.[*].geometryReference")
            .value(hasItem(DEFAULT_GEOMETRY_REFERENCE))
            .jsonPath("$.[*].validFrom")
            .value(hasItem(DEFAULT_VALID_FROM.toString()))
            .jsonPath("$.[*].validUntil")
            .value(hasItem(DEFAULT_VALID_UNTIL.toString()));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllSolarSectorsWithEagerRelationshipsIsEnabled() {
        when(solarSectorServiceMock.findAllWithEagerRelationships()).thenReturn(Flux.empty());

        webTestClient
            .get()
            .uri(ENTITY_API_URL + "?eagerload=true")
            .exchange()
            .expectStatus()
            .isOk();

        verify(solarSectorServiceMock, times(1)).findAllWithEagerRelationships();
    }

    @SuppressWarnings({ "unchecked" })
    void getAllSolarSectorsWithEagerRelationshipsIsNotEnabled() {
        when(solarSectorServiceMock.findAllWithEagerRelationships()).thenReturn(Flux.empty());

        webTestClient
            .get()
            .uri(ENTITY_API_URL + "?eagerload=false")
            .exchange()
            .expectStatus()
            .isOk();
        verify(solarSectorRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    void getSolarSector() {
        // Initialize the database
        insertedSolarSector = solarSectorRepository.save(solarSector).block();

        // Get the solarSector
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, solarSector.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(solarSector.getId().intValue()))
            .jsonPath("$.sectorCode")
            .value(is(DEFAULT_SECTOR_CODE))
            .jsonPath("$.name")
            .value(is(DEFAULT_NAME))
            .jsonPath("$.geometryReference")
            .value(is(DEFAULT_GEOMETRY_REFERENCE))
            .jsonPath("$.validFrom")
            .value(is(DEFAULT_VALID_FROM.toString()))
            .jsonPath("$.validUntil")
            .value(is(DEFAULT_VALID_UNTIL.toString()));
    }

    @Test
    void getNonExistingSolarSector() {
        // Get the solarSector
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingSolarSector() throws Exception {
        // Initialize the database
        insertedSolarSector = solarSectorRepository.save(solarSector).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the solarSector
        SolarSector updatedSolarSector = solarSectorRepository.findById(solarSector.getId()).block();
        updatedSolarSector
            .sectorCode(UPDATED_SECTOR_CODE)
            .name(UPDATED_NAME)
            .geometryReference(UPDATED_GEOMETRY_REFERENCE)
            .validFrom(UPDATED_VALID_FROM)
            .validUntil(UPDATED_VALID_UNTIL);
        SolarSectorDTO solarSectorDTO = solarSectorMapper.toDto(updatedSolarSector);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, solarSectorDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(solarSectorDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the SolarSector in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedSolarSectorToMatchAllProperties(updatedSolarSector);
    }

    @Test
    void putNonExistingSolarSector() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        solarSector.setId(longCount.incrementAndGet());

        // Create the SolarSector
        SolarSectorDTO solarSectorDTO = solarSectorMapper.toDto(solarSector);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, solarSectorDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(solarSectorDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the SolarSector in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchSolarSector() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        solarSector.setId(longCount.incrementAndGet());

        // Create the SolarSector
        SolarSectorDTO solarSectorDTO = solarSectorMapper.toDto(solarSector);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(solarSectorDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the SolarSector in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamSolarSector() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        solarSector.setId(longCount.incrementAndGet());

        // Create the SolarSector
        SolarSectorDTO solarSectorDTO = solarSectorMapper.toDto(solarSector);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(solarSectorDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the SolarSector in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateSolarSectorWithPatch() throws Exception {
        // Initialize the database
        insertedSolarSector = solarSectorRepository.save(solarSector).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the solarSector using partial update
        SolarSector partialUpdatedSolarSector = new SolarSector();
        partialUpdatedSolarSector.setId(solarSector.getId());

        partialUpdatedSolarSector.name(UPDATED_NAME).geometryReference(UPDATED_GEOMETRY_REFERENCE).validFrom(UPDATED_VALID_FROM);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedSolarSector.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedSolarSector))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the SolarSector in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertSolarSectorUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedSolarSector, solarSector),
            getPersistedSolarSector(solarSector)
        );
    }

    @Test
    void fullUpdateSolarSectorWithPatch() throws Exception {
        // Initialize the database
        insertedSolarSector = solarSectorRepository.save(solarSector).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the solarSector using partial update
        SolarSector partialUpdatedSolarSector = new SolarSector();
        partialUpdatedSolarSector.setId(solarSector.getId());

        partialUpdatedSolarSector
            .sectorCode(UPDATED_SECTOR_CODE)
            .name(UPDATED_NAME)
            .geometryReference(UPDATED_GEOMETRY_REFERENCE)
            .validFrom(UPDATED_VALID_FROM)
            .validUntil(UPDATED_VALID_UNTIL);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedSolarSector.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedSolarSector))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the SolarSector in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertSolarSectorUpdatableFieldsEquals(partialUpdatedSolarSector, getPersistedSolarSector(partialUpdatedSolarSector));
    }

    @Test
    void patchNonExistingSolarSector() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        solarSector.setId(longCount.incrementAndGet());

        // Create the SolarSector
        SolarSectorDTO solarSectorDTO = solarSectorMapper.toDto(solarSector);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, solarSectorDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(solarSectorDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the SolarSector in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchSolarSector() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        solarSector.setId(longCount.incrementAndGet());

        // Create the SolarSector
        SolarSectorDTO solarSectorDTO = solarSectorMapper.toDto(solarSector);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(solarSectorDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the SolarSector in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamSolarSector() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        solarSector.setId(longCount.incrementAndGet());

        // Create the SolarSector
        SolarSectorDTO solarSectorDTO = solarSectorMapper.toDto(solarSector);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(solarSectorDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the SolarSector in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteSolarSector() {
        // Initialize the database
        insertedSolarSector = solarSectorRepository.save(solarSector).block();

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the solarSector
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, solarSector.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return solarSectorRepository.count().block();
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

    protected SolarSector getPersistedSolarSector(SolarSector solarSector) {
        return solarSectorRepository.findById(solarSector.getId()).block();
    }

    protected void assertPersistedSolarSectorToMatchAllProperties(SolarSector expectedSolarSector) {
        // Test fails because reactive api returns an empty object instead of null
        // assertSolarSectorAllPropertiesEquals(expectedSolarSector, getPersistedSolarSector(expectedSolarSector));
        assertSolarSectorUpdatableFieldsEquals(expectedSolarSector, getPersistedSolarSector(expectedSolarSector));
    }

    protected void assertPersistedSolarSectorToMatchUpdatableProperties(SolarSector expectedSolarSector) {
        // Test fails because reactive api returns an empty object instead of null
        // assertSolarSectorAllUpdatablePropertiesEquals(expectedSolarSector, getPersistedSolarSector(expectedSolarSector));
        assertSolarSectorUpdatableFieldsEquals(expectedSolarSector, getPersistedSolarSector(expectedSolarSector));
    }
}

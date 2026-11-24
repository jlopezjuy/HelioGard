package ar.com.heliguard.app.web.rest;

import static ar.com.heliguard.app.domain.SolarArrayAsserts.*;
import static ar.com.heliguard.app.web.rest.TestUtil.createUpdateProxyForBean;
import static ar.com.heliguard.app.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

import ar.com.heliguard.app.IntegrationTest;
import ar.com.heliguard.app.domain.Installation;
import ar.com.heliguard.app.domain.Organization;
import ar.com.heliguard.app.domain.SolarArray;
import ar.com.heliguard.app.repository.SolarArrayRepository;
import ar.com.heliguard.app.service.SolarArrayService;
import ar.com.heliguard.app.service.dto.SolarArrayDTO;
import ar.com.heliguard.app.service.mapper.SolarArrayMapper;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
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
 * Integration tests for the {@link SolarArrayResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class SolarArrayResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final BigDecimal DEFAULT_AZIMUTH_DEGREES = new BigDecimal(0);
    private static final BigDecimal UPDATED_AZIMUTH_DEGREES = new BigDecimal(1);

    private static final BigDecimal DEFAULT_TILT_DEGREES = new BigDecimal(0);
    private static final BigDecimal UPDATED_TILT_DEGREES = new BigDecimal(1);

    private static final LocalDate DEFAULT_COMMISSIONED_ON = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_COMMISSIONED_ON = LocalDate.parse("2026-11-22");

    private static final String ENTITY_API_URL = "/api/solar-arrays";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    @Autowired
    private ObjectMapper om;

    @Autowired
    private SolarArrayRepository solarArrayRepository;

    @Mock
    private SolarArrayRepository solarArrayRepositoryMock;

    @Autowired
    private SolarArrayMapper solarArrayMapper;

    @Mock
    private SolarArrayService solarArrayServiceMock;

    @Autowired
    private R2dbcEntityTemplate template;

    @Autowired
    private WebTestClient webTestClient;

    private SolarArray solarArray;

    private SolarArray insertedSolarArray;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static SolarArray createEntity(R2dbcEntityTemplate template) {
        SolarArray solarArray = new SolarArray()
            .name(DEFAULT_NAME)
            .azimuthDegrees(DEFAULT_AZIMUTH_DEGREES)
            .tiltDegrees(DEFAULT_TILT_DEGREES)
            .commissionedOn(DEFAULT_COMMISSIONED_ON);
        // Add required entity
        Organization organization;
        organization = template.insert(OrganizationResourceIT.createEntity()).block();
        solarArray.setOrganization(organization);
        // Add required entity
        Installation installation;
        installation = template.insert(InstallationResourceIT.createEntity(template)).block();
        solarArray.setInstallation(installation);
        return solarArray;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static SolarArray createUpdatedEntity(R2dbcEntityTemplate template) {
        SolarArray updatedSolarArray = new SolarArray()
            .name(UPDATED_NAME)
            .azimuthDegrees(UPDATED_AZIMUTH_DEGREES)
            .tiltDegrees(UPDATED_TILT_DEGREES)
            .commissionedOn(UPDATED_COMMISSIONED_ON);
        // Add required entity
        Organization organization;
        organization = template.insert(OrganizationResourceIT.createUpdatedEntity()).block();
        updatedSolarArray.setOrganization(organization);
        // Add required entity
        Installation installation;
        installation = template.insert(InstallationResourceIT.createUpdatedEntity(template)).block();
        updatedSolarArray.setInstallation(installation);
        return updatedSolarArray;
    }

    public static void deleteEntities(R2dbcEntityTemplate template) {
        try {
            template.delete(SolarArray.class).all().block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
        OrganizationResourceIT.deleteEntities(template);
        InstallationResourceIT.deleteEntities(template);
    }

    @BeforeEach
    void initTest() {
        solarArray = createEntity(template);
    }

    @AfterEach
    void cleanup() {
        if (insertedSolarArray != null) {
            solarArrayRepository.delete(insertedSolarArray).block();
            insertedSolarArray = null;
        }
        deleteEntities(template);
    }

    @Test
    void createSolarArray() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the SolarArray
        SolarArrayDTO solarArrayDTO = solarArrayMapper.toDto(solarArray);
        var returnedSolarArrayDTO = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(solarArrayDTO))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(SolarArrayDTO.class)
            .returnResult()
            .getResponseBody();

        // Validate the SolarArray in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedSolarArray = solarArrayMapper.toEntity(returnedSolarArrayDTO);
        assertSolarArrayUpdatableFieldsEquals(returnedSolarArray, getPersistedSolarArray(returnedSolarArray));

        insertedSolarArray = returnedSolarArray;
    }

    @Test
    void createSolarArrayWithExistingId() throws Exception {
        // Create the SolarArray with an existing ID
        solarArray.setId(1L);
        SolarArrayDTO solarArrayDTO = solarArrayMapper.toDto(solarArray);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(solarArrayDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the SolarArray in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    void checkNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        solarArray.setName(null);

        // Create the SolarArray, which fails.
        SolarArrayDTO solarArrayDTO = solarArrayMapper.toDto(solarArray);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(solarArrayDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void getAllSolarArraysAsStream() {
        // Initialize the database
        solarArrayRepository.save(solarArray).block();

        List<SolarArray> solarArrayList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(SolarArrayDTO.class)
            .getResponseBody()
            .map(solarArrayMapper::toEntity)
            .filter(solarArray::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(solarArrayList).isNotNull();
        assertThat(solarArrayList).hasSize(1);
        SolarArray testSolarArray = solarArrayList.getFirst();

        // Test fails because reactive api returns an empty object instead of null
        // assertSolarArrayAllPropertiesEquals(solarArray, testSolarArray);
        assertSolarArrayUpdatableFieldsEquals(solarArray, testSolarArray);
    }

    @Test
    void getAllSolarArrays() {
        // Initialize the database
        insertedSolarArray = solarArrayRepository.save(solarArray).block();

        // Get all the solarArrayList
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
            .value(hasItem(solarArray.getId().intValue()))
            .jsonPath("$.[*].name")
            .value(hasItem(DEFAULT_NAME))
            .jsonPath("$.[*].azimuthDegrees")
            .value(hasItem(sameNumber(DEFAULT_AZIMUTH_DEGREES)))
            .jsonPath("$.[*].tiltDegrees")
            .value(hasItem(sameNumber(DEFAULT_TILT_DEGREES)))
            .jsonPath("$.[*].commissionedOn")
            .value(hasItem(DEFAULT_COMMISSIONED_ON.toString()));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllSolarArraysWithEagerRelationshipsIsEnabled() {
        when(solarArrayServiceMock.findAllWithEagerRelationships()).thenReturn(Flux.empty());

        webTestClient
            .get()
            .uri(ENTITY_API_URL + "?eagerload=true")
            .exchange()
            .expectStatus()
            .isOk();

        verify(solarArrayServiceMock, times(1)).findAllWithEagerRelationships();
    }

    @SuppressWarnings({ "unchecked" })
    void getAllSolarArraysWithEagerRelationshipsIsNotEnabled() {
        when(solarArrayServiceMock.findAllWithEagerRelationships()).thenReturn(Flux.empty());

        webTestClient
            .get()
            .uri(ENTITY_API_URL + "?eagerload=false")
            .exchange()
            .expectStatus()
            .isOk();
        verify(solarArrayRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    void getSolarArray() {
        // Initialize the database
        insertedSolarArray = solarArrayRepository.save(solarArray).block();

        // Get the solarArray
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, solarArray.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(solarArray.getId().intValue()))
            .jsonPath("$.name")
            .value(is(DEFAULT_NAME))
            .jsonPath("$.azimuthDegrees")
            .value(is(sameNumber(DEFAULT_AZIMUTH_DEGREES)))
            .jsonPath("$.tiltDegrees")
            .value(is(sameNumber(DEFAULT_TILT_DEGREES)))
            .jsonPath("$.commissionedOn")
            .value(is(DEFAULT_COMMISSIONED_ON.toString()));
    }

    @Test
    void getNonExistingSolarArray() {
        // Get the solarArray
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingSolarArray() throws Exception {
        // Initialize the database
        insertedSolarArray = solarArrayRepository.save(solarArray).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the solarArray
        SolarArray updatedSolarArray = solarArrayRepository.findById(solarArray.getId()).block();
        updatedSolarArray
            .name(UPDATED_NAME)
            .azimuthDegrees(UPDATED_AZIMUTH_DEGREES)
            .tiltDegrees(UPDATED_TILT_DEGREES)
            .commissionedOn(UPDATED_COMMISSIONED_ON);
        SolarArrayDTO solarArrayDTO = solarArrayMapper.toDto(updatedSolarArray);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, solarArrayDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(solarArrayDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the SolarArray in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedSolarArrayToMatchAllProperties(updatedSolarArray);
    }

    @Test
    void putNonExistingSolarArray() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        solarArray.setId(longCount.incrementAndGet());

        // Create the SolarArray
        SolarArrayDTO solarArrayDTO = solarArrayMapper.toDto(solarArray);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, solarArrayDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(solarArrayDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the SolarArray in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchSolarArray() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        solarArray.setId(longCount.incrementAndGet());

        // Create the SolarArray
        SolarArrayDTO solarArrayDTO = solarArrayMapper.toDto(solarArray);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(solarArrayDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the SolarArray in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamSolarArray() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        solarArray.setId(longCount.incrementAndGet());

        // Create the SolarArray
        SolarArrayDTO solarArrayDTO = solarArrayMapper.toDto(solarArray);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(solarArrayDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the SolarArray in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateSolarArrayWithPatch() throws Exception {
        // Initialize the database
        insertedSolarArray = solarArrayRepository.save(solarArray).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the solarArray using partial update
        SolarArray partialUpdatedSolarArray = new SolarArray();
        partialUpdatedSolarArray.setId(solarArray.getId());

        partialUpdatedSolarArray
            .name(UPDATED_NAME)
            .azimuthDegrees(UPDATED_AZIMUTH_DEGREES)
            .tiltDegrees(UPDATED_TILT_DEGREES)
            .commissionedOn(UPDATED_COMMISSIONED_ON);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedSolarArray.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedSolarArray))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the SolarArray in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertSolarArrayUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedSolarArray, solarArray),
            getPersistedSolarArray(solarArray)
        );
    }

    @Test
    void fullUpdateSolarArrayWithPatch() throws Exception {
        // Initialize the database
        insertedSolarArray = solarArrayRepository.save(solarArray).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the solarArray using partial update
        SolarArray partialUpdatedSolarArray = new SolarArray();
        partialUpdatedSolarArray.setId(solarArray.getId());

        partialUpdatedSolarArray
            .name(UPDATED_NAME)
            .azimuthDegrees(UPDATED_AZIMUTH_DEGREES)
            .tiltDegrees(UPDATED_TILT_DEGREES)
            .commissionedOn(UPDATED_COMMISSIONED_ON);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedSolarArray.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedSolarArray))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the SolarArray in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertSolarArrayUpdatableFieldsEquals(partialUpdatedSolarArray, getPersistedSolarArray(partialUpdatedSolarArray));
    }

    @Test
    void patchNonExistingSolarArray() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        solarArray.setId(longCount.incrementAndGet());

        // Create the SolarArray
        SolarArrayDTO solarArrayDTO = solarArrayMapper.toDto(solarArray);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, solarArrayDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(solarArrayDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the SolarArray in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchSolarArray() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        solarArray.setId(longCount.incrementAndGet());

        // Create the SolarArray
        SolarArrayDTO solarArrayDTO = solarArrayMapper.toDto(solarArray);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(solarArrayDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the SolarArray in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamSolarArray() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        solarArray.setId(longCount.incrementAndGet());

        // Create the SolarArray
        SolarArrayDTO solarArrayDTO = solarArrayMapper.toDto(solarArray);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(solarArrayDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the SolarArray in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteSolarArray() {
        // Initialize the database
        insertedSolarArray = solarArrayRepository.save(solarArray).block();

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the solarArray
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, solarArray.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return solarArrayRepository.count().block();
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

    protected SolarArray getPersistedSolarArray(SolarArray solarArray) {
        return solarArrayRepository.findById(solarArray.getId()).block();
    }

    protected void assertPersistedSolarArrayToMatchAllProperties(SolarArray expectedSolarArray) {
        // Test fails because reactive api returns an empty object instead of null
        // assertSolarArrayAllPropertiesEquals(expectedSolarArray, getPersistedSolarArray(expectedSolarArray));
        assertSolarArrayUpdatableFieldsEquals(expectedSolarArray, getPersistedSolarArray(expectedSolarArray));
    }

    protected void assertPersistedSolarArrayToMatchUpdatableProperties(SolarArray expectedSolarArray) {
        // Test fails because reactive api returns an empty object instead of null
        // assertSolarArrayAllUpdatablePropertiesEquals(expectedSolarArray, getPersistedSolarArray(expectedSolarArray));
        assertSolarArrayUpdatableFieldsEquals(expectedSolarArray, getPersistedSolarArray(expectedSolarArray));
    }
}

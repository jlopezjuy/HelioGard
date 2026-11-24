package ar.com.heliguard.app.web.rest;

import static ar.com.heliguard.app.domain.SolarStringAsserts.*;
import static ar.com.heliguard.app.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

import ar.com.heliguard.app.IntegrationTest;
import ar.com.heliguard.app.domain.Organization;
import ar.com.heliguard.app.domain.SolarArray;
import ar.com.heliguard.app.domain.SolarString;
import ar.com.heliguard.app.repository.SolarStringRepository;
import ar.com.heliguard.app.service.SolarStringService;
import ar.com.heliguard.app.service.dto.SolarStringDTO;
import ar.com.heliguard.app.service.mapper.SolarStringMapper;
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
 * Integration tests for the {@link SolarStringResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class SolarStringResourceIT {

    private static final String DEFAULT_STRING_CODE = "AAAAAAAAAA";
    private static final String UPDATED_STRING_CODE = "BBBBBBBBBB";

    private static final Integer DEFAULT_MODULES_IN_SERIES = 1;
    private static final Integer UPDATED_MODULES_IN_SERIES = 2;

    private static final Boolean DEFAULT_ACTIVE = false;
    private static final Boolean UPDATED_ACTIVE = true;

    private static final String ENTITY_API_URL = "/api/solar-strings";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    @Autowired
    private ObjectMapper om;

    @Autowired
    private SolarStringRepository solarStringRepository;

    @Mock
    private SolarStringRepository solarStringRepositoryMock;

    @Autowired
    private SolarStringMapper solarStringMapper;

    @Mock
    private SolarStringService solarStringServiceMock;

    @Autowired
    private R2dbcEntityTemplate template;

    @Autowired
    private WebTestClient webTestClient;

    private SolarString solarString;

    private SolarString insertedSolarString;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static SolarString createEntity(R2dbcEntityTemplate template) {
        SolarString solarString = new SolarString()
            .stringCode(DEFAULT_STRING_CODE)
            .modulesInSeries(DEFAULT_MODULES_IN_SERIES)
            .active(DEFAULT_ACTIVE);
        // Add required entity
        Organization organization;
        organization = template.insert(OrganizationResourceIT.createEntity()).block();
        solarString.setOrganization(organization);
        // Add required entity
        SolarArray solarArray;
        solarArray = template.insert(SolarArrayResourceIT.createEntity(template)).block();
        solarString.setSolarArray(solarArray);
        return solarString;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static SolarString createUpdatedEntity(R2dbcEntityTemplate template) {
        SolarString updatedSolarString = new SolarString()
            .stringCode(UPDATED_STRING_CODE)
            .modulesInSeries(UPDATED_MODULES_IN_SERIES)
            .active(UPDATED_ACTIVE);
        // Add required entity
        Organization organization;
        organization = template.insert(OrganizationResourceIT.createUpdatedEntity()).block();
        updatedSolarString.setOrganization(organization);
        // Add required entity
        SolarArray solarArray;
        solarArray = template.insert(SolarArrayResourceIT.createUpdatedEntity(template)).block();
        updatedSolarString.setSolarArray(solarArray);
        return updatedSolarString;
    }

    public static void deleteEntities(R2dbcEntityTemplate template) {
        try {
            template.delete(SolarString.class).all().block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
        OrganizationResourceIT.deleteEntities(template);
        SolarArrayResourceIT.deleteEntities(template);
    }

    @BeforeEach
    void initTest() {
        solarString = createEntity(template);
    }

    @AfterEach
    void cleanup() {
        if (insertedSolarString != null) {
            solarStringRepository.delete(insertedSolarString).block();
            insertedSolarString = null;
        }
        deleteEntities(template);
    }

    @Test
    void createSolarString() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the SolarString
        SolarStringDTO solarStringDTO = solarStringMapper.toDto(solarString);
        var returnedSolarStringDTO = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(solarStringDTO))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(SolarStringDTO.class)
            .returnResult()
            .getResponseBody();

        // Validate the SolarString in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedSolarString = solarStringMapper.toEntity(returnedSolarStringDTO);
        assertSolarStringUpdatableFieldsEquals(returnedSolarString, getPersistedSolarString(returnedSolarString));

        insertedSolarString = returnedSolarString;
    }

    @Test
    void createSolarStringWithExistingId() throws Exception {
        // Create the SolarString with an existing ID
        solarString.setId(1L);
        SolarStringDTO solarStringDTO = solarStringMapper.toDto(solarString);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(solarStringDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the SolarString in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    void checkStringCodeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        solarString.setStringCode(null);

        // Create the SolarString, which fails.
        SolarStringDTO solarStringDTO = solarStringMapper.toDto(solarString);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(solarStringDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkModulesInSeriesIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        solarString.setModulesInSeries(null);

        // Create the SolarString, which fails.
        SolarStringDTO solarStringDTO = solarStringMapper.toDto(solarString);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(solarStringDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkActiveIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        solarString.setActive(null);

        // Create the SolarString, which fails.
        SolarStringDTO solarStringDTO = solarStringMapper.toDto(solarString);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(solarStringDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void getAllSolarStringsAsStream() {
        // Initialize the database
        solarStringRepository.save(solarString).block();

        List<SolarString> solarStringList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(SolarStringDTO.class)
            .getResponseBody()
            .map(solarStringMapper::toEntity)
            .filter(solarString::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(solarStringList).isNotNull();
        assertThat(solarStringList).hasSize(1);
        SolarString testSolarString = solarStringList.getFirst();

        // Test fails because reactive api returns an empty object instead of null
        // assertSolarStringAllPropertiesEquals(solarString, testSolarString);
        assertSolarStringUpdatableFieldsEquals(solarString, testSolarString);
    }

    @Test
    void getAllSolarStrings() {
        // Initialize the database
        insertedSolarString = solarStringRepository.save(solarString).block();

        // Get all the solarStringList
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
            .value(hasItem(solarString.getId().intValue()))
            .jsonPath("$.[*].stringCode")
            .value(hasItem(DEFAULT_STRING_CODE))
            .jsonPath("$.[*].modulesInSeries")
            .value(hasItem(DEFAULT_MODULES_IN_SERIES))
            .jsonPath("$.[*].active")
            .value(hasItem(DEFAULT_ACTIVE));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllSolarStringsWithEagerRelationshipsIsEnabled() {
        when(solarStringServiceMock.findAllWithEagerRelationships()).thenReturn(Flux.empty());

        webTestClient
            .get()
            .uri(ENTITY_API_URL + "?eagerload=true")
            .exchange()
            .expectStatus()
            .isOk();

        verify(solarStringServiceMock, times(1)).findAllWithEagerRelationships();
    }

    @SuppressWarnings({ "unchecked" })
    void getAllSolarStringsWithEagerRelationshipsIsNotEnabled() {
        when(solarStringServiceMock.findAllWithEagerRelationships()).thenReturn(Flux.empty());

        webTestClient
            .get()
            .uri(ENTITY_API_URL + "?eagerload=false")
            .exchange()
            .expectStatus()
            .isOk();
        verify(solarStringRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    void getSolarString() {
        // Initialize the database
        insertedSolarString = solarStringRepository.save(solarString).block();

        // Get the solarString
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, solarString.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(solarString.getId().intValue()))
            .jsonPath("$.stringCode")
            .value(is(DEFAULT_STRING_CODE))
            .jsonPath("$.modulesInSeries")
            .value(is(DEFAULT_MODULES_IN_SERIES))
            .jsonPath("$.active")
            .value(is(DEFAULT_ACTIVE));
    }

    @Test
    void getNonExistingSolarString() {
        // Get the solarString
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingSolarString() throws Exception {
        // Initialize the database
        insertedSolarString = solarStringRepository.save(solarString).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the solarString
        SolarString updatedSolarString = solarStringRepository.findById(solarString.getId()).block();
        updatedSolarString.stringCode(UPDATED_STRING_CODE).modulesInSeries(UPDATED_MODULES_IN_SERIES).active(UPDATED_ACTIVE);
        SolarStringDTO solarStringDTO = solarStringMapper.toDto(updatedSolarString);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, solarStringDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(solarStringDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the SolarString in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedSolarStringToMatchAllProperties(updatedSolarString);
    }

    @Test
    void putNonExistingSolarString() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        solarString.setId(longCount.incrementAndGet());

        // Create the SolarString
        SolarStringDTO solarStringDTO = solarStringMapper.toDto(solarString);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, solarStringDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(solarStringDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the SolarString in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchSolarString() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        solarString.setId(longCount.incrementAndGet());

        // Create the SolarString
        SolarStringDTO solarStringDTO = solarStringMapper.toDto(solarString);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(solarStringDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the SolarString in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamSolarString() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        solarString.setId(longCount.incrementAndGet());

        // Create the SolarString
        SolarStringDTO solarStringDTO = solarStringMapper.toDto(solarString);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(solarStringDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the SolarString in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateSolarStringWithPatch() throws Exception {
        // Initialize the database
        insertedSolarString = solarStringRepository.save(solarString).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the solarString using partial update
        SolarString partialUpdatedSolarString = new SolarString();
        partialUpdatedSolarString.setId(solarString.getId());

        partialUpdatedSolarString.modulesInSeries(UPDATED_MODULES_IN_SERIES).active(UPDATED_ACTIVE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedSolarString.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedSolarString))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the SolarString in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertSolarStringUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedSolarString, solarString),
            getPersistedSolarString(solarString)
        );
    }

    @Test
    void fullUpdateSolarStringWithPatch() throws Exception {
        // Initialize the database
        insertedSolarString = solarStringRepository.save(solarString).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the solarString using partial update
        SolarString partialUpdatedSolarString = new SolarString();
        partialUpdatedSolarString.setId(solarString.getId());

        partialUpdatedSolarString.stringCode(UPDATED_STRING_CODE).modulesInSeries(UPDATED_MODULES_IN_SERIES).active(UPDATED_ACTIVE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedSolarString.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedSolarString))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the SolarString in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertSolarStringUpdatableFieldsEquals(partialUpdatedSolarString, getPersistedSolarString(partialUpdatedSolarString));
    }

    @Test
    void patchNonExistingSolarString() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        solarString.setId(longCount.incrementAndGet());

        // Create the SolarString
        SolarStringDTO solarStringDTO = solarStringMapper.toDto(solarString);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, solarStringDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(solarStringDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the SolarString in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchSolarString() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        solarString.setId(longCount.incrementAndGet());

        // Create the SolarString
        SolarStringDTO solarStringDTO = solarStringMapper.toDto(solarString);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(solarStringDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the SolarString in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamSolarString() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        solarString.setId(longCount.incrementAndGet());

        // Create the SolarString
        SolarStringDTO solarStringDTO = solarStringMapper.toDto(solarString);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(solarStringDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the SolarString in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteSolarString() {
        // Initialize the database
        insertedSolarString = solarStringRepository.save(solarString).block();

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the solarString
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, solarString.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return solarStringRepository.count().block();
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

    protected SolarString getPersistedSolarString(SolarString solarString) {
        return solarStringRepository.findById(solarString.getId()).block();
    }

    protected void assertPersistedSolarStringToMatchAllProperties(SolarString expectedSolarString) {
        // Test fails because reactive api returns an empty object instead of null
        // assertSolarStringAllPropertiesEquals(expectedSolarString, getPersistedSolarString(expectedSolarString));
        assertSolarStringUpdatableFieldsEquals(expectedSolarString, getPersistedSolarString(expectedSolarString));
    }

    protected void assertPersistedSolarStringToMatchUpdatableProperties(SolarString expectedSolarString) {
        // Test fails because reactive api returns an empty object instead of null
        // assertSolarStringAllUpdatablePropertiesEquals(expectedSolarString, getPersistedSolarString(expectedSolarString));
        assertSolarStringUpdatableFieldsEquals(expectedSolarString, getPersistedSolarString(expectedSolarString));
    }
}

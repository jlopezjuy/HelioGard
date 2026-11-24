package ar.com.heliguard.app.web.rest;

import static ar.com.heliguard.app.domain.MaintenancePlanVersionAsserts.*;
import static ar.com.heliguard.app.web.rest.TestUtil.createUpdateProxyForBean;
import static ar.com.heliguard.app.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

import ar.com.heliguard.app.IntegrationTest;
import ar.com.heliguard.app.domain.MaintenancePlan;
import ar.com.heliguard.app.domain.MaintenancePlanVersion;
import ar.com.heliguard.app.domain.Organization;
import ar.com.heliguard.app.repository.MaintenancePlanVersionRepository;
import ar.com.heliguard.app.service.MaintenancePlanVersionService;
import ar.com.heliguard.app.service.dto.MaintenancePlanVersionDTO;
import ar.com.heliguard.app.service.mapper.MaintenancePlanVersionMapper;
import java.math.BigDecimal;
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
 * Integration tests for the {@link MaintenancePlanVersionResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class MaintenancePlanVersionResourceIT {

    private static final String DEFAULT_VERSION_CODE = "AAAAAAAAAA";
    private static final String UPDATED_VERSION_CODE = "BBBBBBBBBB";

    private static final Instant DEFAULT_PUBLISHED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_PUBLISHED_AT = Instant.ofEpochMilli(1795370143700L);

    private static final String DEFAULT_CURRENCY_CODE = "AAA";
    private static final String UPDATED_CURRENCY_CODE = "BBB";

    private static final BigDecimal DEFAULT_RECURRING_AMOUNT = new BigDecimal(1);
    private static final BigDecimal UPDATED_RECURRING_AMOUNT = new BigDecimal(2);

    private static final String DEFAULT_TERMS_REFERENCE = "AAAAAAAAAA";
    private static final String UPDATED_TERMS_REFERENCE = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/maintenance-plan-versions";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    @Autowired
    private ObjectMapper om;

    @Autowired
    private MaintenancePlanVersionRepository maintenancePlanVersionRepository;

    @Mock
    private MaintenancePlanVersionRepository maintenancePlanVersionRepositoryMock;

    @Autowired
    private MaintenancePlanVersionMapper maintenancePlanVersionMapper;

    @Mock
    private MaintenancePlanVersionService maintenancePlanVersionServiceMock;

    @Autowired
    private R2dbcEntityTemplate template;

    @Autowired
    private WebTestClient webTestClient;

    private MaintenancePlanVersion maintenancePlanVersion;

    private MaintenancePlanVersion insertedMaintenancePlanVersion;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static MaintenancePlanVersion createEntity(R2dbcEntityTemplate template) {
        MaintenancePlanVersion maintenancePlanVersion = new MaintenancePlanVersion()
            .versionCode(DEFAULT_VERSION_CODE)
            .publishedAt(DEFAULT_PUBLISHED_AT)
            .currencyCode(DEFAULT_CURRENCY_CODE)
            .recurringAmount(DEFAULT_RECURRING_AMOUNT)
            .termsReference(DEFAULT_TERMS_REFERENCE);
        // Add required entity
        Organization organization;
        organization = template.insert(OrganizationResourceIT.createEntity()).block();
        maintenancePlanVersion.setOrganization(organization);
        // Add required entity
        MaintenancePlan maintenancePlan;
        maintenancePlan = template.insert(MaintenancePlanResourceIT.createEntity(template)).block();
        maintenancePlanVersion.setMaintenancePlan(maintenancePlan);
        return maintenancePlanVersion;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static MaintenancePlanVersion createUpdatedEntity(R2dbcEntityTemplate template) {
        MaintenancePlanVersion updatedMaintenancePlanVersion = new MaintenancePlanVersion()
            .versionCode(UPDATED_VERSION_CODE)
            .publishedAt(UPDATED_PUBLISHED_AT)
            .currencyCode(UPDATED_CURRENCY_CODE)
            .recurringAmount(UPDATED_RECURRING_AMOUNT)
            .termsReference(UPDATED_TERMS_REFERENCE);
        // Add required entity
        Organization organization;
        organization = template.insert(OrganizationResourceIT.createUpdatedEntity()).block();
        updatedMaintenancePlanVersion.setOrganization(organization);
        // Add required entity
        MaintenancePlan maintenancePlan;
        maintenancePlan = template.insert(MaintenancePlanResourceIT.createUpdatedEntity(template)).block();
        updatedMaintenancePlanVersion.setMaintenancePlan(maintenancePlan);
        return updatedMaintenancePlanVersion;
    }

    public static void deleteEntities(R2dbcEntityTemplate template) {
        try {
            template.delete(MaintenancePlanVersion.class).all().block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
        OrganizationResourceIT.deleteEntities(template);
        MaintenancePlanResourceIT.deleteEntities(template);
    }

    @BeforeEach
    void initTest() {
        maintenancePlanVersion = createEntity(template);
    }

    @AfterEach
    void cleanup() {
        if (insertedMaintenancePlanVersion != null) {
            maintenancePlanVersionRepository.delete(insertedMaintenancePlanVersion).block();
            insertedMaintenancePlanVersion = null;
        }
        deleteEntities(template);
    }

    @Test
    void createMaintenancePlanVersion() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the MaintenancePlanVersion
        MaintenancePlanVersionDTO maintenancePlanVersionDTO = maintenancePlanVersionMapper.toDto(maintenancePlanVersion);
        var returnedMaintenancePlanVersionDTO = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(maintenancePlanVersionDTO))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(MaintenancePlanVersionDTO.class)
            .returnResult()
            .getResponseBody();

        // Validate the MaintenancePlanVersion in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedMaintenancePlanVersion = maintenancePlanVersionMapper.toEntity(returnedMaintenancePlanVersionDTO);
        assertMaintenancePlanVersionUpdatableFieldsEquals(
            returnedMaintenancePlanVersion,
            getPersistedMaintenancePlanVersion(returnedMaintenancePlanVersion)
        );

        insertedMaintenancePlanVersion = returnedMaintenancePlanVersion;
    }

    @Test
    void createMaintenancePlanVersionWithExistingId() throws Exception {
        // Create the MaintenancePlanVersion with an existing ID
        maintenancePlanVersion.setId(1L);
        MaintenancePlanVersionDTO maintenancePlanVersionDTO = maintenancePlanVersionMapper.toDto(maintenancePlanVersion);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(maintenancePlanVersionDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the MaintenancePlanVersion in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    void checkVersionCodeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        maintenancePlanVersion.setVersionCode(null);

        // Create the MaintenancePlanVersion, which fails.
        MaintenancePlanVersionDTO maintenancePlanVersionDTO = maintenancePlanVersionMapper.toDto(maintenancePlanVersion);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(maintenancePlanVersionDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkCurrencyCodeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        maintenancePlanVersion.setCurrencyCode(null);

        // Create the MaintenancePlanVersion, which fails.
        MaintenancePlanVersionDTO maintenancePlanVersionDTO = maintenancePlanVersionMapper.toDto(maintenancePlanVersion);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(maintenancePlanVersionDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkRecurringAmountIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        maintenancePlanVersion.setRecurringAmount(null);

        // Create the MaintenancePlanVersion, which fails.
        MaintenancePlanVersionDTO maintenancePlanVersionDTO = maintenancePlanVersionMapper.toDto(maintenancePlanVersion);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(maintenancePlanVersionDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkTermsReferenceIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        maintenancePlanVersion.setTermsReference(null);

        // Create the MaintenancePlanVersion, which fails.
        MaintenancePlanVersionDTO maintenancePlanVersionDTO = maintenancePlanVersionMapper.toDto(maintenancePlanVersion);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(maintenancePlanVersionDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void getAllMaintenancePlanVersionsAsStream() {
        // Initialize the database
        maintenancePlanVersionRepository.save(maintenancePlanVersion).block();

        List<MaintenancePlanVersion> maintenancePlanVersionList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(MaintenancePlanVersionDTO.class)
            .getResponseBody()
            .map(maintenancePlanVersionMapper::toEntity)
            .filter(maintenancePlanVersion::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(maintenancePlanVersionList).isNotNull();
        assertThat(maintenancePlanVersionList).hasSize(1);
        MaintenancePlanVersion testMaintenancePlanVersion = maintenancePlanVersionList.getFirst();

        // Test fails because reactive api returns an empty object instead of null
        // assertMaintenancePlanVersionAllPropertiesEquals(maintenancePlanVersion, testMaintenancePlanVersion);
        assertMaintenancePlanVersionUpdatableFieldsEquals(maintenancePlanVersion, testMaintenancePlanVersion);
    }

    @Test
    void getAllMaintenancePlanVersions() {
        // Initialize the database
        insertedMaintenancePlanVersion = maintenancePlanVersionRepository.save(maintenancePlanVersion).block();

        // Get all the maintenancePlanVersionList
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
            .value(hasItem(maintenancePlanVersion.getId().intValue()))
            .jsonPath("$.[*].versionCode")
            .value(hasItem(DEFAULT_VERSION_CODE))
            .jsonPath("$.[*].publishedAt")
            .value(hasItem(DEFAULT_PUBLISHED_AT.toString()))
            .jsonPath("$.[*].currencyCode")
            .value(hasItem(DEFAULT_CURRENCY_CODE))
            .jsonPath("$.[*].recurringAmount")
            .value(hasItem(sameNumber(DEFAULT_RECURRING_AMOUNT)))
            .jsonPath("$.[*].termsReference")
            .value(hasItem(DEFAULT_TERMS_REFERENCE));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllMaintenancePlanVersionsWithEagerRelationshipsIsEnabled() {
        when(maintenancePlanVersionServiceMock.findAllWithEagerRelationships()).thenReturn(Flux.empty());

        webTestClient
            .get()
            .uri(ENTITY_API_URL + "?eagerload=true")
            .exchange()
            .expectStatus()
            .isOk();

        verify(maintenancePlanVersionServiceMock, times(1)).findAllWithEagerRelationships();
    }

    @SuppressWarnings({ "unchecked" })
    void getAllMaintenancePlanVersionsWithEagerRelationshipsIsNotEnabled() {
        when(maintenancePlanVersionServiceMock.findAllWithEagerRelationships()).thenReturn(Flux.empty());

        webTestClient
            .get()
            .uri(ENTITY_API_URL + "?eagerload=false")
            .exchange()
            .expectStatus()
            .isOk();
        verify(maintenancePlanVersionRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    void getMaintenancePlanVersion() {
        // Initialize the database
        insertedMaintenancePlanVersion = maintenancePlanVersionRepository.save(maintenancePlanVersion).block();

        // Get the maintenancePlanVersion
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, maintenancePlanVersion.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(maintenancePlanVersion.getId().intValue()))
            .jsonPath("$.versionCode")
            .value(is(DEFAULT_VERSION_CODE))
            .jsonPath("$.publishedAt")
            .value(is(DEFAULT_PUBLISHED_AT.toString()))
            .jsonPath("$.currencyCode")
            .value(is(DEFAULT_CURRENCY_CODE))
            .jsonPath("$.recurringAmount")
            .value(is(sameNumber(DEFAULT_RECURRING_AMOUNT)))
            .jsonPath("$.termsReference")
            .value(is(DEFAULT_TERMS_REFERENCE));
    }

    @Test
    void getNonExistingMaintenancePlanVersion() {
        // Get the maintenancePlanVersion
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingMaintenancePlanVersion() throws Exception {
        // Initialize the database
        insertedMaintenancePlanVersion = maintenancePlanVersionRepository.save(maintenancePlanVersion).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the maintenancePlanVersion
        MaintenancePlanVersion updatedMaintenancePlanVersion = maintenancePlanVersionRepository
            .findById(maintenancePlanVersion.getId())
            .block();
        updatedMaintenancePlanVersion
            .versionCode(UPDATED_VERSION_CODE)
            .publishedAt(UPDATED_PUBLISHED_AT)
            .currencyCode(UPDATED_CURRENCY_CODE)
            .recurringAmount(UPDATED_RECURRING_AMOUNT)
            .termsReference(UPDATED_TERMS_REFERENCE);
        MaintenancePlanVersionDTO maintenancePlanVersionDTO = maintenancePlanVersionMapper.toDto(updatedMaintenancePlanVersion);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, maintenancePlanVersionDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(maintenancePlanVersionDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the MaintenancePlanVersion in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedMaintenancePlanVersionToMatchAllProperties(updatedMaintenancePlanVersion);
    }

    @Test
    void putNonExistingMaintenancePlanVersion() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        maintenancePlanVersion.setId(longCount.incrementAndGet());

        // Create the MaintenancePlanVersion
        MaintenancePlanVersionDTO maintenancePlanVersionDTO = maintenancePlanVersionMapper.toDto(maintenancePlanVersion);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, maintenancePlanVersionDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(maintenancePlanVersionDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the MaintenancePlanVersion in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchMaintenancePlanVersion() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        maintenancePlanVersion.setId(longCount.incrementAndGet());

        // Create the MaintenancePlanVersion
        MaintenancePlanVersionDTO maintenancePlanVersionDTO = maintenancePlanVersionMapper.toDto(maintenancePlanVersion);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(maintenancePlanVersionDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the MaintenancePlanVersion in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamMaintenancePlanVersion() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        maintenancePlanVersion.setId(longCount.incrementAndGet());

        // Create the MaintenancePlanVersion
        MaintenancePlanVersionDTO maintenancePlanVersionDTO = maintenancePlanVersionMapper.toDto(maintenancePlanVersion);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(maintenancePlanVersionDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the MaintenancePlanVersion in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateMaintenancePlanVersionWithPatch() throws Exception {
        // Initialize the database
        insertedMaintenancePlanVersion = maintenancePlanVersionRepository.save(maintenancePlanVersion).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the maintenancePlanVersion using partial update
        MaintenancePlanVersion partialUpdatedMaintenancePlanVersion = new MaintenancePlanVersion();
        partialUpdatedMaintenancePlanVersion.setId(maintenancePlanVersion.getId());

        partialUpdatedMaintenancePlanVersion
            .publishedAt(UPDATED_PUBLISHED_AT)
            .recurringAmount(UPDATED_RECURRING_AMOUNT)
            .termsReference(UPDATED_TERMS_REFERENCE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedMaintenancePlanVersion.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedMaintenancePlanVersion))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the MaintenancePlanVersion in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertMaintenancePlanVersionUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedMaintenancePlanVersion, maintenancePlanVersion),
            getPersistedMaintenancePlanVersion(maintenancePlanVersion)
        );
    }

    @Test
    void fullUpdateMaintenancePlanVersionWithPatch() throws Exception {
        // Initialize the database
        insertedMaintenancePlanVersion = maintenancePlanVersionRepository.save(maintenancePlanVersion).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the maintenancePlanVersion using partial update
        MaintenancePlanVersion partialUpdatedMaintenancePlanVersion = new MaintenancePlanVersion();
        partialUpdatedMaintenancePlanVersion.setId(maintenancePlanVersion.getId());

        partialUpdatedMaintenancePlanVersion
            .versionCode(UPDATED_VERSION_CODE)
            .publishedAt(UPDATED_PUBLISHED_AT)
            .currencyCode(UPDATED_CURRENCY_CODE)
            .recurringAmount(UPDATED_RECURRING_AMOUNT)
            .termsReference(UPDATED_TERMS_REFERENCE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedMaintenancePlanVersion.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedMaintenancePlanVersion))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the MaintenancePlanVersion in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertMaintenancePlanVersionUpdatableFieldsEquals(
            partialUpdatedMaintenancePlanVersion,
            getPersistedMaintenancePlanVersion(partialUpdatedMaintenancePlanVersion)
        );
    }

    @Test
    void patchNonExistingMaintenancePlanVersion() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        maintenancePlanVersion.setId(longCount.incrementAndGet());

        // Create the MaintenancePlanVersion
        MaintenancePlanVersionDTO maintenancePlanVersionDTO = maintenancePlanVersionMapper.toDto(maintenancePlanVersion);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, maintenancePlanVersionDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(maintenancePlanVersionDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the MaintenancePlanVersion in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchMaintenancePlanVersion() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        maintenancePlanVersion.setId(longCount.incrementAndGet());

        // Create the MaintenancePlanVersion
        MaintenancePlanVersionDTO maintenancePlanVersionDTO = maintenancePlanVersionMapper.toDto(maintenancePlanVersion);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(maintenancePlanVersionDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the MaintenancePlanVersion in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamMaintenancePlanVersion() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        maintenancePlanVersion.setId(longCount.incrementAndGet());

        // Create the MaintenancePlanVersion
        MaintenancePlanVersionDTO maintenancePlanVersionDTO = maintenancePlanVersionMapper.toDto(maintenancePlanVersion);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(maintenancePlanVersionDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the MaintenancePlanVersion in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteMaintenancePlanVersion() {
        // Initialize the database
        insertedMaintenancePlanVersion = maintenancePlanVersionRepository.save(maintenancePlanVersion).block();

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the maintenancePlanVersion
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, maintenancePlanVersion.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return maintenancePlanVersionRepository.count().block();
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

    protected MaintenancePlanVersion getPersistedMaintenancePlanVersion(MaintenancePlanVersion maintenancePlanVersion) {
        return maintenancePlanVersionRepository.findById(maintenancePlanVersion.getId()).block();
    }

    protected void assertPersistedMaintenancePlanVersionToMatchAllProperties(MaintenancePlanVersion expectedMaintenancePlanVersion) {
        // Test fails because reactive api returns an empty object instead of null
        // assertMaintenancePlanVersionAllPropertiesEquals(expectedMaintenancePlanVersion, getPersistedMaintenancePlanVersion(expectedMaintenancePlanVersion));
        assertMaintenancePlanVersionUpdatableFieldsEquals(
            expectedMaintenancePlanVersion,
            getPersistedMaintenancePlanVersion(expectedMaintenancePlanVersion)
        );
    }

    protected void assertPersistedMaintenancePlanVersionToMatchUpdatableProperties(MaintenancePlanVersion expectedMaintenancePlanVersion) {
        // Test fails because reactive api returns an empty object instead of null
        // assertMaintenancePlanVersionAllUpdatablePropertiesEquals(expectedMaintenancePlanVersion, getPersistedMaintenancePlanVersion(expectedMaintenancePlanVersion));
        assertMaintenancePlanVersionUpdatableFieldsEquals(
            expectedMaintenancePlanVersion,
            getPersistedMaintenancePlanVersion(expectedMaintenancePlanVersion)
        );
    }
}

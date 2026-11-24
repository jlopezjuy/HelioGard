package ar.com.heliguard.app.web.rest;

import static ar.com.heliguard.app.domain.AlertRuleVersionAsserts.*;
import static ar.com.heliguard.app.web.rest.TestUtil.createUpdateProxyForBean;
import static ar.com.heliguard.app.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

import ar.com.heliguard.app.IntegrationTest;
import ar.com.heliguard.app.domain.AlertRule;
import ar.com.heliguard.app.domain.AlertRuleVersion;
import ar.com.heliguard.app.domain.Organization;
import ar.com.heliguard.app.domain.enumeration.RuleComparison;
import ar.com.heliguard.app.repository.AlertRuleVersionRepository;
import ar.com.heliguard.app.service.AlertRuleVersionService;
import ar.com.heliguard.app.service.dto.AlertRuleVersionDTO;
import ar.com.heliguard.app.service.mapper.AlertRuleVersionMapper;
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
 * Integration tests for the {@link AlertRuleVersionResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class AlertRuleVersionResourceIT {

    private static final String DEFAULT_VERSION_CODE = "AAAAAAAAAA";
    private static final String UPDATED_VERSION_CODE = "BBBBBBBBBB";

    private static final RuleComparison DEFAULT_COMPARATOR = RuleComparison.GREATER_THAN;
    private static final RuleComparison UPDATED_COMPARATOR = RuleComparison.LESS_THAN;

    private static final BigDecimal DEFAULT_THRESHOLD_VALUE = new BigDecimal(1);
    private static final BigDecimal UPDATED_THRESHOLD_VALUE = new BigDecimal(2);

    private static final BigDecimal DEFAULT_THRESHOLD_MAX_VALUE = new BigDecimal(1);
    private static final BigDecimal UPDATED_THRESHOLD_MAX_VALUE = new BigDecimal(2);

    private static final String DEFAULT_METRIC_UNIT_CODE = "AAAAAAAAAA";
    private static final String UPDATED_METRIC_UNIT_CODE = "BBBBBBBBBB";

    private static final Integer DEFAULT_WINDOW_SECONDS = 1;
    private static final Integer UPDATED_WINDOW_SECONDS = 2;

    private static final Integer DEFAULT_COOLDOWN_SECONDS = 0;
    private static final Integer UPDATED_COOLDOWN_SECONDS = 1;

    private static final BigDecimal DEFAULT_MIN_COVERAGE_PCT = new BigDecimal(0);
    private static final BigDecimal UPDATED_MIN_COVERAGE_PCT = new BigDecimal(1);

    private static final Instant DEFAULT_VALID_FROM = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_VALID_FROM = Instant.ofEpochMilli(1795370143700L);

    private static final Instant DEFAULT_VALID_UNTIL = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_VALID_UNTIL = Instant.ofEpochMilli(1795370143700L);

    private static final String ENTITY_API_URL = "/api/alert-rule-versions";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    @Autowired
    private ObjectMapper om;

    @Autowired
    private AlertRuleVersionRepository alertRuleVersionRepository;

    @Mock
    private AlertRuleVersionRepository alertRuleVersionRepositoryMock;

    @Autowired
    private AlertRuleVersionMapper alertRuleVersionMapper;

    @Mock
    private AlertRuleVersionService alertRuleVersionServiceMock;

    @Autowired
    private R2dbcEntityTemplate template;

    @Autowired
    private WebTestClient webTestClient;

    private AlertRuleVersion alertRuleVersion;

    private AlertRuleVersion insertedAlertRuleVersion;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static AlertRuleVersion createEntity(R2dbcEntityTemplate template) {
        AlertRuleVersion alertRuleVersion = new AlertRuleVersion()
            .versionCode(DEFAULT_VERSION_CODE)
            .comparator(DEFAULT_COMPARATOR)
            .thresholdValue(DEFAULT_THRESHOLD_VALUE)
            .thresholdMaxValue(DEFAULT_THRESHOLD_MAX_VALUE)
            .metricUnitCode(DEFAULT_METRIC_UNIT_CODE)
            .windowSeconds(DEFAULT_WINDOW_SECONDS)
            .cooldownSeconds(DEFAULT_COOLDOWN_SECONDS)
            .minCoveragePct(DEFAULT_MIN_COVERAGE_PCT)
            .validFrom(DEFAULT_VALID_FROM)
            .validUntil(DEFAULT_VALID_UNTIL);
        // Add required entity
        Organization organization;
        organization = template.insert(OrganizationResourceIT.createEntity()).block();
        alertRuleVersion.setOrganization(organization);
        // Add required entity
        AlertRule alertRule;
        alertRule = template.insert(AlertRuleResourceIT.createEntity(template)).block();
        alertRuleVersion.setAlertRule(alertRule);
        return alertRuleVersion;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static AlertRuleVersion createUpdatedEntity(R2dbcEntityTemplate template) {
        AlertRuleVersion updatedAlertRuleVersion = new AlertRuleVersion()
            .versionCode(UPDATED_VERSION_CODE)
            .comparator(UPDATED_COMPARATOR)
            .thresholdValue(UPDATED_THRESHOLD_VALUE)
            .thresholdMaxValue(UPDATED_THRESHOLD_MAX_VALUE)
            .metricUnitCode(UPDATED_METRIC_UNIT_CODE)
            .windowSeconds(UPDATED_WINDOW_SECONDS)
            .cooldownSeconds(UPDATED_COOLDOWN_SECONDS)
            .minCoveragePct(UPDATED_MIN_COVERAGE_PCT)
            .validFrom(UPDATED_VALID_FROM)
            .validUntil(UPDATED_VALID_UNTIL);
        // Add required entity
        Organization organization;
        organization = template.insert(OrganizationResourceIT.createUpdatedEntity()).block();
        updatedAlertRuleVersion.setOrganization(organization);
        // Add required entity
        AlertRule alertRule;
        alertRule = template.insert(AlertRuleResourceIT.createUpdatedEntity(template)).block();
        updatedAlertRuleVersion.setAlertRule(alertRule);
        return updatedAlertRuleVersion;
    }

    public static void deleteEntities(R2dbcEntityTemplate template) {
        try {
            template.delete(AlertRuleVersion.class).all().block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
        OrganizationResourceIT.deleteEntities(template);
        AlertRuleResourceIT.deleteEntities(template);
    }

    @BeforeEach
    void initTest() {
        alertRuleVersion = createEntity(template);
    }

    @AfterEach
    void cleanup() {
        if (insertedAlertRuleVersion != null) {
            alertRuleVersionRepository.delete(insertedAlertRuleVersion).block();
            insertedAlertRuleVersion = null;
        }
        deleteEntities(template);
    }

    @Test
    void createAlertRuleVersion() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the AlertRuleVersion
        AlertRuleVersionDTO alertRuleVersionDTO = alertRuleVersionMapper.toDto(alertRuleVersion);
        var returnedAlertRuleVersionDTO = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(alertRuleVersionDTO))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(AlertRuleVersionDTO.class)
            .returnResult()
            .getResponseBody();

        // Validate the AlertRuleVersion in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedAlertRuleVersion = alertRuleVersionMapper.toEntity(returnedAlertRuleVersionDTO);
        assertAlertRuleVersionUpdatableFieldsEquals(returnedAlertRuleVersion, getPersistedAlertRuleVersion(returnedAlertRuleVersion));

        insertedAlertRuleVersion = returnedAlertRuleVersion;
    }

    @Test
    void createAlertRuleVersionWithExistingId() throws Exception {
        // Create the AlertRuleVersion with an existing ID
        alertRuleVersion.setId(1L);
        AlertRuleVersionDTO alertRuleVersionDTO = alertRuleVersionMapper.toDto(alertRuleVersion);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(alertRuleVersionDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the AlertRuleVersion in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    void checkVersionCodeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        alertRuleVersion.setVersionCode(null);

        // Create the AlertRuleVersion, which fails.
        AlertRuleVersionDTO alertRuleVersionDTO = alertRuleVersionMapper.toDto(alertRuleVersion);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(alertRuleVersionDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkComparatorIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        alertRuleVersion.setComparator(null);

        // Create the AlertRuleVersion, which fails.
        AlertRuleVersionDTO alertRuleVersionDTO = alertRuleVersionMapper.toDto(alertRuleVersion);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(alertRuleVersionDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkValidFromIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        alertRuleVersion.setValidFrom(null);

        // Create the AlertRuleVersion, which fails.
        AlertRuleVersionDTO alertRuleVersionDTO = alertRuleVersionMapper.toDto(alertRuleVersion);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(alertRuleVersionDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void getAllAlertRuleVersionsAsStream() {
        // Initialize the database
        alertRuleVersionRepository.save(alertRuleVersion).block();

        List<AlertRuleVersion> alertRuleVersionList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(AlertRuleVersionDTO.class)
            .getResponseBody()
            .map(alertRuleVersionMapper::toEntity)
            .filter(alertRuleVersion::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(alertRuleVersionList).isNotNull();
        assertThat(alertRuleVersionList).hasSize(1);
        AlertRuleVersion testAlertRuleVersion = alertRuleVersionList.getFirst();

        // Test fails because reactive api returns an empty object instead of null
        // assertAlertRuleVersionAllPropertiesEquals(alertRuleVersion, testAlertRuleVersion);
        assertAlertRuleVersionUpdatableFieldsEquals(alertRuleVersion, testAlertRuleVersion);
    }

    @Test
    void getAllAlertRuleVersions() {
        // Initialize the database
        insertedAlertRuleVersion = alertRuleVersionRepository.save(alertRuleVersion).block();

        // Get all the alertRuleVersionList
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
            .value(hasItem(alertRuleVersion.getId().intValue()))
            .jsonPath("$.[*].versionCode")
            .value(hasItem(DEFAULT_VERSION_CODE))
            .jsonPath("$.[*].comparator")
            .value(hasItem(DEFAULT_COMPARATOR.toString()))
            .jsonPath("$.[*].thresholdValue")
            .value(hasItem(sameNumber(DEFAULT_THRESHOLD_VALUE)))
            .jsonPath("$.[*].thresholdMaxValue")
            .value(hasItem(sameNumber(DEFAULT_THRESHOLD_MAX_VALUE)))
            .jsonPath("$.[*].metricUnitCode")
            .value(hasItem(DEFAULT_METRIC_UNIT_CODE))
            .jsonPath("$.[*].windowSeconds")
            .value(hasItem(DEFAULT_WINDOW_SECONDS))
            .jsonPath("$.[*].cooldownSeconds")
            .value(hasItem(DEFAULT_COOLDOWN_SECONDS))
            .jsonPath("$.[*].minCoveragePct")
            .value(hasItem(sameNumber(DEFAULT_MIN_COVERAGE_PCT)))
            .jsonPath("$.[*].validFrom")
            .value(hasItem(DEFAULT_VALID_FROM.toString()))
            .jsonPath("$.[*].validUntil")
            .value(hasItem(DEFAULT_VALID_UNTIL.toString()));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllAlertRuleVersionsWithEagerRelationshipsIsEnabled() {
        when(alertRuleVersionServiceMock.findAllWithEagerRelationships()).thenReturn(Flux.empty());

        webTestClient
            .get()
            .uri(ENTITY_API_URL + "?eagerload=true")
            .exchange()
            .expectStatus()
            .isOk();

        verify(alertRuleVersionServiceMock, times(1)).findAllWithEagerRelationships();
    }

    @SuppressWarnings({ "unchecked" })
    void getAllAlertRuleVersionsWithEagerRelationshipsIsNotEnabled() {
        when(alertRuleVersionServiceMock.findAllWithEagerRelationships()).thenReturn(Flux.empty());

        webTestClient
            .get()
            .uri(ENTITY_API_URL + "?eagerload=false")
            .exchange()
            .expectStatus()
            .isOk();
        verify(alertRuleVersionRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    void getAlertRuleVersion() {
        // Initialize the database
        insertedAlertRuleVersion = alertRuleVersionRepository.save(alertRuleVersion).block();

        // Get the alertRuleVersion
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, alertRuleVersion.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(alertRuleVersion.getId().intValue()))
            .jsonPath("$.versionCode")
            .value(is(DEFAULT_VERSION_CODE))
            .jsonPath("$.comparator")
            .value(is(DEFAULT_COMPARATOR.toString()))
            .jsonPath("$.thresholdValue")
            .value(is(sameNumber(DEFAULT_THRESHOLD_VALUE)))
            .jsonPath("$.thresholdMaxValue")
            .value(is(sameNumber(DEFAULT_THRESHOLD_MAX_VALUE)))
            .jsonPath("$.metricUnitCode")
            .value(is(DEFAULT_METRIC_UNIT_CODE))
            .jsonPath("$.windowSeconds")
            .value(is(DEFAULT_WINDOW_SECONDS))
            .jsonPath("$.cooldownSeconds")
            .value(is(DEFAULT_COOLDOWN_SECONDS))
            .jsonPath("$.minCoveragePct")
            .value(is(sameNumber(DEFAULT_MIN_COVERAGE_PCT)))
            .jsonPath("$.validFrom")
            .value(is(DEFAULT_VALID_FROM.toString()))
            .jsonPath("$.validUntil")
            .value(is(DEFAULT_VALID_UNTIL.toString()));
    }

    @Test
    void getNonExistingAlertRuleVersion() {
        // Get the alertRuleVersion
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingAlertRuleVersion() throws Exception {
        // Initialize the database
        insertedAlertRuleVersion = alertRuleVersionRepository.save(alertRuleVersion).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the alertRuleVersion
        AlertRuleVersion updatedAlertRuleVersion = alertRuleVersionRepository.findById(alertRuleVersion.getId()).block();
        updatedAlertRuleVersion
            .versionCode(UPDATED_VERSION_CODE)
            .comparator(UPDATED_COMPARATOR)
            .thresholdValue(UPDATED_THRESHOLD_VALUE)
            .thresholdMaxValue(UPDATED_THRESHOLD_MAX_VALUE)
            .metricUnitCode(UPDATED_METRIC_UNIT_CODE)
            .windowSeconds(UPDATED_WINDOW_SECONDS)
            .cooldownSeconds(UPDATED_COOLDOWN_SECONDS)
            .minCoveragePct(UPDATED_MIN_COVERAGE_PCT)
            .validFrom(UPDATED_VALID_FROM)
            .validUntil(UPDATED_VALID_UNTIL);
        AlertRuleVersionDTO alertRuleVersionDTO = alertRuleVersionMapper.toDto(updatedAlertRuleVersion);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, alertRuleVersionDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(alertRuleVersionDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the AlertRuleVersion in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedAlertRuleVersionToMatchAllProperties(updatedAlertRuleVersion);
    }

    @Test
    void putNonExistingAlertRuleVersion() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        alertRuleVersion.setId(longCount.incrementAndGet());

        // Create the AlertRuleVersion
        AlertRuleVersionDTO alertRuleVersionDTO = alertRuleVersionMapper.toDto(alertRuleVersion);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, alertRuleVersionDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(alertRuleVersionDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the AlertRuleVersion in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchAlertRuleVersion() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        alertRuleVersion.setId(longCount.incrementAndGet());

        // Create the AlertRuleVersion
        AlertRuleVersionDTO alertRuleVersionDTO = alertRuleVersionMapper.toDto(alertRuleVersion);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(alertRuleVersionDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the AlertRuleVersion in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamAlertRuleVersion() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        alertRuleVersion.setId(longCount.incrementAndGet());

        // Create the AlertRuleVersion
        AlertRuleVersionDTO alertRuleVersionDTO = alertRuleVersionMapper.toDto(alertRuleVersion);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(alertRuleVersionDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the AlertRuleVersion in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateAlertRuleVersionWithPatch() throws Exception {
        // Initialize the database
        insertedAlertRuleVersion = alertRuleVersionRepository.save(alertRuleVersion).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the alertRuleVersion using partial update
        AlertRuleVersion partialUpdatedAlertRuleVersion = new AlertRuleVersion();
        partialUpdatedAlertRuleVersion.setId(alertRuleVersion.getId());

        partialUpdatedAlertRuleVersion
            .comparator(UPDATED_COMPARATOR)
            .thresholdMaxValue(UPDATED_THRESHOLD_MAX_VALUE)
            .validUntil(UPDATED_VALID_UNTIL);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedAlertRuleVersion.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedAlertRuleVersion))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the AlertRuleVersion in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertAlertRuleVersionUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedAlertRuleVersion, alertRuleVersion),
            getPersistedAlertRuleVersion(alertRuleVersion)
        );
    }

    @Test
    void fullUpdateAlertRuleVersionWithPatch() throws Exception {
        // Initialize the database
        insertedAlertRuleVersion = alertRuleVersionRepository.save(alertRuleVersion).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the alertRuleVersion using partial update
        AlertRuleVersion partialUpdatedAlertRuleVersion = new AlertRuleVersion();
        partialUpdatedAlertRuleVersion.setId(alertRuleVersion.getId());

        partialUpdatedAlertRuleVersion
            .versionCode(UPDATED_VERSION_CODE)
            .comparator(UPDATED_COMPARATOR)
            .thresholdValue(UPDATED_THRESHOLD_VALUE)
            .thresholdMaxValue(UPDATED_THRESHOLD_MAX_VALUE)
            .metricUnitCode(UPDATED_METRIC_UNIT_CODE)
            .windowSeconds(UPDATED_WINDOW_SECONDS)
            .cooldownSeconds(UPDATED_COOLDOWN_SECONDS)
            .minCoveragePct(UPDATED_MIN_COVERAGE_PCT)
            .validFrom(UPDATED_VALID_FROM)
            .validUntil(UPDATED_VALID_UNTIL);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedAlertRuleVersion.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedAlertRuleVersion))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the AlertRuleVersion in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertAlertRuleVersionUpdatableFieldsEquals(
            partialUpdatedAlertRuleVersion,
            getPersistedAlertRuleVersion(partialUpdatedAlertRuleVersion)
        );
    }

    @Test
    void patchNonExistingAlertRuleVersion() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        alertRuleVersion.setId(longCount.incrementAndGet());

        // Create the AlertRuleVersion
        AlertRuleVersionDTO alertRuleVersionDTO = alertRuleVersionMapper.toDto(alertRuleVersion);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, alertRuleVersionDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(alertRuleVersionDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the AlertRuleVersion in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchAlertRuleVersion() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        alertRuleVersion.setId(longCount.incrementAndGet());

        // Create the AlertRuleVersion
        AlertRuleVersionDTO alertRuleVersionDTO = alertRuleVersionMapper.toDto(alertRuleVersion);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(alertRuleVersionDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the AlertRuleVersion in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamAlertRuleVersion() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        alertRuleVersion.setId(longCount.incrementAndGet());

        // Create the AlertRuleVersion
        AlertRuleVersionDTO alertRuleVersionDTO = alertRuleVersionMapper.toDto(alertRuleVersion);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(alertRuleVersionDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the AlertRuleVersion in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteAlertRuleVersion() {
        // Initialize the database
        insertedAlertRuleVersion = alertRuleVersionRepository.save(alertRuleVersion).block();

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the alertRuleVersion
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, alertRuleVersion.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return alertRuleVersionRepository.count().block();
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

    protected AlertRuleVersion getPersistedAlertRuleVersion(AlertRuleVersion alertRuleVersion) {
        return alertRuleVersionRepository.findById(alertRuleVersion.getId()).block();
    }

    protected void assertPersistedAlertRuleVersionToMatchAllProperties(AlertRuleVersion expectedAlertRuleVersion) {
        // Test fails because reactive api returns an empty object instead of null
        // assertAlertRuleVersionAllPropertiesEquals(expectedAlertRuleVersion, getPersistedAlertRuleVersion(expectedAlertRuleVersion));
        assertAlertRuleVersionUpdatableFieldsEquals(expectedAlertRuleVersion, getPersistedAlertRuleVersion(expectedAlertRuleVersion));
    }

    protected void assertPersistedAlertRuleVersionToMatchUpdatableProperties(AlertRuleVersion expectedAlertRuleVersion) {
        // Test fails because reactive api returns an empty object instead of null
        // assertAlertRuleVersionAllUpdatablePropertiesEquals(expectedAlertRuleVersion, getPersistedAlertRuleVersion(expectedAlertRuleVersion));
        assertAlertRuleVersionUpdatableFieldsEquals(expectedAlertRuleVersion, getPersistedAlertRuleVersion(expectedAlertRuleVersion));
    }
}

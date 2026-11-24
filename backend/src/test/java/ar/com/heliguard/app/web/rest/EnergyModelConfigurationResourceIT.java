package ar.com.heliguard.app.web.rest;

import static ar.com.heliguard.app.domain.EnergyModelConfigurationAsserts.*;
import static ar.com.heliguard.app.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

import ar.com.heliguard.app.IntegrationTest;
import ar.com.heliguard.app.domain.EnergyModelConfiguration;
import ar.com.heliguard.app.domain.Installation;
import ar.com.heliguard.app.domain.Organization;
import ar.com.heliguard.app.repository.EnergyModelConfigurationRepository;
import ar.com.heliguard.app.service.EnergyModelConfigurationService;
import ar.com.heliguard.app.service.dto.EnergyModelConfigurationDTO;
import ar.com.heliguard.app.service.mapper.EnergyModelConfigurationMapper;
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
 * Integration tests for the {@link EnergyModelConfigurationResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class EnergyModelConfigurationResourceIT {

    private static final String DEFAULT_ALGORITHM_CODE = "AAAAAAAAAA";
    private static final String UPDATED_ALGORITHM_CODE = "BBBBBBBBBB";

    private static final String DEFAULT_CONFIG_VERSION = "AAAAAAAAAA";
    private static final String UPDATED_CONFIG_VERSION = "BBBBBBBBBB";

    private static final String DEFAULT_PARAMETER_SNAPSHOT_REF = "AAAAAAAAAA";
    private static final String UPDATED_PARAMETER_SNAPSHOT_REF = "BBBBBBBBBB";

    private static final String DEFAULT_INPUT_FINGERPRINT = "AAAAAAAAAA";
    private static final String UPDATED_INPUT_FINGERPRINT = "BBBBBBBBBB";

    private static final Instant DEFAULT_CALIBRATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CALIBRATED_AT = Instant.ofEpochMilli(1795370143700L);

    private static final Instant DEFAULT_ACTIVE_FROM = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_ACTIVE_FROM = Instant.ofEpochMilli(1795370143700L);

    private static final Instant DEFAULT_ACTIVE_UNTIL = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_ACTIVE_UNTIL = Instant.ofEpochMilli(1795370143700L);

    private static final String ENTITY_API_URL = "/api/energy-model-configurations";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    @Autowired
    private ObjectMapper om;

    @Autowired
    private EnergyModelConfigurationRepository energyModelConfigurationRepository;

    @Mock
    private EnergyModelConfigurationRepository energyModelConfigurationRepositoryMock;

    @Autowired
    private EnergyModelConfigurationMapper energyModelConfigurationMapper;

    @Mock
    private EnergyModelConfigurationService energyModelConfigurationServiceMock;

    @Autowired
    private R2dbcEntityTemplate template;

    @Autowired
    private WebTestClient webTestClient;

    private EnergyModelConfiguration energyModelConfiguration;

    private EnergyModelConfiguration insertedEnergyModelConfiguration;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static EnergyModelConfiguration createEntity(R2dbcEntityTemplate template) {
        EnergyModelConfiguration energyModelConfiguration = new EnergyModelConfiguration()
            .algorithmCode(DEFAULT_ALGORITHM_CODE)
            .configVersion(DEFAULT_CONFIG_VERSION)
            .parameterSnapshotRef(DEFAULT_PARAMETER_SNAPSHOT_REF)
            .inputFingerprint(DEFAULT_INPUT_FINGERPRINT)
            .calibratedAt(DEFAULT_CALIBRATED_AT)
            .activeFrom(DEFAULT_ACTIVE_FROM)
            .activeUntil(DEFAULT_ACTIVE_UNTIL);
        // Add required entity
        Organization organization;
        organization = template.insert(OrganizationResourceIT.createEntity()).block();
        energyModelConfiguration.setOrganization(organization);
        // Add required entity
        Installation installation;
        installation = template.insert(InstallationResourceIT.createEntity(template)).block();
        energyModelConfiguration.setInstallation(installation);
        return energyModelConfiguration;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static EnergyModelConfiguration createUpdatedEntity(R2dbcEntityTemplate template) {
        EnergyModelConfiguration updatedEnergyModelConfiguration = new EnergyModelConfiguration()
            .algorithmCode(UPDATED_ALGORITHM_CODE)
            .configVersion(UPDATED_CONFIG_VERSION)
            .parameterSnapshotRef(UPDATED_PARAMETER_SNAPSHOT_REF)
            .inputFingerprint(UPDATED_INPUT_FINGERPRINT)
            .calibratedAt(UPDATED_CALIBRATED_AT)
            .activeFrom(UPDATED_ACTIVE_FROM)
            .activeUntil(UPDATED_ACTIVE_UNTIL);
        // Add required entity
        Organization organization;
        organization = template.insert(OrganizationResourceIT.createUpdatedEntity()).block();
        updatedEnergyModelConfiguration.setOrganization(organization);
        // Add required entity
        Installation installation;
        installation = template.insert(InstallationResourceIT.createUpdatedEntity(template)).block();
        updatedEnergyModelConfiguration.setInstallation(installation);
        return updatedEnergyModelConfiguration;
    }

    public static void deleteEntities(R2dbcEntityTemplate template) {
        try {
            template.delete(EnergyModelConfiguration.class).all().block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
        OrganizationResourceIT.deleteEntities(template);
        InstallationResourceIT.deleteEntities(template);
    }

    @BeforeEach
    void initTest() {
        energyModelConfiguration = createEntity(template);
    }

    @AfterEach
    void cleanup() {
        if (insertedEnergyModelConfiguration != null) {
            energyModelConfigurationRepository.delete(insertedEnergyModelConfiguration).block();
            insertedEnergyModelConfiguration = null;
        }
        deleteEntities(template);
    }

    @Test
    void createEnergyModelConfiguration() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the EnergyModelConfiguration
        EnergyModelConfigurationDTO energyModelConfigurationDTO = energyModelConfigurationMapper.toDto(energyModelConfiguration);
        var returnedEnergyModelConfigurationDTO = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(energyModelConfigurationDTO))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(EnergyModelConfigurationDTO.class)
            .returnResult()
            .getResponseBody();

        // Validate the EnergyModelConfiguration in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedEnergyModelConfiguration = energyModelConfigurationMapper.toEntity(returnedEnergyModelConfigurationDTO);
        assertEnergyModelConfigurationUpdatableFieldsEquals(
            returnedEnergyModelConfiguration,
            getPersistedEnergyModelConfiguration(returnedEnergyModelConfiguration)
        );

        insertedEnergyModelConfiguration = returnedEnergyModelConfiguration;
    }

    @Test
    void createEnergyModelConfigurationWithExistingId() throws Exception {
        // Create the EnergyModelConfiguration with an existing ID
        energyModelConfiguration.setId(1L);
        EnergyModelConfigurationDTO energyModelConfigurationDTO = energyModelConfigurationMapper.toDto(energyModelConfiguration);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(energyModelConfigurationDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the EnergyModelConfiguration in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    void checkAlgorithmCodeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        energyModelConfiguration.setAlgorithmCode(null);

        // Create the EnergyModelConfiguration, which fails.
        EnergyModelConfigurationDTO energyModelConfigurationDTO = energyModelConfigurationMapper.toDto(energyModelConfiguration);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(energyModelConfigurationDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkConfigVersionIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        energyModelConfiguration.setConfigVersion(null);

        // Create the EnergyModelConfiguration, which fails.
        EnergyModelConfigurationDTO energyModelConfigurationDTO = energyModelConfigurationMapper.toDto(energyModelConfiguration);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(energyModelConfigurationDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkParameterSnapshotRefIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        energyModelConfiguration.setParameterSnapshotRef(null);

        // Create the EnergyModelConfiguration, which fails.
        EnergyModelConfigurationDTO energyModelConfigurationDTO = energyModelConfigurationMapper.toDto(energyModelConfiguration);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(energyModelConfigurationDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkInputFingerprintIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        energyModelConfiguration.setInputFingerprint(null);

        // Create the EnergyModelConfiguration, which fails.
        EnergyModelConfigurationDTO energyModelConfigurationDTO = energyModelConfigurationMapper.toDto(energyModelConfiguration);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(energyModelConfigurationDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkActiveFromIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        energyModelConfiguration.setActiveFrom(null);

        // Create the EnergyModelConfiguration, which fails.
        EnergyModelConfigurationDTO energyModelConfigurationDTO = energyModelConfigurationMapper.toDto(energyModelConfiguration);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(energyModelConfigurationDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void getAllEnergyModelConfigurationsAsStream() {
        // Initialize the database
        energyModelConfigurationRepository.save(energyModelConfiguration).block();

        List<EnergyModelConfiguration> energyModelConfigurationList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(EnergyModelConfigurationDTO.class)
            .getResponseBody()
            .map(energyModelConfigurationMapper::toEntity)
            .filter(energyModelConfiguration::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(energyModelConfigurationList).isNotNull();
        assertThat(energyModelConfigurationList).hasSize(1);
        EnergyModelConfiguration testEnergyModelConfiguration = energyModelConfigurationList.getFirst();

        // Test fails because reactive api returns an empty object instead of null
        // assertEnergyModelConfigurationAllPropertiesEquals(energyModelConfiguration, testEnergyModelConfiguration);
        assertEnergyModelConfigurationUpdatableFieldsEquals(energyModelConfiguration, testEnergyModelConfiguration);
    }

    @Test
    void getAllEnergyModelConfigurations() {
        // Initialize the database
        insertedEnergyModelConfiguration = energyModelConfigurationRepository.save(energyModelConfiguration).block();

        // Get all the energyModelConfigurationList
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
            .value(hasItem(energyModelConfiguration.getId().intValue()))
            .jsonPath("$.[*].algorithmCode")
            .value(hasItem(DEFAULT_ALGORITHM_CODE))
            .jsonPath("$.[*].configVersion")
            .value(hasItem(DEFAULT_CONFIG_VERSION))
            .jsonPath("$.[*].parameterSnapshotRef")
            .value(hasItem(DEFAULT_PARAMETER_SNAPSHOT_REF))
            .jsonPath("$.[*].inputFingerprint")
            .value(hasItem(DEFAULT_INPUT_FINGERPRINT))
            .jsonPath("$.[*].calibratedAt")
            .value(hasItem(DEFAULT_CALIBRATED_AT.toString()))
            .jsonPath("$.[*].activeFrom")
            .value(hasItem(DEFAULT_ACTIVE_FROM.toString()))
            .jsonPath("$.[*].activeUntil")
            .value(hasItem(DEFAULT_ACTIVE_UNTIL.toString()));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllEnergyModelConfigurationsWithEagerRelationshipsIsEnabled() {
        when(energyModelConfigurationServiceMock.findAllWithEagerRelationships()).thenReturn(Flux.empty());

        webTestClient
            .get()
            .uri(ENTITY_API_URL + "?eagerload=true")
            .exchange()
            .expectStatus()
            .isOk();

        verify(energyModelConfigurationServiceMock, times(1)).findAllWithEagerRelationships();
    }

    @SuppressWarnings({ "unchecked" })
    void getAllEnergyModelConfigurationsWithEagerRelationshipsIsNotEnabled() {
        when(energyModelConfigurationServiceMock.findAllWithEagerRelationships()).thenReturn(Flux.empty());

        webTestClient
            .get()
            .uri(ENTITY_API_URL + "?eagerload=false")
            .exchange()
            .expectStatus()
            .isOk();
        verify(energyModelConfigurationRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    void getEnergyModelConfiguration() {
        // Initialize the database
        insertedEnergyModelConfiguration = energyModelConfigurationRepository.save(energyModelConfiguration).block();

        // Get the energyModelConfiguration
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, energyModelConfiguration.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(energyModelConfiguration.getId().intValue()))
            .jsonPath("$.algorithmCode")
            .value(is(DEFAULT_ALGORITHM_CODE))
            .jsonPath("$.configVersion")
            .value(is(DEFAULT_CONFIG_VERSION))
            .jsonPath("$.parameterSnapshotRef")
            .value(is(DEFAULT_PARAMETER_SNAPSHOT_REF))
            .jsonPath("$.inputFingerprint")
            .value(is(DEFAULT_INPUT_FINGERPRINT))
            .jsonPath("$.calibratedAt")
            .value(is(DEFAULT_CALIBRATED_AT.toString()))
            .jsonPath("$.activeFrom")
            .value(is(DEFAULT_ACTIVE_FROM.toString()))
            .jsonPath("$.activeUntil")
            .value(is(DEFAULT_ACTIVE_UNTIL.toString()));
    }

    @Test
    void getNonExistingEnergyModelConfiguration() {
        // Get the energyModelConfiguration
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingEnergyModelConfiguration() throws Exception {
        // Initialize the database
        insertedEnergyModelConfiguration = energyModelConfigurationRepository.save(energyModelConfiguration).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the energyModelConfiguration
        EnergyModelConfiguration updatedEnergyModelConfiguration = energyModelConfigurationRepository
            .findById(energyModelConfiguration.getId())
            .block();
        updatedEnergyModelConfiguration
            .algorithmCode(UPDATED_ALGORITHM_CODE)
            .configVersion(UPDATED_CONFIG_VERSION)
            .parameterSnapshotRef(UPDATED_PARAMETER_SNAPSHOT_REF)
            .inputFingerprint(UPDATED_INPUT_FINGERPRINT)
            .calibratedAt(UPDATED_CALIBRATED_AT)
            .activeFrom(UPDATED_ACTIVE_FROM)
            .activeUntil(UPDATED_ACTIVE_UNTIL);
        EnergyModelConfigurationDTO energyModelConfigurationDTO = energyModelConfigurationMapper.toDto(updatedEnergyModelConfiguration);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, energyModelConfigurationDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(energyModelConfigurationDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the EnergyModelConfiguration in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedEnergyModelConfigurationToMatchAllProperties(updatedEnergyModelConfiguration);
    }

    @Test
    void putNonExistingEnergyModelConfiguration() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        energyModelConfiguration.setId(longCount.incrementAndGet());

        // Create the EnergyModelConfiguration
        EnergyModelConfigurationDTO energyModelConfigurationDTO = energyModelConfigurationMapper.toDto(energyModelConfiguration);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, energyModelConfigurationDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(energyModelConfigurationDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the EnergyModelConfiguration in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchEnergyModelConfiguration() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        energyModelConfiguration.setId(longCount.incrementAndGet());

        // Create the EnergyModelConfiguration
        EnergyModelConfigurationDTO energyModelConfigurationDTO = energyModelConfigurationMapper.toDto(energyModelConfiguration);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(energyModelConfigurationDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the EnergyModelConfiguration in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamEnergyModelConfiguration() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        energyModelConfiguration.setId(longCount.incrementAndGet());

        // Create the EnergyModelConfiguration
        EnergyModelConfigurationDTO energyModelConfigurationDTO = energyModelConfigurationMapper.toDto(energyModelConfiguration);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(energyModelConfigurationDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the EnergyModelConfiguration in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateEnergyModelConfigurationWithPatch() throws Exception {
        // Initialize the database
        insertedEnergyModelConfiguration = energyModelConfigurationRepository.save(energyModelConfiguration).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the energyModelConfiguration using partial update
        EnergyModelConfiguration partialUpdatedEnergyModelConfiguration = new EnergyModelConfiguration();
        partialUpdatedEnergyModelConfiguration.setId(energyModelConfiguration.getId());

        partialUpdatedEnergyModelConfiguration
            .algorithmCode(UPDATED_ALGORITHM_CODE)
            .configVersion(UPDATED_CONFIG_VERSION)
            .inputFingerprint(UPDATED_INPUT_FINGERPRINT)
            .calibratedAt(UPDATED_CALIBRATED_AT);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedEnergyModelConfiguration.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedEnergyModelConfiguration))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the EnergyModelConfiguration in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertEnergyModelConfigurationUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedEnergyModelConfiguration, energyModelConfiguration),
            getPersistedEnergyModelConfiguration(energyModelConfiguration)
        );
    }

    @Test
    void fullUpdateEnergyModelConfigurationWithPatch() throws Exception {
        // Initialize the database
        insertedEnergyModelConfiguration = energyModelConfigurationRepository.save(energyModelConfiguration).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the energyModelConfiguration using partial update
        EnergyModelConfiguration partialUpdatedEnergyModelConfiguration = new EnergyModelConfiguration();
        partialUpdatedEnergyModelConfiguration.setId(energyModelConfiguration.getId());

        partialUpdatedEnergyModelConfiguration
            .algorithmCode(UPDATED_ALGORITHM_CODE)
            .configVersion(UPDATED_CONFIG_VERSION)
            .parameterSnapshotRef(UPDATED_PARAMETER_SNAPSHOT_REF)
            .inputFingerprint(UPDATED_INPUT_FINGERPRINT)
            .calibratedAt(UPDATED_CALIBRATED_AT)
            .activeFrom(UPDATED_ACTIVE_FROM)
            .activeUntil(UPDATED_ACTIVE_UNTIL);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedEnergyModelConfiguration.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedEnergyModelConfiguration))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the EnergyModelConfiguration in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertEnergyModelConfigurationUpdatableFieldsEquals(
            partialUpdatedEnergyModelConfiguration,
            getPersistedEnergyModelConfiguration(partialUpdatedEnergyModelConfiguration)
        );
    }

    @Test
    void patchNonExistingEnergyModelConfiguration() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        energyModelConfiguration.setId(longCount.incrementAndGet());

        // Create the EnergyModelConfiguration
        EnergyModelConfigurationDTO energyModelConfigurationDTO = energyModelConfigurationMapper.toDto(energyModelConfiguration);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, energyModelConfigurationDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(energyModelConfigurationDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the EnergyModelConfiguration in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchEnergyModelConfiguration() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        energyModelConfiguration.setId(longCount.incrementAndGet());

        // Create the EnergyModelConfiguration
        EnergyModelConfigurationDTO energyModelConfigurationDTO = energyModelConfigurationMapper.toDto(energyModelConfiguration);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(energyModelConfigurationDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the EnergyModelConfiguration in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamEnergyModelConfiguration() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        energyModelConfiguration.setId(longCount.incrementAndGet());

        // Create the EnergyModelConfiguration
        EnergyModelConfigurationDTO energyModelConfigurationDTO = energyModelConfigurationMapper.toDto(energyModelConfiguration);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(energyModelConfigurationDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the EnergyModelConfiguration in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteEnergyModelConfiguration() {
        // Initialize the database
        insertedEnergyModelConfiguration = energyModelConfigurationRepository.save(energyModelConfiguration).block();

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the energyModelConfiguration
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, energyModelConfiguration.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return energyModelConfigurationRepository.count().block();
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

    protected EnergyModelConfiguration getPersistedEnergyModelConfiguration(EnergyModelConfiguration energyModelConfiguration) {
        return energyModelConfigurationRepository.findById(energyModelConfiguration.getId()).block();
    }

    protected void assertPersistedEnergyModelConfigurationToMatchAllProperties(EnergyModelConfiguration expectedEnergyModelConfiguration) {
        // Test fails because reactive api returns an empty object instead of null
        // assertEnergyModelConfigurationAllPropertiesEquals(expectedEnergyModelConfiguration, getPersistedEnergyModelConfiguration(expectedEnergyModelConfiguration));
        assertEnergyModelConfigurationUpdatableFieldsEquals(
            expectedEnergyModelConfiguration,
            getPersistedEnergyModelConfiguration(expectedEnergyModelConfiguration)
        );
    }

    protected void assertPersistedEnergyModelConfigurationToMatchUpdatableProperties(
        EnergyModelConfiguration expectedEnergyModelConfiguration
    ) {
        // Test fails because reactive api returns an empty object instead of null
        // assertEnergyModelConfigurationAllUpdatablePropertiesEquals(expectedEnergyModelConfiguration, getPersistedEnergyModelConfiguration(expectedEnergyModelConfiguration));
        assertEnergyModelConfigurationUpdatableFieldsEquals(
            expectedEnergyModelConfiguration,
            getPersistedEnergyModelConfiguration(expectedEnergyModelConfiguration)
        );
    }
}

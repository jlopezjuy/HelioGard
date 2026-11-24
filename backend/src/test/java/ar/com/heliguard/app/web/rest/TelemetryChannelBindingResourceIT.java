package ar.com.heliguard.app.web.rest;

import static ar.com.heliguard.app.domain.TelemetryChannelBindingAsserts.*;
import static ar.com.heliguard.app.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

import ar.com.heliguard.app.IntegrationTest;
import ar.com.heliguard.app.domain.Gateway;
import ar.com.heliguard.app.domain.Organization;
import ar.com.heliguard.app.domain.TelemetryChannel;
import ar.com.heliguard.app.domain.TelemetryChannelBinding;
import ar.com.heliguard.app.repository.TelemetryChannelBindingRepository;
import ar.com.heliguard.app.service.TelemetryChannelBindingService;
import ar.com.heliguard.app.service.dto.TelemetryChannelBindingDTO;
import ar.com.heliguard.app.service.mapper.TelemetryChannelBindingMapper;
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
 * Integration tests for the {@link TelemetryChannelBindingResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class TelemetryChannelBindingResourceIT {

    private static final String DEFAULT_EXTERNAL_PATH = "AAAAAAAAAA";
    private static final String UPDATED_EXTERNAL_PATH = "BBBBBBBBBB";

    private static final Instant DEFAULT_VALID_FROM = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_VALID_FROM = Instant.ofEpochMilli(1795370143700L);

    private static final Instant DEFAULT_VALID_UNTIL = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_VALID_UNTIL = Instant.ofEpochMilli(1795370143700L);

    private static final String ENTITY_API_URL = "/api/telemetry-channel-bindings";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    @Autowired
    private ObjectMapper om;

    @Autowired
    private TelemetryChannelBindingRepository telemetryChannelBindingRepository;

    @Mock
    private TelemetryChannelBindingRepository telemetryChannelBindingRepositoryMock;

    @Autowired
    private TelemetryChannelBindingMapper telemetryChannelBindingMapper;

    @Mock
    private TelemetryChannelBindingService telemetryChannelBindingServiceMock;

    @Autowired
    private R2dbcEntityTemplate template;

    @Autowired
    private WebTestClient webTestClient;

    private TelemetryChannelBinding telemetryChannelBinding;

    private TelemetryChannelBinding insertedTelemetryChannelBinding;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TelemetryChannelBinding createEntity(R2dbcEntityTemplate template) {
        TelemetryChannelBinding telemetryChannelBinding = new TelemetryChannelBinding()
            .externalPath(DEFAULT_EXTERNAL_PATH)
            .validFrom(DEFAULT_VALID_FROM)
            .validUntil(DEFAULT_VALID_UNTIL);
        // Add required entity
        Organization organization;
        organization = template.insert(OrganizationResourceIT.createEntity()).block();
        telemetryChannelBinding.setOrganization(organization);
        // Add required entity
        TelemetryChannel telemetryChannel;
        telemetryChannel = template.insert(TelemetryChannelResourceIT.createEntity(template)).block();
        telemetryChannelBinding.setTelemetryChannel(telemetryChannel);
        // Add required entity
        Gateway gateway;
        gateway = template.insert(GatewayResourceIT.createEntity(template)).block();
        telemetryChannelBinding.setGateway(gateway);
        return telemetryChannelBinding;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TelemetryChannelBinding createUpdatedEntity(R2dbcEntityTemplate template) {
        TelemetryChannelBinding updatedTelemetryChannelBinding = new TelemetryChannelBinding()
            .externalPath(UPDATED_EXTERNAL_PATH)
            .validFrom(UPDATED_VALID_FROM)
            .validUntil(UPDATED_VALID_UNTIL);
        // Add required entity
        Organization organization;
        organization = template.insert(OrganizationResourceIT.createUpdatedEntity()).block();
        updatedTelemetryChannelBinding.setOrganization(organization);
        // Add required entity
        TelemetryChannel telemetryChannel;
        telemetryChannel = template.insert(TelemetryChannelResourceIT.createUpdatedEntity(template)).block();
        updatedTelemetryChannelBinding.setTelemetryChannel(telemetryChannel);
        // Add required entity
        Gateway gateway;
        gateway = template.insert(GatewayResourceIT.createUpdatedEntity(template)).block();
        updatedTelemetryChannelBinding.setGateway(gateway);
        return updatedTelemetryChannelBinding;
    }

    public static void deleteEntities(R2dbcEntityTemplate template) {
        try {
            template.delete(TelemetryChannelBinding.class).all().block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
        OrganizationResourceIT.deleteEntities(template);
        TelemetryChannelResourceIT.deleteEntities(template);
        GatewayResourceIT.deleteEntities(template);
    }

    @BeforeEach
    void initTest() {
        telemetryChannelBinding = createEntity(template);
    }

    @AfterEach
    void cleanup() {
        if (insertedTelemetryChannelBinding != null) {
            telemetryChannelBindingRepository.delete(insertedTelemetryChannelBinding).block();
            insertedTelemetryChannelBinding = null;
        }
        deleteEntities(template);
    }

    @Test
    void createTelemetryChannelBinding() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the TelemetryChannelBinding
        TelemetryChannelBindingDTO telemetryChannelBindingDTO = telemetryChannelBindingMapper.toDto(telemetryChannelBinding);
        var returnedTelemetryChannelBindingDTO = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(telemetryChannelBindingDTO))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(TelemetryChannelBindingDTO.class)
            .returnResult()
            .getResponseBody();

        // Validate the TelemetryChannelBinding in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedTelemetryChannelBinding = telemetryChannelBindingMapper.toEntity(returnedTelemetryChannelBindingDTO);
        assertTelemetryChannelBindingUpdatableFieldsEquals(
            returnedTelemetryChannelBinding,
            getPersistedTelemetryChannelBinding(returnedTelemetryChannelBinding)
        );

        insertedTelemetryChannelBinding = returnedTelemetryChannelBinding;
    }

    @Test
    void createTelemetryChannelBindingWithExistingId() throws Exception {
        // Create the TelemetryChannelBinding with an existing ID
        telemetryChannelBinding.setId(1L);
        TelemetryChannelBindingDTO telemetryChannelBindingDTO = telemetryChannelBindingMapper.toDto(telemetryChannelBinding);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(telemetryChannelBindingDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the TelemetryChannelBinding in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    void checkExternalPathIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        telemetryChannelBinding.setExternalPath(null);

        // Create the TelemetryChannelBinding, which fails.
        TelemetryChannelBindingDTO telemetryChannelBindingDTO = telemetryChannelBindingMapper.toDto(telemetryChannelBinding);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(telemetryChannelBindingDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkValidFromIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        telemetryChannelBinding.setValidFrom(null);

        // Create the TelemetryChannelBinding, which fails.
        TelemetryChannelBindingDTO telemetryChannelBindingDTO = telemetryChannelBindingMapper.toDto(telemetryChannelBinding);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(telemetryChannelBindingDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void getAllTelemetryChannelBindingsAsStream() {
        // Initialize the database
        telemetryChannelBindingRepository.save(telemetryChannelBinding).block();

        List<TelemetryChannelBinding> telemetryChannelBindingList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(TelemetryChannelBindingDTO.class)
            .getResponseBody()
            .map(telemetryChannelBindingMapper::toEntity)
            .filter(telemetryChannelBinding::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(telemetryChannelBindingList).isNotNull();
        assertThat(telemetryChannelBindingList).hasSize(1);
        TelemetryChannelBinding testTelemetryChannelBinding = telemetryChannelBindingList.getFirst();

        // Test fails because reactive api returns an empty object instead of null
        // assertTelemetryChannelBindingAllPropertiesEquals(telemetryChannelBinding, testTelemetryChannelBinding);
        assertTelemetryChannelBindingUpdatableFieldsEquals(telemetryChannelBinding, testTelemetryChannelBinding);
    }

    @Test
    void getAllTelemetryChannelBindings() {
        // Initialize the database
        insertedTelemetryChannelBinding = telemetryChannelBindingRepository.save(telemetryChannelBinding).block();

        // Get all the telemetryChannelBindingList
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
            .value(hasItem(telemetryChannelBinding.getId().intValue()))
            .jsonPath("$.[*].externalPath")
            .value(hasItem(DEFAULT_EXTERNAL_PATH))
            .jsonPath("$.[*].validFrom")
            .value(hasItem(DEFAULT_VALID_FROM.toString()))
            .jsonPath("$.[*].validUntil")
            .value(hasItem(DEFAULT_VALID_UNTIL.toString()));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllTelemetryChannelBindingsWithEagerRelationshipsIsEnabled() {
        when(telemetryChannelBindingServiceMock.findAllWithEagerRelationships()).thenReturn(Flux.empty());

        webTestClient
            .get()
            .uri(ENTITY_API_URL + "?eagerload=true")
            .exchange()
            .expectStatus()
            .isOk();

        verify(telemetryChannelBindingServiceMock, times(1)).findAllWithEagerRelationships();
    }

    @SuppressWarnings({ "unchecked" })
    void getAllTelemetryChannelBindingsWithEagerRelationshipsIsNotEnabled() {
        when(telemetryChannelBindingServiceMock.findAllWithEagerRelationships()).thenReturn(Flux.empty());

        webTestClient
            .get()
            .uri(ENTITY_API_URL + "?eagerload=false")
            .exchange()
            .expectStatus()
            .isOk();
        verify(telemetryChannelBindingRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    void getTelemetryChannelBinding() {
        // Initialize the database
        insertedTelemetryChannelBinding = telemetryChannelBindingRepository.save(telemetryChannelBinding).block();

        // Get the telemetryChannelBinding
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, telemetryChannelBinding.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(telemetryChannelBinding.getId().intValue()))
            .jsonPath("$.externalPath")
            .value(is(DEFAULT_EXTERNAL_PATH))
            .jsonPath("$.validFrom")
            .value(is(DEFAULT_VALID_FROM.toString()))
            .jsonPath("$.validUntil")
            .value(is(DEFAULT_VALID_UNTIL.toString()));
    }

    @Test
    void getNonExistingTelemetryChannelBinding() {
        // Get the telemetryChannelBinding
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingTelemetryChannelBinding() throws Exception {
        // Initialize the database
        insertedTelemetryChannelBinding = telemetryChannelBindingRepository.save(telemetryChannelBinding).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the telemetryChannelBinding
        TelemetryChannelBinding updatedTelemetryChannelBinding = telemetryChannelBindingRepository
            .findById(telemetryChannelBinding.getId())
            .block();
        updatedTelemetryChannelBinding.externalPath(UPDATED_EXTERNAL_PATH).validFrom(UPDATED_VALID_FROM).validUntil(UPDATED_VALID_UNTIL);
        TelemetryChannelBindingDTO telemetryChannelBindingDTO = telemetryChannelBindingMapper.toDto(updatedTelemetryChannelBinding);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, telemetryChannelBindingDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(telemetryChannelBindingDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the TelemetryChannelBinding in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedTelemetryChannelBindingToMatchAllProperties(updatedTelemetryChannelBinding);
    }

    @Test
    void putNonExistingTelemetryChannelBinding() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        telemetryChannelBinding.setId(longCount.incrementAndGet());

        // Create the TelemetryChannelBinding
        TelemetryChannelBindingDTO telemetryChannelBindingDTO = telemetryChannelBindingMapper.toDto(telemetryChannelBinding);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, telemetryChannelBindingDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(telemetryChannelBindingDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the TelemetryChannelBinding in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchTelemetryChannelBinding() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        telemetryChannelBinding.setId(longCount.incrementAndGet());

        // Create the TelemetryChannelBinding
        TelemetryChannelBindingDTO telemetryChannelBindingDTO = telemetryChannelBindingMapper.toDto(telemetryChannelBinding);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(telemetryChannelBindingDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the TelemetryChannelBinding in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamTelemetryChannelBinding() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        telemetryChannelBinding.setId(longCount.incrementAndGet());

        // Create the TelemetryChannelBinding
        TelemetryChannelBindingDTO telemetryChannelBindingDTO = telemetryChannelBindingMapper.toDto(telemetryChannelBinding);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(telemetryChannelBindingDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the TelemetryChannelBinding in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateTelemetryChannelBindingWithPatch() throws Exception {
        // Initialize the database
        insertedTelemetryChannelBinding = telemetryChannelBindingRepository.save(telemetryChannelBinding).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the telemetryChannelBinding using partial update
        TelemetryChannelBinding partialUpdatedTelemetryChannelBinding = new TelemetryChannelBinding();
        partialUpdatedTelemetryChannelBinding.setId(telemetryChannelBinding.getId());

        partialUpdatedTelemetryChannelBinding
            .externalPath(UPDATED_EXTERNAL_PATH)
            .validFrom(UPDATED_VALID_FROM)
            .validUntil(UPDATED_VALID_UNTIL);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedTelemetryChannelBinding.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedTelemetryChannelBinding))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the TelemetryChannelBinding in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertTelemetryChannelBindingUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedTelemetryChannelBinding, telemetryChannelBinding),
            getPersistedTelemetryChannelBinding(telemetryChannelBinding)
        );
    }

    @Test
    void fullUpdateTelemetryChannelBindingWithPatch() throws Exception {
        // Initialize the database
        insertedTelemetryChannelBinding = telemetryChannelBindingRepository.save(telemetryChannelBinding).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the telemetryChannelBinding using partial update
        TelemetryChannelBinding partialUpdatedTelemetryChannelBinding = new TelemetryChannelBinding();
        partialUpdatedTelemetryChannelBinding.setId(telemetryChannelBinding.getId());

        partialUpdatedTelemetryChannelBinding
            .externalPath(UPDATED_EXTERNAL_PATH)
            .validFrom(UPDATED_VALID_FROM)
            .validUntil(UPDATED_VALID_UNTIL);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedTelemetryChannelBinding.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedTelemetryChannelBinding))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the TelemetryChannelBinding in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertTelemetryChannelBindingUpdatableFieldsEquals(
            partialUpdatedTelemetryChannelBinding,
            getPersistedTelemetryChannelBinding(partialUpdatedTelemetryChannelBinding)
        );
    }

    @Test
    void patchNonExistingTelemetryChannelBinding() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        telemetryChannelBinding.setId(longCount.incrementAndGet());

        // Create the TelemetryChannelBinding
        TelemetryChannelBindingDTO telemetryChannelBindingDTO = telemetryChannelBindingMapper.toDto(telemetryChannelBinding);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, telemetryChannelBindingDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(telemetryChannelBindingDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the TelemetryChannelBinding in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchTelemetryChannelBinding() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        telemetryChannelBinding.setId(longCount.incrementAndGet());

        // Create the TelemetryChannelBinding
        TelemetryChannelBindingDTO telemetryChannelBindingDTO = telemetryChannelBindingMapper.toDto(telemetryChannelBinding);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(telemetryChannelBindingDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the TelemetryChannelBinding in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamTelemetryChannelBinding() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        telemetryChannelBinding.setId(longCount.incrementAndGet());

        // Create the TelemetryChannelBinding
        TelemetryChannelBindingDTO telemetryChannelBindingDTO = telemetryChannelBindingMapper.toDto(telemetryChannelBinding);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(telemetryChannelBindingDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the TelemetryChannelBinding in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteTelemetryChannelBinding() {
        // Initialize the database
        insertedTelemetryChannelBinding = telemetryChannelBindingRepository.save(telemetryChannelBinding).block();

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the telemetryChannelBinding
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, telemetryChannelBinding.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return telemetryChannelBindingRepository.count().block();
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

    protected TelemetryChannelBinding getPersistedTelemetryChannelBinding(TelemetryChannelBinding telemetryChannelBinding) {
        return telemetryChannelBindingRepository.findById(telemetryChannelBinding.getId()).block();
    }

    protected void assertPersistedTelemetryChannelBindingToMatchAllProperties(TelemetryChannelBinding expectedTelemetryChannelBinding) {
        // Test fails because reactive api returns an empty object instead of null
        // assertTelemetryChannelBindingAllPropertiesEquals(expectedTelemetryChannelBinding, getPersistedTelemetryChannelBinding(expectedTelemetryChannelBinding));
        assertTelemetryChannelBindingUpdatableFieldsEquals(
            expectedTelemetryChannelBinding,
            getPersistedTelemetryChannelBinding(expectedTelemetryChannelBinding)
        );
    }

    protected void assertPersistedTelemetryChannelBindingToMatchUpdatableProperties(
        TelemetryChannelBinding expectedTelemetryChannelBinding
    ) {
        // Test fails because reactive api returns an empty object instead of null
        // assertTelemetryChannelBindingAllUpdatablePropertiesEquals(expectedTelemetryChannelBinding, getPersistedTelemetryChannelBinding(expectedTelemetryChannelBinding));
        assertTelemetryChannelBindingUpdatableFieldsEquals(
            expectedTelemetryChannelBinding,
            getPersistedTelemetryChannelBinding(expectedTelemetryChannelBinding)
        );
    }
}

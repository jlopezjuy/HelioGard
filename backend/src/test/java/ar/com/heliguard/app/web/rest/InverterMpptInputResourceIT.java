package ar.com.heliguard.app.web.rest;

import static ar.com.heliguard.app.domain.InverterMpptInputAsserts.*;
import static ar.com.heliguard.app.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

import ar.com.heliguard.app.IntegrationTest;
import ar.com.heliguard.app.domain.Equipment;
import ar.com.heliguard.app.domain.InverterMpptInput;
import ar.com.heliguard.app.domain.Organization;
import ar.com.heliguard.app.repository.InverterMpptInputRepository;
import ar.com.heliguard.app.service.InverterMpptInputService;
import ar.com.heliguard.app.service.dto.InverterMpptInputDTO;
import ar.com.heliguard.app.service.mapper.InverterMpptInputMapper;
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
 * Integration tests for the {@link InverterMpptInputResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class InverterMpptInputResourceIT {

    private static final String DEFAULT_INPUT_CODE = "AAAAAAAAAA";
    private static final String UPDATED_INPUT_CODE = "BBBBBBBBBB";

    private static final Boolean DEFAULT_ENABLED = false;
    private static final Boolean UPDATED_ENABLED = true;

    private static final Instant DEFAULT_VALID_FROM = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_VALID_FROM = Instant.ofEpochMilli(1795370143700L);

    private static final Instant DEFAULT_VALID_UNTIL = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_VALID_UNTIL = Instant.ofEpochMilli(1795370143700L);

    private static final String ENTITY_API_URL = "/api/inverter-mppt-inputs";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    @Autowired
    private ObjectMapper om;

    @Autowired
    private InverterMpptInputRepository inverterMpptInputRepository;

    @Mock
    private InverterMpptInputRepository inverterMpptInputRepositoryMock;

    @Autowired
    private InverterMpptInputMapper inverterMpptInputMapper;

    @Mock
    private InverterMpptInputService inverterMpptInputServiceMock;

    @Autowired
    private R2dbcEntityTemplate template;

    @Autowired
    private WebTestClient webTestClient;

    private InverterMpptInput inverterMpptInput;

    private InverterMpptInput insertedInverterMpptInput;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static InverterMpptInput createEntity(R2dbcEntityTemplate template) {
        InverterMpptInput inverterMpptInput = new InverterMpptInput()
            .inputCode(DEFAULT_INPUT_CODE)
            .enabled(DEFAULT_ENABLED)
            .validFrom(DEFAULT_VALID_FROM)
            .validUntil(DEFAULT_VALID_UNTIL);
        // Add required entity
        Organization organization;
        organization = template.insert(OrganizationResourceIT.createEntity()).block();
        inverterMpptInput.setOrganization(organization);
        // Add required entity
        Equipment equipment;
        equipment = template.insert(EquipmentResourceIT.createEntity(template)).block();
        inverterMpptInput.setInverter(equipment);
        return inverterMpptInput;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static InverterMpptInput createUpdatedEntity(R2dbcEntityTemplate template) {
        InverterMpptInput updatedInverterMpptInput = new InverterMpptInput()
            .inputCode(UPDATED_INPUT_CODE)
            .enabled(UPDATED_ENABLED)
            .validFrom(UPDATED_VALID_FROM)
            .validUntil(UPDATED_VALID_UNTIL);
        // Add required entity
        Organization organization;
        organization = template.insert(OrganizationResourceIT.createUpdatedEntity()).block();
        updatedInverterMpptInput.setOrganization(organization);
        // Add required entity
        Equipment equipment;
        equipment = template.insert(EquipmentResourceIT.createUpdatedEntity(template)).block();
        updatedInverterMpptInput.setInverter(equipment);
        return updatedInverterMpptInput;
    }

    public static void deleteEntities(R2dbcEntityTemplate template) {
        try {
            template.delete(InverterMpptInput.class).all().block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
        OrganizationResourceIT.deleteEntities(template);
        EquipmentResourceIT.deleteEntities(template);
    }

    @BeforeEach
    void initTest() {
        inverterMpptInput = createEntity(template);
    }

    @AfterEach
    void cleanup() {
        if (insertedInverterMpptInput != null) {
            inverterMpptInputRepository.delete(insertedInverterMpptInput).block();
            insertedInverterMpptInput = null;
        }
        deleteEntities(template);
    }

    @Test
    void createInverterMpptInput() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the InverterMpptInput
        InverterMpptInputDTO inverterMpptInputDTO = inverterMpptInputMapper.toDto(inverterMpptInput);
        var returnedInverterMpptInputDTO = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(inverterMpptInputDTO))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(InverterMpptInputDTO.class)
            .returnResult()
            .getResponseBody();

        // Validate the InverterMpptInput in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedInverterMpptInput = inverterMpptInputMapper.toEntity(returnedInverterMpptInputDTO);
        assertInverterMpptInputUpdatableFieldsEquals(returnedInverterMpptInput, getPersistedInverterMpptInput(returnedInverterMpptInput));

        insertedInverterMpptInput = returnedInverterMpptInput;
    }

    @Test
    void createInverterMpptInputWithExistingId() throws Exception {
        // Create the InverterMpptInput with an existing ID
        inverterMpptInput.setId(1L);
        InverterMpptInputDTO inverterMpptInputDTO = inverterMpptInputMapper.toDto(inverterMpptInput);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(inverterMpptInputDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the InverterMpptInput in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    void checkInputCodeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        inverterMpptInput.setInputCode(null);

        // Create the InverterMpptInput, which fails.
        InverterMpptInputDTO inverterMpptInputDTO = inverterMpptInputMapper.toDto(inverterMpptInput);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(inverterMpptInputDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkEnabledIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        inverterMpptInput.setEnabled(null);

        // Create the InverterMpptInput, which fails.
        InverterMpptInputDTO inverterMpptInputDTO = inverterMpptInputMapper.toDto(inverterMpptInput);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(inverterMpptInputDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkValidFromIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        inverterMpptInput.setValidFrom(null);

        // Create the InverterMpptInput, which fails.
        InverterMpptInputDTO inverterMpptInputDTO = inverterMpptInputMapper.toDto(inverterMpptInput);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(inverterMpptInputDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void getAllInverterMpptInputsAsStream() {
        // Initialize the database
        inverterMpptInputRepository.save(inverterMpptInput).block();

        List<InverterMpptInput> inverterMpptInputList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(InverterMpptInputDTO.class)
            .getResponseBody()
            .map(inverterMpptInputMapper::toEntity)
            .filter(inverterMpptInput::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(inverterMpptInputList).isNotNull();
        assertThat(inverterMpptInputList).hasSize(1);
        InverterMpptInput testInverterMpptInput = inverterMpptInputList.getFirst();

        // Test fails because reactive api returns an empty object instead of null
        // assertInverterMpptInputAllPropertiesEquals(inverterMpptInput, testInverterMpptInput);
        assertInverterMpptInputUpdatableFieldsEquals(inverterMpptInput, testInverterMpptInput);
    }

    @Test
    void getAllInverterMpptInputs() {
        // Initialize the database
        insertedInverterMpptInput = inverterMpptInputRepository.save(inverterMpptInput).block();

        // Get all the inverterMpptInputList
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
            .value(hasItem(inverterMpptInput.getId().intValue()))
            .jsonPath("$.[*].inputCode")
            .value(hasItem(DEFAULT_INPUT_CODE))
            .jsonPath("$.[*].enabled")
            .value(hasItem(DEFAULT_ENABLED))
            .jsonPath("$.[*].validFrom")
            .value(hasItem(DEFAULT_VALID_FROM.toString()))
            .jsonPath("$.[*].validUntil")
            .value(hasItem(DEFAULT_VALID_UNTIL.toString()));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllInverterMpptInputsWithEagerRelationshipsIsEnabled() {
        when(inverterMpptInputServiceMock.findAllWithEagerRelationships()).thenReturn(Flux.empty());

        webTestClient
            .get()
            .uri(ENTITY_API_URL + "?eagerload=true")
            .exchange()
            .expectStatus()
            .isOk();

        verify(inverterMpptInputServiceMock, times(1)).findAllWithEagerRelationships();
    }

    @SuppressWarnings({ "unchecked" })
    void getAllInverterMpptInputsWithEagerRelationshipsIsNotEnabled() {
        when(inverterMpptInputServiceMock.findAllWithEagerRelationships()).thenReturn(Flux.empty());

        webTestClient
            .get()
            .uri(ENTITY_API_URL + "?eagerload=false")
            .exchange()
            .expectStatus()
            .isOk();
        verify(inverterMpptInputRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    void getInverterMpptInput() {
        // Initialize the database
        insertedInverterMpptInput = inverterMpptInputRepository.save(inverterMpptInput).block();

        // Get the inverterMpptInput
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, inverterMpptInput.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(inverterMpptInput.getId().intValue()))
            .jsonPath("$.inputCode")
            .value(is(DEFAULT_INPUT_CODE))
            .jsonPath("$.enabled")
            .value(is(DEFAULT_ENABLED))
            .jsonPath("$.validFrom")
            .value(is(DEFAULT_VALID_FROM.toString()))
            .jsonPath("$.validUntil")
            .value(is(DEFAULT_VALID_UNTIL.toString()));
    }

    @Test
    void getNonExistingInverterMpptInput() {
        // Get the inverterMpptInput
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingInverterMpptInput() throws Exception {
        // Initialize the database
        insertedInverterMpptInput = inverterMpptInputRepository.save(inverterMpptInput).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the inverterMpptInput
        InverterMpptInput updatedInverterMpptInput = inverterMpptInputRepository.findById(inverterMpptInput.getId()).block();
        updatedInverterMpptInput
            .inputCode(UPDATED_INPUT_CODE)
            .enabled(UPDATED_ENABLED)
            .validFrom(UPDATED_VALID_FROM)
            .validUntil(UPDATED_VALID_UNTIL);
        InverterMpptInputDTO inverterMpptInputDTO = inverterMpptInputMapper.toDto(updatedInverterMpptInput);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, inverterMpptInputDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(inverterMpptInputDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the InverterMpptInput in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedInverterMpptInputToMatchAllProperties(updatedInverterMpptInput);
    }

    @Test
    void putNonExistingInverterMpptInput() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        inverterMpptInput.setId(longCount.incrementAndGet());

        // Create the InverterMpptInput
        InverterMpptInputDTO inverterMpptInputDTO = inverterMpptInputMapper.toDto(inverterMpptInput);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, inverterMpptInputDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(inverterMpptInputDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the InverterMpptInput in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchInverterMpptInput() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        inverterMpptInput.setId(longCount.incrementAndGet());

        // Create the InverterMpptInput
        InverterMpptInputDTO inverterMpptInputDTO = inverterMpptInputMapper.toDto(inverterMpptInput);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(inverterMpptInputDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the InverterMpptInput in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamInverterMpptInput() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        inverterMpptInput.setId(longCount.incrementAndGet());

        // Create the InverterMpptInput
        InverterMpptInputDTO inverterMpptInputDTO = inverterMpptInputMapper.toDto(inverterMpptInput);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(inverterMpptInputDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the InverterMpptInput in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateInverterMpptInputWithPatch() throws Exception {
        // Initialize the database
        insertedInverterMpptInput = inverterMpptInputRepository.save(inverterMpptInput).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the inverterMpptInput using partial update
        InverterMpptInput partialUpdatedInverterMpptInput = new InverterMpptInput();
        partialUpdatedInverterMpptInput.setId(inverterMpptInput.getId());

        partialUpdatedInverterMpptInput.inputCode(UPDATED_INPUT_CODE).enabled(UPDATED_ENABLED).validFrom(UPDATED_VALID_FROM);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedInverterMpptInput.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedInverterMpptInput))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the InverterMpptInput in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertInverterMpptInputUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedInverterMpptInput, inverterMpptInput),
            getPersistedInverterMpptInput(inverterMpptInput)
        );
    }

    @Test
    void fullUpdateInverterMpptInputWithPatch() throws Exception {
        // Initialize the database
        insertedInverterMpptInput = inverterMpptInputRepository.save(inverterMpptInput).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the inverterMpptInput using partial update
        InverterMpptInput partialUpdatedInverterMpptInput = new InverterMpptInput();
        partialUpdatedInverterMpptInput.setId(inverterMpptInput.getId());

        partialUpdatedInverterMpptInput
            .inputCode(UPDATED_INPUT_CODE)
            .enabled(UPDATED_ENABLED)
            .validFrom(UPDATED_VALID_FROM)
            .validUntil(UPDATED_VALID_UNTIL);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedInverterMpptInput.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedInverterMpptInput))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the InverterMpptInput in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertInverterMpptInputUpdatableFieldsEquals(
            partialUpdatedInverterMpptInput,
            getPersistedInverterMpptInput(partialUpdatedInverterMpptInput)
        );
    }

    @Test
    void patchNonExistingInverterMpptInput() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        inverterMpptInput.setId(longCount.incrementAndGet());

        // Create the InverterMpptInput
        InverterMpptInputDTO inverterMpptInputDTO = inverterMpptInputMapper.toDto(inverterMpptInput);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, inverterMpptInputDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(inverterMpptInputDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the InverterMpptInput in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchInverterMpptInput() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        inverterMpptInput.setId(longCount.incrementAndGet());

        // Create the InverterMpptInput
        InverterMpptInputDTO inverterMpptInputDTO = inverterMpptInputMapper.toDto(inverterMpptInput);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(inverterMpptInputDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the InverterMpptInput in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamInverterMpptInput() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        inverterMpptInput.setId(longCount.incrementAndGet());

        // Create the InverterMpptInput
        InverterMpptInputDTO inverterMpptInputDTO = inverterMpptInputMapper.toDto(inverterMpptInput);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(inverterMpptInputDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the InverterMpptInput in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteInverterMpptInput() {
        // Initialize the database
        insertedInverterMpptInput = inverterMpptInputRepository.save(inverterMpptInput).block();

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the inverterMpptInput
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, inverterMpptInput.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return inverterMpptInputRepository.count().block();
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

    protected InverterMpptInput getPersistedInverterMpptInput(InverterMpptInput inverterMpptInput) {
        return inverterMpptInputRepository.findById(inverterMpptInput.getId()).block();
    }

    protected void assertPersistedInverterMpptInputToMatchAllProperties(InverterMpptInput expectedInverterMpptInput) {
        // Test fails because reactive api returns an empty object instead of null
        // assertInverterMpptInputAllPropertiesEquals(expectedInverterMpptInput, getPersistedInverterMpptInput(expectedInverterMpptInput));
        assertInverterMpptInputUpdatableFieldsEquals(expectedInverterMpptInput, getPersistedInverterMpptInput(expectedInverterMpptInput));
    }

    protected void assertPersistedInverterMpptInputToMatchUpdatableProperties(InverterMpptInput expectedInverterMpptInput) {
        // Test fails because reactive api returns an empty object instead of null
        // assertInverterMpptInputAllUpdatablePropertiesEquals(expectedInverterMpptInput, getPersistedInverterMpptInput(expectedInverterMpptInput));
        assertInverterMpptInputUpdatableFieldsEquals(expectedInverterMpptInput, getPersistedInverterMpptInput(expectedInverterMpptInput));
    }
}

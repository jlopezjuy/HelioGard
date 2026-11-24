package ar.com.heliguard.app.web.rest;

import static ar.com.heliguard.app.domain.StringInputAssignmentAsserts.*;
import static ar.com.heliguard.app.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

import ar.com.heliguard.app.IntegrationTest;
import ar.com.heliguard.app.domain.InverterMpptInput;
import ar.com.heliguard.app.domain.Organization;
import ar.com.heliguard.app.domain.SolarString;
import ar.com.heliguard.app.domain.StringInputAssignment;
import ar.com.heliguard.app.repository.StringInputAssignmentRepository;
import ar.com.heliguard.app.service.StringInputAssignmentService;
import ar.com.heliguard.app.service.dto.StringInputAssignmentDTO;
import ar.com.heliguard.app.service.mapper.StringInputAssignmentMapper;
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
 * Integration tests for the {@link StringInputAssignmentResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class StringInputAssignmentResourceIT {

    private static final Instant DEFAULT_VALID_FROM = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_VALID_FROM = Instant.ofEpochMilli(1795370143700L);

    private static final Instant DEFAULT_VALID_UNTIL = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_VALID_UNTIL = Instant.ofEpochMilli(1795370143700L);

    private static final String DEFAULT_NOTES = "AAAAAAAAAA";
    private static final String UPDATED_NOTES = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/string-input-assignments";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    @Autowired
    private ObjectMapper om;

    @Autowired
    private StringInputAssignmentRepository stringInputAssignmentRepository;

    @Mock
    private StringInputAssignmentRepository stringInputAssignmentRepositoryMock;

    @Autowired
    private StringInputAssignmentMapper stringInputAssignmentMapper;

    @Mock
    private StringInputAssignmentService stringInputAssignmentServiceMock;

    @Autowired
    private R2dbcEntityTemplate template;

    @Autowired
    private WebTestClient webTestClient;

    private StringInputAssignment stringInputAssignment;

    private StringInputAssignment insertedStringInputAssignment;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static StringInputAssignment createEntity(R2dbcEntityTemplate template) {
        StringInputAssignment stringInputAssignment = new StringInputAssignment()
            .validFrom(DEFAULT_VALID_FROM)
            .validUntil(DEFAULT_VALID_UNTIL)
            .notes(DEFAULT_NOTES);
        // Add required entity
        Organization organization;
        organization = template.insert(OrganizationResourceIT.createEntity()).block();
        stringInputAssignment.setOrganization(organization);
        // Add required entity
        SolarString solarString;
        solarString = template.insert(SolarStringResourceIT.createEntity(template)).block();
        stringInputAssignment.setSolarString(solarString);
        // Add required entity
        InverterMpptInput inverterMpptInput;
        inverterMpptInput = template.insert(InverterMpptInputResourceIT.createEntity(template)).block();
        stringInputAssignment.setMpptInput(inverterMpptInput);
        return stringInputAssignment;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static StringInputAssignment createUpdatedEntity(R2dbcEntityTemplate template) {
        StringInputAssignment updatedStringInputAssignment = new StringInputAssignment()
            .validFrom(UPDATED_VALID_FROM)
            .validUntil(UPDATED_VALID_UNTIL)
            .notes(UPDATED_NOTES);
        // Add required entity
        Organization organization;
        organization = template.insert(OrganizationResourceIT.createUpdatedEntity()).block();
        updatedStringInputAssignment.setOrganization(organization);
        // Add required entity
        SolarString solarString;
        solarString = template.insert(SolarStringResourceIT.createUpdatedEntity(template)).block();
        updatedStringInputAssignment.setSolarString(solarString);
        // Add required entity
        InverterMpptInput inverterMpptInput;
        inverterMpptInput = template.insert(InverterMpptInputResourceIT.createUpdatedEntity(template)).block();
        updatedStringInputAssignment.setMpptInput(inverterMpptInput);
        return updatedStringInputAssignment;
    }

    public static void deleteEntities(R2dbcEntityTemplate template) {
        try {
            template.delete(StringInputAssignment.class).all().block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
        OrganizationResourceIT.deleteEntities(template);
        SolarStringResourceIT.deleteEntities(template);
        InverterMpptInputResourceIT.deleteEntities(template);
    }

    @BeforeEach
    void initTest() {
        stringInputAssignment = createEntity(template);
    }

    @AfterEach
    void cleanup() {
        if (insertedStringInputAssignment != null) {
            stringInputAssignmentRepository.delete(insertedStringInputAssignment).block();
            insertedStringInputAssignment = null;
        }
        deleteEntities(template);
    }

    @Test
    void createStringInputAssignment() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the StringInputAssignment
        StringInputAssignmentDTO stringInputAssignmentDTO = stringInputAssignmentMapper.toDto(stringInputAssignment);
        var returnedStringInputAssignmentDTO = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(stringInputAssignmentDTO))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(StringInputAssignmentDTO.class)
            .returnResult()
            .getResponseBody();

        // Validate the StringInputAssignment in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedStringInputAssignment = stringInputAssignmentMapper.toEntity(returnedStringInputAssignmentDTO);
        assertStringInputAssignmentUpdatableFieldsEquals(
            returnedStringInputAssignment,
            getPersistedStringInputAssignment(returnedStringInputAssignment)
        );

        insertedStringInputAssignment = returnedStringInputAssignment;
    }

    @Test
    void createStringInputAssignmentWithExistingId() throws Exception {
        // Create the StringInputAssignment with an existing ID
        stringInputAssignment.setId(1L);
        StringInputAssignmentDTO stringInputAssignmentDTO = stringInputAssignmentMapper.toDto(stringInputAssignment);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(stringInputAssignmentDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the StringInputAssignment in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    void checkValidFromIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        stringInputAssignment.setValidFrom(null);

        // Create the StringInputAssignment, which fails.
        StringInputAssignmentDTO stringInputAssignmentDTO = stringInputAssignmentMapper.toDto(stringInputAssignment);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(stringInputAssignmentDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void getAllStringInputAssignmentsAsStream() {
        // Initialize the database
        stringInputAssignmentRepository.save(stringInputAssignment).block();

        List<StringInputAssignment> stringInputAssignmentList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(StringInputAssignmentDTO.class)
            .getResponseBody()
            .map(stringInputAssignmentMapper::toEntity)
            .filter(stringInputAssignment::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(stringInputAssignmentList).isNotNull();
        assertThat(stringInputAssignmentList).hasSize(1);
        StringInputAssignment testStringInputAssignment = stringInputAssignmentList.getFirst();

        // Test fails because reactive api returns an empty object instead of null
        // assertStringInputAssignmentAllPropertiesEquals(stringInputAssignment, testStringInputAssignment);
        assertStringInputAssignmentUpdatableFieldsEquals(stringInputAssignment, testStringInputAssignment);
    }

    @Test
    void getAllStringInputAssignments() {
        // Initialize the database
        insertedStringInputAssignment = stringInputAssignmentRepository.save(stringInputAssignment).block();

        // Get all the stringInputAssignmentList
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
            .value(hasItem(stringInputAssignment.getId().intValue()))
            .jsonPath("$.[*].validFrom")
            .value(hasItem(DEFAULT_VALID_FROM.toString()))
            .jsonPath("$.[*].validUntil")
            .value(hasItem(DEFAULT_VALID_UNTIL.toString()))
            .jsonPath("$.[*].notes")
            .value(hasItem(DEFAULT_NOTES));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllStringInputAssignmentsWithEagerRelationshipsIsEnabled() {
        when(stringInputAssignmentServiceMock.findAllWithEagerRelationships()).thenReturn(Flux.empty());

        webTestClient
            .get()
            .uri(ENTITY_API_URL + "?eagerload=true")
            .exchange()
            .expectStatus()
            .isOk();

        verify(stringInputAssignmentServiceMock, times(1)).findAllWithEagerRelationships();
    }

    @SuppressWarnings({ "unchecked" })
    void getAllStringInputAssignmentsWithEagerRelationshipsIsNotEnabled() {
        when(stringInputAssignmentServiceMock.findAllWithEagerRelationships()).thenReturn(Flux.empty());

        webTestClient
            .get()
            .uri(ENTITY_API_URL + "?eagerload=false")
            .exchange()
            .expectStatus()
            .isOk();
        verify(stringInputAssignmentRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    void getStringInputAssignment() {
        // Initialize the database
        insertedStringInputAssignment = stringInputAssignmentRepository.save(stringInputAssignment).block();

        // Get the stringInputAssignment
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, stringInputAssignment.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(stringInputAssignment.getId().intValue()))
            .jsonPath("$.validFrom")
            .value(is(DEFAULT_VALID_FROM.toString()))
            .jsonPath("$.validUntil")
            .value(is(DEFAULT_VALID_UNTIL.toString()))
            .jsonPath("$.notes")
            .value(is(DEFAULT_NOTES));
    }

    @Test
    void getNonExistingStringInputAssignment() {
        // Get the stringInputAssignment
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingStringInputAssignment() throws Exception {
        // Initialize the database
        insertedStringInputAssignment = stringInputAssignmentRepository.save(stringInputAssignment).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the stringInputAssignment
        StringInputAssignment updatedStringInputAssignment = stringInputAssignmentRepository
            .findById(stringInputAssignment.getId())
            .block();
        updatedStringInputAssignment.validFrom(UPDATED_VALID_FROM).validUntil(UPDATED_VALID_UNTIL).notes(UPDATED_NOTES);
        StringInputAssignmentDTO stringInputAssignmentDTO = stringInputAssignmentMapper.toDto(updatedStringInputAssignment);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, stringInputAssignmentDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(stringInputAssignmentDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the StringInputAssignment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedStringInputAssignmentToMatchAllProperties(updatedStringInputAssignment);
    }

    @Test
    void putNonExistingStringInputAssignment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        stringInputAssignment.setId(longCount.incrementAndGet());

        // Create the StringInputAssignment
        StringInputAssignmentDTO stringInputAssignmentDTO = stringInputAssignmentMapper.toDto(stringInputAssignment);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, stringInputAssignmentDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(stringInputAssignmentDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the StringInputAssignment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchStringInputAssignment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        stringInputAssignment.setId(longCount.incrementAndGet());

        // Create the StringInputAssignment
        StringInputAssignmentDTO stringInputAssignmentDTO = stringInputAssignmentMapper.toDto(stringInputAssignment);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(stringInputAssignmentDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the StringInputAssignment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamStringInputAssignment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        stringInputAssignment.setId(longCount.incrementAndGet());

        // Create the StringInputAssignment
        StringInputAssignmentDTO stringInputAssignmentDTO = stringInputAssignmentMapper.toDto(stringInputAssignment);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(stringInputAssignmentDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the StringInputAssignment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateStringInputAssignmentWithPatch() throws Exception {
        // Initialize the database
        insertedStringInputAssignment = stringInputAssignmentRepository.save(stringInputAssignment).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the stringInputAssignment using partial update
        StringInputAssignment partialUpdatedStringInputAssignment = new StringInputAssignment();
        partialUpdatedStringInputAssignment.setId(stringInputAssignment.getId());

        partialUpdatedStringInputAssignment.validFrom(UPDATED_VALID_FROM).validUntil(UPDATED_VALID_UNTIL);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedStringInputAssignment.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedStringInputAssignment))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the StringInputAssignment in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertStringInputAssignmentUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedStringInputAssignment, stringInputAssignment),
            getPersistedStringInputAssignment(stringInputAssignment)
        );
    }

    @Test
    void fullUpdateStringInputAssignmentWithPatch() throws Exception {
        // Initialize the database
        insertedStringInputAssignment = stringInputAssignmentRepository.save(stringInputAssignment).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the stringInputAssignment using partial update
        StringInputAssignment partialUpdatedStringInputAssignment = new StringInputAssignment();
        partialUpdatedStringInputAssignment.setId(stringInputAssignment.getId());

        partialUpdatedStringInputAssignment.validFrom(UPDATED_VALID_FROM).validUntil(UPDATED_VALID_UNTIL).notes(UPDATED_NOTES);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedStringInputAssignment.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedStringInputAssignment))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the StringInputAssignment in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertStringInputAssignmentUpdatableFieldsEquals(
            partialUpdatedStringInputAssignment,
            getPersistedStringInputAssignment(partialUpdatedStringInputAssignment)
        );
    }

    @Test
    void patchNonExistingStringInputAssignment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        stringInputAssignment.setId(longCount.incrementAndGet());

        // Create the StringInputAssignment
        StringInputAssignmentDTO stringInputAssignmentDTO = stringInputAssignmentMapper.toDto(stringInputAssignment);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, stringInputAssignmentDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(stringInputAssignmentDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the StringInputAssignment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchStringInputAssignment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        stringInputAssignment.setId(longCount.incrementAndGet());

        // Create the StringInputAssignment
        StringInputAssignmentDTO stringInputAssignmentDTO = stringInputAssignmentMapper.toDto(stringInputAssignment);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(stringInputAssignmentDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the StringInputAssignment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamStringInputAssignment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        stringInputAssignment.setId(longCount.incrementAndGet());

        // Create the StringInputAssignment
        StringInputAssignmentDTO stringInputAssignmentDTO = stringInputAssignmentMapper.toDto(stringInputAssignment);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(stringInputAssignmentDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the StringInputAssignment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteStringInputAssignment() {
        // Initialize the database
        insertedStringInputAssignment = stringInputAssignmentRepository.save(stringInputAssignment).block();

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the stringInputAssignment
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, stringInputAssignment.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return stringInputAssignmentRepository.count().block();
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

    protected StringInputAssignment getPersistedStringInputAssignment(StringInputAssignment stringInputAssignment) {
        return stringInputAssignmentRepository.findById(stringInputAssignment.getId()).block();
    }

    protected void assertPersistedStringInputAssignmentToMatchAllProperties(StringInputAssignment expectedStringInputAssignment) {
        // Test fails because reactive api returns an empty object instead of null
        // assertStringInputAssignmentAllPropertiesEquals(expectedStringInputAssignment, getPersistedStringInputAssignment(expectedStringInputAssignment));
        assertStringInputAssignmentUpdatableFieldsEquals(
            expectedStringInputAssignment,
            getPersistedStringInputAssignment(expectedStringInputAssignment)
        );
    }

    protected void assertPersistedStringInputAssignmentToMatchUpdatableProperties(StringInputAssignment expectedStringInputAssignment) {
        // Test fails because reactive api returns an empty object instead of null
        // assertStringInputAssignmentAllUpdatablePropertiesEquals(expectedStringInputAssignment, getPersistedStringInputAssignment(expectedStringInputAssignment));
        assertStringInputAssignmentUpdatableFieldsEquals(
            expectedStringInputAssignment,
            getPersistedStringInputAssignment(expectedStringInputAssignment)
        );
    }
}

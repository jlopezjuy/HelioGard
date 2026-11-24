package ar.com.heliguard.app.web.rest;

import static ar.com.heliguard.app.domain.IncidentLocalizationAsserts.*;
import static ar.com.heliguard.app.web.rest.TestUtil.createUpdateProxyForBean;
import static ar.com.heliguard.app.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

import ar.com.heliguard.app.IntegrationTest;
import ar.com.heliguard.app.domain.Incident;
import ar.com.heliguard.app.domain.IncidentLocalization;
import ar.com.heliguard.app.domain.Organization;
import ar.com.heliguard.app.domain.enumeration.LocalizationScope;
import ar.com.heliguard.app.repository.IncidentLocalizationRepository;
import ar.com.heliguard.app.service.IncidentLocalizationService;
import ar.com.heliguard.app.service.dto.IncidentLocalizationDTO;
import ar.com.heliguard.app.service.mapper.IncidentLocalizationMapper;
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
 * Integration tests for the {@link IncidentLocalizationResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class IncidentLocalizationResourceIT {

    private static final LocalizationScope DEFAULT_SCOPE = LocalizationScope.INSTALLATION;
    private static final LocalizationScope UPDATED_SCOPE = LocalizationScope.SECTOR;

    private static final Instant DEFAULT_LOCATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_LOCATED_AT = Instant.ofEpochMilli(1795370143700L);

    private static final BigDecimal DEFAULT_CONFIDENCE_PCT = new BigDecimal(0);
    private static final BigDecimal UPDATED_CONFIDENCE_PCT = new BigDecimal(1);

    private static final String DEFAULT_EVIDENCE_SUMMARY = "AAAAAAAAAA";
    private static final String UPDATED_EVIDENCE_SUMMARY = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/incident-localizations";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    @Autowired
    private ObjectMapper om;

    @Autowired
    private IncidentLocalizationRepository incidentLocalizationRepository;

    @Mock
    private IncidentLocalizationRepository incidentLocalizationRepositoryMock;

    @Autowired
    private IncidentLocalizationMapper incidentLocalizationMapper;

    @Mock
    private IncidentLocalizationService incidentLocalizationServiceMock;

    @Autowired
    private R2dbcEntityTemplate template;

    @Autowired
    private WebTestClient webTestClient;

    private IncidentLocalization incidentLocalization;

    private IncidentLocalization insertedIncidentLocalization;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static IncidentLocalization createEntity(R2dbcEntityTemplate template) {
        IncidentLocalization incidentLocalization = new IncidentLocalization()
            .scope(DEFAULT_SCOPE)
            .locatedAt(DEFAULT_LOCATED_AT)
            .confidencePct(DEFAULT_CONFIDENCE_PCT)
            .evidenceSummary(DEFAULT_EVIDENCE_SUMMARY);
        // Add required entity
        Organization organization;
        organization = template.insert(OrganizationResourceIT.createEntity()).block();
        incidentLocalization.setOrganization(organization);
        // Add required entity
        Incident incident;
        incident = template.insert(IncidentResourceIT.createEntity(template)).block();
        incidentLocalization.setIncident(incident);
        return incidentLocalization;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static IncidentLocalization createUpdatedEntity(R2dbcEntityTemplate template) {
        IncidentLocalization updatedIncidentLocalization = new IncidentLocalization()
            .scope(UPDATED_SCOPE)
            .locatedAt(UPDATED_LOCATED_AT)
            .confidencePct(UPDATED_CONFIDENCE_PCT)
            .evidenceSummary(UPDATED_EVIDENCE_SUMMARY);
        // Add required entity
        Organization organization;
        organization = template.insert(OrganizationResourceIT.createUpdatedEntity()).block();
        updatedIncidentLocalization.setOrganization(organization);
        // Add required entity
        Incident incident;
        incident = template.insert(IncidentResourceIT.createUpdatedEntity(template)).block();
        updatedIncidentLocalization.setIncident(incident);
        return updatedIncidentLocalization;
    }

    public static void deleteEntities(R2dbcEntityTemplate template) {
        try {
            template.delete(IncidentLocalization.class).all().block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
        OrganizationResourceIT.deleteEntities(template);
        IncidentResourceIT.deleteEntities(template);
    }

    @BeforeEach
    void initTest() {
        incidentLocalization = createEntity(template);
    }

    @AfterEach
    void cleanup() {
        if (insertedIncidentLocalization != null) {
            incidentLocalizationRepository.delete(insertedIncidentLocalization).block();
            insertedIncidentLocalization = null;
        }
        deleteEntities(template);
    }

    @Test
    void createIncidentLocalization() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the IncidentLocalization
        IncidentLocalizationDTO incidentLocalizationDTO = incidentLocalizationMapper.toDto(incidentLocalization);
        var returnedIncidentLocalizationDTO = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(incidentLocalizationDTO))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(IncidentLocalizationDTO.class)
            .returnResult()
            .getResponseBody();

        // Validate the IncidentLocalization in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedIncidentLocalization = incidentLocalizationMapper.toEntity(returnedIncidentLocalizationDTO);
        assertIncidentLocalizationUpdatableFieldsEquals(
            returnedIncidentLocalization,
            getPersistedIncidentLocalization(returnedIncidentLocalization)
        );

        insertedIncidentLocalization = returnedIncidentLocalization;
    }

    @Test
    void createIncidentLocalizationWithExistingId() throws Exception {
        // Create the IncidentLocalization with an existing ID
        incidentLocalization.setId(1L);
        IncidentLocalizationDTO incidentLocalizationDTO = incidentLocalizationMapper.toDto(incidentLocalization);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(incidentLocalizationDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the IncidentLocalization in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    void checkScopeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        incidentLocalization.setScope(null);

        // Create the IncidentLocalization, which fails.
        IncidentLocalizationDTO incidentLocalizationDTO = incidentLocalizationMapper.toDto(incidentLocalization);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(incidentLocalizationDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkLocatedAtIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        incidentLocalization.setLocatedAt(null);

        // Create the IncidentLocalization, which fails.
        IncidentLocalizationDTO incidentLocalizationDTO = incidentLocalizationMapper.toDto(incidentLocalization);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(incidentLocalizationDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void getAllIncidentLocalizationsAsStream() {
        // Initialize the database
        incidentLocalizationRepository.save(incidentLocalization).block();

        List<IncidentLocalization> incidentLocalizationList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(IncidentLocalizationDTO.class)
            .getResponseBody()
            .map(incidentLocalizationMapper::toEntity)
            .filter(incidentLocalization::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(incidentLocalizationList).isNotNull();
        assertThat(incidentLocalizationList).hasSize(1);
        IncidentLocalization testIncidentLocalization = incidentLocalizationList.getFirst();

        // Test fails because reactive api returns an empty object instead of null
        // assertIncidentLocalizationAllPropertiesEquals(incidentLocalization, testIncidentLocalization);
        assertIncidentLocalizationUpdatableFieldsEquals(incidentLocalization, testIncidentLocalization);
    }

    @Test
    void getAllIncidentLocalizations() {
        // Initialize the database
        insertedIncidentLocalization = incidentLocalizationRepository.save(incidentLocalization).block();

        // Get all the incidentLocalizationList
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
            .value(hasItem(incidentLocalization.getId().intValue()))
            .jsonPath("$.[*].scope")
            .value(hasItem(DEFAULT_SCOPE.toString()))
            .jsonPath("$.[*].locatedAt")
            .value(hasItem(DEFAULT_LOCATED_AT.toString()))
            .jsonPath("$.[*].confidencePct")
            .value(hasItem(sameNumber(DEFAULT_CONFIDENCE_PCT)))
            .jsonPath("$.[*].evidenceSummary")
            .value(hasItem(DEFAULT_EVIDENCE_SUMMARY));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllIncidentLocalizationsWithEagerRelationshipsIsEnabled() {
        when(incidentLocalizationServiceMock.findAllWithEagerRelationships()).thenReturn(Flux.empty());

        webTestClient
            .get()
            .uri(ENTITY_API_URL + "?eagerload=true")
            .exchange()
            .expectStatus()
            .isOk();

        verify(incidentLocalizationServiceMock, times(1)).findAllWithEagerRelationships();
    }

    @SuppressWarnings({ "unchecked" })
    void getAllIncidentLocalizationsWithEagerRelationshipsIsNotEnabled() {
        when(incidentLocalizationServiceMock.findAllWithEagerRelationships()).thenReturn(Flux.empty());

        webTestClient
            .get()
            .uri(ENTITY_API_URL + "?eagerload=false")
            .exchange()
            .expectStatus()
            .isOk();
        verify(incidentLocalizationRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    void getIncidentLocalization() {
        // Initialize the database
        insertedIncidentLocalization = incidentLocalizationRepository.save(incidentLocalization).block();

        // Get the incidentLocalization
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, incidentLocalization.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(incidentLocalization.getId().intValue()))
            .jsonPath("$.scope")
            .value(is(DEFAULT_SCOPE.toString()))
            .jsonPath("$.locatedAt")
            .value(is(DEFAULT_LOCATED_AT.toString()))
            .jsonPath("$.confidencePct")
            .value(is(sameNumber(DEFAULT_CONFIDENCE_PCT)))
            .jsonPath("$.evidenceSummary")
            .value(is(DEFAULT_EVIDENCE_SUMMARY));
    }

    @Test
    void getNonExistingIncidentLocalization() {
        // Get the incidentLocalization
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingIncidentLocalization() throws Exception {
        // Initialize the database
        insertedIncidentLocalization = incidentLocalizationRepository.save(incidentLocalization).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the incidentLocalization
        IncidentLocalization updatedIncidentLocalization = incidentLocalizationRepository.findById(incidentLocalization.getId()).block();
        updatedIncidentLocalization
            .scope(UPDATED_SCOPE)
            .locatedAt(UPDATED_LOCATED_AT)
            .confidencePct(UPDATED_CONFIDENCE_PCT)
            .evidenceSummary(UPDATED_EVIDENCE_SUMMARY);
        IncidentLocalizationDTO incidentLocalizationDTO = incidentLocalizationMapper.toDto(updatedIncidentLocalization);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, incidentLocalizationDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(incidentLocalizationDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the IncidentLocalization in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedIncidentLocalizationToMatchAllProperties(updatedIncidentLocalization);
    }

    @Test
    void putNonExistingIncidentLocalization() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        incidentLocalization.setId(longCount.incrementAndGet());

        // Create the IncidentLocalization
        IncidentLocalizationDTO incidentLocalizationDTO = incidentLocalizationMapper.toDto(incidentLocalization);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, incidentLocalizationDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(incidentLocalizationDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the IncidentLocalization in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchIncidentLocalization() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        incidentLocalization.setId(longCount.incrementAndGet());

        // Create the IncidentLocalization
        IncidentLocalizationDTO incidentLocalizationDTO = incidentLocalizationMapper.toDto(incidentLocalization);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(incidentLocalizationDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the IncidentLocalization in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamIncidentLocalization() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        incidentLocalization.setId(longCount.incrementAndGet());

        // Create the IncidentLocalization
        IncidentLocalizationDTO incidentLocalizationDTO = incidentLocalizationMapper.toDto(incidentLocalization);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(incidentLocalizationDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the IncidentLocalization in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateIncidentLocalizationWithPatch() throws Exception {
        // Initialize the database
        insertedIncidentLocalization = incidentLocalizationRepository.save(incidentLocalization).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the incidentLocalization using partial update
        IncidentLocalization partialUpdatedIncidentLocalization = new IncidentLocalization();
        partialUpdatedIncidentLocalization.setId(incidentLocalization.getId());

        partialUpdatedIncidentLocalization.locatedAt(UPDATED_LOCATED_AT);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedIncidentLocalization.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedIncidentLocalization))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the IncidentLocalization in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertIncidentLocalizationUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedIncidentLocalization, incidentLocalization),
            getPersistedIncidentLocalization(incidentLocalization)
        );
    }

    @Test
    void fullUpdateIncidentLocalizationWithPatch() throws Exception {
        // Initialize the database
        insertedIncidentLocalization = incidentLocalizationRepository.save(incidentLocalization).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the incidentLocalization using partial update
        IncidentLocalization partialUpdatedIncidentLocalization = new IncidentLocalization();
        partialUpdatedIncidentLocalization.setId(incidentLocalization.getId());

        partialUpdatedIncidentLocalization
            .scope(UPDATED_SCOPE)
            .locatedAt(UPDATED_LOCATED_AT)
            .confidencePct(UPDATED_CONFIDENCE_PCT)
            .evidenceSummary(UPDATED_EVIDENCE_SUMMARY);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedIncidentLocalization.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedIncidentLocalization))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the IncidentLocalization in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertIncidentLocalizationUpdatableFieldsEquals(
            partialUpdatedIncidentLocalization,
            getPersistedIncidentLocalization(partialUpdatedIncidentLocalization)
        );
    }

    @Test
    void patchNonExistingIncidentLocalization() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        incidentLocalization.setId(longCount.incrementAndGet());

        // Create the IncidentLocalization
        IncidentLocalizationDTO incidentLocalizationDTO = incidentLocalizationMapper.toDto(incidentLocalization);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, incidentLocalizationDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(incidentLocalizationDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the IncidentLocalization in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchIncidentLocalization() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        incidentLocalization.setId(longCount.incrementAndGet());

        // Create the IncidentLocalization
        IncidentLocalizationDTO incidentLocalizationDTO = incidentLocalizationMapper.toDto(incidentLocalization);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(incidentLocalizationDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the IncidentLocalization in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamIncidentLocalization() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        incidentLocalization.setId(longCount.incrementAndGet());

        // Create the IncidentLocalization
        IncidentLocalizationDTO incidentLocalizationDTO = incidentLocalizationMapper.toDto(incidentLocalization);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(incidentLocalizationDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the IncidentLocalization in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteIncidentLocalization() {
        // Initialize the database
        insertedIncidentLocalization = incidentLocalizationRepository.save(incidentLocalization).block();

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the incidentLocalization
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, incidentLocalization.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return incidentLocalizationRepository.count().block();
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

    protected IncidentLocalization getPersistedIncidentLocalization(IncidentLocalization incidentLocalization) {
        return incidentLocalizationRepository.findById(incidentLocalization.getId()).block();
    }

    protected void assertPersistedIncidentLocalizationToMatchAllProperties(IncidentLocalization expectedIncidentLocalization) {
        // Test fails because reactive api returns an empty object instead of null
        // assertIncidentLocalizationAllPropertiesEquals(expectedIncidentLocalization, getPersistedIncidentLocalization(expectedIncidentLocalization));
        assertIncidentLocalizationUpdatableFieldsEquals(
            expectedIncidentLocalization,
            getPersistedIncidentLocalization(expectedIncidentLocalization)
        );
    }

    protected void assertPersistedIncidentLocalizationToMatchUpdatableProperties(IncidentLocalization expectedIncidentLocalization) {
        // Test fails because reactive api returns an empty object instead of null
        // assertIncidentLocalizationAllUpdatablePropertiesEquals(expectedIncidentLocalization, getPersistedIncidentLocalization(expectedIncidentLocalization));
        assertIncidentLocalizationUpdatableFieldsEquals(
            expectedIncidentLocalization,
            getPersistedIncidentLocalization(expectedIncidentLocalization)
        );
    }
}

package ar.com.heliguard.app.web.rest;

import static ar.com.heliguard.app.domain.AuditTemplateAsserts.*;
import static ar.com.heliguard.app.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

import ar.com.heliguard.app.IntegrationTest;
import ar.com.heliguard.app.domain.AuditTemplate;
import ar.com.heliguard.app.domain.Organization;
import ar.com.heliguard.app.repository.AuditTemplateRepository;
import ar.com.heliguard.app.service.AuditTemplateService;
import ar.com.heliguard.app.service.dto.AuditTemplateDTO;
import ar.com.heliguard.app.service.mapper.AuditTemplateMapper;
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
 * Integration tests for the {@link AuditTemplateResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class AuditTemplateResourceIT {

    private static final String DEFAULT_TITLE = "AAAAAAAAAA";
    private static final String UPDATED_TITLE = "BBBBBBBBBB";

    private static final String DEFAULT_TEMPLATE_VERSION = "AAAAAAAAAA";
    private static final String UPDATED_TEMPLATE_VERSION = "BBBBBBBBBB";

    private static final Boolean DEFAULT_ACTIVE = false;
    private static final Boolean UPDATED_ACTIVE = true;

    private static final Instant DEFAULT_PUBLISHED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_PUBLISHED_AT = Instant.ofEpochMilli(1795370143700L);

    private static final String ENTITY_API_URL = "/api/audit-templates";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    @Autowired
    private ObjectMapper om;

    @Autowired
    private AuditTemplateRepository auditTemplateRepository;

    @Mock
    private AuditTemplateRepository auditTemplateRepositoryMock;

    @Autowired
    private AuditTemplateMapper auditTemplateMapper;

    @Mock
    private AuditTemplateService auditTemplateServiceMock;

    @Autowired
    private R2dbcEntityTemplate template;

    @Autowired
    private WebTestClient webTestClient;

    private AuditTemplate auditTemplate;

    private AuditTemplate insertedAuditTemplate;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static AuditTemplate createEntity(R2dbcEntityTemplate template) {
        AuditTemplate auditTemplate = new AuditTemplate()
            .title(DEFAULT_TITLE)
            .templateVersion(DEFAULT_TEMPLATE_VERSION)
            .active(DEFAULT_ACTIVE)
            .publishedAt(DEFAULT_PUBLISHED_AT);
        // Add required entity
        Organization organization;
        organization = template.insert(OrganizationResourceIT.createEntity()).block();
        auditTemplate.setOrganization(organization);
        return auditTemplate;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static AuditTemplate createUpdatedEntity(R2dbcEntityTemplate template) {
        AuditTemplate updatedAuditTemplate = new AuditTemplate()
            .title(UPDATED_TITLE)
            .templateVersion(UPDATED_TEMPLATE_VERSION)
            .active(UPDATED_ACTIVE)
            .publishedAt(UPDATED_PUBLISHED_AT);
        // Add required entity
        Organization organization;
        organization = template.insert(OrganizationResourceIT.createUpdatedEntity()).block();
        updatedAuditTemplate.setOrganization(organization);
        return updatedAuditTemplate;
    }

    public static void deleteEntities(R2dbcEntityTemplate template) {
        try {
            template.delete(AuditTemplate.class).all().block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
        OrganizationResourceIT.deleteEntities(template);
    }

    @BeforeEach
    void initTest() {
        auditTemplate = createEntity(template);
    }

    @AfterEach
    void cleanup() {
        if (insertedAuditTemplate != null) {
            auditTemplateRepository.delete(insertedAuditTemplate).block();
            insertedAuditTemplate = null;
        }
        deleteEntities(template);
    }

    @Test
    void createAuditTemplate() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the AuditTemplate
        AuditTemplateDTO auditTemplateDTO = auditTemplateMapper.toDto(auditTemplate);
        var returnedAuditTemplateDTO = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(auditTemplateDTO))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(AuditTemplateDTO.class)
            .returnResult()
            .getResponseBody();

        // Validate the AuditTemplate in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedAuditTemplate = auditTemplateMapper.toEntity(returnedAuditTemplateDTO);
        assertAuditTemplateUpdatableFieldsEquals(returnedAuditTemplate, getPersistedAuditTemplate(returnedAuditTemplate));

        insertedAuditTemplate = returnedAuditTemplate;
    }

    @Test
    void createAuditTemplateWithExistingId() throws Exception {
        // Create the AuditTemplate with an existing ID
        auditTemplate.setId(1L);
        AuditTemplateDTO auditTemplateDTO = auditTemplateMapper.toDto(auditTemplate);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(auditTemplateDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the AuditTemplate in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    void checkTitleIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        auditTemplate.setTitle(null);

        // Create the AuditTemplate, which fails.
        AuditTemplateDTO auditTemplateDTO = auditTemplateMapper.toDto(auditTemplate);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(auditTemplateDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkTemplateVersionIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        auditTemplate.setTemplateVersion(null);

        // Create the AuditTemplate, which fails.
        AuditTemplateDTO auditTemplateDTO = auditTemplateMapper.toDto(auditTemplate);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(auditTemplateDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkActiveIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        auditTemplate.setActive(null);

        // Create the AuditTemplate, which fails.
        AuditTemplateDTO auditTemplateDTO = auditTemplateMapper.toDto(auditTemplate);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(auditTemplateDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void getAllAuditTemplatesAsStream() {
        // Initialize the database
        auditTemplateRepository.save(auditTemplate).block();

        List<AuditTemplate> auditTemplateList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(AuditTemplateDTO.class)
            .getResponseBody()
            .map(auditTemplateMapper::toEntity)
            .filter(auditTemplate::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(auditTemplateList).isNotNull();
        assertThat(auditTemplateList).hasSize(1);
        AuditTemplate testAuditTemplate = auditTemplateList.getFirst();

        // Test fails because reactive api returns an empty object instead of null
        // assertAuditTemplateAllPropertiesEquals(auditTemplate, testAuditTemplate);
        assertAuditTemplateUpdatableFieldsEquals(auditTemplate, testAuditTemplate);
    }

    @Test
    void getAllAuditTemplates() {
        // Initialize the database
        insertedAuditTemplate = auditTemplateRepository.save(auditTemplate).block();

        // Get all the auditTemplateList
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
            .value(hasItem(auditTemplate.getId().intValue()))
            .jsonPath("$.[*].title")
            .value(hasItem(DEFAULT_TITLE))
            .jsonPath("$.[*].templateVersion")
            .value(hasItem(DEFAULT_TEMPLATE_VERSION))
            .jsonPath("$.[*].active")
            .value(hasItem(DEFAULT_ACTIVE))
            .jsonPath("$.[*].publishedAt")
            .value(hasItem(DEFAULT_PUBLISHED_AT.toString()));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllAuditTemplatesWithEagerRelationshipsIsEnabled() {
        when(auditTemplateServiceMock.findAllWithEagerRelationships()).thenReturn(Flux.empty());

        webTestClient
            .get()
            .uri(ENTITY_API_URL + "?eagerload=true")
            .exchange()
            .expectStatus()
            .isOk();

        verify(auditTemplateServiceMock, times(1)).findAllWithEagerRelationships();
    }

    @SuppressWarnings({ "unchecked" })
    void getAllAuditTemplatesWithEagerRelationshipsIsNotEnabled() {
        when(auditTemplateServiceMock.findAllWithEagerRelationships()).thenReturn(Flux.empty());

        webTestClient
            .get()
            .uri(ENTITY_API_URL + "?eagerload=false")
            .exchange()
            .expectStatus()
            .isOk();
        verify(auditTemplateRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    void getAuditTemplate() {
        // Initialize the database
        insertedAuditTemplate = auditTemplateRepository.save(auditTemplate).block();

        // Get the auditTemplate
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, auditTemplate.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(auditTemplate.getId().intValue()))
            .jsonPath("$.title")
            .value(is(DEFAULT_TITLE))
            .jsonPath("$.templateVersion")
            .value(is(DEFAULT_TEMPLATE_VERSION))
            .jsonPath("$.active")
            .value(is(DEFAULT_ACTIVE))
            .jsonPath("$.publishedAt")
            .value(is(DEFAULT_PUBLISHED_AT.toString()));
    }

    @Test
    void getNonExistingAuditTemplate() {
        // Get the auditTemplate
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingAuditTemplate() throws Exception {
        // Initialize the database
        insertedAuditTemplate = auditTemplateRepository.save(auditTemplate).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the auditTemplate
        AuditTemplate updatedAuditTemplate = auditTemplateRepository.findById(auditTemplate.getId()).block();
        updatedAuditTemplate
            .title(UPDATED_TITLE)
            .templateVersion(UPDATED_TEMPLATE_VERSION)
            .active(UPDATED_ACTIVE)
            .publishedAt(UPDATED_PUBLISHED_AT);
        AuditTemplateDTO auditTemplateDTO = auditTemplateMapper.toDto(updatedAuditTemplate);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, auditTemplateDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(auditTemplateDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the AuditTemplate in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedAuditTemplateToMatchAllProperties(updatedAuditTemplate);
    }

    @Test
    void putNonExistingAuditTemplate() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        auditTemplate.setId(longCount.incrementAndGet());

        // Create the AuditTemplate
        AuditTemplateDTO auditTemplateDTO = auditTemplateMapper.toDto(auditTemplate);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, auditTemplateDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(auditTemplateDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the AuditTemplate in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchAuditTemplate() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        auditTemplate.setId(longCount.incrementAndGet());

        // Create the AuditTemplate
        AuditTemplateDTO auditTemplateDTO = auditTemplateMapper.toDto(auditTemplate);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(auditTemplateDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the AuditTemplate in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamAuditTemplate() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        auditTemplate.setId(longCount.incrementAndGet());

        // Create the AuditTemplate
        AuditTemplateDTO auditTemplateDTO = auditTemplateMapper.toDto(auditTemplate);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(auditTemplateDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the AuditTemplate in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateAuditTemplateWithPatch() throws Exception {
        // Initialize the database
        insertedAuditTemplate = auditTemplateRepository.save(auditTemplate).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the auditTemplate using partial update
        AuditTemplate partialUpdatedAuditTemplate = new AuditTemplate();
        partialUpdatedAuditTemplate.setId(auditTemplate.getId());

        partialUpdatedAuditTemplate.templateVersion(UPDATED_TEMPLATE_VERSION);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedAuditTemplate.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedAuditTemplate))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the AuditTemplate in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertAuditTemplateUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedAuditTemplate, auditTemplate),
            getPersistedAuditTemplate(auditTemplate)
        );
    }

    @Test
    void fullUpdateAuditTemplateWithPatch() throws Exception {
        // Initialize the database
        insertedAuditTemplate = auditTemplateRepository.save(auditTemplate).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the auditTemplate using partial update
        AuditTemplate partialUpdatedAuditTemplate = new AuditTemplate();
        partialUpdatedAuditTemplate.setId(auditTemplate.getId());

        partialUpdatedAuditTemplate
            .title(UPDATED_TITLE)
            .templateVersion(UPDATED_TEMPLATE_VERSION)
            .active(UPDATED_ACTIVE)
            .publishedAt(UPDATED_PUBLISHED_AT);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedAuditTemplate.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedAuditTemplate))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the AuditTemplate in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertAuditTemplateUpdatableFieldsEquals(partialUpdatedAuditTemplate, getPersistedAuditTemplate(partialUpdatedAuditTemplate));
    }

    @Test
    void patchNonExistingAuditTemplate() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        auditTemplate.setId(longCount.incrementAndGet());

        // Create the AuditTemplate
        AuditTemplateDTO auditTemplateDTO = auditTemplateMapper.toDto(auditTemplate);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, auditTemplateDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(auditTemplateDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the AuditTemplate in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchAuditTemplate() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        auditTemplate.setId(longCount.incrementAndGet());

        // Create the AuditTemplate
        AuditTemplateDTO auditTemplateDTO = auditTemplateMapper.toDto(auditTemplate);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(auditTemplateDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the AuditTemplate in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamAuditTemplate() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        auditTemplate.setId(longCount.incrementAndGet());

        // Create the AuditTemplate
        AuditTemplateDTO auditTemplateDTO = auditTemplateMapper.toDto(auditTemplate);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(auditTemplateDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the AuditTemplate in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteAuditTemplate() {
        // Initialize the database
        insertedAuditTemplate = auditTemplateRepository.save(auditTemplate).block();

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the auditTemplate
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, auditTemplate.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return auditTemplateRepository.count().block();
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

    protected AuditTemplate getPersistedAuditTemplate(AuditTemplate auditTemplate) {
        return auditTemplateRepository.findById(auditTemplate.getId()).block();
    }

    protected void assertPersistedAuditTemplateToMatchAllProperties(AuditTemplate expectedAuditTemplate) {
        // Test fails because reactive api returns an empty object instead of null
        // assertAuditTemplateAllPropertiesEquals(expectedAuditTemplate, getPersistedAuditTemplate(expectedAuditTemplate));
        assertAuditTemplateUpdatableFieldsEquals(expectedAuditTemplate, getPersistedAuditTemplate(expectedAuditTemplate));
    }

    protected void assertPersistedAuditTemplateToMatchUpdatableProperties(AuditTemplate expectedAuditTemplate) {
        // Test fails because reactive api returns an empty object instead of null
        // assertAuditTemplateAllUpdatablePropertiesEquals(expectedAuditTemplate, getPersistedAuditTemplate(expectedAuditTemplate));
        assertAuditTemplateUpdatableFieldsEquals(expectedAuditTemplate, getPersistedAuditTemplate(expectedAuditTemplate));
    }
}

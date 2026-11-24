package ar.com.heliguard.app.web.rest;

import static ar.com.heliguard.app.domain.AuditReportSignatureAsserts.*;
import static ar.com.heliguard.app.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

import ar.com.heliguard.app.IntegrationTest;
import ar.com.heliguard.app.domain.AuditReport;
import ar.com.heliguard.app.domain.AuditReportSignature;
import ar.com.heliguard.app.domain.Organization;
import ar.com.heliguard.app.domain.OrganizationMembership;
import ar.com.heliguard.app.repository.AuditReportSignatureRepository;
import ar.com.heliguard.app.service.AuditReportSignatureService;
import ar.com.heliguard.app.service.dto.AuditReportSignatureDTO;
import ar.com.heliguard.app.service.mapper.AuditReportSignatureMapper;
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
 * Integration tests for the {@link AuditReportSignatureResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class AuditReportSignatureResourceIT {

    private static final Instant DEFAULT_SIGNED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_SIGNED_AT = Instant.ofEpochMilli(1795370143700L);

    private static final String DEFAULT_DOCUMENT_SHA_256 = "AAAAAAAAAA";
    private static final String UPDATED_DOCUMENT_SHA_256 = "BBBBBBBBBB";

    private static final String DEFAULT_SIGNATURE_REFERENCE = "AAAAAAAAAA";
    private static final String UPDATED_SIGNATURE_REFERENCE = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/audit-report-signatures";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    @Autowired
    private ObjectMapper om;

    @Autowired
    private AuditReportSignatureRepository auditReportSignatureRepository;

    @Mock
    private AuditReportSignatureRepository auditReportSignatureRepositoryMock;

    @Autowired
    private AuditReportSignatureMapper auditReportSignatureMapper;

    @Mock
    private AuditReportSignatureService auditReportSignatureServiceMock;

    @Autowired
    private R2dbcEntityTemplate template;

    @Autowired
    private WebTestClient webTestClient;

    private AuditReportSignature auditReportSignature;

    private AuditReportSignature insertedAuditReportSignature;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static AuditReportSignature createEntity(R2dbcEntityTemplate template) {
        AuditReportSignature auditReportSignature = new AuditReportSignature()
            .signedAt(DEFAULT_SIGNED_AT)
            .documentSha256(DEFAULT_DOCUMENT_SHA_256)
            .signatureReference(DEFAULT_SIGNATURE_REFERENCE);
        // Add required entity
        Organization organization;
        organization = template.insert(OrganizationResourceIT.createEntity()).block();
        auditReportSignature.setOrganization(organization);
        // Add required entity
        AuditReport auditReport;
        auditReport = template.insert(AuditReportResourceIT.createEntity(template)).block();
        auditReportSignature.setAuditReport(auditReport);
        // Add required entity
        OrganizationMembership organizationMembership;
        organizationMembership = template.insert(OrganizationMembershipResourceIT.createEntity(template)).block();
        auditReportSignature.setSignerMembership(organizationMembership);
        return auditReportSignature;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static AuditReportSignature createUpdatedEntity(R2dbcEntityTemplate template) {
        AuditReportSignature updatedAuditReportSignature = new AuditReportSignature()
            .signedAt(UPDATED_SIGNED_AT)
            .documentSha256(UPDATED_DOCUMENT_SHA_256)
            .signatureReference(UPDATED_SIGNATURE_REFERENCE);
        // Add required entity
        Organization organization;
        organization = template.insert(OrganizationResourceIT.createUpdatedEntity()).block();
        updatedAuditReportSignature.setOrganization(organization);
        // Add required entity
        AuditReport auditReport;
        auditReport = template.insert(AuditReportResourceIT.createUpdatedEntity(template)).block();
        updatedAuditReportSignature.setAuditReport(auditReport);
        // Add required entity
        OrganizationMembership organizationMembership;
        organizationMembership = template.insert(OrganizationMembershipResourceIT.createUpdatedEntity(template)).block();
        updatedAuditReportSignature.setSignerMembership(organizationMembership);
        return updatedAuditReportSignature;
    }

    public static void deleteEntities(R2dbcEntityTemplate template) {
        try {
            template.delete(AuditReportSignature.class).all().block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
        OrganizationResourceIT.deleteEntities(template);
        AuditReportResourceIT.deleteEntities(template);
        OrganizationMembershipResourceIT.deleteEntities(template);
    }

    @BeforeEach
    void initTest() {
        auditReportSignature = createEntity(template);
    }

    @AfterEach
    void cleanup() {
        if (insertedAuditReportSignature != null) {
            auditReportSignatureRepository.delete(insertedAuditReportSignature).block();
            insertedAuditReportSignature = null;
        }
        deleteEntities(template);
    }

    @Test
    void createAuditReportSignature() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the AuditReportSignature
        AuditReportSignatureDTO auditReportSignatureDTO = auditReportSignatureMapper.toDto(auditReportSignature);
        var returnedAuditReportSignatureDTO = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(auditReportSignatureDTO))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(AuditReportSignatureDTO.class)
            .returnResult()
            .getResponseBody();

        // Validate the AuditReportSignature in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedAuditReportSignature = auditReportSignatureMapper.toEntity(returnedAuditReportSignatureDTO);
        assertAuditReportSignatureUpdatableFieldsEquals(
            returnedAuditReportSignature,
            getPersistedAuditReportSignature(returnedAuditReportSignature)
        );

        insertedAuditReportSignature = returnedAuditReportSignature;
    }

    @Test
    void createAuditReportSignatureWithExistingId() throws Exception {
        // Create the AuditReportSignature with an existing ID
        auditReportSignature.setId(1L);
        AuditReportSignatureDTO auditReportSignatureDTO = auditReportSignatureMapper.toDto(auditReportSignature);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(auditReportSignatureDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the AuditReportSignature in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    void checkSignedAtIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        auditReportSignature.setSignedAt(null);

        // Create the AuditReportSignature, which fails.
        AuditReportSignatureDTO auditReportSignatureDTO = auditReportSignatureMapper.toDto(auditReportSignature);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(auditReportSignatureDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkDocumentSha256IsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        auditReportSignature.setDocumentSha256(null);

        // Create the AuditReportSignature, which fails.
        AuditReportSignatureDTO auditReportSignatureDTO = auditReportSignatureMapper.toDto(auditReportSignature);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(auditReportSignatureDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void getAllAuditReportSignaturesAsStream() {
        // Initialize the database
        auditReportSignatureRepository.save(auditReportSignature).block();

        List<AuditReportSignature> auditReportSignatureList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(AuditReportSignatureDTO.class)
            .getResponseBody()
            .map(auditReportSignatureMapper::toEntity)
            .filter(auditReportSignature::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(auditReportSignatureList).isNotNull();
        assertThat(auditReportSignatureList).hasSize(1);
        AuditReportSignature testAuditReportSignature = auditReportSignatureList.getFirst();

        // Test fails because reactive api returns an empty object instead of null
        // assertAuditReportSignatureAllPropertiesEquals(auditReportSignature, testAuditReportSignature);
        assertAuditReportSignatureUpdatableFieldsEquals(auditReportSignature, testAuditReportSignature);
    }

    @Test
    void getAllAuditReportSignatures() {
        // Initialize the database
        insertedAuditReportSignature = auditReportSignatureRepository.save(auditReportSignature).block();

        // Get all the auditReportSignatureList
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
            .value(hasItem(auditReportSignature.getId().intValue()))
            .jsonPath("$.[*].signedAt")
            .value(hasItem(DEFAULT_SIGNED_AT.toString()))
            .jsonPath("$.[*].documentSha256")
            .value(hasItem(DEFAULT_DOCUMENT_SHA_256))
            .jsonPath("$.[*].signatureReference")
            .value(hasItem(DEFAULT_SIGNATURE_REFERENCE));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllAuditReportSignaturesWithEagerRelationshipsIsEnabled() {
        when(auditReportSignatureServiceMock.findAllWithEagerRelationships()).thenReturn(Flux.empty());

        webTestClient
            .get()
            .uri(ENTITY_API_URL + "?eagerload=true")
            .exchange()
            .expectStatus()
            .isOk();

        verify(auditReportSignatureServiceMock, times(1)).findAllWithEagerRelationships();
    }

    @SuppressWarnings({ "unchecked" })
    void getAllAuditReportSignaturesWithEagerRelationshipsIsNotEnabled() {
        when(auditReportSignatureServiceMock.findAllWithEagerRelationships()).thenReturn(Flux.empty());

        webTestClient
            .get()
            .uri(ENTITY_API_URL + "?eagerload=false")
            .exchange()
            .expectStatus()
            .isOk();
        verify(auditReportSignatureRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    void getAuditReportSignature() {
        // Initialize the database
        insertedAuditReportSignature = auditReportSignatureRepository.save(auditReportSignature).block();

        // Get the auditReportSignature
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, auditReportSignature.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(auditReportSignature.getId().intValue()))
            .jsonPath("$.signedAt")
            .value(is(DEFAULT_SIGNED_AT.toString()))
            .jsonPath("$.documentSha256")
            .value(is(DEFAULT_DOCUMENT_SHA_256))
            .jsonPath("$.signatureReference")
            .value(is(DEFAULT_SIGNATURE_REFERENCE));
    }

    @Test
    void getNonExistingAuditReportSignature() {
        // Get the auditReportSignature
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingAuditReportSignature() throws Exception {
        // Initialize the database
        insertedAuditReportSignature = auditReportSignatureRepository.save(auditReportSignature).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the auditReportSignature
        AuditReportSignature updatedAuditReportSignature = auditReportSignatureRepository.findById(auditReportSignature.getId()).block();
        updatedAuditReportSignature
            .signedAt(UPDATED_SIGNED_AT)
            .documentSha256(UPDATED_DOCUMENT_SHA_256)
            .signatureReference(UPDATED_SIGNATURE_REFERENCE);
        AuditReportSignatureDTO auditReportSignatureDTO = auditReportSignatureMapper.toDto(updatedAuditReportSignature);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, auditReportSignatureDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(auditReportSignatureDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the AuditReportSignature in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedAuditReportSignatureToMatchAllProperties(updatedAuditReportSignature);
    }

    @Test
    void putNonExistingAuditReportSignature() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        auditReportSignature.setId(longCount.incrementAndGet());

        // Create the AuditReportSignature
        AuditReportSignatureDTO auditReportSignatureDTO = auditReportSignatureMapper.toDto(auditReportSignature);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, auditReportSignatureDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(auditReportSignatureDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the AuditReportSignature in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchAuditReportSignature() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        auditReportSignature.setId(longCount.incrementAndGet());

        // Create the AuditReportSignature
        AuditReportSignatureDTO auditReportSignatureDTO = auditReportSignatureMapper.toDto(auditReportSignature);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(auditReportSignatureDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the AuditReportSignature in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamAuditReportSignature() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        auditReportSignature.setId(longCount.incrementAndGet());

        // Create the AuditReportSignature
        AuditReportSignatureDTO auditReportSignatureDTO = auditReportSignatureMapper.toDto(auditReportSignature);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(auditReportSignatureDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the AuditReportSignature in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateAuditReportSignatureWithPatch() throws Exception {
        // Initialize the database
        insertedAuditReportSignature = auditReportSignatureRepository.save(auditReportSignature).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the auditReportSignature using partial update
        AuditReportSignature partialUpdatedAuditReportSignature = new AuditReportSignature();
        partialUpdatedAuditReportSignature.setId(auditReportSignature.getId());

        partialUpdatedAuditReportSignature.signedAt(UPDATED_SIGNED_AT);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedAuditReportSignature.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedAuditReportSignature))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the AuditReportSignature in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertAuditReportSignatureUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedAuditReportSignature, auditReportSignature),
            getPersistedAuditReportSignature(auditReportSignature)
        );
    }

    @Test
    void fullUpdateAuditReportSignatureWithPatch() throws Exception {
        // Initialize the database
        insertedAuditReportSignature = auditReportSignatureRepository.save(auditReportSignature).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the auditReportSignature using partial update
        AuditReportSignature partialUpdatedAuditReportSignature = new AuditReportSignature();
        partialUpdatedAuditReportSignature.setId(auditReportSignature.getId());

        partialUpdatedAuditReportSignature
            .signedAt(UPDATED_SIGNED_AT)
            .documentSha256(UPDATED_DOCUMENT_SHA_256)
            .signatureReference(UPDATED_SIGNATURE_REFERENCE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedAuditReportSignature.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedAuditReportSignature))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the AuditReportSignature in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertAuditReportSignatureUpdatableFieldsEquals(
            partialUpdatedAuditReportSignature,
            getPersistedAuditReportSignature(partialUpdatedAuditReportSignature)
        );
    }

    @Test
    void patchNonExistingAuditReportSignature() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        auditReportSignature.setId(longCount.incrementAndGet());

        // Create the AuditReportSignature
        AuditReportSignatureDTO auditReportSignatureDTO = auditReportSignatureMapper.toDto(auditReportSignature);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, auditReportSignatureDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(auditReportSignatureDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the AuditReportSignature in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchAuditReportSignature() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        auditReportSignature.setId(longCount.incrementAndGet());

        // Create the AuditReportSignature
        AuditReportSignatureDTO auditReportSignatureDTO = auditReportSignatureMapper.toDto(auditReportSignature);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(auditReportSignatureDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the AuditReportSignature in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamAuditReportSignature() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        auditReportSignature.setId(longCount.incrementAndGet());

        // Create the AuditReportSignature
        AuditReportSignatureDTO auditReportSignatureDTO = auditReportSignatureMapper.toDto(auditReportSignature);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(auditReportSignatureDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the AuditReportSignature in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteAuditReportSignature() {
        // Initialize the database
        insertedAuditReportSignature = auditReportSignatureRepository.save(auditReportSignature).block();

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the auditReportSignature
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, auditReportSignature.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return auditReportSignatureRepository.count().block();
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

    protected AuditReportSignature getPersistedAuditReportSignature(AuditReportSignature auditReportSignature) {
        return auditReportSignatureRepository.findById(auditReportSignature.getId()).block();
    }

    protected void assertPersistedAuditReportSignatureToMatchAllProperties(AuditReportSignature expectedAuditReportSignature) {
        // Test fails because reactive api returns an empty object instead of null
        // assertAuditReportSignatureAllPropertiesEquals(expectedAuditReportSignature, getPersistedAuditReportSignature(expectedAuditReportSignature));
        assertAuditReportSignatureUpdatableFieldsEquals(
            expectedAuditReportSignature,
            getPersistedAuditReportSignature(expectedAuditReportSignature)
        );
    }

    protected void assertPersistedAuditReportSignatureToMatchUpdatableProperties(AuditReportSignature expectedAuditReportSignature) {
        // Test fails because reactive api returns an empty object instead of null
        // assertAuditReportSignatureAllUpdatablePropertiesEquals(expectedAuditReportSignature, getPersistedAuditReportSignature(expectedAuditReportSignature));
        assertAuditReportSignatureUpdatableFieldsEquals(
            expectedAuditReportSignature,
            getPersistedAuditReportSignature(expectedAuditReportSignature)
        );
    }
}

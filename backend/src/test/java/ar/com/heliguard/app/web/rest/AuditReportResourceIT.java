package ar.com.heliguard.app.web.rest;

import static ar.com.heliguard.app.domain.AuditReportAsserts.*;
import static ar.com.heliguard.app.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

import ar.com.heliguard.app.IntegrationTest;
import ar.com.heliguard.app.domain.AuditReport;
import ar.com.heliguard.app.domain.AuditTemplate;
import ar.com.heliguard.app.domain.Installation;
import ar.com.heliguard.app.domain.Organization;
import ar.com.heliguard.app.domain.enumeration.AuditStatus;
import ar.com.heliguard.app.repository.AuditReportRepository;
import ar.com.heliguard.app.service.AuditReportService;
import ar.com.heliguard.app.service.dto.AuditReportDTO;
import ar.com.heliguard.app.service.mapper.AuditReportMapper;
import java.time.Instant;
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
 * Integration tests for the {@link AuditReportResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class AuditReportResourceIT {

    private static final AuditStatus DEFAULT_STATUS = AuditStatus.DRAFT;
    private static final AuditStatus UPDATED_STATUS = AuditStatus.IN_PROGRESS;

    private static final Instant DEFAULT_STARTED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_STARTED_AT = Instant.ofEpochMilli(1795370143700L);

    private static final Instant DEFAULT_COMPLETED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_COMPLETED_AT = Instant.ofEpochMilli(1795370143700L);

    private static final String DEFAULT_SUMMARY = "AAAAAAAAAA";
    private static final String UPDATED_SUMMARY = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/audit-reports";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    @Autowired
    private ObjectMapper om;

    @Autowired
    private AuditReportRepository auditReportRepository;

    @Mock
    private AuditReportRepository auditReportRepositoryMock;

    @Autowired
    private AuditReportMapper auditReportMapper;

    @Mock
    private AuditReportService auditReportServiceMock;

    @Autowired
    private R2dbcEntityTemplate template;

    @Autowired
    private WebTestClient webTestClient;

    private AuditReport auditReport;

    private AuditReport insertedAuditReport;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static AuditReport createEntity(R2dbcEntityTemplate template) {
        AuditReport auditReport = new AuditReport()
            .status(DEFAULT_STATUS)
            .startedAt(DEFAULT_STARTED_AT)
            .completedAt(DEFAULT_COMPLETED_AT)
            .summary(DEFAULT_SUMMARY);
        // Add required entity
        Organization organization;
        organization = template.insert(OrganizationResourceIT.createEntity()).block();
        auditReport.setOrganization(organization);
        // Add required entity
        Installation installation;
        installation = template.insert(InstallationResourceIT.createEntity(template)).block();
        auditReport.setInstallation(installation);
        // Add required entity
        AuditTemplate auditTemplate;
        auditTemplate = template.insert(AuditTemplateResourceIT.createEntity(template)).block();
        auditReport.setAuditTemplate(auditTemplate);
        return auditReport;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static AuditReport createUpdatedEntity(R2dbcEntityTemplate template) {
        AuditReport updatedAuditReport = new AuditReport()
            .status(UPDATED_STATUS)
            .startedAt(UPDATED_STARTED_AT)
            .completedAt(UPDATED_COMPLETED_AT)
            .summary(UPDATED_SUMMARY);
        // Add required entity
        Organization organization;
        organization = template.insert(OrganizationResourceIT.createUpdatedEntity()).block();
        updatedAuditReport.setOrganization(organization);
        // Add required entity
        Installation installation;
        installation = template.insert(InstallationResourceIT.createUpdatedEntity(template)).block();
        updatedAuditReport.setInstallation(installation);
        // Add required entity
        AuditTemplate auditTemplate;
        auditTemplate = template.insert(AuditTemplateResourceIT.createUpdatedEntity(template)).block();
        updatedAuditReport.setAuditTemplate(auditTemplate);
        return updatedAuditReport;
    }

    public static void deleteEntities(R2dbcEntityTemplate template) {
        try {
            template.delete(AuditReport.class).all().block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
        OrganizationResourceIT.deleteEntities(template);
        InstallationResourceIT.deleteEntities(template);
        AuditTemplateResourceIT.deleteEntities(template);
    }

    @BeforeEach
    void initTest() {
        auditReport = createEntity(template);
    }

    @AfterEach
    void cleanup() {
        if (insertedAuditReport != null) {
            auditReportRepository.delete(insertedAuditReport).block();
            insertedAuditReport = null;
        }
        deleteEntities(template);
    }

    @Test
    void createAuditReport() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the AuditReport
        AuditReportDTO auditReportDTO = auditReportMapper.toDto(auditReport);
        var returnedAuditReportDTO = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(auditReportDTO))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(AuditReportDTO.class)
            .returnResult()
            .getResponseBody();

        // Validate the AuditReport in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedAuditReport = auditReportMapper.toEntity(returnedAuditReportDTO);
        assertAuditReportUpdatableFieldsEquals(returnedAuditReport, getPersistedAuditReport(returnedAuditReport));

        insertedAuditReport = returnedAuditReport;
    }

    @Test
    void createAuditReportWithExistingId() throws Exception {
        // Create the AuditReport with an existing ID
        auditReport.setId(1L);
        AuditReportDTO auditReportDTO = auditReportMapper.toDto(auditReport);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(auditReportDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the AuditReport in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    void checkStatusIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        auditReport.setStatus(null);

        // Create the AuditReport, which fails.
        AuditReportDTO auditReportDTO = auditReportMapper.toDto(auditReport);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(auditReportDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkStartedAtIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        auditReport.setStartedAt(null);

        // Create the AuditReport, which fails.
        AuditReportDTO auditReportDTO = auditReportMapper.toDto(auditReport);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(auditReportDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void getAllAuditReports() {
        // Initialize the database
        insertedAuditReport = auditReportRepository.save(auditReport).block();

        // Get all the auditReportList
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
            .value(hasItem(auditReport.getId().intValue()))
            .jsonPath("$.[*].status")
            .value(hasItem(DEFAULT_STATUS.toString()))
            .jsonPath("$.[*].startedAt")
            .value(hasItem(DEFAULT_STARTED_AT.toString()))
            .jsonPath("$.[*].completedAt")
            .value(hasItem(DEFAULT_COMPLETED_AT.toString()))
            .jsonPath("$.[*].summary")
            .value(hasItem(DEFAULT_SUMMARY));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllAuditReportsWithEagerRelationshipsIsEnabled() {
        when(auditReportServiceMock.findAllWithEagerRelationships(any())).thenReturn(Flux.empty());

        webTestClient
            .get()
            .uri(ENTITY_API_URL + "?eagerload=true")
            .exchange()
            .expectStatus()
            .isOk();

        verify(auditReportServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllAuditReportsWithEagerRelationshipsIsNotEnabled() {
        when(auditReportServiceMock.findAllWithEagerRelationships(any())).thenReturn(Flux.empty());

        webTestClient
            .get()
            .uri(ENTITY_API_URL + "?eagerload=false")
            .exchange()
            .expectStatus()
            .isOk();
        verify(auditReportRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    void getAuditReport() {
        // Initialize the database
        insertedAuditReport = auditReportRepository.save(auditReport).block();

        // Get the auditReport
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, auditReport.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(auditReport.getId().intValue()))
            .jsonPath("$.status")
            .value(is(DEFAULT_STATUS.toString()))
            .jsonPath("$.startedAt")
            .value(is(DEFAULT_STARTED_AT.toString()))
            .jsonPath("$.completedAt")
            .value(is(DEFAULT_COMPLETED_AT.toString()))
            .jsonPath("$.summary")
            .value(is(DEFAULT_SUMMARY));
    }

    @Test
    void getNonExistingAuditReport() {
        // Get the auditReport
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingAuditReport() throws Exception {
        // Initialize the database
        insertedAuditReport = auditReportRepository.save(auditReport).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the auditReport
        AuditReport updatedAuditReport = auditReportRepository.findById(auditReport.getId()).block();
        updatedAuditReport.status(UPDATED_STATUS).startedAt(UPDATED_STARTED_AT).completedAt(UPDATED_COMPLETED_AT).summary(UPDATED_SUMMARY);
        AuditReportDTO auditReportDTO = auditReportMapper.toDto(updatedAuditReport);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, auditReportDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(auditReportDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the AuditReport in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedAuditReportToMatchAllProperties(updatedAuditReport);
    }

    @Test
    void putNonExistingAuditReport() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        auditReport.setId(longCount.incrementAndGet());

        // Create the AuditReport
        AuditReportDTO auditReportDTO = auditReportMapper.toDto(auditReport);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, auditReportDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(auditReportDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the AuditReport in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchAuditReport() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        auditReport.setId(longCount.incrementAndGet());

        // Create the AuditReport
        AuditReportDTO auditReportDTO = auditReportMapper.toDto(auditReport);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(auditReportDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the AuditReport in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamAuditReport() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        auditReport.setId(longCount.incrementAndGet());

        // Create the AuditReport
        AuditReportDTO auditReportDTO = auditReportMapper.toDto(auditReport);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(auditReportDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the AuditReport in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateAuditReportWithPatch() throws Exception {
        // Initialize the database
        insertedAuditReport = auditReportRepository.save(auditReport).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the auditReport using partial update
        AuditReport partialUpdatedAuditReport = new AuditReport();
        partialUpdatedAuditReport.setId(auditReport.getId());

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedAuditReport.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedAuditReport))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the AuditReport in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertAuditReportUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedAuditReport, auditReport),
            getPersistedAuditReport(auditReport)
        );
    }

    @Test
    void fullUpdateAuditReportWithPatch() throws Exception {
        // Initialize the database
        insertedAuditReport = auditReportRepository.save(auditReport).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the auditReport using partial update
        AuditReport partialUpdatedAuditReport = new AuditReport();
        partialUpdatedAuditReport.setId(auditReport.getId());

        partialUpdatedAuditReport
            .status(UPDATED_STATUS)
            .startedAt(UPDATED_STARTED_AT)
            .completedAt(UPDATED_COMPLETED_AT)
            .summary(UPDATED_SUMMARY);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedAuditReport.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedAuditReport))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the AuditReport in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertAuditReportUpdatableFieldsEquals(partialUpdatedAuditReport, getPersistedAuditReport(partialUpdatedAuditReport));
    }

    @Test
    void patchNonExistingAuditReport() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        auditReport.setId(longCount.incrementAndGet());

        // Create the AuditReport
        AuditReportDTO auditReportDTO = auditReportMapper.toDto(auditReport);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, auditReportDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(auditReportDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the AuditReport in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchAuditReport() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        auditReport.setId(longCount.incrementAndGet());

        // Create the AuditReport
        AuditReportDTO auditReportDTO = auditReportMapper.toDto(auditReport);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(auditReportDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the AuditReport in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamAuditReport() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        auditReport.setId(longCount.incrementAndGet());

        // Create the AuditReport
        AuditReportDTO auditReportDTO = auditReportMapper.toDto(auditReport);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(auditReportDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the AuditReport in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteAuditReport() {
        // Initialize the database
        insertedAuditReport = auditReportRepository.save(auditReport).block();

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the auditReport
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, auditReport.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return auditReportRepository.count().block();
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

    protected AuditReport getPersistedAuditReport(AuditReport auditReport) {
        return auditReportRepository.findById(auditReport.getId()).block();
    }

    protected void assertPersistedAuditReportToMatchAllProperties(AuditReport expectedAuditReport) {
        // Test fails because reactive api returns an empty object instead of null
        // assertAuditReportAllPropertiesEquals(expectedAuditReport, getPersistedAuditReport(expectedAuditReport));
        assertAuditReportUpdatableFieldsEquals(expectedAuditReport, getPersistedAuditReport(expectedAuditReport));
    }

    protected void assertPersistedAuditReportToMatchUpdatableProperties(AuditReport expectedAuditReport) {
        // Test fails because reactive api returns an empty object instead of null
        // assertAuditReportAllUpdatablePropertiesEquals(expectedAuditReport, getPersistedAuditReport(expectedAuditReport));
        assertAuditReportUpdatableFieldsEquals(expectedAuditReport, getPersistedAuditReport(expectedAuditReport));
    }
}

package ar.com.heliguard.app.web.rest;

import static ar.com.heliguard.app.domain.InstallationAsserts.*;
import static ar.com.heliguard.app.web.rest.TestUtil.createUpdateProxyForBean;
import static ar.com.heliguard.app.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

import ar.com.heliguard.app.IntegrationTest;
import ar.com.heliguard.app.domain.Installation;
import ar.com.heliguard.app.domain.Organization;
import ar.com.heliguard.app.domain.Site;
import ar.com.heliguard.app.domain.enumeration.GridMode;
import ar.com.heliguard.app.domain.enumeration.InstallationStatus;
import ar.com.heliguard.app.repository.InstallationRepository;
import ar.com.heliguard.app.service.InstallationService;
import ar.com.heliguard.app.service.dto.InstallationDTO;
import ar.com.heliguard.app.service.mapper.InstallationMapper;
import java.math.BigDecimal;
import java.time.LocalDate;
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
 * Integration tests for the {@link InstallationResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class InstallationResourceIT {

    private static final String DEFAULT_CODE = "AAAAAAAAAA";
    private static final String UPDATED_CODE = "BBBBBBBBBB";

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final GridMode DEFAULT_GRID_MODE = GridMode.GRID_TIED;
    private static final GridMode UPDATED_GRID_MODE = GridMode.HYBRID;

    private static final InstallationStatus DEFAULT_STATUS = InstallationStatus.COMMISSIONING;
    private static final InstallationStatus UPDATED_STATUS = InstallationStatus.ACTIVE;

    private static final BigDecimal DEFAULT_NOMINAL_DC_KW = new BigDecimal(0);
    private static final BigDecimal UPDATED_NOMINAL_DC_KW = new BigDecimal(1);

    private static final LocalDate DEFAULT_COMMISSIONED_ON = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_COMMISSIONED_ON = LocalDate.parse("2026-11-22");

    private static final String ENTITY_API_URL = "/api/installations";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    @Autowired
    private ObjectMapper om;

    @Autowired
    private InstallationRepository installationRepository;

    @Mock
    private InstallationRepository installationRepositoryMock;

    @Autowired
    private InstallationMapper installationMapper;

    @Mock
    private InstallationService installationServiceMock;

    @Autowired
    private R2dbcEntityTemplate template;

    @Autowired
    private WebTestClient webTestClient;

    private Installation installation;

    private Installation insertedInstallation;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Installation createEntity(R2dbcEntityTemplate template) {
        Installation installation = new Installation()
            .code(DEFAULT_CODE)
            .name(DEFAULT_NAME)
            .gridMode(DEFAULT_GRID_MODE)
            .status(DEFAULT_STATUS)
            .nominalDcKw(DEFAULT_NOMINAL_DC_KW)
            .commissionedOn(DEFAULT_COMMISSIONED_ON);
        // Add required entity
        Organization organization;
        organization = template.insert(OrganizationResourceIT.createEntity()).block();
        installation.setOrganization(organization);
        // Add required entity
        Site site;
        site = template.insert(SiteResourceIT.createEntity(template)).block();
        installation.setSite(site);
        return installation;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Installation createUpdatedEntity(R2dbcEntityTemplate template) {
        Installation updatedInstallation = new Installation()
            .code(UPDATED_CODE)
            .name(UPDATED_NAME)
            .gridMode(UPDATED_GRID_MODE)
            .status(UPDATED_STATUS)
            .nominalDcKw(UPDATED_NOMINAL_DC_KW)
            .commissionedOn(UPDATED_COMMISSIONED_ON);
        // Add required entity
        Organization organization;
        organization = template.insert(OrganizationResourceIT.createUpdatedEntity()).block();
        updatedInstallation.setOrganization(organization);
        // Add required entity
        Site site;
        site = template.insert(SiteResourceIT.createUpdatedEntity(template)).block();
        updatedInstallation.setSite(site);
        return updatedInstallation;
    }

    public static void deleteEntities(R2dbcEntityTemplate template) {
        try {
            template.delete(Installation.class).all().block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
        OrganizationResourceIT.deleteEntities(template);
        SiteResourceIT.deleteEntities(template);
    }

    @BeforeEach
    void initTest() {
        installation = createEntity(template);
    }

    @AfterEach
    void cleanup() {
        if (insertedInstallation != null) {
            installationRepository.delete(insertedInstallation).block();
            insertedInstallation = null;
        }
        deleteEntities(template);
    }

    @Test
    void createInstallation() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Installation
        InstallationDTO installationDTO = installationMapper.toDto(installation);
        var returnedInstallationDTO = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(installationDTO))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(InstallationDTO.class)
            .returnResult()
            .getResponseBody();

        // Validate the Installation in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedInstallation = installationMapper.toEntity(returnedInstallationDTO);
        assertInstallationUpdatableFieldsEquals(returnedInstallation, getPersistedInstallation(returnedInstallation));

        insertedInstallation = returnedInstallation;
    }

    @Test
    void createInstallationWithExistingId() throws Exception {
        // Create the Installation with an existing ID
        installation.setId(1L);
        InstallationDTO installationDTO = installationMapper.toDto(installation);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(installationDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Installation in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    void checkCodeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        installation.setCode(null);

        // Create the Installation, which fails.
        InstallationDTO installationDTO = installationMapper.toDto(installation);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(installationDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        installation.setName(null);

        // Create the Installation, which fails.
        InstallationDTO installationDTO = installationMapper.toDto(installation);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(installationDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkGridModeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        installation.setGridMode(null);

        // Create the Installation, which fails.
        InstallationDTO installationDTO = installationMapper.toDto(installation);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(installationDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkStatusIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        installation.setStatus(null);

        // Create the Installation, which fails.
        InstallationDTO installationDTO = installationMapper.toDto(installation);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(installationDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkNominalDcKwIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        installation.setNominalDcKw(null);

        // Create the Installation, which fails.
        InstallationDTO installationDTO = installationMapper.toDto(installation);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(installationDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void getAllInstallations() {
        // Initialize the database
        insertedInstallation = installationRepository.save(installation).block();

        // Get all the installationList
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
            .value(hasItem(installation.getId().intValue()))
            .jsonPath("$.[*].code")
            .value(hasItem(DEFAULT_CODE))
            .jsonPath("$.[*].name")
            .value(hasItem(DEFAULT_NAME))
            .jsonPath("$.[*].gridMode")
            .value(hasItem(DEFAULT_GRID_MODE.toString()))
            .jsonPath("$.[*].status")
            .value(hasItem(DEFAULT_STATUS.toString()))
            .jsonPath("$.[*].nominalDcKw")
            .value(hasItem(sameNumber(DEFAULT_NOMINAL_DC_KW)))
            .jsonPath("$.[*].commissionedOn")
            .value(hasItem(DEFAULT_COMMISSIONED_ON.toString()));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllInstallationsWithEagerRelationshipsIsEnabled() {
        when(installationServiceMock.findAllWithEagerRelationships(any())).thenReturn(Flux.empty());

        webTestClient
            .get()
            .uri(ENTITY_API_URL + "?eagerload=true")
            .exchange()
            .expectStatus()
            .isOk();

        verify(installationServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllInstallationsWithEagerRelationshipsIsNotEnabled() {
        when(installationServiceMock.findAllWithEagerRelationships(any())).thenReturn(Flux.empty());

        webTestClient
            .get()
            .uri(ENTITY_API_URL + "?eagerload=false")
            .exchange()
            .expectStatus()
            .isOk();
        verify(installationRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    void getInstallation() {
        // Initialize the database
        insertedInstallation = installationRepository.save(installation).block();

        // Get the installation
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, installation.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(installation.getId().intValue()))
            .jsonPath("$.code")
            .value(is(DEFAULT_CODE))
            .jsonPath("$.name")
            .value(is(DEFAULT_NAME))
            .jsonPath("$.gridMode")
            .value(is(DEFAULT_GRID_MODE.toString()))
            .jsonPath("$.status")
            .value(is(DEFAULT_STATUS.toString()))
            .jsonPath("$.nominalDcKw")
            .value(is(sameNumber(DEFAULT_NOMINAL_DC_KW)))
            .jsonPath("$.commissionedOn")
            .value(is(DEFAULT_COMMISSIONED_ON.toString()));
    }

    @Test
    void getNonExistingInstallation() {
        // Get the installation
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingInstallation() throws Exception {
        // Initialize the database
        insertedInstallation = installationRepository.save(installation).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the installation
        Installation updatedInstallation = installationRepository.findById(installation.getId()).block();
        updatedInstallation
            .code(UPDATED_CODE)
            .name(UPDATED_NAME)
            .gridMode(UPDATED_GRID_MODE)
            .status(UPDATED_STATUS)
            .nominalDcKw(UPDATED_NOMINAL_DC_KW)
            .commissionedOn(UPDATED_COMMISSIONED_ON);
        InstallationDTO installationDTO = installationMapper.toDto(updatedInstallation);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, installationDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(installationDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Installation in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedInstallationToMatchAllProperties(updatedInstallation);
    }

    @Test
    void putNonExistingInstallation() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        installation.setId(longCount.incrementAndGet());

        // Create the Installation
        InstallationDTO installationDTO = installationMapper.toDto(installation);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, installationDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(installationDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Installation in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchInstallation() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        installation.setId(longCount.incrementAndGet());

        // Create the Installation
        InstallationDTO installationDTO = installationMapper.toDto(installation);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(installationDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Installation in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamInstallation() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        installation.setId(longCount.incrementAndGet());

        // Create the Installation
        InstallationDTO installationDTO = installationMapper.toDto(installation);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(installationDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Installation in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateInstallationWithPatch() throws Exception {
        // Initialize the database
        insertedInstallation = installationRepository.save(installation).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the installation using partial update
        Installation partialUpdatedInstallation = new Installation();
        partialUpdatedInstallation.setId(installation.getId());

        partialUpdatedInstallation.name(UPDATED_NAME).gridMode(UPDATED_GRID_MODE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedInstallation.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedInstallation))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Installation in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertInstallationUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedInstallation, installation),
            getPersistedInstallation(installation)
        );
    }

    @Test
    void fullUpdateInstallationWithPatch() throws Exception {
        // Initialize the database
        insertedInstallation = installationRepository.save(installation).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the installation using partial update
        Installation partialUpdatedInstallation = new Installation();
        partialUpdatedInstallation.setId(installation.getId());

        partialUpdatedInstallation
            .code(UPDATED_CODE)
            .name(UPDATED_NAME)
            .gridMode(UPDATED_GRID_MODE)
            .status(UPDATED_STATUS)
            .nominalDcKw(UPDATED_NOMINAL_DC_KW)
            .commissionedOn(UPDATED_COMMISSIONED_ON);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedInstallation.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedInstallation))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Installation in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertInstallationUpdatableFieldsEquals(partialUpdatedInstallation, getPersistedInstallation(partialUpdatedInstallation));
    }

    @Test
    void patchNonExistingInstallation() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        installation.setId(longCount.incrementAndGet());

        // Create the Installation
        InstallationDTO installationDTO = installationMapper.toDto(installation);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, installationDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(installationDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Installation in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchInstallation() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        installation.setId(longCount.incrementAndGet());

        // Create the Installation
        InstallationDTO installationDTO = installationMapper.toDto(installation);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(installationDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Installation in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamInstallation() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        installation.setId(longCount.incrementAndGet());

        // Create the Installation
        InstallationDTO installationDTO = installationMapper.toDto(installation);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(installationDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Installation in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteInstallation() {
        // Initialize the database
        insertedInstallation = installationRepository.save(installation).block();

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the installation
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, installation.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return installationRepository.count().block();
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

    protected Installation getPersistedInstallation(Installation installation) {
        return installationRepository.findById(installation.getId()).block();
    }

    protected void assertPersistedInstallationToMatchAllProperties(Installation expectedInstallation) {
        // Test fails because reactive api returns an empty object instead of null
        // assertInstallationAllPropertiesEquals(expectedInstallation, getPersistedInstallation(expectedInstallation));
        assertInstallationUpdatableFieldsEquals(expectedInstallation, getPersistedInstallation(expectedInstallation));
    }

    protected void assertPersistedInstallationToMatchUpdatableProperties(Installation expectedInstallation) {
        // Test fails because reactive api returns an empty object instead of null
        // assertInstallationAllUpdatablePropertiesEquals(expectedInstallation, getPersistedInstallation(expectedInstallation));
        assertInstallationUpdatableFieldsEquals(expectedInstallation, getPersistedInstallation(expectedInstallation));
    }
}

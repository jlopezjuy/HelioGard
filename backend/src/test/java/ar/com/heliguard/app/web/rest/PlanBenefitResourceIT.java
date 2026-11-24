package ar.com.heliguard.app.web.rest;

import static ar.com.heliguard.app.domain.PlanBenefitAsserts.*;
import static ar.com.heliguard.app.web.rest.TestUtil.createUpdateProxyForBean;
import static ar.com.heliguard.app.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

import ar.com.heliguard.app.IntegrationTest;
import ar.com.heliguard.app.domain.MaintenancePlanVersion;
import ar.com.heliguard.app.domain.Organization;
import ar.com.heliguard.app.domain.PlanBenefit;
import ar.com.heliguard.app.repository.PlanBenefitRepository;
import ar.com.heliguard.app.service.PlanBenefitService;
import ar.com.heliguard.app.service.dto.PlanBenefitDTO;
import ar.com.heliguard.app.service.mapper.PlanBenefitMapper;
import java.math.BigDecimal;
import java.time.Duration;
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
 * Integration tests for the {@link PlanBenefitResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class PlanBenefitResourceIT {

    private static final String DEFAULT_BENEFIT_CODE = "AAAAAAAAAA";
    private static final String UPDATED_BENEFIT_CODE = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final BigDecimal DEFAULT_INCLUDED_QUANTITY = new BigDecimal(1);
    private static final BigDecimal UPDATED_INCLUDED_QUANTITY = new BigDecimal(2);

    private static final String DEFAULT_UNIT_CODE = "AAAAAAAAAA";
    private static final String UPDATED_UNIT_CODE = "BBBBBBBBBB";

    private static final Integer DEFAULT_INTERVAL_MONTHS = 1;
    private static final Integer UPDATED_INTERVAL_MONTHS = 2;

    private static final String DEFAULT_TERMS_REFERENCE = "AAAAAAAAAA";
    private static final String UPDATED_TERMS_REFERENCE = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/plan-benefits";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    @Autowired
    private ObjectMapper om;

    @Autowired
    private PlanBenefitRepository planBenefitRepository;

    @Mock
    private PlanBenefitRepository planBenefitRepositoryMock;

    @Autowired
    private PlanBenefitMapper planBenefitMapper;

    @Mock
    private PlanBenefitService planBenefitServiceMock;

    @Autowired
    private R2dbcEntityTemplate template;

    @Autowired
    private WebTestClient webTestClient;

    private PlanBenefit planBenefit;

    private PlanBenefit insertedPlanBenefit;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static PlanBenefit createEntity(R2dbcEntityTemplate template) {
        PlanBenefit planBenefit = new PlanBenefit()
            .benefitCode(DEFAULT_BENEFIT_CODE)
            .description(DEFAULT_DESCRIPTION)
            .includedQuantity(DEFAULT_INCLUDED_QUANTITY)
            .unitCode(DEFAULT_UNIT_CODE)
            .intervalMonths(DEFAULT_INTERVAL_MONTHS)
            .termsReference(DEFAULT_TERMS_REFERENCE);
        // Add required entity
        Organization organization;
        organization = template.insert(OrganizationResourceIT.createEntity()).block();
        planBenefit.setOrganization(organization);
        // Add required entity
        MaintenancePlanVersion maintenancePlanVersion;
        maintenancePlanVersion = template.insert(MaintenancePlanVersionResourceIT.createEntity(template)).block();
        planBenefit.setPlanVersion(maintenancePlanVersion);
        return planBenefit;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static PlanBenefit createUpdatedEntity(R2dbcEntityTemplate template) {
        PlanBenefit updatedPlanBenefit = new PlanBenefit()
            .benefitCode(UPDATED_BENEFIT_CODE)
            .description(UPDATED_DESCRIPTION)
            .includedQuantity(UPDATED_INCLUDED_QUANTITY)
            .unitCode(UPDATED_UNIT_CODE)
            .intervalMonths(UPDATED_INTERVAL_MONTHS)
            .termsReference(UPDATED_TERMS_REFERENCE);
        // Add required entity
        Organization organization;
        organization = template.insert(OrganizationResourceIT.createUpdatedEntity()).block();
        updatedPlanBenefit.setOrganization(organization);
        // Add required entity
        MaintenancePlanVersion maintenancePlanVersion;
        maintenancePlanVersion = template.insert(MaintenancePlanVersionResourceIT.createUpdatedEntity(template)).block();
        updatedPlanBenefit.setPlanVersion(maintenancePlanVersion);
        return updatedPlanBenefit;
    }

    public static void deleteEntities(R2dbcEntityTemplate template) {
        try {
            template.delete(PlanBenefit.class).all().block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
        OrganizationResourceIT.deleteEntities(template);
        MaintenancePlanVersionResourceIT.deleteEntities(template);
    }

    @BeforeEach
    void initTest() {
        planBenefit = createEntity(template);
    }

    @AfterEach
    void cleanup() {
        if (insertedPlanBenefit != null) {
            planBenefitRepository.delete(insertedPlanBenefit).block();
            insertedPlanBenefit = null;
        }
        deleteEntities(template);
    }

    @Test
    void createPlanBenefit() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the PlanBenefit
        PlanBenefitDTO planBenefitDTO = planBenefitMapper.toDto(planBenefit);
        var returnedPlanBenefitDTO = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(planBenefitDTO))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(PlanBenefitDTO.class)
            .returnResult()
            .getResponseBody();

        // Validate the PlanBenefit in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedPlanBenefit = planBenefitMapper.toEntity(returnedPlanBenefitDTO);
        assertPlanBenefitUpdatableFieldsEquals(returnedPlanBenefit, getPersistedPlanBenefit(returnedPlanBenefit));

        insertedPlanBenefit = returnedPlanBenefit;
    }

    @Test
    void createPlanBenefitWithExistingId() throws Exception {
        // Create the PlanBenefit with an existing ID
        planBenefit.setId(1L);
        PlanBenefitDTO planBenefitDTO = planBenefitMapper.toDto(planBenefit);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(planBenefitDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the PlanBenefit in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    void checkBenefitCodeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        planBenefit.setBenefitCode(null);

        // Create the PlanBenefit, which fails.
        PlanBenefitDTO planBenefitDTO = planBenefitMapper.toDto(planBenefit);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(planBenefitDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkDescriptionIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        planBenefit.setDescription(null);

        // Create the PlanBenefit, which fails.
        PlanBenefitDTO planBenefitDTO = planBenefitMapper.toDto(planBenefit);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(planBenefitDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void getAllPlanBenefitsAsStream() {
        // Initialize the database
        planBenefitRepository.save(planBenefit).block();

        List<PlanBenefit> planBenefitList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(PlanBenefitDTO.class)
            .getResponseBody()
            .map(planBenefitMapper::toEntity)
            .filter(planBenefit::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(planBenefitList).isNotNull();
        assertThat(planBenefitList).hasSize(1);
        PlanBenefit testPlanBenefit = planBenefitList.getFirst();

        // Test fails because reactive api returns an empty object instead of null
        // assertPlanBenefitAllPropertiesEquals(planBenefit, testPlanBenefit);
        assertPlanBenefitUpdatableFieldsEquals(planBenefit, testPlanBenefit);
    }

    @Test
    void getAllPlanBenefits() {
        // Initialize the database
        insertedPlanBenefit = planBenefitRepository.save(planBenefit).block();

        // Get all the planBenefitList
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
            .value(hasItem(planBenefit.getId().intValue()))
            .jsonPath("$.[*].benefitCode")
            .value(hasItem(DEFAULT_BENEFIT_CODE))
            .jsonPath("$.[*].description")
            .value(hasItem(DEFAULT_DESCRIPTION))
            .jsonPath("$.[*].includedQuantity")
            .value(hasItem(sameNumber(DEFAULT_INCLUDED_QUANTITY)))
            .jsonPath("$.[*].unitCode")
            .value(hasItem(DEFAULT_UNIT_CODE))
            .jsonPath("$.[*].intervalMonths")
            .value(hasItem(DEFAULT_INTERVAL_MONTHS))
            .jsonPath("$.[*].termsReference")
            .value(hasItem(DEFAULT_TERMS_REFERENCE));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllPlanBenefitsWithEagerRelationshipsIsEnabled() {
        when(planBenefitServiceMock.findAllWithEagerRelationships()).thenReturn(Flux.empty());

        webTestClient
            .get()
            .uri(ENTITY_API_URL + "?eagerload=true")
            .exchange()
            .expectStatus()
            .isOk();

        verify(planBenefitServiceMock, times(1)).findAllWithEagerRelationships();
    }

    @SuppressWarnings({ "unchecked" })
    void getAllPlanBenefitsWithEagerRelationshipsIsNotEnabled() {
        when(planBenefitServiceMock.findAllWithEagerRelationships()).thenReturn(Flux.empty());

        webTestClient
            .get()
            .uri(ENTITY_API_URL + "?eagerload=false")
            .exchange()
            .expectStatus()
            .isOk();
        verify(planBenefitRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    void getPlanBenefit() {
        // Initialize the database
        insertedPlanBenefit = planBenefitRepository.save(planBenefit).block();

        // Get the planBenefit
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, planBenefit.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(planBenefit.getId().intValue()))
            .jsonPath("$.benefitCode")
            .value(is(DEFAULT_BENEFIT_CODE))
            .jsonPath("$.description")
            .value(is(DEFAULT_DESCRIPTION))
            .jsonPath("$.includedQuantity")
            .value(is(sameNumber(DEFAULT_INCLUDED_QUANTITY)))
            .jsonPath("$.unitCode")
            .value(is(DEFAULT_UNIT_CODE))
            .jsonPath("$.intervalMonths")
            .value(is(DEFAULT_INTERVAL_MONTHS))
            .jsonPath("$.termsReference")
            .value(is(DEFAULT_TERMS_REFERENCE));
    }

    @Test
    void getNonExistingPlanBenefit() {
        // Get the planBenefit
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingPlanBenefit() throws Exception {
        // Initialize the database
        insertedPlanBenefit = planBenefitRepository.save(planBenefit).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the planBenefit
        PlanBenefit updatedPlanBenefit = planBenefitRepository.findById(planBenefit.getId()).block();
        updatedPlanBenefit
            .benefitCode(UPDATED_BENEFIT_CODE)
            .description(UPDATED_DESCRIPTION)
            .includedQuantity(UPDATED_INCLUDED_QUANTITY)
            .unitCode(UPDATED_UNIT_CODE)
            .intervalMonths(UPDATED_INTERVAL_MONTHS)
            .termsReference(UPDATED_TERMS_REFERENCE);
        PlanBenefitDTO planBenefitDTO = planBenefitMapper.toDto(updatedPlanBenefit);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, planBenefitDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(planBenefitDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the PlanBenefit in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedPlanBenefitToMatchAllProperties(updatedPlanBenefit);
    }

    @Test
    void putNonExistingPlanBenefit() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        planBenefit.setId(longCount.incrementAndGet());

        // Create the PlanBenefit
        PlanBenefitDTO planBenefitDTO = planBenefitMapper.toDto(planBenefit);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, planBenefitDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(planBenefitDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the PlanBenefit in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchPlanBenefit() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        planBenefit.setId(longCount.incrementAndGet());

        // Create the PlanBenefit
        PlanBenefitDTO planBenefitDTO = planBenefitMapper.toDto(planBenefit);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(planBenefitDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the PlanBenefit in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamPlanBenefit() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        planBenefit.setId(longCount.incrementAndGet());

        // Create the PlanBenefit
        PlanBenefitDTO planBenefitDTO = planBenefitMapper.toDto(planBenefit);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(planBenefitDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the PlanBenefit in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdatePlanBenefitWithPatch() throws Exception {
        // Initialize the database
        insertedPlanBenefit = planBenefitRepository.save(planBenefit).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the planBenefit using partial update
        PlanBenefit partialUpdatedPlanBenefit = new PlanBenefit();
        partialUpdatedPlanBenefit.setId(planBenefit.getId());

        partialUpdatedPlanBenefit.benefitCode(UPDATED_BENEFIT_CODE).termsReference(UPDATED_TERMS_REFERENCE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedPlanBenefit.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedPlanBenefit))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the PlanBenefit in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPlanBenefitUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedPlanBenefit, planBenefit),
            getPersistedPlanBenefit(planBenefit)
        );
    }

    @Test
    void fullUpdatePlanBenefitWithPatch() throws Exception {
        // Initialize the database
        insertedPlanBenefit = planBenefitRepository.save(planBenefit).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the planBenefit using partial update
        PlanBenefit partialUpdatedPlanBenefit = new PlanBenefit();
        partialUpdatedPlanBenefit.setId(planBenefit.getId());

        partialUpdatedPlanBenefit
            .benefitCode(UPDATED_BENEFIT_CODE)
            .description(UPDATED_DESCRIPTION)
            .includedQuantity(UPDATED_INCLUDED_QUANTITY)
            .unitCode(UPDATED_UNIT_CODE)
            .intervalMonths(UPDATED_INTERVAL_MONTHS)
            .termsReference(UPDATED_TERMS_REFERENCE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedPlanBenefit.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedPlanBenefit))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the PlanBenefit in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPlanBenefitUpdatableFieldsEquals(partialUpdatedPlanBenefit, getPersistedPlanBenefit(partialUpdatedPlanBenefit));
    }

    @Test
    void patchNonExistingPlanBenefit() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        planBenefit.setId(longCount.incrementAndGet());

        // Create the PlanBenefit
        PlanBenefitDTO planBenefitDTO = planBenefitMapper.toDto(planBenefit);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, planBenefitDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(planBenefitDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the PlanBenefit in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchPlanBenefit() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        planBenefit.setId(longCount.incrementAndGet());

        // Create the PlanBenefit
        PlanBenefitDTO planBenefitDTO = planBenefitMapper.toDto(planBenefit);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(planBenefitDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the PlanBenefit in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamPlanBenefit() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        planBenefit.setId(longCount.incrementAndGet());

        // Create the PlanBenefit
        PlanBenefitDTO planBenefitDTO = planBenefitMapper.toDto(planBenefit);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(planBenefitDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the PlanBenefit in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void deletePlanBenefit() {
        // Initialize the database
        insertedPlanBenefit = planBenefitRepository.save(planBenefit).block();

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the planBenefit
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, planBenefit.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return planBenefitRepository.count().block();
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

    protected PlanBenefit getPersistedPlanBenefit(PlanBenefit planBenefit) {
        return planBenefitRepository.findById(planBenefit.getId()).block();
    }

    protected void assertPersistedPlanBenefitToMatchAllProperties(PlanBenefit expectedPlanBenefit) {
        // Test fails because reactive api returns an empty object instead of null
        // assertPlanBenefitAllPropertiesEquals(expectedPlanBenefit, getPersistedPlanBenefit(expectedPlanBenefit));
        assertPlanBenefitUpdatableFieldsEquals(expectedPlanBenefit, getPersistedPlanBenefit(expectedPlanBenefit));
    }

    protected void assertPersistedPlanBenefitToMatchUpdatableProperties(PlanBenefit expectedPlanBenefit) {
        // Test fails because reactive api returns an empty object instead of null
        // assertPlanBenefitAllUpdatablePropertiesEquals(expectedPlanBenefit, getPersistedPlanBenefit(expectedPlanBenefit));
        assertPlanBenefitUpdatableFieldsEquals(expectedPlanBenefit, getPersistedPlanBenefit(expectedPlanBenefit));
    }
}

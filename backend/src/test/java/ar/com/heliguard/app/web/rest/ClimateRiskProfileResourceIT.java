package ar.com.heliguard.app.web.rest;

import static ar.com.heliguard.app.domain.ClimateRiskProfileAsserts.*;
import static ar.com.heliguard.app.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import ar.com.heliguard.app.IntegrationTest;
import ar.com.heliguard.app.domain.ClimateRiskProfile;
import ar.com.heliguard.app.domain.enumeration.ClimateZone;
import ar.com.heliguard.app.repository.ClimateRiskProfileRepository;
import ar.com.heliguard.app.service.dto.ClimateRiskProfileDTO;
import ar.com.heliguard.app.service.mapper.ClimateRiskProfileMapper;
import java.time.Duration;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;
import tools.jackson.databind.ObjectMapper;

/**
 * Integration tests for the {@link ClimateRiskProfileResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class ClimateRiskProfileResourceIT {

    private static final String DEFAULT_CODE = "AAAAAAAAAA";
    private static final String UPDATED_CODE = "BBBBBBBBBB";

    private static final ClimateZone DEFAULT_REGION = ClimateZone.PUNA;
    private static final ClimateZone UPDATED_REGION = ClimateZone.QUEBRADA;

    private static final String DEFAULT_PROFILE_VERSION = "AAAAAAAAAA";
    private static final String UPDATED_PROFILE_VERSION = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final Boolean DEFAULT_ACTIVE = false;
    private static final Boolean UPDATED_ACTIVE = true;

    private static final String ENTITY_API_URL = "/api/climate-risk-profiles";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ClimateRiskProfileRepository climateRiskProfileRepository;

    @Autowired
    private ClimateRiskProfileMapper climateRiskProfileMapper;

    @Autowired
    private R2dbcEntityTemplate template;

    @Autowired
    private WebTestClient webTestClient;

    private ClimateRiskProfile climateRiskProfile;

    private ClimateRiskProfile insertedClimateRiskProfile;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ClimateRiskProfile createEntity() {
        return new ClimateRiskProfile()
            .code(DEFAULT_CODE)
            .region(DEFAULT_REGION)
            .profileVersion(DEFAULT_PROFILE_VERSION)
            .description(DEFAULT_DESCRIPTION)
            .active(DEFAULT_ACTIVE);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ClimateRiskProfile createUpdatedEntity() {
        return new ClimateRiskProfile()
            .code(UPDATED_CODE)
            .region(UPDATED_REGION)
            .profileVersion(UPDATED_PROFILE_VERSION)
            .description(UPDATED_DESCRIPTION)
            .active(UPDATED_ACTIVE);
    }

    public static void deleteEntities(R2dbcEntityTemplate template) {
        try {
            template.delete(ClimateRiskProfile.class).all().block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
    }

    @BeforeEach
    void initTest() {
        climateRiskProfile = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedClimateRiskProfile != null) {
            climateRiskProfileRepository.delete(insertedClimateRiskProfile).block();
            insertedClimateRiskProfile = null;
        }
        deleteEntities(template);
    }

    @Test
    void createClimateRiskProfile() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the ClimateRiskProfile
        ClimateRiskProfileDTO climateRiskProfileDTO = climateRiskProfileMapper.toDto(climateRiskProfile);
        var returnedClimateRiskProfileDTO = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(climateRiskProfileDTO))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(ClimateRiskProfileDTO.class)
            .returnResult()
            .getResponseBody();

        // Validate the ClimateRiskProfile in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedClimateRiskProfile = climateRiskProfileMapper.toEntity(returnedClimateRiskProfileDTO);
        assertClimateRiskProfileUpdatableFieldsEquals(
            returnedClimateRiskProfile,
            getPersistedClimateRiskProfile(returnedClimateRiskProfile)
        );

        insertedClimateRiskProfile = returnedClimateRiskProfile;
    }

    @Test
    void createClimateRiskProfileWithExistingId() throws Exception {
        // Create the ClimateRiskProfile with an existing ID
        climateRiskProfile.setId(1L);
        ClimateRiskProfileDTO climateRiskProfileDTO = climateRiskProfileMapper.toDto(climateRiskProfile);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(climateRiskProfileDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the ClimateRiskProfile in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    void checkCodeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        climateRiskProfile.setCode(null);

        // Create the ClimateRiskProfile, which fails.
        ClimateRiskProfileDTO climateRiskProfileDTO = climateRiskProfileMapper.toDto(climateRiskProfile);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(climateRiskProfileDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkRegionIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        climateRiskProfile.setRegion(null);

        // Create the ClimateRiskProfile, which fails.
        ClimateRiskProfileDTO climateRiskProfileDTO = climateRiskProfileMapper.toDto(climateRiskProfile);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(climateRiskProfileDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkProfileVersionIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        climateRiskProfile.setProfileVersion(null);

        // Create the ClimateRiskProfile, which fails.
        ClimateRiskProfileDTO climateRiskProfileDTO = climateRiskProfileMapper.toDto(climateRiskProfile);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(climateRiskProfileDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkActiveIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        climateRiskProfile.setActive(null);

        // Create the ClimateRiskProfile, which fails.
        ClimateRiskProfileDTO climateRiskProfileDTO = climateRiskProfileMapper.toDto(climateRiskProfile);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(climateRiskProfileDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void getAllClimateRiskProfilesAsStream() {
        // Initialize the database
        climateRiskProfileRepository.save(climateRiskProfile).block();

        List<ClimateRiskProfile> climateRiskProfileList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(ClimateRiskProfileDTO.class)
            .getResponseBody()
            .map(climateRiskProfileMapper::toEntity)
            .filter(climateRiskProfile::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(climateRiskProfileList).isNotNull();
        assertThat(climateRiskProfileList).hasSize(1);
        ClimateRiskProfile testClimateRiskProfile = climateRiskProfileList.getFirst();

        // Test fails because reactive api returns an empty object instead of null
        // assertClimateRiskProfileAllPropertiesEquals(climateRiskProfile, testClimateRiskProfile);
        assertClimateRiskProfileUpdatableFieldsEquals(climateRiskProfile, testClimateRiskProfile);
    }

    @Test
    void getAllClimateRiskProfiles() {
        // Initialize the database
        insertedClimateRiskProfile = climateRiskProfileRepository.save(climateRiskProfile).block();

        // Get all the climateRiskProfileList
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
            .value(hasItem(climateRiskProfile.getId().intValue()))
            .jsonPath("$.[*].code")
            .value(hasItem(DEFAULT_CODE))
            .jsonPath("$.[*].region")
            .value(hasItem(DEFAULT_REGION.toString()))
            .jsonPath("$.[*].profileVersion")
            .value(hasItem(DEFAULT_PROFILE_VERSION))
            .jsonPath("$.[*].description")
            .value(hasItem(DEFAULT_DESCRIPTION))
            .jsonPath("$.[*].active")
            .value(hasItem(DEFAULT_ACTIVE));
    }

    @Test
    void getClimateRiskProfile() {
        // Initialize the database
        insertedClimateRiskProfile = climateRiskProfileRepository.save(climateRiskProfile).block();

        // Get the climateRiskProfile
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, climateRiskProfile.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(climateRiskProfile.getId().intValue()))
            .jsonPath("$.code")
            .value(is(DEFAULT_CODE))
            .jsonPath("$.region")
            .value(is(DEFAULT_REGION.toString()))
            .jsonPath("$.profileVersion")
            .value(is(DEFAULT_PROFILE_VERSION))
            .jsonPath("$.description")
            .value(is(DEFAULT_DESCRIPTION))
            .jsonPath("$.active")
            .value(is(DEFAULT_ACTIVE));
    }

    @Test
    void getNonExistingClimateRiskProfile() {
        // Get the climateRiskProfile
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingClimateRiskProfile() throws Exception {
        // Initialize the database
        insertedClimateRiskProfile = climateRiskProfileRepository.save(climateRiskProfile).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the climateRiskProfile
        ClimateRiskProfile updatedClimateRiskProfile = climateRiskProfileRepository.findById(climateRiskProfile.getId()).block();
        updatedClimateRiskProfile
            .code(UPDATED_CODE)
            .region(UPDATED_REGION)
            .profileVersion(UPDATED_PROFILE_VERSION)
            .description(UPDATED_DESCRIPTION)
            .active(UPDATED_ACTIVE);
        ClimateRiskProfileDTO climateRiskProfileDTO = climateRiskProfileMapper.toDto(updatedClimateRiskProfile);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, climateRiskProfileDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(climateRiskProfileDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the ClimateRiskProfile in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedClimateRiskProfileToMatchAllProperties(updatedClimateRiskProfile);
    }

    @Test
    void putNonExistingClimateRiskProfile() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        climateRiskProfile.setId(longCount.incrementAndGet());

        // Create the ClimateRiskProfile
        ClimateRiskProfileDTO climateRiskProfileDTO = climateRiskProfileMapper.toDto(climateRiskProfile);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, climateRiskProfileDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(climateRiskProfileDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the ClimateRiskProfile in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchClimateRiskProfile() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        climateRiskProfile.setId(longCount.incrementAndGet());

        // Create the ClimateRiskProfile
        ClimateRiskProfileDTO climateRiskProfileDTO = climateRiskProfileMapper.toDto(climateRiskProfile);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(climateRiskProfileDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the ClimateRiskProfile in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamClimateRiskProfile() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        climateRiskProfile.setId(longCount.incrementAndGet());

        // Create the ClimateRiskProfile
        ClimateRiskProfileDTO climateRiskProfileDTO = climateRiskProfileMapper.toDto(climateRiskProfile);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(climateRiskProfileDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the ClimateRiskProfile in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateClimateRiskProfileWithPatch() throws Exception {
        // Initialize the database
        insertedClimateRiskProfile = climateRiskProfileRepository.save(climateRiskProfile).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the climateRiskProfile using partial update
        ClimateRiskProfile partialUpdatedClimateRiskProfile = new ClimateRiskProfile();
        partialUpdatedClimateRiskProfile.setId(climateRiskProfile.getId());

        partialUpdatedClimateRiskProfile.code(UPDATED_CODE).region(UPDATED_REGION).profileVersion(UPDATED_PROFILE_VERSION);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedClimateRiskProfile.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedClimateRiskProfile))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the ClimateRiskProfile in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertClimateRiskProfileUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedClimateRiskProfile, climateRiskProfile),
            getPersistedClimateRiskProfile(climateRiskProfile)
        );
    }

    @Test
    void fullUpdateClimateRiskProfileWithPatch() throws Exception {
        // Initialize the database
        insertedClimateRiskProfile = climateRiskProfileRepository.save(climateRiskProfile).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the climateRiskProfile using partial update
        ClimateRiskProfile partialUpdatedClimateRiskProfile = new ClimateRiskProfile();
        partialUpdatedClimateRiskProfile.setId(climateRiskProfile.getId());

        partialUpdatedClimateRiskProfile
            .code(UPDATED_CODE)
            .region(UPDATED_REGION)
            .profileVersion(UPDATED_PROFILE_VERSION)
            .description(UPDATED_DESCRIPTION)
            .active(UPDATED_ACTIVE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedClimateRiskProfile.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedClimateRiskProfile))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the ClimateRiskProfile in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertClimateRiskProfileUpdatableFieldsEquals(
            partialUpdatedClimateRiskProfile,
            getPersistedClimateRiskProfile(partialUpdatedClimateRiskProfile)
        );
    }

    @Test
    void patchNonExistingClimateRiskProfile() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        climateRiskProfile.setId(longCount.incrementAndGet());

        // Create the ClimateRiskProfile
        ClimateRiskProfileDTO climateRiskProfileDTO = climateRiskProfileMapper.toDto(climateRiskProfile);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, climateRiskProfileDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(climateRiskProfileDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the ClimateRiskProfile in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchClimateRiskProfile() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        climateRiskProfile.setId(longCount.incrementAndGet());

        // Create the ClimateRiskProfile
        ClimateRiskProfileDTO climateRiskProfileDTO = climateRiskProfileMapper.toDto(climateRiskProfile);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(climateRiskProfileDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the ClimateRiskProfile in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamClimateRiskProfile() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        climateRiskProfile.setId(longCount.incrementAndGet());

        // Create the ClimateRiskProfile
        ClimateRiskProfileDTO climateRiskProfileDTO = climateRiskProfileMapper.toDto(climateRiskProfile);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(climateRiskProfileDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the ClimateRiskProfile in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteClimateRiskProfile() {
        // Initialize the database
        insertedClimateRiskProfile = climateRiskProfileRepository.save(climateRiskProfile).block();

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the climateRiskProfile
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, climateRiskProfile.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return climateRiskProfileRepository.count().block();
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

    protected ClimateRiskProfile getPersistedClimateRiskProfile(ClimateRiskProfile climateRiskProfile) {
        return climateRiskProfileRepository.findById(climateRiskProfile.getId()).block();
    }

    protected void assertPersistedClimateRiskProfileToMatchAllProperties(ClimateRiskProfile expectedClimateRiskProfile) {
        // Test fails because reactive api returns an empty object instead of null
        // assertClimateRiskProfileAllPropertiesEquals(expectedClimateRiskProfile, getPersistedClimateRiskProfile(expectedClimateRiskProfile));
        assertClimateRiskProfileUpdatableFieldsEquals(
            expectedClimateRiskProfile,
            getPersistedClimateRiskProfile(expectedClimateRiskProfile)
        );
    }

    protected void assertPersistedClimateRiskProfileToMatchUpdatableProperties(ClimateRiskProfile expectedClimateRiskProfile) {
        // Test fails because reactive api returns an empty object instead of null
        // assertClimateRiskProfileAllUpdatablePropertiesEquals(expectedClimateRiskProfile, getPersistedClimateRiskProfile(expectedClimateRiskProfile));
        assertClimateRiskProfileUpdatableFieldsEquals(
            expectedClimateRiskProfile,
            getPersistedClimateRiskProfile(expectedClimateRiskProfile)
        );
    }
}

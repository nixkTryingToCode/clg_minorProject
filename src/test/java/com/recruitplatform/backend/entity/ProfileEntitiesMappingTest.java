package com.recruitplatform.backend.entity;

import com.recruitplatform.backend.repository.CandidateProfileRepository;
import com.recruitplatform.backend.repository.CompanyRepository;
import com.recruitplatform.backend.repository.RecruiterProfileRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@TestPropertySource(properties = {
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.flyway.enabled=false",
        "spring.jpa.properties.hibernate.jdbc.time_zone=UTC",
        "spring.datasource.url=jdbc:h2:mem:entities;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE;DEFAULT_NULL_ORDERING=HIGH"
})
class ProfileEntitiesMappingTest {

    @Autowired
    private CandidateProfileRepository candidateProfileRepository;
    @Autowired
    private RecruiterProfileRepository recruiterProfileRepository;
    @Autowired
    private CompanyRepository companyRepository;

    @Test
    void candidateProfilePersistsArrayAndJsonColumns() {
        CandidateProfile profile = new CandidateProfile();
        profile.setUserId(UUID.randomUUID());
        profile.setLocation("Dhaka");
        profile.setPreferredRoles(List.of("Java Developer", "Backend Engineer"));
        profile.setExperienceLevel("JUNIOR");
        profile.setPreferences("{\"remote\":true}");

        CandidateProfile saved = candidateProfileRepository.save(profile);

        assertThat(saved.getPreferredRoles()).containsExactly("Java Developer", "Backend Engineer");
        assertThat(saved.getPreferences()).isEqualTo("{\"remote\":true}");
        assertThat(saved.getCreatedAt()).isNotNull();
    }

    @Test
    void recruiterProfileLinksCompany() {
        Company company = Company.builder().name("Acme Inc").location("Dhaka").build();
        company = companyRepository.save(company);

        RecruiterProfile profile = new RecruiterProfile();
        profile.setUserId(UUID.randomUUID());
        profile.setCompanyId(company.getId());
        profile.setDesignation("Talent Lead");
        profile.setContactInfo("{\"phone\":\"123\"}");

        RecruiterProfile saved = recruiterProfileRepository.save(profile);

        assertThat(saved.getCompanyId()).isEqualTo(company.getId());
        assertThat(saved.getDesignation()).isEqualTo("Talent Lead");
        assertThat(saved.getContactInfo()).isEqualTo("{\"phone\":\"123\"}");
    }
}
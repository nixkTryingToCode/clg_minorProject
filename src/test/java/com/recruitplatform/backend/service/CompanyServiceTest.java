package com.recruitplatform.backend.service;

import com.recruitplatform.backend.dto.request.CompanyRequest;
import com.recruitplatform.backend.dto.response.CompanyResponse;
import com.recruitplatform.backend.entity.Company;
import com.recruitplatform.backend.exception.ApiException;
import com.recruitplatform.backend.repository.CompanyRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CompanyServiceTest {

    @Mock
    private CompanyRepository companyRepository;

    @InjectMocks
    private CompanyService companyService;

    @Test
    void createCompanyPersistsRequestFields() {
        CompanyRequest request = new CompanyRequest();
        request.setName("Acme Inc");
        request.setDescription("Engineering");
        request.setWebsite("https://acme.com");
        request.setLocation("Dhaka");

        Company saved = Company.builder().id(UUID.randomUUID()).name("Acme Inc").build();
        when(companyRepository.save(any(Company.class))).thenReturn(saved);

        CompanyResponse response = companyService.createCompany(request);

        verify(companyRepository).save(any(Company.class));
        assertThat(response.getId()).isEqualTo(saved.getId());
        assertThat(response.getName()).isEqualTo("Acme Inc");
    }

    @Test
    void listCompaniesReturnsAll() {
        Company a = Company.builder().id(UUID.randomUUID()).name("A").build();
        Company b = Company.builder().id(UUID.randomUUID()).name("B").build();
        when(companyRepository.findAll()).thenReturn(List.of(a, b));

        List<CompanyResponse> companies = companyService.listCompanies();

        assertThat(companies).hasSize(2);
    }

    @Test
    void getCompanyThrowsNotFoundWhenMissing() {
        UUID id = UUID.randomUUID();
        when(companyRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> companyService.getCompany(id))
                .isInstanceOf(ApiException.class)
                .hasMessage("Company not found");
    }

    @Test
    void updateCompanyModifiesExisting() {
        UUID id = UUID.randomUUID();
        Company existing = Company.builder().id(id).name("Old").build();
        when(companyRepository.findById(id)).thenReturn(Optional.of(existing));
        when(companyRepository.save(any(Company.class))).thenAnswer(inv -> inv.getArgument(0));

        CompanyRequest request = new CompanyRequest();
        request.setName("New Name");

        CompanyResponse response = companyService.updateCompany(id, request);

        assertThat(response.getName()).isEqualTo("New Name");
    }
}
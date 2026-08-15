package com.recruitplatform.backend.service;

import com.recruitplatform.backend.dto.request.CompanyRequest;
import com.recruitplatform.backend.dto.response.CompanyResponse;
import com.recruitplatform.backend.entity.Company;
import com.recruitplatform.backend.exception.ApiException;
import com.recruitplatform.backend.repository.CompanyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CompanyService {

    private final CompanyRepository companyRepository;

    public CompanyResponse createCompany(CompanyRequest request) {
        Company company = Company.builder()
                .name(request.getName())
                .description(request.getDescription())
                .website(request.getWebsite())
                .location(request.getLocation())
                .build();
        return toResponse(companyRepository.save(company));
    }

    public List<CompanyResponse> listCompanies() {
        return companyRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    public CompanyResponse getCompany(UUID id) {
        return toResponse(findCompany(id));
    }

    public CompanyResponse updateCompany(UUID id, CompanyRequest request) {
        Company company = findCompany(id);
        company.setName(request.getName());
        company.setDescription(request.getDescription());
        company.setWebsite(request.getWebsite());
        company.setLocation(request.getLocation());
        return toResponse(companyRepository.save(company));
    }

    private Company findCompany(UUID id) {
        return companyRepository.findById(id)
                .orElseThrow(() -> new ApiException("Company not found", HttpStatus.NOT_FOUND));
    }

    private CompanyResponse toResponse(Company company) {
        return CompanyResponse.builder()
                .id(company.getId())
                .name(company.getName())
                .description(company.getDescription())
                .website(company.getWebsite())
                .location(company.getLocation())
                .createdAt(company.getCreatedAt())
                .build();
    }
}
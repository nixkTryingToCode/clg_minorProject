package com.recruitplatform.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CompanyRequest {

    @NotBlank
    @Size(max = 255)
    private String name;

    private String description;

    @Size(max = 255)
    private String website;

    @Size(max = 255)
    private String location;
}
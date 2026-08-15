package com.recruitplatform.backend.dto.request;

import com.recruitplatform.backend.enums.JobSource;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class JobRequest {

    @NotBlank
    @Size(max = 255)
    private String title;

    @Size(max = 255)
    private String companyName;

    private String description;

    private List<@Size(max = 255) String> requiredSkills;

    @Size(max = 255)
    private String location;

    @Size(max = 50)
    private String employmentType;

    private JobSource source;
}
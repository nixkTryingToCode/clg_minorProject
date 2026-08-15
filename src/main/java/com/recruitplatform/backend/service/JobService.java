package com.recruitplatform.backend.service;

import com.recruitplatform.backend.dto.request.JobRequest;
import com.recruitplatform.backend.dto.response.JobResponse;
import com.recruitplatform.backend.entity.Job;
import com.recruitplatform.backend.enums.JobSource;
import com.recruitplatform.backend.enums.JobStatus;
import com.recruitplatform.backend.exception.ApiException;
import com.recruitplatform.backend.repository.JobRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class JobService {

    private final JobRepository jobRepository;

    @Transactional
    public JobResponse createJob(UUID recruiterId, JobRequest request) {
        Job job = Job.builder()
                .recruiterId(recruiterId)
                .source(request.getSource() != null ? request.getSource() : JobSource.RECRUITER)
                .title(request.getTitle())
                .companyName(request.getCompanyName())
                .description(request.getDescription())
                .requiredSkills(request.getRequiredSkills())
                .location(request.getLocation())
                .employmentType(request.getEmploymentType())
                .status(JobStatus.DRAFT)
                .build();
        return toResponse(jobRepository.save(job));
    }

    @Transactional
    public JobResponse updateJob(UUID recruiterId, UUID id, JobRequest request) {
        Job job = findOwnedJob(recruiterId, id);
        job.setTitle(request.getTitle());
        job.setCompanyName(request.getCompanyName());
        job.setDescription(request.getDescription());
        job.setRequiredSkills(request.getRequiredSkills());
        job.setLocation(request.getLocation());
        job.setEmploymentType(request.getEmploymentType());
        if (request.getSource() != null) {
            job.setSource(request.getSource());
        }
        return toResponse(jobRepository.save(job));
    }

    @Transactional
    public JobResponse changeStatus(UUID recruiterId, UUID id, JobStatus status) {
        Job job = findOwnedJob(recruiterId, id);
        job.setStatus(status);
        return toResponse(jobRepository.save(job));
    }

    @Transactional
    public void deleteJob(UUID recruiterId, UUID id) {
        jobRepository.delete(findOwnedJob(recruiterId, id));
    }

    public Page<JobResponse> listPublished(String location, String keyword, Pageable pageable) {
        return jobRepository.search(JobStatus.PUBLISHED, location, keyword, pageable)
                .map(this::toResponse);
    }

    public JobResponse getJob(UUID id) {
        return toResponse(findJob(id));
    }

    public List<JobResponse> listRecruiterJobs(UUID recruiterId) {
        return jobRepository.findByRecruiterId(recruiterId).stream()
                .map(this::toResponse)
                .toList();
    }

    private Job findOwnedJob(UUID recruiterId, UUID id) {
        Job job = findJob(id);
        if (!job.getRecruiterId().equals(recruiterId)) {
            throw new ApiException("Job not found", HttpStatus.NOT_FOUND);
        }
        return job;
    }

    private Job findJob(UUID id) {
        return jobRepository.findById(id)
                .orElseThrow(() -> new ApiException("Job not found", HttpStatus.NOT_FOUND));
    }

    private JobResponse toResponse(Job job) {
        return JobResponse.builder()
                .id(job.getId())
                .recruiterId(job.getRecruiterId())
                .source(job.getSource())
                .sourceJobId(job.getSourceJobId())
                .title(job.getTitle())
                .companyName(job.getCompanyName())
                .description(job.getDescription())
                .requiredSkills(job.getRequiredSkills())
                .location(job.getLocation())
                .employmentType(job.getEmploymentType())
                .status(job.getStatus())
                .createdAt(job.getCreatedAt())
                .updatedAt(job.getUpdatedAt())
                .build();
    }
}
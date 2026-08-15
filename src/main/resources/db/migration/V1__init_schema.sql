-- V1: Core schema for AI-Powered Recruitment Platform
-- Owns: users, profiles, resumes, skills, jobs, matches, applications, audit

CREATE EXTENSION IF NOT EXISTS vector;
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- ========== Users & Auth ==========
CREATE TABLE users (
    id              UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    email           VARCHAR(255) NOT NULL UNIQUE,
    password_hash   VARCHAR(255) NOT NULL,
    name            VARCHAR(255) NOT NULL,
    role            VARCHAR(20)  NOT NULL CHECK (role IN ('CANDIDATE', 'RECRUITER', 'ADMIN')),
    is_active       BOOLEAN NOT NULL DEFAULT TRUE,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- ========== Candidate / Recruiter / Company ==========
CREATE TABLE candidate_profiles (
    user_id             UUID PRIMARY KEY REFERENCES users(id) ON DELETE CASCADE,
    location            VARCHAR(255),
    preferred_roles     TEXT[],
    experience_level    VARCHAR(50),
    preferences         JSONB,
    created_at          TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at          TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE companies (
    id              UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name            VARCHAR(255) NOT NULL,
    description     TEXT,
    website         VARCHAR(255),
    location        VARCHAR(255),
    created_at      TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE recruiter_profiles (
    user_id         UUID PRIMARY KEY REFERENCES users(id) ON DELETE CASCADE,
    company_id      UUID REFERENCES companies(id),
    designation     VARCHAR(255),
    contact_info    JSONB,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- ========== Resume & Skills ==========
CREATE TABLE resumes (
    id              UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id         UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    file_url        VARCHAR(500) NOT NULL,
    parsed_text     TEXT,
    embedding       vector(384),          -- dimension must match Python's sentence-transformer model
    version         INT NOT NULL DEFAULT 1,
    status          VARCHAR(30) NOT NULL DEFAULT 'UPLOADED', -- UPLOADED, PROCESSING, PARSED, FAILED
    uploaded_at     TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE skills (
    id          UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name        VARCHAR(255) NOT NULL UNIQUE,
    category    VARCHAR(100),
    aliases     TEXT[]
);

CREATE TABLE candidate_skills (
    candidate_id    UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    skill_id        UUID NOT NULL REFERENCES skills(id) ON DELETE CASCADE,
    proficiency     VARCHAR(50),
    evidence        TEXT,
    PRIMARY KEY (candidate_id, skill_id)
);

-- ========== Jobs ==========
CREATE TABLE jobs (
    id              UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    recruiter_id    UUID REFERENCES users(id),
    source          VARCHAR(20) NOT NULL DEFAULT 'RECRUITER' CHECK (source IN ('RECRUITER', 'EXTERNAL', 'INTERNAL')),
    source_job_id   VARCHAR(255),
    title           VARCHAR(255) NOT NULL,
    company_name    VARCHAR(255),
    description     TEXT,
    required_skills TEXT[],
    location        VARCHAR(255),
    employment_type VARCHAR(50),
    status          VARCHAR(20) NOT NULL DEFAULT 'DRAFT' CHECK (status IN ('DRAFT', 'PUBLISHED', 'UNPUBLISHED', 'CLOSED')),
    embedding       vector(384),
    created_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_jobs_status ON jobs(status);
CREATE INDEX idx_jobs_recruiter ON jobs(recruiter_id);

-- ========== Matching ==========
CREATE TABLE job_matches (
    id              UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    candidate_id    UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    job_id          UUID NOT NULL REFERENCES jobs(id) ON DELETE CASCADE,
    score           NUMERIC(5,2) NOT NULL,
    matched_skills  TEXT[],
    missing_skills  TEXT[],
    explanation     TEXT,
    computed_at     TIMESTAMPTZ NOT NULL DEFAULT now(),
    UNIQUE (candidate_id, job_id)
);

CREATE TABLE saved_jobs (
    candidate_id    UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    job_id          UUID NOT NULL REFERENCES jobs(id) ON DELETE CASCADE,
    status          VARCHAR(30) NOT NULL DEFAULT 'SAVED',
    saved_at        TIMESTAMPTZ NOT NULL DEFAULT now(),
    PRIMARY KEY (candidate_id, job_id)
);

-- ========== Applications ==========
CREATE TABLE applications (
    id              UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    candidate_id    UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    job_id          UUID NOT NULL REFERENCES jobs(id) ON DELETE CASCADE,
    status          VARCHAR(30) NOT NULL DEFAULT 'APPLIED',
    -- APPLIED, SCREENING, SHORTLISTED, INTERVIEW, SELECTED, REJECTED
    source          VARCHAR(20) NOT NULL DEFAULT 'PLATFORM',
    external_id     VARCHAR(255),
    submitted_at    TIMESTAMPTZ NOT NULL DEFAULT now(),
    UNIQUE (candidate_id, job_id)
);

CREATE TABLE application_events (
    id              UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    application_id  UUID NOT NULL REFERENCES applications(id) ON DELETE CASCADE,
    event_type      VARCHAR(50) NOT NULL,
    details         JSONB,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE resume_versions (
    id              UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    candidate_id    UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    job_id          UUID REFERENCES jobs(id),
    content         TEXT,
    metadata        JSONB,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- ========== Admin / Audit ==========
CREATE TABLE audit_logs (
    id          UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    actor_id    UUID REFERENCES users(id),
    action      VARCHAR(100) NOT NULL,
    entity      VARCHAR(100) NOT NULL,
    entity_id   UUID,
    details     JSONB,
    created_at  TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- Helpful indexes
CREATE INDEX idx_applications_candidate ON applications(candidate_id);
CREATE INDEX idx_applications_job ON applications(job_id);
CREATE INDEX idx_job_matches_candidate ON job_matches(candidate_id);
CREATE INDEX idx_candidate_skills_candidate ON candidate_skills(candidate_id);

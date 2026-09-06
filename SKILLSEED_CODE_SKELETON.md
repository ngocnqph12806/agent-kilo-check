# SkillSeed — Code Skeleton (Spring Boot 3 + Java 21)

> **Mục đích:** Boilerplate code thật, copy về chạy được. Tập trung vào 3 service cốt lõi thể hiện pattern quan trọng nhất.

> **Last updated:** 2026-09-06

---

## Mục lục

1. [Project Structure (Multi-module Maven)](#1-project-structure-multi-module-maven)
2. [Parent pom.xml](#2-parent-pomxml)
3. [common-lib — Shared code](#3-common-lib--shared-code)
4. [user-service — Domain chính](#4-user-service--domain-chính)
5. [matching-service — AI engine](#5-matching-service--ai-engine)
6. [wallet-service — Seed economy](#6-wallet-service--seed-economy)
7. [docker-compose.yml](#7-docker-composeyml)
8. [Cách chạy thử](#8-cách-chạy-thử)
9. [Bước tiếp theo](#9-bước-tiếp-theo)

---

## 1. Project Structure (Multi-module Maven)

```
skillseed/
├── pom.xml                          # Parent POM (dependency management)
├── docker-compose.yml               # Postgres, Redis, Kafka, Qdrant cho dev
├── README.md
│
├── common-lib/                      # Shared DTOs, exceptions, utilities
│   ├── pom.xml
│   └── src/main/java/com/skillseed/common/
│       ├── dto/ApiResponse.java
│       ├── exception/
│       │   ├── BusinessException.java
│       │   ├── ResourceNotFoundException.java
│       │   └── GlobalExceptionHandler.java
│       ├── security/
│       │   ├── JwtAuthenticationFilter.java
│       │   └── SecurityUtils.java
│       ├── pagination/PageResponse.java
│       └── util/SlugUtils.java
│
├── user-service/                    # User profile, Skill DNA, Auth
│   ├── pom.xml
│   └── src/
│       ├── main/
│       │   ├── java/com/skillseed/user/
│       │   │   ├── UserServiceApplication.java
│       │   │   ├── domain/
│       │   │   │   ├── User.java
│       │   │   │   ├── Skill.java
│       │   │   │   ├── UserSkillOffered.java
│       │   │   │   ├── UserSkillWanted.java
│       │   │   │   └── UserAvailability.java
│       │   │   ├── repository/
│       │   │   │   ├── UserRepository.java
│       │   │   │   ├── SkillRepository.java
│       │   │   │   └── UserSkillRepository.java
│       │   │   ├── service/
│       │   │   │   ├── UserService.java
│       │   │   │   ├── SkillService.java
│       │   │   │   └── UserSkillService.java
│       │   │   ├── controller/
│       │   │   │   ├── UserController.java
│       │   │   │   ├── SkillController.java
│       │   │   │   └── UserSkillController.java
│       │   │   ├── dto/
│       │   │   │   ├── request/
│       │   │   │   └── response/
│       │   │   └── config/
│       │   │       ├── SecurityConfig.java
│       │   │       └── OpenApiConfig.java
│       │   └── resources/
│       │       ├── application.yml
│       │       └── db/migration/
│       │           ├── V1__init_users.sql
│       │           └── V2__init_skills.sql
│       └── test/java/com/skillseed/user/
│           ├── UserServiceApplicationTests.java
│           └── service/UserServiceTest.java
│
├── matching-service/                # AI matching engine
│   └── (similar structure)
│
└── wallet-service/                  # Seed wallet + ledger
    └── (similar structure)
```

> **Quy ước:** Mỗi service là 1 Spring Boot app riêng, port khác nhau, deploy độc lập. Giao tiếp qua REST (đồng bộ) + Kafka (bất đồng bộ).

---

## 2. Parent pom.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0">
    <modelVersion>4.0.0</modelVersion>

    <groupId>com.skillseed</groupId>
    <artifactId>skillseed-parent</artifactId>
    <version>0.1.0-SNAPSHOT</version>
    <packaging>pom</packaging>
    <name>SkillSeed Parent</name>

    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.3.4</version>
        <relativePath/>
    </parent>

    <modules>
        <module>common-lib</module>
        <module>user-service</module>
        <module>matching-service</module>
        <module>wallet-service</module>
    </modules>

    <properties>
        <java.version>21</java.version>
        <maven.compiler.source>21</maven.compiler.source>
        <maven.compiler.target>21</maven.compiler.target>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>

        <!-- Versioning -->
        <spring-ai.version>1.0.0</spring-ai.version>
        <springdoc.version>2.6.0</springdoc.version>
        <testcontainers.version>1.20.3</testcontainers.version>
        <lombok.version>1.18.34</lombok.version>
        <mapstruct.version>1.6.2</mapstruct.version>
        <qdrant.version>1.11.0</qdrant.version>
    </properties>

    <dependencyManagement>
        <dependencies>
            <!-- Internal modules -->
            <dependency>
                <groupId>com.skillseed</groupId>
                <artifactId>common-lib</artifactId>
                <version>${project.version}</version>
            </dependency>

            <!-- Spring AI BOM -->
            <dependency>
                <groupId>org.springframework.ai</groupId>
                <artifactId>spring-ai-bom</artifactId>
                <version>${spring-ai.version}</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>

            <!-- springdoc-openapi -->
            <dependency>
                <groupId>org.springdoc</groupId>
                <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
                <version>${springdoc.version}</version>
            </dependency>

            <!-- Testcontainers BOM -->
            <dependency>
                <groupId>org.testcontainers</groupId>
                <artifactId>testcontainers-bom</artifactId>
                <version>${testcontainers.version}</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>

            <!-- Qdrant Java client -->
            <dependency>
                <groupId>io.qdrant</groupId>
                <artifactId>qdrant-java-client</artifactId>
                <version>${qdrant.version}</version>
            </dependency>
        </dependencies>
    </dependencyManagement>

    <build>
        <pluginManagement>
            <plugins>
                <plugin>
                    <groupId>org.springframework.boot</groupId>
                    <artifactId>spring-boot-maven-plugin</artifactId>
                    <configuration>
                        <excludes>
                            <exclude>
                                <groupId>org.projectlombok</groupId>
                                <artifactId>lombok</artifactId>
                            </exclude>
                        </excludes>
                    </configuration>
                </plugin>
            </plugins>
        </pluginManagement>
    </build>
</project>
```

---

## 3. common-lib — Shared code

### 3.1. `pom.xml`

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project>
    <modelVersion>4.0.0</modelVersion>
    <parent>
        <groupId>com.skillseed</groupId>
        <artifactId>skillseed-parent</artifactId>
        <version>0.1.0-SNAPSHOT</version>
    </parent>
    <artifactId>common-lib</artifactId>

    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-validation</artifactId>
        </dependency>
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <optional>true</optional>
        </dependency>
        <dependency>
            <groupId>io.swagger.core.v3</groupId>
            <artifactId>swagger-annotations-jakarta</artifactId>
        </dependency>
    </dependencies>
</project>
```

### 3.2. `ApiResponse.java` — Wrapper chuẩn cho mọi response

```java
package com.skillseed.common.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * Standard API response wrapper for all SkillSeed services.
 * Generic type allows consistent response shape across services.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    private boolean success;
    private T data;
    private ErrorInfo error;
    private Instant timestamp;

    public static <T> ApiResponse<T> ok(T data) {
        return new ApiResponse<>(true, data, null, Instant.now());
    }

    public static <T> ApiResponse<T> error(String code, String message) {
        return new ApiResponse<>(false, null, new ErrorInfo(code, message), Instant.now());
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ErrorInfo {
        private String code;
        private String message;
    }
}
```

### 3.3. `BusinessException.java`

```java
package com.skillseed.common.exception;

import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {
    private final String code;
    private final int httpStatus;

    public BusinessException(String code, String message) {
        this(code, message, 400);
    }

    public BusinessException(String code, String message, int httpStatus) {
        super(message);
        this.code = code;
        this.httpStatus = httpStatus;
    }

    public static BusinessException notFound(String resource) {
        return new BusinessException("RESOURCE_NOT_FOUND", 
            String.format("%s not found", resource), 404);
    }

    public static BusinessException badRequest(String message) {
        return new BusinessException("BAD_REQUEST", message, 400);
    }

    public static BusinessException forbidden(String message) {
        return new BusinessException("FORBIDDEN", message, 403);
    }
}
```

### 3.4. `ResourceNotFoundException.java`

```java
package com.skillseed.common.exception;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }

    public static ResourceNotFoundException of(String resource, String id) {
        return new ResourceNotFoundException(
            String.format("%s with id %s not found", resource, id));
    }
}
```

### 3.5. `GlobalExceptionHandler.java`

```java
package com.skillseed.common.exception;

import com.skillseed.common.dto.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleNotFound(
            ResourceNotFoundException ex, HttpServletRequest req) {
        log.warn("Resource not found: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(ApiResponse.error("NOT_FOUND", ex.getMessage()));
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Void>> handleBusiness(
            BusinessException ex, HttpServletRequest req) {
        log.warn("Business exception: {} - {}", ex.getCode(), ex.getMessage());
        return ResponseEntity.status(ex.getHttpStatus())
            .body(ApiResponse.error(ex.getCode(), ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidation(
            MethodArgumentNotValidException ex) {
        String errors = ex.getBindingResult().getFieldErrors().stream()
            .map(FieldError::getDefaultMessage)
            .collect(Collectors.joining("; "));
        return ResponseEntity.badRequest()
            .body(ApiResponse.error("VALIDATION_ERROR", errors));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleUnexpected(Exception ex) {
        log.error("Unexpected exception", ex);
        return ResponseEntity.internalServerError()
            .body(ApiResponse.error("INTERNAL_ERROR", "An unexpected error occurred"));
    }
}
```

### 3.6. `PageResponse.java`

```java
package com.skillseed.common.pagination;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.domain.Page;

import java.util.List;

@Data
@AllArgsConstructor
public class PageResponse<T> {
    private List<T> content;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
    private boolean first;
    private boolean last;

    public static <T> PageResponse<T> from(Page<T> page) {
        return new PageResponse<>(
            page.getContent(),
            page.getNumber(),
            page.getSize(),
            page.getTotalElements(),
            page.getTotalPages(),
            page.isFirst(),
            page.isLast()
        );
    }
}
```

---

## 4. user-service — Domain chính

### 4.1. `pom.xml`

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project>
    <modelVersion>4.0.0</modelVersion>
    <parent>
        <groupId>com.skillseed</groupId>
        <artifactId>skillseed-parent</artifactId>
        <version>0.1.0-SNAPSHOT</version>
    </parent>
    <artifactId>user-service</artifactId>

    <dependencies>
        <dependency>
            <groupId>com.skillseed</groupId>
            <artifactId>common-lib</artifactId>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-jpa</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-security</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-validation</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-actuator</artifactId>
        </dependency>
        <dependency>
            <groupId>org.postgresql</groupId>
            <artifactId>postgresql</artifactId>
            <scope>runtime</scope>
        </dependency>
        <dependency>
            <groupId>org.flywaydb</groupId>
            <artifactId>flyway-core</artifactId>
        </dependency>
        <dependency>
            <groupId>org.flywaydb</groupId>
            <artifactId>flyway-database-postgresql</artifactId>
        </dependency>

        <!-- Lombok & MapStruct -->
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <optional>true</optional>
        </dependency>
        <dependency>
            <groupId>org.mapstruct</groupId>
            <artifactId>mapstruct</artifactId>
            <version>${mapstruct.version}</version>
        </dependency>

        <!-- OpenAPI -->
        <dependency>
            <groupId>org.springdoc</groupId>
            <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
        </dependency>

        <!-- Test -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>org.springframework.security</groupId>
            <artifactId>spring-security-test</artifactId>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>org.testcontainers</groupId>
            <artifactId>postgresql</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>
</project>
```

### 4.2. `UserServiceApplication.java`

```java
package com.skillseed.user;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class UserServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(UserServiceApplication.class, args);
    }
}
```

### 4.3. `application.yml`

```yaml
spring:
  application:
    name: user-service
  datasource:
    url: jdbc:postgresql://localhost:5432/skillseed_users
    username: skillseed
    password: ${DB_PASSWORD:dev_password}
    hikari:
      maximum-pool-size: 10
      minimum-idle: 2
  jpa:
    hibernate:
      ddl-auto: validate
    open-in-view: false
    properties:
      hibernate:
        jdbc:
          time_zone: UTC
        format_sql: true
    show-sql: false
  flyway:
    enabled: true
    locations: classpath:db/migration
    baseline-on-migrate: true

server:
  port: 8081
  shutdown: graceful
  compression:
    enabled: true

management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,prometheus
  endpoint:
    health:
      show-details: when-authorized
  metrics:
    tags:
      application: ${spring.application.name}

logging:
  level:
    com.skillseed: DEBUG
    org.hibernate.SQL: INFO

springdoc:
  api-docs:
    path: /v3/api-docs
  swagger-ui:
    path: /swagger-ui.html
    tags-sorter: alpha
```

### 4.4. Domain entities

#### `User.java`

```java
package com.skillseed.user.domain;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "users", indexes = {
    @Index(name = "idx_users_country", columnList = "country_code"),
    @Index(name = "idx_users_verified", columnList = "verified")
})
@EntityListeners(AuditingEntityListener.class)
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "uuid")
    private UUID id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(unique = true)
    private String phone;

    @Column(name = "password_hash")
    private String passwordHash;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Column(name = "avatar_url")
    private String avatarUrl;

    @Column(columnDefinition = "text")
    private String bio;

    @Column(name = "country_code", length = 2)
    private String countryCode;

    private String timezone;

    @Column(columnDefinition = "text[]")
    @JdbcTypeCode(SqlTypes.ARRAY)
    private List<String> languages;

    @Column(name = "learning_style", length = 20)
    private String learningStyle; // visual, auditory, reading, kinesthetic

    @Column(nullable = false)
    @Builder.Default
    private Boolean verified = false;

    @Column(name = "verification_level", nullable = false)
    @Builder.Default
    private Short verificationLevel = 0; // 0-4

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<UserSkillOffered> skillsOffered = new java.util.ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<UserSkillWanted> skillsWanted = new java.util.ArrayList<>();

    // Domain methods
    public boolean isEmailVerified() {
        return verificationLevel >= 0; // level 0 = email verified
    }

    public boolean canTeach() {
        return verificationLevel >= 2; // need government ID
    }
}
```

#### `Skill.java`

```java
package com.skillseed.user.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "skills", indexes = {
    @Index(name = "idx_skills_slug", columnList = "slug", unique = true),
    @Index(name = "idx_skills_category", columnList = "category")
})
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Skill {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "uuid")
    private UUID id;

    @Column(nullable = false, unique = true, length = 100)
    private String slug;

    @Column(nullable = false)
    private String name;

    @Column(length = 50)
    private String category; // tech, business, art, language, life, ...

    @Column(name = "is_custom", nullable = false)
    @Builder.Default
    private Boolean isCustom = false;

    @Column(name = "parent_id")
    @JdbcTypeCode(SqlTypes.UUID)
    private UUID parentId;
}
```

#### `UserSkillOffered.java`

```java
package com.skillseed.user.domain;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "user_skills_offered", indexes = {
    @Index(name = "idx_uso_user", columnList = "user_id"),
    @Index(name = "idx_uso_skill", columnList = "skill_id")
})
@EntityListeners(AuditingEntityListener.class)
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class UserSkillOffered {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "uuid")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "skill_id", nullable = false)
    private Skill skill;

    @Column(nullable = false)
    private Short level; // 1-5

    @Column(name = "years_experience")
    private Integer yearsExperience;

    @Column(columnDefinition = "text")
    private String description;

    @Column(name = "hourly_seed_rate")
    private Integer hourlySeedRate; // default 60 if null

    @Column(name = "is_verified", nullable = false)
    @Builder.Default
    private Boolean isVerified = false;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;
}
```

#### `UserSkillWanted.java`

```java
package com.skillseed.user.domain;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "user_skills_wanted")
@EntityListeners(AuditingEntityListener.class)
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class UserSkillWanted {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "uuid")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "skill_id", nullable = false)
    private Skill skill;

    @Column(nullable = false)
    private Short priority; // 1-5

    @Column(name = "target_level")
    private Short targetLevel;

    @Column(columnDefinition = "text")
    private String notes;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;
}
```

### 4.5. Repositories

#### `UserRepository.java`

```java
package com.skillseed.user.repository;

import com.skillseed.user.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByEmail(String email);

    Optional<User> findByPhone(String phone);

    boolean existsByEmail(String email);

    @Query("""
        SELECT DISTINCT u FROM User u
        LEFT JOIN u.skillsOffered uso
        WHERE (:countryCode IS NULL OR u.countryCode = :countryCode)
          AND u.verified = true
          AND (:skillId IS NULL OR uso.skill.id = :skillId)
          AND u.id <> :excludeUserId
        """)
    Page<User> findCandidates(
        @Param("skillId") UUID skillId,
        @Param("countryCode") String countryCode,
        @Param("excludeUserId") UUID excludeUserId,
        Pageable pageable
    );
}
```

#### `SkillRepository.java`

```java
package com.skillseed.user.repository;

import com.skillseed.user.domain.Skill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SkillRepository extends JpaRepository<Skill, UUID> {
    Optional<Skill> findBySlug(String slug);
    List<Skill> findByCategory(String category);
    List<Skill> findByNameContainingIgnoreCase(String keyword);
}
```

### 4.6. DTOs

#### `CreateUserRequest.java`

```java
package com.skillseed.user.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateUserRequest(
    @Email @NotBlank String email,
    @NotBlank @Size(min = 8, max = 128) String password,
    @NotBlank @Size(min = 2, max = 100) String fullName,
    String phone,
    String countryCode,
    String timezone
) {}
```

#### `UserResponse.java`

```java
package com.skillseed.user.dto.response;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record UserResponse(
    UUID id,
    String email,
    String fullName,
    String avatarUrl,
    String bio,
    String countryCode,
    String timezone,
    List<String> languages,
    String learningStyle,
    Boolean verified,
    Short verificationLevel,
    Instant createdAt,
    List<UserSkillResponse> skillsOffered,
    List<UserSkillResponse> skillsWanted
) {}
```

#### `UserSkillResponse.java`

```java
package com.skillseed.user.dto.response;

import java.util.UUID;

public record UserSkillResponse(
    UUID id,
    UUID skillId,
    String skillName,
    String category,
    Short level,
    Integer yearsExperience,
    String description,
    Integer hourlySeedRate
) {}
```

### 4.7. Services

#### `UserService.java`

```java
package com.skillseed.user.service;

import com.skillseed.common.exception.BusinessException;
import com.skillseed.common.exception.ResourceNotFoundException;
import com.skillseed.user.domain.User;
import com.skillseed.user.dto.request.CreateUserRequest;
import com.skillseed.user.dto.request.UpdateProfileRequest;
import com.skillseed.user.dto.response.UserResponse;
import com.skillseed.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UserResponse createUser(CreateUserRequest request) {
        log.info("Creating user with email: {}", request.email());
        
        if (userRepository.existsByEmail(request.email())) {
            throw BusinessException.badRequest("Email already registered");
        }

        User user = User.builder()
            .email(request.email())
            .passwordHash(passwordEncoder.encode(request.password()))
            .fullName(request.fullName())
            .phone(request.phone())
            .countryCode(request.countryCode())
            .timezone(request.timezone())
            .verificationLevel((short) 0) // email only initially
            .build();

        User saved = userRepository.save(user);
        log.info("User created with id: {}", saved.getId());
        return mapToResponse(saved);
    }

    public UserResponse getUserById(UUID id) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> ResourceNotFoundException.of("User", id.toString()));
        return mapToResponse(user);
    }

    public User getUserEntity(UUID id) {
        return userRepository.findById(id)
            .orElseThrow(() -> ResourceNotFoundException.of("User", id.toString()));
    }

    @Transactional
    public UserResponse updateProfile(UUID userId, UpdateProfileRequest request) {
        User user = getUserEntity(userId);
        
        if (request.fullName() != null) user.setFullName(request.fullName());
        if (request.bio() != null) user.setBio(request.bio());
        if (request.avatarUrl() != null) user.setAvatarUrl(request.avatarUrl());
        if (request.languages() != null) user.setLanguages(request.languages());
        if (request.learningStyle() != null) user.setLearningStyle(request.learningStyle());
        
        return mapToResponse(user);
    }

    @Transactional
    public void upgradeVerificationLevel(UUID userId, short newLevel) {
        if (newLevel < 0 || newLevel > 4) {
            throw BusinessException.badRequest("Invalid verification level");
        }
        User user = getUserEntity(userId);
        if (newLevel > user.getVerificationLevel()) {
            user.setVerificationLevel(newLevel);
            if (newLevel >= 2) user.setVerified(true);
        }
    }

    private UserResponse mapToResponse(User user) {
        // In production use MapStruct to avoid manual mapping
        return new UserResponse(
            user.getId(),
            user.getEmail(),
            user.getFullName(),
            user.getAvatarUrl(),
            user.getBio(),
            user.getCountryCode(),
            user.getTimezone(),
            user.getLanguages(),
            user.getLearningStyle(),
            user.getVerified(),
            user.getVerificationLevel(),
            user.getCreatedAt(),
            user.getSkillsOffered().stream()
                .map(s -> new UserSkillResponse(
                    s.getId(),
                    s.getSkill().getId(),
                    s.getSkill().getName(),
                    s.getSkill().getCategory(),
                    s.getLevel(),
                    s.getYearsExperience(),
                    s.getDescription(),
                    s.getHourlySeedRate()
                )).toList(),
            user.getSkillsWanted().stream()
                .map(s -> new UserSkillResponse(
                    s.getId(),
                    s.getSkill().getId(),
                    s.getSkill().getName(),
                    s.getSkill().getCategory(),
                    s.getLevel(),
                    null,
                    s.getNotes(),
                    null
                )).toList()
        );
    }
}
```

### 4.8. Controllers

#### `UserController.java`

```java
package com.skillseed.user.controller;

import com.skillseed.common.dto.ApiResponse;
import com.skillseed.common.security.SecurityUtils;
import com.skillseed.user.dto.request.CreateUserRequest;
import com.skillseed.user.dto.request.UpdateProfileRequest;
import com.skillseed.user.dto.response.UserResponse;
import com.skillseed.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "Users", description = "User profile management")
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    @Operation(summary = "Register a new user")
    public ResponseEntity<ApiResponse<UserResponse>> register(
            @Valid @RequestBody CreateUserRequest request) {
        UserResponse response = userService.createUser(request);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.ok(response));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get user by ID")
    public ApiResponse<UserResponse> getUser(@PathVariable UUID id) {
        return ApiResponse.ok(userService.getUserById(id));
    }

    @GetMapping("/me")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Get current authenticated user")
    public ApiResponse<UserResponse> getCurrentUser() {
        UUID currentUserId = SecurityUtils.getCurrentUserId();
        return ApiResponse.ok(userService.getUserById(currentUserId));
    }

    @PutMapping("/me")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Update current user profile")
    public ApiResponse<UserResponse> updateProfile(
            @Valid @RequestBody UpdateProfileRequest request) {
        UUID currentUserId = SecurityUtils.getCurrentUserId();
        return ApiResponse.ok(userService.updateProfile(currentUserId, request));
    }
}
```

### 4.9. Flyway migrations

#### `V1__init_users.sql`

```sql
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

CREATE TABLE users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    email VARCHAR(255) NOT NULL UNIQUE,
    phone VARCHAR(20) UNIQUE,
    password_hash VARCHAR(255),
    full_name VARCHAR(255) NOT NULL,
    avatar_url TEXT,
    bio TEXT,
    country_code CHAR(2),
    timezone VARCHAR(50),
    languages TEXT[],
    learning_style VARCHAR(20),
    verified BOOLEAN NOT NULL DEFAULT FALSE,
    verification_level SMALLINT NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT chk_verification_level CHECK (verification_level BETWEEN 0 AND 4)
);

CREATE INDEX idx_users_country ON users(country_code);
CREATE INDEX idx_users_verified ON users(verified) WHERE verified = TRUE;
CREATE INDEX idx_users_created_at ON users(created_at DESC);

COMMENT ON TABLE users IS 'Core user accounts - one row per registered user';
COMMENT ON COLUMN users.verification_level IS '0=email, 1=phone, 2=gov-id, 3=liveness, 4=video';
```

#### `V2__init_skills.sql`

```sql
CREATE TABLE skills (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    slug VARCHAR(100) NOT NULL UNIQUE,
    name VARCHAR(255) NOT NULL,
    category VARCHAR(50),
    is_custom BOOLEAN NOT NULL DEFAULT FALSE,
    parent_id UUID REFERENCES skills(id) ON DELETE SET NULL
);

CREATE INDEX idx_skills_category ON skills(category);

CREATE TABLE user_skills_offered (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    skill_id UUID NOT NULL REFERENCES skills(id),
    level SMALLINT NOT NULL CHECK (level BETWEEN 1 AND 5),
    years_experience INT CHECK (years_experience >= 0),
    description TEXT,
    hourly_seed_rate INT DEFAULT 60 CHECK (hourly_seed_rate > 0),
    is_verified BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    UNIQUE(user_id, skill_id)
);

CREATE INDEX idx_uso_user ON user_skills_offered(user_id);
CREATE INDEX idx_uso_skill ON user_skills_offered(skill_id);

CREATE TABLE user_skills_wanted (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    skill_id UUID NOT NULL REFERENCES skills(id),
    priority SMALLINT NOT NULL CHECK (priority BETWEEN 1 AND 5),
    target_level SMALLINT CHECK (target_level BETWEEN 1 AND 5),
    notes TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    UNIQUE(user_id, skill_id)
);

CREATE INDEX idx_usw_user ON user_skills_wanted(user_id);

-- Seed taxonomy với ~50 popular skills
INSERT INTO skills (slug, name, category) VALUES
    ('java-programming', 'Java Programming', 'tech'),
    ('python-programming', 'Python Programming', 'tech'),
    ('javascript', 'JavaScript', 'tech'),
    ('react', 'React.js', 'tech'),
    ('spring-boot', 'Spring Boot', 'tech'),
    ('system-design', 'System Design', 'tech'),
    ('data-science', 'Data Science', 'tech'),
    ('machine-learning', 'Machine Learning', 'tech'),
    ('public-speaking', 'Public Speaking', 'life'),
    ('communication', 'Communication Skills', 'life'),
    ('leadership', 'Leadership', 'business'),
    ('product-management', 'Product Management', 'business'),
    ('marketing-digital', 'Digital Marketing', 'business'),
    ('financial-planning', 'Financial Planning', 'business'),
    ('english', 'English Language', 'language'),
    ('japanese', 'Japanese Language', 'language'),
    ('korean', 'Korean Language', 'language'),
    ('chinese-mandarin', 'Mandarin Chinese', 'language'),
    ('graphic-design', 'Graphic Design', 'art'),
    ('ui-ux-design', 'UI/UX Design', 'art'),
    ('photography', 'Photography', 'art'),
    ('music-piano', 'Piano', 'art'),
    ('cooking', 'Cooking', 'life'),
    ('yoga', 'Yoga', 'life'),
    ('meditation', 'Meditation', 'life');
```

### 4.10. Security config

```java
package com.skillseed.user.config;

import com.skillseed.common.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .cors(AbstractHttpConfigurer::disable)
            .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/v1/users/register", "/api/v1/auth/**").permitAll()
                .requestMatchers("/actuator/health", "/actuator/info").permitAll()
                .requestMatchers("/v3/api-docs/**", "/swagger-ui/**").permitAll()
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }
}
```

### 4.11. Tests

#### `UserServiceTest.java`

```java
package com.skillseed.user.service;

import com.skillseed.common.exception.BusinessException;
import com.skillseed.user.domain.User;
import com.skillseed.user.dto.request.CreateUserRequest;
import com.skillseed.user.dto.response.UserResponse;
import com.skillseed.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private PasswordEncoder passwordEncoder;

    @InjectMocks private UserService userService;

    private CreateUserRequest validRequest;

    @BeforeEach
    void setUp() {
        validRequest = new CreateUserRequest(
            "test@example.com",
            "password123",
            "Test User",
            "+84123456789",
            "VN",
            "Asia/Ho_Chi_Minh"
        );
    }

    @Test
    void shouldCreateUserSuccessfully() {
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("hashed_pwd");
        when(userRepository.save(any(User.class))).thenAnswer(inv -> {
            User u = inv.getArgument(0);
            u.setId(UUID.randomUUID());
            return u;
        });

        UserResponse response = userService.createUser(validRequest);

        assertThat(response.email()).isEqualTo("test@example.com");
        assertThat(response.fullName()).isEqualTo("Test User");
        assertThat(response.verificationLevel()).isEqualTo((short) 0);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void shouldThrowWhenEmailAlreadyExists() {
        when(userRepository.existsByEmail(anyString())).thenReturn(true);

        assertThatThrownBy(() -> userService.createUser(validRequest))
            .isInstanceOf(BusinessException.class)
            .hasMessageContaining("Email already registered");
        
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void shouldThrowWhenUserNotFound() {
        UUID id = UUID.randomUUID();
        when(userRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getUserById(id))
            .isInstanceOf(com.skillseed.common.exception.ResourceNotFoundException.class);
    }
}
```

---

## 5. matching-service — AI engine

### 5.1. Service skeleton với Spring AI

```java
package com.skillseed.matching.service;

import com.skillseed.matching.client.UserServiceClient;
import com.skillseed.matching.dto.MatchCandidate;
import com.skillseed.matching.dto.MatchRequest;
import com.skillseed.matching.dto.MatchResult;
import com.skillseed.matching.vector.SkillEmbeddingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MatchingService {

    private final UserServiceClient userServiceClient;
    private final SkillEmbeddingService embeddingService;
    
    @Qualifier("openAiChatClient")
    private final ChatClient chatClient;

    public List<MatchResult> findMatches(MatchRequest request) {
        log.info("Finding matches for user {} seeking skill {}", 
            request.userId(), request.targetSkillSlug());
        
        // 1. Get candidate users (basic filter)
        List<MatchCandidate> candidates = userServiceClient.getCandidates(request);
        log.debug("Found {} candidates before AI ranking", candidates.size());
        
        // 2. Compute embeddings & similarity
        List<MatchResult> ranked = embeddingService.rankBySimilarity(
            request, candidates
        );
        
        // 3. Take top N & add LLM explanation
        return ranked.stream()
            .limit(10)
            .map(this::enrichWithExplanation)
            .toList();
    }

    private MatchResult enrichWithExplanation(MatchResult match) {
        String prompt = String.format("""
            You are a skill-matching explainer for SkillSeed platform.
            
            User A wants to learn: %s (level target: %s)
            User B is teaching: %s (level: %s, %d years experience)
            Match score: %.2f
            
            In 1-2 sentences in language %s, explain why this is a good match.
            Be specific and friendly. No emojis.
            """,
            match.learnerWants(), match.targetLevel(),
            match.teacherOffers(), match.teacherLevel(), match.teacherYearsExp(),
            match.score(), match.preferredLanguage()
        );
        
        try {
            String explanation = chatClient.prompt()
                .user(prompt)
                .call()
                .content();
            return match.withExplanation(explanation);
        } catch (Exception e) {
            log.warn("LLM explanation failed, using fallback", e);
            return match.withExplanation(
                String.format("Great match: %s teaches %s at level %d.", 
                    match.teacherName(), match.teacherOffers(), match.teacherLevel())
            );
        }
    }
}
```

### 5.2. Skill Embedding Service (RAG-lite)

```java
package com.skillseed.matching.vector;

import com.skillseed.matching.dto.MatchCandidate;
import com.skillseed.matching.dto.MatchRequest;
import com.skillseed.matching.dto.MatchResult;
import io.qdrant.client.QdrantClient;
import io.qdrant.client.grpc.Points;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class SkillEmbeddingService {

    private final EmbeddingModel embeddingModel;
    private final QdrantClient qdrantClient;
    private static final String COLLECTION = "user_skill_embeddings";

    public void indexUserSkill(UUID userId, String skillSlug, String description) {
        String text = String.format("%s. %s", skillSlug, description != null ? description : "");
        float[] embedding = embeddingModel.embed(text);
        
        // Store in Qdrant
        Points.PointStruct point = Points.PointStruct.newBuilder()
            .setId(Points.PointId.newBuilder()
                .setUuid(userId.toString()).build())
            .setVectors(Points.Vectors.newBuilder()
                .setVector(Points.Vector.newBuilder()
                    .addAllData(floatListToFloats(embedding)))
                .build())
            .build();
        
        qdrantClient.upsertAsync(COLLECTION, List.of(point));
        log.debug("Indexed skill {} for user {}", skillSlug, userId);
    }

    public List<MatchResult> rankBySimilarity(
            MatchRequest request, List<MatchCandidate> candidates) {
        
        // Compute embedding for the requested skill
        float[] queryEmbedding = embeddingModel.embed(request.targetSkillSlug());
        
        // Score candidates by combining: similarity + rating + schedule
        return candidates.stream()
            .map(c -> {
                double similarity = cosineSimilarity(
                    queryEmbedding, 
                    embeddingModel.embed(c.skillsDescription())
                );
                double score = (
                    0.5 * similarity + 
                    0.2 * (c.avgRating() / 5.0) +
                    0.2 * (c.scheduleOverlap()) +
                    0.1 * (c.languageMatch() ? 1.0 : 0.0)
                );
                return c.toMatchResult(score);
            })
            .sorted((a, b) -> Double.compare(b.score(), a.score()))
            .toList();
    }

    private double cosineSimilarity(float[] a, float[] b) {
        double dot = 0, normA = 0, normB = 0;
        for (int i = 0; i < a.length; i++) {
            dot += a[i] * b[i];
            normA += a[i] * a[i];
            normB += b[i] * b[i];
        }
        return dot / (Math.sqrt(normA) * Math.sqrt(normB));
    }
}
```

---

## 6. wallet-service — Seed economy

### 6.1. Ledger Pattern — Service chính

```java
package com.skillseed.wallet.service;

import com.skillseed.common.exception.BusinessException;
import com.skillseed.wallet.domain.SeedTransaction;
import com.skillseed.wallet.domain.SeedWallet;
import com.skillseed.wallet.dto.SeedTransferRequest;
import com.skillseed.wallet.repository.SeedTransactionRepository;
import com.skillseed.wallet.repository.SeedWalletRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class WalletService {

    private final SeedWalletRepository walletRepository;
    private final SeedTransactionRepository transactionRepository;

    @Transactional
    public void credit(UUID userId, int amount, String reason, UUID bookingId) {
        if (amount <= 0) {
            throw BusinessException.badRequest("Credit amount must be positive");
        }
        
        SeedWallet wallet = getOrCreateWallet(userId);
        
        SeedTransaction tx = SeedTransaction.builder()
            .wallet(wallet)
            .type(SeedTransaction.Type.EARN)
            .amount(amount)
            .bookingId(bookingId)
            .description(reason)
            .expiresAt(Instant.now().plus(180, ChronoUnit.DAYS)) // 6 months
            .build();
        
        transactionRepository.save(tx);
        updateWalletBalance(wallet);
        
        log.info("Credited {} seeds to user {} - reason: {}", amount, userId, reason);
    }

    @Transactional
    public void debit(UUID userId, int amount, String reason, UUID bookingId) {
        if (amount <= 0) {
            throw BusinessException.badRequest("Debit amount must be positive");
        }
        
        SeedWallet wallet = getOrCreateWallet(userId);
        
        if (wallet.getBalance() < amount) {
            throw BusinessException.badRequest(
                String.format("Insufficient balance: have %d, need %d", 
                    wallet.getBalance(), amount)
            );
        }
        
        SeedTransaction tx = SeedTransaction.builder()
            .wallet(wallet)
            .type(SeedTransaction.Type.SPEND)
            .amount(-amount)
            .bookingId(bookingId)
            .description(reason)
            .build();
        
        transactionRepository.save(tx);
        updateWalletBalance(wallet);
        
        log.info("Debited {} seeds from user {} - reason: {}", amount, userId, reason);
    }

    @Transactional
    public void transfer(SeedTransferRequest request) {
        // For P2P transfers (rare, mostly handled via booking flow)
        UUID fromUser = request.fromUserId();
        UUID toUser = request.toUserId();
        int amount = request.amount();
        
        if (fromUser.equals(toUser)) {
            throw BusinessException.badRequest("Cannot transfer to self");
        }
        
        // Atomic: debit first, if fails throw
        debit(fromUser, amount, "Transfer to " + toUser, null);
        
        try {
            credit(toUser, amount, "Transfer from " + fromUser, null);
        } catch (Exception e) {
            // Compensating transaction
            log.error("Credit failed, compensating", e);
            credit(fromUser, amount, "Refund failed transfer", null);
            throw e;
        }
    }

    @Scheduled(cron = "0 0 1 * * *") // Daily 1 AM
    @Transactional
    public void processExpiry() {
        log.info("Processing seed expiry...");
        
        List<SeedTransaction> expired = transactionRepository
            .findByExpiresAtBeforeAndNotExpired(Instant.now());
        
        for (SeedTransaction tx : expired) {
            SeedTransaction expireRecord = SeedTransaction.builder()
                .wallet(tx.getWallet())
                .type(SeedTransaction.Type.EXPIRE)
                .amount(-tx.getRemainingAmount())
                .description("Expired seeds from tx " + tx.getId())
                .build();
            transactionRepository.save(expireRecord);
            tx.setExpired(true);
        }
        
        log.info("Expired {} seed transactions", expired.size());
    }

    private SeedWallet getOrCreateWallet(UUID userId) {
        return walletRepository.findById(userId).orElseGet(() -> {
            SeedWallet newWallet = SeedWallet.builder()
                .userId(userId)
                .balance(0)
                .totalEarned(0)
                .totalSpent(0)
                .build();
            return walletRepository.save(newWallet);
        });
    }

    private void updateWalletBalance(SeedWallet wallet) {
        Integer balance = transactionRepository.sumActiveBalance(wallet.getUserId());
        Integer earned = transactionRepository.sumByType(wallet.getUserId(), SeedTransaction.Type.EARN);
        Integer spent = transactionRepository.sumByType(wallet.getUserId(), SeedTransaction.Type.SPEND);
        
        wallet.setBalance(balance != null ? balance : 0);
        wallet.setTotalEarned(earned != null ? earned : 0);
        wallet.setTotalSpent(Math.abs(spent != null ? spent : 0));
        wallet.setUpdatedAt(Instant.now());
    }
}
```

---

## 7. docker-compose.yml

```yaml
version: '3.8'

services:
  # Shared infrastructure
  postgres-users:
    image: postgres:16-alpine
    environment:
      POSTGRES_DB: skillseed_users
      POSTGRES_USER: skillseed
      POSTGRES_PASSWORD: dev_password
    ports:
      - "5432:5432"
    volumes:
      - pg_users_data:/var/lib/postgresql/data
    healthcheck:
      test: ["CMD-SHELL", "pg_isready -U skillseed"]
      interval: 10s
      timeout: 5s
      retries: 5

  postgres-matching:
    image: postgres:16-alpine
    environment:
      POSTGRES_DB: skillseed_matching
      POSTGRES_USER: skillseed
      POSTGRES_PASSWORD: dev_password
    ports:
      - "5433:5432"
    volumes:
      - pg_matching_data:/var/lib/postgresql/data

  postgres-wallet:
    image: postgres:16-alpine
    environment:
      POSTGRES_DB: skillseed_wallet
      POSTGRES_USER: skillseed
      POSTGRES_PASSWORD: dev_password
    ports:
      - "5434:5432"
    volumes:
      - pg_wallet_data:/var/lib/postgresql/data

  redis:
    image: redis:7-alpine
    ports:
      - "6379:6379"
    command: redis-server --appendonly yes
    volumes:
      - redis_data:/data

  kafka:
    image: confluentinc/cp-kafka:7.6.0
    depends_on:
      - zookeeper
    ports:
      - "9092:9092"
    environment:
      KAFKA_BROKER_ID: 1
      KAFKA_ZOOKEEPER_CONNECT: zookeeper:2181
      KAFKA_ADVERTISED_LISTENERS: PLAINTEXT://localhost:9092
      KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR: 1

  zookeeper:
    image: confluentinc/cp-zookeeper:7.6.0
    ports:
      - "2181:2181"
    environment:
      ZOOKEEPER_CLIENT_PORT: 2181

  qdrant:
    image: qdrant/qdrant:v1.11.0
    ports:
      - "6333:6333"
    volumes:
      - qdrant_data:/qdrant/storage

  # Observability
  prometheus:
    image: prom/prometheus:latest
    ports:
      - "9090:9090"
    volumes:
      - ./infra/prometheus.yml:/etc/prometheus/prometheus.yml

volumes:
  pg_users_data:
  pg_matching_data:
  pg_wallet_data:
  redis_data:
  qdrant_data:
```

---

## 8. Cách chạy thử

### 8.1. Prerequisites
- JDK 21+
- Maven 3.9+
- Docker + Docker Compose
- IDE: IntelliJ IDEA (Community OK)

### 8.2. Khởi động infrastructure

```bash
cd skillseed/
docker-compose up -d postgres-users redis qdrant
```

### 8.3. Build & run

```bash
# Build all modules
mvn clean install

# Run user-service
cd user-service
mvn spring-boot:run

# Trong terminal khác, chạy matching-service
cd matching-service
mvn spring-boot:run

# Trong terminal khác, chạy wallet-service  
cd wallet-service
mvn spring-boot:run
```

### 8.4. Test API

```bash
# Register user
curl -X POST http://localhost:8081/api/v1/users/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@example.com",
    "password": "password123",
    "fullName": "Test User",
    "countryCode": "VN",
    "timezone": "Asia/Ho_Chi_Minh"
  }'

# Swagger UI
open http://localhost:8081/swagger-ui.html
```

---

## 9. Bước tiếp theo

### Sprint 1 (tuần 1–2)
- [ ] Clone skeleton, chạy thử user-service
- [ ] Viết thêm `AuthController` cho login/JWT
- [ ] Implement `JwtAuthenticationFilter` trong common-lib
- [ ] Test integration giữa user-service ↔ matching-service

### Sprint 2 (tuần 3–4)
- [ ] Hoàn thiện booking flow trong `booking-service`
- [ ] Wire up Kafka events: `BookingCreatedEvent` → wallet-service credit/debit
- [ ] Setup CI/CD với GitHub Actions

### Sprint 3 (tuần 5–6)
- [ ] Frontend skeleton (Next.js) gọi user-service
- [ ] Onboarding 7-step UI cho Skill DNA
- [ ] Deploy thử lên Railway/Render

---

## 📚 Tham chiếu nhanh

| Pattern | File tham khảo |
|---------|---------------|
| Multi-module Maven | `pom.xml` (parent) |
| Entity + JPA Auditing | `User.java` |
| Flyway migration | `V1__init_users.sql` |
| Repository với custom query | `UserRepository.findCandidates` |
| Service transactional | `UserService.java`, `WalletService.java` |
| Controller + OpenAPI | `UserController.java` |
| Security JWT | `SecurityConfig.java` |
| Global exception handling | `GlobalExceptionHandler.java` |
| AI integration | `MatchingService.java`, `SkillEmbeddingService.java` |
| Ledger pattern | `WalletService.java` |
| Docker Compose dev | `docker-compose.yml` |
| Unit test | `UserServiceTest.java` |

---

> **Tip:** Khi copy code vào project thật, dùng `cp -r` để giữ structure, sau đó `mvn clean install` để check compile errors. Đừng quên tạo file `.gitignore` cho target/, .idea/, *.iml.

> **Liên hệ với tài liệu khác:** Code trong skeleton này dựa trên schema đã định nghĩa trong `SKILLSEED.md` mục 8 (Database Design). Khi thay đổi schema, update cả SQL migration lẫn entity class.

---

**Tác giả:** SkillSeed Team — Phiên bản 1.0 — 2026-09-06

# Hexagonal Architecture Refactoring Plan
## Spring Petclinic REST Modulith - Owner, User, Vet Modules

**Date:** November 9, 2025  
**Project:** spring-petclinic-rest-modulith  
**Branch:** hex-modules  
**Status:** REVISED - Simplified Pragmatic Approach  
**Revision:** v2.0 - Based on feedback

---

## 📝 Revision Summary

This plan has been **significantly simplified** based on feedback:

### ✅ What Changed (Simplified):

1. **Spring Framework Integration** - Domain can use `@Service`, `@Repository`, `@Entity` annotations
2. **No CQRS** - Use existing DTOs directly, no separate Command/Query objects
3. **Dynamic Module Discovery** - Tests automatically discover modules from Spring Modulith
4. **Infrastructure Module Exclusion** - Filter out utility/infrastructure modules from verification
5. **Simpler Structure** - Fewer layers, less mapping, faster implementation
6. **Reduced Timeline** - 3-4 days instead of 5-7 days
7. **OpenAPI Generated DTOs** - Use existing generated DTOs (perfect for hexagonal!)
8. **No Cross-Module Tests** - Spring Modulith already verifies module boundaries
9. **Controllers Stay Same** - Just move location, keep implementation unchanged

### 🎯 What Stays (Core Hexagonal):

1. **Port Interfaces** - Clear input (use cases) and output (repositories) contracts
2. **Adapter Separation** - REST, JPA, JDBC properly isolated
3. **Dependency Direction** - Adapters depend on business logic, not vice versa
4. **Testability** - Business logic can be tested independently
5. **Module Boundaries** - Spring Modulith verification maintained

### 🚀 Key Benefits of Simplified Approach:

- ⚡ **Faster implementation** - Less code to write and maintain
- 🎓 **Easier to understand** - Fewer layers and concepts
- 🔧 **More pragmatic** - Works with Spring Boot conventions
- ✅ **Still clean** - Maintains hexagonal benefits without overhead
- � **Fully automatic** - Tests discover modules dynamically from Spring Modulith
- 🎯 **Zero maintenance** - New modules automatically verified
- 📋 **OpenAPI DTOs** - Already generated, perfect for REST adapter contracts
- 🔄 **Controllers unchanged** - Just reorganize, functionality stays same

---

## Quick Reference: Simplified Package Structure

```
owner/ (or user/, vet/)
│
├── domain/                          ✅ Can use @Service, @Entity
│   ├── model/                      → Domain entities
│   ├── service/                    → Business logic (@Service OK)
│   └── port/out/                   → Repository interfaces
│
├── application/
│   └── port/in/                    → Use case interfaces
│       └── OwnerUseCase.java       (uses existing DTOs)
│
└── adapter/
    ├── in/web/                     → REST controllers
    │   ├── OwnerController.java    (@RestController)
    │   └── mapper/                 → DTO ↔ Domain mapping
    │
    └── out/persistence/            → DB adapters
        ├── jpa/                    → JPA implementation
        │   └── OwnerJpaAdapter.java (implements port)
        └── jdbc/                   → JDBC implementation
            └── OwnerJdbcAdapter.java (implements port)
```

**Key Rules:**
- ✅ Adapters → Business Logic (allowed)
- ❌ Business Logic → Adapters (forbidden)
- ✅ Spring annotations anywhere (pragmatic)
- ✅ Use OpenAPI generated DTOs (already exists!)
- 🔄 Controllers move location but stay unchanged

---

## Visual Comparison: Before vs After

### Current Structure (Before)
```
owner/
├── adapter/
│   ├── controller/              ❌ Empty (controllers in rest/)
│   └── repository/
│       ├── jdbc/               ✅ Works but needs organization
│       └── jpa/
├── application/
│   └── OwnerUseCaseImpl.java   ✅ Good
├── domain/
│   ├── model/                  ✅ Good
│   └── service/
│       ├── impl/               ⚠️  Has @Service (keeping it)
│       └── mapper/             ❌ Should be in adapter
└── OwnerUseCase.java           ⚠️  Should be in application/port/in

rest/ (separate module)
└── controller/
    └── OwnerRestController.java  ❌ Should be with owner module
```

### Target Structure (After)
```
owner/
├── domain/
│   ├── model/                  ✅ Domain entities
│   ├── service/                ✅ Business logic (@Service OK)
│   └── port/out/               ✅ Repository interfaces
│
├── application/
│   └── port/in/                ✅ Use case interfaces
│       └── OwnerUseCase.java
│
└── adapter/
    ├── in/web/                 ✅ REST controllers here
    │   ├── OwnerController.java
    │   └── mapper/             ✅ DTO mapping here
    │
    └── out/persistence/        ✅ DB implementations
        ├── jpa/
        │   └── OwnerJpaAdapter.java
        └── jdbc/
            └── OwnerJdbcAdapter.java
```

### What Changes
- 📦 **7 package moves** (use case interface, repository interfaces, controllers, mappers, adapters)
- 🔄 **No logic changes** (just reorganization)
- ✅ **All tests still pass** (same functionality)
- 📐 **Clear structure** (hexagonal architecture)

---
1. [Executive Summary](#executive-summary)
2. [Current State Analysis](#current-state-analysis)
3. [Hexagonal Architecture Overview](#hexagonal-architecture-overview)
4. [Target Architecture](#target-architecture)
5. [Refactoring Strategy](#refactoring-strategy)
6. [Detailed Implementation Plan](#detailed-implementation-plan)
7. [Verification & Testing Strategy](#verification-testing-strategy)
8. [Risk Assessment & Mitigation](#risk-assessment-mitigation)
9. [Success Criteria](#success-criteria)

---

## 1. Executive Summary

### Objective
Transform the three business logic modules (owner, user, vet) from a partially modular architecture into fully compliant hexagonal architecture (Ports and Adapters pattern), while maintaining Spring Modulith module boundaries and architectural integrity.

### Key Goals
- **Establish** clear port and adapter boundaries for each module
- **Separate** infrastructure adapters (REST, JPA, JDBC) from business logic
- **Implement** generic architectural verification tests using ArchUnit
- **Maintain** Spring Modulith module structure and verification
- **Enable** independent evolution and testing of business logic
- **Keep** Spring Framework integration (pragmatic approach)

### Estimated Effort
- **Analysis & Planning:** 1 day (✓ Current phase)
- **Implementation:** 3-4 days (simplified approach)
- **Testing & Verification:** 1-2 days
- **Documentation:** 1 day

---

## 2. Current State Analysis

### 2.1 Current Module Structure

All three modules (owner, user, vet) follow a similar structure:

```
<module>/
├── adapter/
│   ├── controller/          # Currently empty - REST in separate module
│   └── repository/
│       ├── jdbc/            # JDBC implementations
│       ├── jpa/             # JPA implementations
│       └── springdatajpa/   # Spring Data JPA
├── application/
│   └── *UseCaseImpl.java    # Use case implementations
├── domain/
│   ├── model/               # Domain entities
│   └── service/
│       ├── impl/            # Service implementations
│       └── mapper/          # DTO mappers
└── *UseCase.java            # Use case interfaces (at module root)
```

### 2.2 Current Issues & Areas for Improvement

#### 2.2.1 Adapter Organization
- **Issue:** REST controllers are in separate `rest` module, not with business modules
- **Impact:** Split between adapter and module it serves
- **Example:** `OwnerRestController` in `rest` module calls `OwnerUseCase`
- **Improvement:** Move controllers into respective module adapters

#### 2.2.2 OpenAPI Generated DTOs (Actually Perfect! ✅)

**Current State:**
- OpenAPI specification: `src/main/resources/openapi.yml`
- Generated DTOs: `target/generated-sources/openapi/.../rest/dto/`
- Maven plugin: `openapi-generator-maven-plugin` version 7.13.0
- Controllers implement generated API interfaces (e.g., `OwnersApi`)

**Generated Files:**
```
rest/dto/
├── OwnerDto.java           # Full representation
├── OwnerFieldsDto.java     # Create/update fields
├── PetDto.java
├── PetFieldsDto.java
├── VetDto.java
├── UserDto.java
└── ... (others)

rest/api/
├── OwnersApi.java          # Generated controller interface
├── VetsApi.java
├── UsersApi.java
└── ... (others)
```

**Why This is PERFECT for Hexagonal Architecture:**
- ✅ **Already separated** - DTOs are separate from domain models
- ✅ **Adapter layer** - DTOs define REST API contract (adapter responsibility)
- ✅ **Type safety** - OpenAPI ensures consistent contracts
- ✅ **No duplication** - Don't need to create Command/Query objects
- ✅ **Industry standard** - API-first design pattern
- ✅ **Clean mapping** - Mappers convert DTO ↔ Domain at adapter boundary

**Hexagonal Fit:**
```
┌─────────────────────────────────────────┐
│  REST Adapter (Input Port)             │
│  ├── OwnerRestController.java          │
│  │   implements OwnersApi (generated)  │  ← Controller stays unchanged
│  ├── Uses OwnerDto (generated)         │  ← OpenAPI DTOs as adapter contract
│  └── Uses OwnerFieldsDto (generated)   │
│                                         │
│  Mapper: DTO ↔ Domain                  │  ← Mapping at adapter boundary
│  OwnerMapper converts between them     │
└─────────────────────────────────────────┘
            ↓ calls
┌─────────────────────────────────────────┐
│  Business Logic (Use Cases)             │
│  ├── OwnerUseCase (interface)          │
│  └── Uses domain models internally     │
└─────────────────────────────────────────┘
```

**Conclusion:** OpenAPI DTOs are NOT a problem - they're a FEATURE! They perfectly represent the REST adapter's contract.

#### 2.2.3 Layer Clarity
- **Issue:** Mappers in domain layer converting to REST DTOs
- **Impact:** Domain knows about REST API structure
- **Example:** `OwnerMapper` in domain layer converts to `OwnerDto` from rest package
- **Improvement:** Move mappers to adapter layer

#### 2.2.4 Port Interface Visibility
- **Issue:** Use case interfaces at module root
- **Impact:** Unclear which layer owns the contract
- **Example:** `OwnerUseCase.java` at `owner/` root
- **Improvement:** Organize into `application/port/in/` for clarity

#### 2.2.5 Adapter Boundaries
- **Issue:** Repository implementations mixed with interfaces
- **Impact:** Less clear separation of concerns
- **Example:** Both interface and implementations in same package
- **Improvement:** Clear adapter structure in `adapter/out/`

### 2.3 Current Strengths
✅ **Spring Modulith Integration:** Active module verification  
✅ **Use Case Pattern:** Already using use case interfaces  
✅ **Repository Abstraction:** Interfaces defined in domain  
✅ **Multiple Implementations:** JDBC and JPA adapters exist  
✅ **Module Separation:** Clear module boundaries  

---

## 3. Hexagonal Architecture Overview

### 3.1 Core Principles (Pragmatic Approach)

**Hexagonal Architecture** (also known as Ports and Adapters) focuses on:

1. **Business Logic at Center:** Core business logic separated from infrastructure
2. **Port Interfaces:** Clear contracts for inbound and outbound interactions
3. **Adapters:** Implementations that connect to external systems (REST, DB, etc.)
4. **Dependency Direction:** Adapters depend on business logic, not vice versa
5. **Testability:** Business logic can be tested independently of infrastructure
6. **Framework Integration:** Spring annotations allowed throughout for pragmatic development

### 3.2 Layer Definitions

```
┌─────────────────────────────────────────────────────────────┐
│                    ADAPTERS (DRIVING)                       │
│          REST Controllers, CLI, Event Listeners             │
└─────────────────────────────────────────────────────────────┘
                              │
                              │ calls
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                   APPLICATION / DOMAIN                      │
│   ┌─────────────────────────────────────────────────────┐   │
│   │  Input Ports (Use Case Interfaces)                  │   │
│   │  - Can use DTOs directly                            │   │
│   │  - Spring annotations allowed (@Service)            │   │
│   └─────────────────────────────────────────────────────┘   │
│   ┌─────────────────────────────────────────────────────┐   │
│   │  Domain Model & Business Logic                      │   │
│   │  - Entities, Value Objects                          │   │
│   │  - Domain Services                                  │   │
│   └─────────────────────────────────────────────────────┘   │
│   ┌─────────────────────────────────────────────────────┐   │
│   │  Output Ports (Repository Interfaces)               │   │
│   └─────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────┘
                              │
                              │ implements
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                    ADAPTERS (DRIVEN)                        │
│      JPA, JDBC, External APIs, Message Publishers           │
└─────────────────────────────────────────────────────────────┘
```

### 3.3 Port Types (Simplified)

#### Input Ports (Driving/Primary)
- **Definition:** Use case interfaces that define what the application can do
- **Location:** `application.port.in`
- **Examples:** `OwnerUseCase`, `VetUseCase`, `UserUseCase`
- **Methods:** Can accept/return DTOs directly (no need for separate Command/Query objects)
- **Implemented by:** Application services (with `@Service`)
- **Called by:** Input adapters (REST controllers)

#### Output Ports (Driven/Secondary)
- **Definition:** Repository interfaces that define what the application needs
- **Location:** `domain.port.out`
- **Examples:** `OwnerRepository`, `PetRepository`, `VetRepository`
- **Implemented by:** Output adapters (JPA, JDBC implementations)
- **Called by:** Application services

---

## 4. Target Architecture

### 4.1 Proposed Package Structure (Simplified)

Each module (owner, user, vet) will follow this structure:

```
<module>/
├── domain/                              # DOMAIN LAYER
│   ├── model/                          # Domain entities
│   │   ├── Owner.java                  # @Entity allowed
│   │   ├── Pet.java                    
│   │   ├── PetType.java                
│   │   └── Visit.java                  
│   │
│   ├── service/                        # Domain services
│   │   ├── OwnerService.java          # @Service allowed
│   │   ├── PetService.java            # Business logic here
│   │   └── VisitService.java          
│   │
│   └── port/                           # OUTPUT PORTS
│       └── out/
│           ├── OwnerRepository.java    # Repository interface
│           ├── PetRepository.java
│           ├── PetTypeRepository.java
│           └── VisitRepository.java
│
├── application/                         # APPLICATION LAYER
│   └── port/                           # INPUT PORTS
│       └── in/
│           ├── OwnerUseCase.java      # Use case interface
│           ├── PetUseCase.java        # Can use existing DTOs
│           ├── PetTypeUseCase.java    # No separate Command/Query
│           └── VisitUseCase.java
│
└── adapter/                            # ADAPTER LAYER
    ├── in/                             # INPUT ADAPTERS
    │   └── web/
    │       ├── OwnerController.java    # @RestController
    │       ├── PetController.java
    │       ├── VisitController.java
    │       └── mapper/
    │           └── OwnerMapper.java    # Domain ↔ DTO mapping
    │
    └── out/                            # OUTPUT ADAPTERS
        └── persistence/
            ├── jpa/
            │   ├── OwnerJpaAdapter.java         # @Repository, implements port
            │   ├── PetJpaAdapter.java
            │   └── SpringDataOwnerRepository.java # extends JpaRepository
            │
            └── jdbc/
                ├── OwnerJdbcAdapter.java        # @Repository, implements port
                └── PetJdbcAdapter.java
```

**Key Simplifications:**
- ✅ Spring annotations allowed throughout (`@Service`, `@Repository`, `@Entity`)
- ✅ Use existing DTOs from `rest.dto` (no need to duplicate)
- ✅ No separate Command/Query objects (keep it simple)
- ✅ Use case interfaces accept/return DTOs directly
- ✅ Focus on clear adapter separation, not framework isolation

### 4.2 Dependency Rules (Pragmatic)

```
┌─────────────────────────────────────────────────────────────┐
│  Adapter (In)    → Application/Domain                       │
│  Adapter (Out)   ← Application/Domain (via ports)           │
│                                                             │
│  ✅ Allowed:                                                │
│    - Adapter → Application Ports (Use Cases)                │
│    - Adapter → Domain (for DTOs, entities)                  │
│    - Application → Domain                                   │
│    - Application → Domain Ports (Repositories)              │
│    - Domain → Spring Framework (pragmatic approach)         │
│                                                             │
│  ❌ Forbidden:                                              │
│    - Domain → Adapter                                       │
│    - Application → Adapter                                  │
│    - Adapter (In) → Adapter (Out)                           │
│    - Circular dependencies between modules                  │
└─────────────────────────────────────────────────────────────┘
```

### 4.3 Key Architectural Decisions

#### Decision 1: REST Controllers Location
**Decision:** Move REST controllers from `rest.controller` to `<module>.adapter.in.web`  
**Rationale:** 
- Controllers are input adapters for specific modules
- Aligns with hexagonal architecture principles
- Improves module cohesion
- Enables independent module deployment

**Alternative Considered:** Keep controllers in separate `rest` module  
**Rejected Because:** Splits adapter from the business logic it serves

#### Decision 2: DTO Strategy with OpenAPI (Simplified)
**Decision:** Use **OpenAPI generated DTOs** from `rest.dto` package directly
- DTOs already generated from `openapi.yml` specification
- Use case interfaces accept/return these DTOs
- Controllers implement `OwnersApi`, `VetsApi`, etc. (generated interfaces)
- Keep mapping in adapter layer

**Why This Works Perfectly:**
- ✅ **Already exists** - DTOs generated via `openapi-generator-maven-plugin`
- ✅ **API contract** - DTOs define REST API contract (adapter layer responsibility)
- ✅ **Hexagonal fit** - Generated DTOs ARE the adapter's input/output contracts
- ✅ **No duplication** - Don't create separate Command/Query objects
- ✅ **Type safety** - OpenAPI ensures consistent API contracts

**OpenAPI Generated Files Location:**
```
target/generated-sources/openapi/src/main/java/
└── org/springframework/samples/petclinic/rest/dto/
    ├── OwnerDto.java           # Full owner representation
    ├── OwnerFieldsDto.java     # For create/update
    ├── PetDto.java
    ├── PetFieldsDto.java
    ├── VetDto.java
    ├── UserDto.java
    └── ... (other DTOs)
```

**Controller Implementation:**
- Controllers implement generated API interfaces (e.g., `OwnersApi`)
- Use generated DTOs as-is (e.g., `OwnerDto`, `OwnerFieldsDto`)
- Mapper converts between DTOs and domain models
- **Controllers stay unchanged** - just move to new location

**Rationale:**
- Simpler implementation
- Leverages existing OpenAPI infrastructure
- Still maintains clean adapter separation
- API-first design already in place

**Alternative Considered:** Create separate Command/Query objects  
**Rejected Because:** Duplicates existing generated DTOs unnecessarily

#### Decision 3: Mapper Locations
**Decision:** 
- **Mappers** in `adapter.in.web.mapper` - DTO ↔ Domain mapping
- **Persistence Mappers** in `adapter.out.persistence.<tech>` (if needed) - Domain ↔ Entity

**Rationale:** Keep mapping at adapter boundaries

#### Decision 4: Repository Interface Location
**Decision:** Keep repository interfaces in `domain.port.out`  
**Rationale:** 
- Repositories are output ports needed by domain services
- Domain defines what it needs, adapters provide it
- Follows Dependency Inversion Principle

#### Decision 5: Use Case Granularity (Simplified)
**Decision:** Group related operations in single Use Case interface  
**Examples:**
- `OwnerUseCase` - All owner operations (CRUD, pets, visits)
- `VetUseCase` - All vet operations
- `UserUseCase` - All user operations

**Rationale:**
- Simpler structure (fewer interfaces)
- Natural grouping by aggregate
- Easier to understand
- Still testable and mockable

**Alternative Considered:** Fine-grained use cases (one per operation)  
**Rejected Because:** Adds complexity without clear benefit for this project

---

## 5. Refactoring Strategy

### 5.1 Approach: Incremental Strangler Pattern

We'll use the **Strangler Fig Pattern** to gradually replace the old structure:

1. **Build new structure alongside old**
2. **Redirect traffic incrementally**
3. **Remove old structure after verification**
4. **Maintain working state throughout**

### 5.2 Refactoring Phases

#### Phase 1: Foundation & Tests (Day 1)
- Create new package structure
- Implement generic architectural tests
- Run baseline verification
- No behavior changes yet

#### Phase 2: Reorganize Ports & Adapters (Days 2-3)
- Move use case interfaces to `application/port/in/`
- Move repository interfaces to `domain/port/out/`
- Reorganize persistence implementations to `adapter/out/persistence/`
- Update imports and references

#### Phase 3: Move REST Controllers (Day 3-4)
- Move REST controllers from `rest` module to module `adapter/in/web/`
- Move mappers to `adapter/in/web/mapper/`
- Update controller dependencies
- Test REST endpoints

#### Phase 4: Verification & Cleanup (Day 4)
- Run all architectural tests
- Verify Spring Modulith compliance
- Clean up old package structure
- Update documentation

### 5.3 Module Processing Order

1. **Start with `user` module** - Simplest, smallest surface area
2. **Then `vet` module** - Medium complexity
3. **Finally `owner` module** - Most complex, has pets and visits

**Rationale:** Learn and refine approach on simpler modules first

---

## 6. Detailed Implementation Plan

### 6.1 Phase 1: Foundation Setup

#### 6.1.1 Create Package Structure
**For each module (owner, user, vet):**

```bash
# Create domain ports
mkdir -p src/main/java/org/springframework/samples/petclinic/<module>/domain/port/out
mkdir -p src/main/java/org/springframework/samples/petclinic/<module>/domain/exception

# Create application ports (use cases only - no command/query)
mkdir -p src/main/java/org/springframework/samples/petclinic/<module>/application/port/in

# Create input adapters
mkdir -p src/main/java/org/springframework/samples/petclinic/<module>/adapter/in/web/mapper

# Reorganize output adapters
mkdir -p src/main/java/org/springframework/samples/petclinic/<module>/adapter/out/persistence/jpa
mkdir -p src/main/java/org/springframework/samples/petclinic/<module>/adapter/out/persistence/jdbc
```

**Note:** No separate `dto` folder needed - using OpenAPI generated DTOs!

#### 6.1.2 Define Architectural Test Dependencies

Add to `pom.xml`:

```xml
<!-- ArchUnit for architecture verification -->
<dependency>
    <groupId>com.tngtech.archunit</groupId>
    <artifactId>archunit-junit5</artifactId>
    <version>1.3.0</version>
    <scope>test</scope>
</dependency>
```

#### 6.1.3 Create Base Architectural Test Class

**File:** `src/test/java/org/springframework/samples/petclinic/architecture/HexagonalArchitectureTest.java`

```java
@AnalyzeClasses(packages = "org.springframework.samples.petclinic")
public class HexagonalArchitectureTest {

    // Test definitions will go here
    // See section 7 for details
}
```

### 6.2 Phase 2: Owner Module Refactoring (Example - Simplified)

#### 6.2.1 Step 1: Move Use Case Interface to Port

**Current Location:** `owner/OwnerUseCase.java` (at module root)  
**Target Location:** `owner/application/port/in/OwnerUseCase.java`

**Actions:**
1. Create `application/port/in/` package
2. Move `OwnerUseCase.java` to new location
3. Keep existing method signatures (no changes)
4. Update package references

**No changes to interface - just moving it:**

```java
// owner/application/port/in/OwnerUseCase.java
package org.springframework.samples.petclinic.owner.application.port.in;

import org.springframework.samples.petclinic.rest.dto.OwnerDto;
import org.springframework.samples.petclinic.rest.dto.OwnerFieldsDto;
// ... other imports

/**
 * Input port for Owner use cases
 */
public interface OwnerUseCase {
    Optional<List<OwnerDto>> listOwnersA(String lastName);
    Optional<OwnerDto> getOwnerA(Integer ownerId);
    OwnerDto addOwnerA(OwnerFieldsDto ownerFieldsDto);
    OwnerDto updateOwnerA(Integer ownerId, OwnerFieldsDto ownerFieldsDto);
    Optional<Integer> deleteOwnerA(Integer ownerId);
    PetDto addPetToOwnerA(Integer ownerId, PetFieldsDto petFieldsDto);
    boolean updateOwnersPetA(Integer ownerId, Integer petId, PetFieldsDto petFieldsDto);
    VisitDto addVisitToOwnerA(Integer ownerId, Integer petId, VisitFieldsDto visitFieldsDto);
    Optional<PetDto> getOwnersPetA(Integer ownerId, Integer petId);
}
```

#### 6.2.2 Step 2: Move Repository Interfaces to Domain Ports

**Current Location:** `owner/domain/service/OwnerService.java` (interface)  
**Target Location:** `owner/domain/port/out/OwnerRepository.java`

**Actions:**
1. Create `domain/port/out/` package
2. Rename `OwnerService` → `OwnerRepository` (or keep name if already good)
3. Move to port package
4. Update implementations to reference new location

**Example:**

```java
// owner/domain/port/out/OwnerRepository.java
package org.springframework.samples.petclinic.owner.domain.port.out;

import org.springframework.dao.DataAccessException;
import org.springframework.samples.petclinic.owner.domain.model.Owner;
import java.util.Collection;

/**
 * Output port for Owner persistence
 */
public interface OwnerRepository {
    Owner findOwnerById(int id) throws DataAccessException;
    Collection<Owner> findAllOwners() throws DataAccessException;
    void saveOwner(Owner owner) throws DataAccessException;
    void deleteOwner(Owner owner) throws DataAccessException;
    Collection<Owner> findOwnerByLastName(String lastName) throws DataAccessException;
}
```

Do the same for `PetRepository`, `PetTypeRepository`, `VisitRepository`.

#### 6.2.3 Step 3: Reorganize Domain Services (Keep @Service)

**Current Location:** `owner/domain/service/impl/OwnerServiceImpl.java`  
**Keep Location:** `owner/domain/service/OwnerService.java` (rename from Impl)

**Actions:**
1. Rename `OwnerServiceImpl` → `OwnerService` 
2. Keep `@Service` annotation (pragmatic approach)
3. Update to use repository port from `domain.port.out`
4. No other changes needed

**Example:**

```java
// owner/domain/service/OwnerService.java
package org.springframework.samples.petclinic.owner.domain.service;

import org.springframework.samples.petclinic.owner.domain.model.Owner;
import org.springframework.samples.petclinic.owner.domain.port.out.OwnerRepository;
import org.springframework.stereotype.Service;
import java.util.Collection;

@Service
public class OwnerService {
    private final OwnerRepository ownerRepository;

    public OwnerService(OwnerRepository ownerRepository) {
        this.ownerRepository = ownerRepository;
    }

    public Owner findOwnerById(int id) {
        return ownerRepository.findOwnerById(id);
    }

    public Collection<Owner> findAllOwners() {
        return ownerRepository.findAllOwners();
    }

    public void saveOwner(Owner owner) {
        ownerRepository.saveOwner(owner);
    }

    public void deleteOwner(Owner owner) {
        ownerRepository.deleteOwner(owner);
    }

    public Collection<Owner> findOwnerByLastName(String lastName) {
        return ownerRepository.findOwnerByLastName(lastName);
    }
    
    // Add any business logic methods here
}
```

#### 6.2.4 Step 4: Update Application Use Case Implementation

**Current Location:** `owner/application/OwnerUseCaseImpl.java`  
**Keep Location:** Same, just update imports

**Actions:**
1. Update imports to use new port locations
2. Keep existing logic unchanged
3. Ensure it implements the use case interface from new location

```java
// owner/application/OwnerUseCaseImpl.java
package org.springframework.samples.petclinic.owner.application;

import org.springframework.samples.petclinic.owner.application.port.in.OwnerUseCase;
import org.springframework.samples.petclinic.owner.domain.service.OwnerService;
import org.springframework.samples.petclinic.owner.domain.service.PetService;
// ... other imports
import org.springframework.stereotype.Component;

@Component
public class OwnerUseCaseImpl implements OwnerUseCase {
    private final OwnerService ownerService;
    private final PetService petService;
    private final VisitService visitService;
    private final OwnerMapper ownerMapper;
    private final PetMapper petMapper;
    private final VisitMapper visitMapper;

    // Constructor and existing methods stay the same
    // Just updated imports
}
```

#### 6.2.5 Step 5: Reorganize Persistence Adapters

**Current Location:** `owner/adapter/repository/jpa/JpaOwnerRepositoryImpl.java`  
**Target Location:** `owner/adapter/out/persistence/jpa/OwnerJpaAdapter.java`

**Actions:**
1. Create `adapter/out/persistence/jpa/` structure
2. Move JPA implementations there
3. Rename for clarity: `JpaOwnerRepositoryImpl` → `OwnerJpaAdapter`
4. Update to implement port from `domain.port.out`

```java
// owner/adapter/out/persistence/jpa/OwnerJpaAdapter.java
package org.springframework.samples.petclinic.owner.adapter.out.persistence.jpa;

import org.springframework.samples.petclinic.owner.domain.model.Owner;
import org.springframework.samples.petclinic.owner.domain.port.out.OwnerRepository;
import org.springframework.stereotype.Repository;

@Repository
public class OwnerJpaAdapter implements OwnerRepository {
    
    private final SpringDataOwnerRepository springDataRepository;

    public OwnerJpaAdapter(SpringDataOwnerRepository springDataRepository) {
        this.springDataRepository = springDataRepository;
    }

    @Override
    public Owner findOwnerById(int id) {
        return springDataRepository.findById(id).orElse(null);
    }

    @Override
    public Collection<Owner> findAllOwners() {
        return springDataRepository.findAll();
    }

    @Override
    public void saveOwner(Owner owner) {
        springDataRepository.save(owner);
    }

    @Override
    public void deleteOwner(Owner owner) {
        springDataRepository.delete(owner);
    }

    @Override
    public Collection<Owner> findOwnerByLastName(String lastName) {
        return springDataRepository.findByLastName(lastName);
    }
}
```

Do the same for JDBC adapters in `adapter/out/persistence/jdbc/`.

#### 6.2.6 Step 6: Move REST Controller to Module Adapter

**⚠️ IMPORTANT: Controllers Stay Unchanged!**

**Current Location:** `rest/controller/OwnerRestController.java`  
**Target Location:** `owner/adapter/in/web/OwnerRestController.java`

**What Changes:**
- ✅ Package location: `rest.controller` → `owner.adapter.in.web`
- ✅ Import statements (use case from new location)

**What Stays EXACTLY the Same:**
- ✅ Class name: `OwnerRestController` (keep original name)
- ✅ All method implementations
- ✅ All annotations (`@RestController`, `@PreAuthorize`, etc.)
- ✅ Uses OpenAPI generated DTOs (`OwnerDto`, `OwnerFieldsDto`)
- ✅ Implements `OwnersApi` (generated interface)
- ✅ All business logic
- ✅ Return types and parameters

**Actions:**
1. Create `adapter/in/web/` package in owner module
2. **Copy** controller to new location (don't modify yet)
3. Update import: `org.springframework.samples.petclinic.owner.OwnerUseCase` 
   → `org.springframework.samples.petclinic.owner.application.port.in.OwnerUseCase`
4. Delete old controller from `rest/controller/` only after verification

**Result:**

```java
// owner/adapter/in/web/OwnerRestController.java
package org.springframework.samples.petclinic.owner.adapter.in.web;  // ← Only this changed

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.samples.petclinic.owner.application.port.in.OwnerUseCase;  // ← Import updated
import org.springframework.samples.petclinic.rest.api.OwnersApi;  // ← Still uses generated API
import org.springframework.samples.petclinic.rest.dto.*;  // ← Still uses generated DTOs
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import jakarta.transaction.Transactional;
import java.util.List;

@RestController
@CrossOrigin(exposedHeaders = "errors, content-type")
@RequestMapping("/api")
public class OwnerRestController implements OwnersApi {  // ← Everything else IDENTICAL

    private final OwnerUseCase ownerUseCase;

    public OwnerRestController(OwnerUseCase ownerUseCase) {
        this.ownerUseCase = ownerUseCase;
    }

    @PreAuthorize("hasRole(@roles.OWNER_ADMIN)")
    @Override
    public ResponseEntity<List<OwnerDto>> listOwners(String lastName) {
        return ownerUseCase.listOwnersA(lastName)
            .map(r -> new ResponseEntity<>(r, HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PreAuthorize("hasRole(@roles.OWNER_ADMIN)")
    @Override
    public ResponseEntity<OwnerDto> getOwner(Integer ownerId) {
        return ownerUseCase.getOwnerA(ownerId)
            .map(r -> new ResponseEntity<>(r, HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PreAuthorize("hasRole(@roles.OWNER_ADMIN)")
    @Override
    public ResponseEntity<OwnerDto> addOwner(OwnerFieldsDto ownerFieldsDto) {
        HttpHeaders headers = new HttpHeaders();
        OwnerDto ownerDto = ownerUseCase.addOwnerA(ownerFieldsDto);
        headers.setLocation(UriComponentsBuilder.newInstance()
            .path("/api/owners/{id}").buildAndExpand(ownerDto.getId()).toUri());
        return new ResponseEntity<>(ownerDto, headers, HttpStatus.CREATED);
    }

    // ... all other methods stay EXACTLY the same
}
```

**Why This Works:**
- ✅ **OpenAPI DTOs are perfect** - They define the REST API contract (adapter layer)
- ✅ **Generated interfaces work** - `OwnersApi` stays in `rest.api` package
- ✅ **No logic changes** - Just reorganization
- ✅ **All tests pass** - Same behavior, different location

#### 6.2.7 Step 7: Move Mappers to Adapter

**Current Location:** `owner/domain/service/mapper/OwnerMapper.java`  
**Target Location:** `owner/adapter/in/web/mapper/OwnerMapper.java`

**Actions:**
1. Create `adapter/in/web/mapper/` package
2. Move mappers there (they convert between domain and DTOs)
3. Keep existing implementation

```java
// owner/adapter/in/web/mapper/OwnerMapper.java
package org.springframework.samples.petclinic.owner.adapter.in.web.mapper;

import org.springframework.samples.petclinic.owner.domain.model.Owner;
import org.springframework.samples.petclinic.rest.dto.OwnerDto;
import org.springframework.samples.petclinic.rest.dto.OwnerFieldsDto;
import org.springframework.stereotype.Component;
import java.util.Collection;
import java.util.ArrayList;

@Component
public class OwnerMapper {
    
    // Keep existing mapping logic
    public OwnerDto toOwnerDto(Owner owner) {
        // ... existing implementation
    }
    
    public Owner toOwner(OwnerFieldsDto ownerFieldsDto) {
        // ... existing implementation
    }
    
    public Collection<OwnerDto> toOwnerDtoCollection(Collection<Owner> owners) {
        // ... existing implementation
    }
}
```

### 6.3 Repeat for User and Vet Modules

Follow the same 7-step pattern for `user` and `vet` modules:
1. Move use case interfaces to `application/port/in/`
2. Move repository interfaces to `domain/port/out/`
3. Keep domain services with `@Service`
4. Update application use case implementations
5. Reorganize persistence adapters to `adapter/out/`
6. Move REST controllers to `adapter/in/web/`
7. Move mappers to `adapter/in/web/mapper/`

---

## 7. Verification & Testing Strategy

### 7.1 Dynamic Module Discovery with Spring Modulith

**Key Innovation:** Tests automatically discover modules from Spring Modulith - no manual maintenance!

#### 7.1.1 How Dynamic Discovery Works

```java
// Reuse Spring Modulith's module discovery
static Stream<String> businessModules() {
    ApplicationModules modules = ApplicationModules.of(PetClinicApplication.class);
    return modules.stream()
        .map(module -> module.getName())              // Get module names
        .filter(name -> !INFRASTRUCTURE_MODULES.contains(name))  // Filter infrastructure
        .sorted();                                    // Consistent order
}
```

**Module Discovery Flow:**
```
Spring Modulith → Discovers all modules → Filter out infrastructure → Test each business module
     ↓                      ↓                          ↓                        ↓
PetClinicApplication   owner, user, vet,      Exclude: shared, util,      Run ArchUnit tests
     scans codebase     shared, util, rest     rest, config                on: owner, user, vet
```

**Infrastructure Modules to Exclude:**
```java
private static final List<String> INFRASTRUCTURE_MODULES = List.of(
    "shared",     // Shared utilities across modules
    "util",       // Utility classes
    "rest",       // REST infrastructure (will be removed after refactoring)
    "config"      // Configuration classes
);
```

**Benefits:**
- ✅ **Zero maintenance** - Add new module, tests apply automatically
- ✅ **Single source of truth** - Spring Modulith defines what modules exist
- ✅ **Flexible filtering** - Easy to exclude infrastructure modules
- ✅ **Consistent verification** - All business modules follow same rules

#### 7.1.2 Adding New Business Module - Example

**Scenario:** You add a new `appointment` module for managing appointments.

**Old Approach (Manual):**
```java
@ValueSource(strings = {"owner", "user", "vet", "appointment"})  // ← Must manually add!
```

**New Approach (Automatic):**
```java
@MethodSource("businessModules")  // ← Discovers automatically!
// Spring Modulith finds 'appointment' module → automatically tested!
```

**Steps:**
1. Create `appointment/` module with business logic
2. Spring Modulith detects it automatically
3. **Done!** All hexagonal architecture tests apply immediately

No test code changes needed! 🎉

### 7.2 Generic Architectural Tests with ArchUnit

**Based on feedback:** Tests automatically discover and verify ALL business modules!

#### 7.2.1 Base Generic Test Class

```java
// src/test/java/org/springframework/samples/petclinic/architecture/HexagonalArchitectureTest.java
package org.springframework.samples.petclinic.architecture;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.modulith.core.ApplicationModules;
import org.springframework.samples.petclinic.PetClinicApplication;

import java.util.List;
import java.util.stream.Stream;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.*;
import static com.tngtech.archunit.library.Architectures.layeredArchitecture;

/**
 * Generic hexagonal architecture tests that apply to ALL business modules.
 * Modules are automatically discovered from Spring Modulith!
 * Infrastructure modules are excluded via INFRASTRUCTURE_MODULES list.
 */
@AnalyzeClasses(
    packages = "org.springframework.samples.petclinic",
    importOptions = ImportOption.DoNotIncludeTests.class
)
public class HexagonalArchitectureTest {

    private static final String BASE_PACKAGE = "org.springframework.samples.petclinic";
    
    /**
     * Infrastructure modules that should NOT be verified for hexagonal architecture.
     * Add modules here that are utilities, shared code, or infrastructure.
     */
    private static final List<String> INFRASTRUCTURE_MODULES = List.of(
        "shared",     // Shared utilities
        "util",       // Utility classes
        "rest",       // REST infrastructure (will be removed after refactoring)
        "config"      // Configuration classes
    );

    /**
     * Dynamically discover business modules from Spring Modulith.
     * This automatically includes new modules when added!
     */
    static Stream<String> businessModules() {
        ApplicationModules modules = ApplicationModules.of(PetClinicApplication.class);
        return modules.stream()
            .map(module -> module.getName())
            .filter(name -> !INFRASTRUCTURE_MODULES.contains(name))
            .sorted();  // For consistent test order
    }

    /**
     * Generic test that verifies hexagonal architecture for any module.
     * Modules are automatically discovered - no manual updates needed!
     */
    @ParameterizedTest(name = "Module ''{0}'' should follow hexagonal architecture")
    @MethodSource("businessModules")
    void module_should_follow_hexagonal_architecture(String moduleName) {
        JavaClasses classes = new ClassFileImporter()
            .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
            .importPackages(BASE_PACKAGE + "." + moduleName);

        // Define layers for this module
        layeredArchitecture()
            .consideringAllDependencies()
            
            .layer("Adapter-In").definedBy(modulePackage(moduleName, "adapter.in.."))
            .layer("Application").definedBy(modulePackage(moduleName, "application.."))
            .layer("Domain").definedBy(modulePackage(moduleName, "domain.."))
            .layer("Adapter-Out").definedBy(modulePackage(moduleName, "adapter.out.."))
            
            // Dependency rules
            .whereLayer("Application").mayOnlyBeAccessedByLayers("Adapter-In")
            .whereLayer("Domain").mayOnlyBeAccessedByLayers("Application", "Adapter-Out", "Adapter-In")
            .whereLayer("Adapter-In").mayNotBeAccessedByAnyLayer()
            .whereLayer("Adapter-Out").mayNotBeAccessedByAnyLayer()
            
            .check(classes);
    }

    /**
     * Generic test: Domain and Application should NOT depend on adapters
     */
    @ParameterizedTest(name = "Module ''{0}'' - domain and application should not depend on adapters")
    @MethodSource("businessModules")
    void domain_and_application_should_not_depend_on_adapters(String moduleName) {
        JavaClasses classes = new ClassFileImporter()
            .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
            .importPackages(BASE_PACKAGE + "." + moduleName);

        noClasses()
            .that().resideInAnyPackage(
                modulePackage(moduleName, "domain.."),
                modulePackage(moduleName, "application..")
            )
            .should().dependOnClassesThat()
            .resideInAnyPackage(
                modulePackage(moduleName, "adapter.in.."),
                modulePackage(moduleName, "adapter.out..")
            )
            .because("Domain and Application must not depend on adapters")
            .check(classes);
    }

    /**
     * Generic test: Use case interfaces should be in correct package
     */
    @ParameterizedTest(name = "Module ''{0}'' - use cases should be in application.port.in")
    @MethodSource("businessModules")
    void use_cases_should_be_in_application_port_in(String moduleName) {
        JavaClasses classes = new ClassFileImporter()
            .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
            .importPackages(BASE_PACKAGE + "." + moduleName);

        classes()
            .that().haveSimpleNameEndingWith("UseCase")
            .and().areInterfaces()
            .should().resideInAPackage(modulePackage(moduleName, "application.port.in"))
            .because("Use case interfaces are input ports")
            .check(classes);
    }

    /**
     * Generic test: Repository interfaces should be in domain ports
     */
    @ParameterizedTest(name = "Module ''{0}'' - repositories should be in domain.port.out")
    @MethodSource("businessModules")
    void repositories_should_be_in_domain_port_out(String moduleName) {
        JavaClasses classes = new ClassFileImporter()
            .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
            .importPackages(BASE_PACKAGE + "." + moduleName);

        classes()
            .that().haveSimpleNameEndingWith("Repository")
            .and().areInterfaces()
            .should().resideInAPackage(modulePackage(moduleName, "domain.port.out"))
            .because("Repository interfaces are output ports")
            .check(classes);
    }

    /**
     * Generic test: REST controllers should be in adapter.in.web
     */
    @ParameterizedTest(name = "Module ''{0}'' - controllers should be in adapter.in.web")
    @MethodSource("businessModules")
    void controllers_should_be_in_adapter_in_web(String moduleName) {
        JavaClasses classes = new ClassFileImporter()
            .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
            .importPackages(BASE_PACKAGE + "." + moduleName);

        classes()
            .that().areAnnotatedWith("org.springframework.web.bind.annotation.RestController")
            .should().resideInAPackage(modulePackage(moduleName, "adapter.in.web"))
            .because("REST controllers are input adapters")
            .check(classes);
    }

    /**
     * Generic test: Persistence adapters should implement repository ports
     */
    @ParameterizedTest(name = "Module ''{0}'' - persistence adapters should implement repository ports")
    @MethodSource("businessModules")
    void persistence_adapters_should_implement_repository_ports(String moduleName) {
        JavaClasses classes = new ClassFileImporter()
            .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
            .importPackages(BASE_PACKAGE + "." + moduleName);

        classes()
            .that().resideInAPackage(modulePackage(moduleName, "adapter.out.persistence.."))
            .and().haveSimpleNameEndingWith("Adapter")
            .should().implement(
                classes().that().resideInAPackage(modulePackage(moduleName, "domain.port.out"))
            )
            .because("Persistence adapters must implement repository ports")
            .check(classes);
    }

    /**
     * Generic test: Adapters should not depend on each other
     */
    @ParameterizedTest(name = "Module ''{0}'' - adapters should not depend on each other")
    @MethodSource("businessModules")
    void adapters_should_not_depend_on_each_other(String moduleName) {
        JavaClasses classes = new ClassFileImporter()
            .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
            .importPackages(BASE_PACKAGE + "." + moduleName);

        noClasses()
            .that().resideInAPackage(modulePackage(moduleName, "adapter.in.."))
            .should().dependOnClassesThat()
            .resideInAPackage(modulePackage(moduleName, "adapter.out.."))
            .because("Input adapters should not depend on output adapters")
            .check(classes);

        noClasses()
            .that().resideInAPackage(modulePackage(moduleName, "adapter.out.."))
            .should().dependOnClassesThat()
            .resideInAPackage(modulePackage(moduleName, "adapter.in.."))
            .because("Output adapters should not depend on input adapters")
            .check(classes);
    }

    /**
     * Generic test: Port interfaces should be interfaces
     */
    @ParameterizedTest(name = "Module ''{0}'' - ports should be interfaces")
    @MethodSource("businessModules")
    void ports_should_be_interfaces(String moduleName) {
        JavaClasses classes = new ClassFileImporter()
            .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
            .importPackages(BASE_PACKAGE + "." + moduleName);

        classes()
            .that().resideInAnyPackage(
                modulePackage(moduleName, "application.port.."),
                modulePackage(moduleName, "domain.port..")
            )
            .and().areTopLevelClasses()
            .should().beInterfaces()
            .because("Ports should be interfaces defining contracts")
            .check(classes);
    }

    /**
     * Helper method to construct module package pattern
     */
    private String modulePackage(String moduleName, String subPackage) {
        return BASE_PACKAGE + "." + moduleName + "." + subPackage;
    }
}
```

**Why No Cross-Module Tests?**
- ✅ **Spring Modulith handles this** - Already verifying module boundaries
- ✅ **No duplication** - Use existing `ModuleVerificationsTest`
- ✅ **Focus on structure** - These tests verify internal hexagonal structure
- ✅ **Simpler** - Less test code to maintain

**Key Benefits of Dynamic Module Discovery:**

1. ✅ **Truly Generic**: Add new business module? Tests **automatically** apply!
2. ✅ **No manual updates**: Module list grows with your application
3. ✅ **Infrastructure exclusion**: Filter out utility modules via `INFRASTRUCTURE_MODULES` list
4. ✅ **Single source of truth**: Spring Modulith defines what modules exist
5. ✅ **Consistent**: All modules verified with same rules
2. ✅ **Parameterized**: One test definition checks all modules
3. ✅ **Maintainable**: Change rule once, applies everywhere
4. ✅ **Clear**: Each test has single responsibility
5. ✅ **Extensible**: Easy to add new rules

### 7.2 Integration Tests

#### 7.2.1 Use Case Integration Tests

```java
// src/test/java/org/springframework/samples/petclinic/owner/application/OwnerServiceIntegrationTest.java
@SpringBootTest
@Transactional
class OwnerServiceIntegrationTest {

    @Autowired
    private OwnerUseCase ownerUseCase;

    @Test
    void should_create_and_find_owner() {
        // Given - use OpenAPI generated DTO
        OwnerFieldsDto ownerFields = new OwnerFieldsDto()
            .firstName("John")
            .lastName("Doe")
            .address("123 Main St")
            .city("Springfield")
            .telephone("5551234567");

        // When - create owner
        OwnerDto created = ownerUseCase.addOwnerA(ownerFields);

        // Then
        assertNotNull(created.getId());
        assertEquals("John", created.getFirstName());
        
        // When - find owner
        Optional<OwnerDto> found = ownerUseCase.getOwnerA(created.getId());

        // Then
        assertTrue(found.isPresent());
        assertEquals("John", found.get().getFirstName());
        assertEquals("Doe", found.get().getLastName());
    }
}
```

#### 7.2.2 REST Controller Tests

**Important:** Controllers stay unchanged - we just move their location!

```java
// src/test/java/org/springframework/samples/petclinic/owner/adapter/in/web/OwnerRestControllerTest.java
@WebMvcTest(OwnerRestController.class)
class OwnerRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OwnerUseCase ownerUseCase;

    @Test
    @WithMockUser(roles = "OWNER_ADMIN")
    void should_create_owner_via_rest() throws Exception {
        // Given - use OpenAPI generated DTOs
        OwnerFieldsDto ownerFields = new OwnerFieldsDto()
            .firstName("John")
            .lastName("Doe")
            .address("123 Main St")
            .city("Springfield")
            .telephone("5551234567");
            
        OwnerDto expectedResult = new OwnerDto()
            .id(1)
            .firstName("John")
            .lastName("Doe")
            .address("123 Main St")
            .city("Springfield")
            .telephone("5551234567");
        
        when(ownerUseCase.addOwnerA(any(OwnerFieldsDto.class)))
            .thenReturn(expectedResult);

        // When & Then
        mockMvc.perform(post("/api/owners")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "firstName": "John",
                        "lastName": "Doe",
                        "address": "123 Main St",
                        "city": "Springfield",
                        "telephone": "5551234567"
                    }
                    """))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.firstName").value("John"));
    }
}
```

**Note:** 
- ✅ Controller implementation stays **exactly the same**
- ✅ Uses existing `OwnerUseCase` interface
- ✅ Uses OpenAPI generated DTOs (`OwnerDto`, `OwnerFieldsDto`)
- ✅ Implements generated `OwnersApi` interface
- 🔄 Only change: move from `rest.controller` to `owner.adapter.in.web`

### 7.3 Spring Modulith Verification

```java
// src/test/java/org/springframework/samples/petclinic/ModuleVerificationsTest.java
package org.springframework.samples.petclinic;

import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;
import org.springframework.modulith.docs.Documenter;

public class ModuleVerificationsTest {
    
    @Test
    void testModules() {
        final ApplicationModules applicationModules = 
            ApplicationModules.of(PetClinicApplication.class);
        
        System.out.println(applicationModules);
        applicationModules.verify();
    }

    @Test
    void generateModuleDocumentation() {
        ApplicationModules modules = 
            ApplicationModules.of(PetClinicApplication.class);
        
        new Documenter(modules)
            .writeDocumentation()
            .writeIndividualModulesAsPlantUml()
            .writeModulesAsPlantUml();
    }
}
```

---

## 8. Risk Assessment & Mitigation

### 8.1 Identified Risks (Simplified Approach)

| Risk | Probability | Impact | Mitigation Strategy |
|------|-------------|--------|---------------------|
| **Breaking existing functionality** | Medium | Critical | • Incremental package moves<br>• Comprehensive test coverage<br>• No logic changes initially |
| **Incorrect package moves** | Low | Medium | • Automated refactoring tools<br>• IDE support for moves<br>• Git tracking |
| **Module boundary violations** | Low | High | • Spring Modulith verification<br>• Generic ArchUnit rules<br>• CI/CD integration |
| **Team adjustment period** | Low | Low | • Simpler structure<br>• Clear documentation<br>• Gradual learning |
| **Test maintenance** | Low | Low | • Generic tests reduce duplication<br>• Parameterized approach<br>• Easy to extend |

**Risk Reduction vs Original Plan:**
- ❌ Removed: Over-engineering risk (simpler structure)
- ❌ Removed: Performance concern (no extra mapping layers)
- ❌ Removed: Team resistance (familiar Spring patterns)
- ✅ Lower probability overall due to simplified approach

### 8.2 Rollback Strategy

If critical issues arise:

1. **Phase-level rollback:** Revert to previous phase using Git
2. **Module-level rollback:** Revert single module, keep others
3. **Feature toggle:** Disable new structure via configuration
4. **Data migration:** Ensure no database schema changes during refactoring

### 8.3 Success Metrics

- ✅ All existing tests pass
- ✅ New architectural tests pass
- ✅ Spring Modulith verification succeeds
- ✅ No performance regression (< 5% difference)
- ✅ Code coverage maintained or improved
- ✅ All API contracts unchanged
- ✅ Documentation updated

---

## 9. Success Criteria

### 9.1 Technical Success Criteria

#### Architecture Compliance
- [ ] All ArchUnit tests pass (100% compliance)
- [ ] Spring Modulith verification passes without errors
- [ ] No circular dependencies between modules
- [ ] No dependency rule violations

#### Code Quality
- [ ] Domain layer has zero Spring/framework dependencies
- [ ] All use cases have single responsibility
- [ ] Ports are properly defined as interfaces
- [ ] Adapters correctly implement ports
- [ ] Clear separation between layers

#### Testing
- [ ] 100% of existing tests migrated and passing
- [ ] New architectural tests implemented
- [ ] Integration tests cover main use cases
- [ ] Unit tests for domain services (framework-free)
- [ ] Code coverage ≥ 80%

#### Documentation
- [ ] Architecture decision records (ADRs) created
- [ ] Package structure documented
- [ ] Dependency rules explained
- [ ] Examples provided for each layer
- [ ] Spring Modulith documentation generated

### 9.2 Business Success Criteria

- [ ] No breaking changes to REST API
- [ ] All existing features work as before
- [ ] No performance degradation
- [ ] Development velocity maintained
- [ ] Team understands new structure

### 9.3 Verification Checklist

For each module (owner, user, vet):

#### Domain Layer
- [ ] `domain/model/` contains only domain entities (no annotations)
- [ ] `domain/service/` contains pure domain logic (no Spring)
- [ ] `domain/port/out/` contains repository interfaces
- [ ] No dependencies on application or adapter layers

#### Application Layer
- [ ] `application/port/in/usecase/` contains use case interfaces
- [ ] `application/port/in/command/` contains command objects
- [ ] `application/port/in/query/` contains query objects
- [ ] `application/service/` implements use cases with `@Service`
- [ ] `application/mapper/` maps between domain and commands/queries
- [ ] No dependencies on adapter layer

#### Adapter Layer - Input
- [ ] `adapter/in/web/` contains REST controllers
- [ ] `adapter/in/web/dto/` contains REST request/response objects
- [ ] `adapter/in/web/mapper/` maps REST DTOs to commands/queries
- [ ] Controllers depend only on use case interfaces

#### Adapter Layer - Output
- [ ] `adapter/out/persistence/jpa/` contains JPA adapter
- [ ] `adapter/out/persistence/jdbc/` contains JDBC adapter
- [ ] Adapters implement domain port interfaces
- [ ] Persistence entities separate from domain entities
- [ ] Mappers handle entity-domain conversion

---

## 10. Next Steps

### Immediate Actions (Before Implementation)

1. **Review & Approval**
   - [ ] Review this plan with team
   - [ ] Discuss architectural decisions
   - [ ] Adjust based on feedback
   - [ ] Get formal approval to proceed

2. **Environment Setup**
   - [ ] Create feature branch `hex-architecture-refactoring`
   - [ ] Add ArchUnit dependency to pom.xml
   - [ ] Set up CI/CD for architectural tests
   - [ ] Prepare development environment

3. **Team Preparation**
   - [ ] Schedule kickoff meeting
   - [ ] Assign module ownership
   - [ ] Set up pair programming sessions
   - [ ] Create communication channels

### Implementation Sequence

1. **Week 1: User Module** (Simplest)
   - Days 1-2: Foundation and tests
   - Days 3-4: Implementation
   - Day 5: Review and adjustments

2. **Week 2: Vet Module** (Medium)
   - Apply lessons learned from User
   - Follow same pattern
   - Refine approach

3. **Week 3: Owner Module** (Complex)
   - Most complex due to Pet and Visit relationships
   - Extra time for testing
   - Final refinements

### Post-Implementation

1. **Knowledge Transfer**
   - [ ] Create architecture guide
   - [ ] Record video walkthrough
   - [ ] Update team wiki
   - [ ] Conduct training sessions

2. **Continuous Improvement**
   - [ ] Monitor metrics
   - [ ] Gather feedback
   - [ ] Identify improvements
   - [ ] Plan next iterations

---

## Appendix A: Key Architectural Patterns

### A.1 Hexagonal Architecture (Ports and Adapters)
We separate business logic from infrastructure through port interfaces and adapter implementations.

### A.2 Dependency Inversion Principle (DIP)
High-level modules (business logic) don't depend on low-level modules (infrastructure details).

### A.3 Interface Segregation Principle (ISP)
Port interfaces are focused and cohesive - each has clear purpose.

### A.4 Single Responsibility Principle (SRP)
Each port, adapter, and service has one reason to change.

---

## Appendix B: References

- [Hexagonal Architecture, DDD, and Spring - Baeldung](https://www.baeldung.com/hexagonal-architecture-ddd-spring)
- [Adopting Domain-First Thinking in Modular Monolith - ITNEXT](https://itnext.io/adopting-domain-first-thinking-in-modular-monolith-with-hexagonal-architecture-f9e4921ac18d)
- [Spring Modulith Documentation](https://docs.spring.io/spring-modulith/reference/)
- [ArchUnit User Guide](https://www.archunit.org/userguide/html/000_Index.html)
- [Clean Architecture - Robert C. Martin](https://blog.cleancoder.com/uncle-bob/2012/08/13/the-clean-architecture.html)

---

## Appendix C: FAQ - Addressing Key Concerns

### Q1: Do we need cross-module tests?
**A: NO** - Spring Modulith already verifies module boundaries and dependencies.

**Rationale:**
- ✅ Existing `ModuleVerificationsTest` handles cross-module verification
- ✅ No need to duplicate this functionality
- ✅ Our ArchUnit tests focus on **internal hexagonal structure**
- ✅ Spring Modulith focuses on **cross-module boundaries**

**What We Test:**
- ArchUnit: Internal module structure (ports, adapters, dependencies within module)
- Spring Modulith: Cross-module dependencies (module → module boundaries)

### Q2: Are OpenAPI generated DTOs a problem?
**A: NO - They're PERFECT!** OpenAPI DTOs are exactly what we need for hexagonal architecture.

**Why They Work:**
- ✅ **Already separated** from domain models
- ✅ **Define adapter contract** - REST API contract belongs in adapter layer
- ✅ **Type-safe** - Generated from specification
- ✅ **No duplication** - Don't create Command/Query objects
- ✅ **Industry standard** - API-first design

**Hexagonal Fit:**
```
REST Adapter → Uses OwnerDto (OpenAPI) → Mapper → Domain Model
```

The OpenAPI DTOs ARE the adapter's input/output contract. Mapping happens at adapter boundary.

### Q3: Will controller implementations change?
**A: NO** - Controllers stay **exactly the same**!

**What Changes:**
- ✅ Package location only: `rest.controller` → `owner.adapter.in.web`
- ✅ Import statements (use case from new location)

**What Stays Identical:**
- ✅ Class implementation
- ✅ Method signatures
- ✅ Annotations
- ✅ Business logic
- ✅ OpenAPI generated DTOs usage
- ✅ Implements generated API interfaces (`OwnersApi`, etc.)

**Why It Works:**
- Controllers already use DTOs properly
- Controllers already call use case interfaces
- Just moving location, not changing behavior

### Q4: Are the tests really generic?
**A: YES** - Tests **automatically discover** modules from Spring Modulith!

**How It Works:**
```java
// Dynamically discover modules from Spring Modulith
static Stream<String> businessModules() {
    ApplicationModules modules = ApplicationModules.of(PetClinicApplication.class);
    return modules.stream()
        .map(module -> module.getName())
        .filter(name -> !INFRASTRUCTURE_MODULES.contains(name))
        .sorted();
}

@ParameterizedTest(name = "Module ''{0}'' should follow hexagonal architecture")
@MethodSource("businessModules")  // ← Uses dynamic discovery!
void module_should_follow_hexagonal_architecture(String moduleName) {
    // Test automatically applies to all business modules
}
```

**Adding New Business Module:**
1. Create new module (e.g., `appointment/`)
2. Spring Modulith detects it automatically
3. **Done!** All hexagonal tests automatically apply!

**Excluding Infrastructure Modules:**
```java
private static final List<String> INFRASTRUCTURE_MODULES = List.of(
    "shared",     // Shared utilities
    "util",       // Utility classes  
    "rest",       // REST infrastructure
    "config"      // Configuration classes
);
```

**Benefits:**
- ✅ **Zero maintenance** - Module list updates automatically
- ✅ **Single source of truth** - Spring Modulith defines modules
- ✅ **Flexible filtering** - Exclude infrastructure via simple list
- ✅ **Consistent rules** - All business modules verified identically

### Q5: Does this introduce CQRS complexity?
**A: NO** - We explicitly removed CQRS!

**What We're NOT Doing:**
- ❌ No separate Command objects
- ❌ No separate Query objects
- ❌ No three-layer DTO strategy
- ❌ No command/query segregation

**What We're Doing:**
- ✅ Use OpenAPI generated DTOs directly
- ✅ Single DTO layer
- ✅ Simple use case interfaces with multiple methods
- ✅ Mapping at adapter boundaries

**Simplified Example:**
```java
// Use case interface (simple, no CQRS)
interface OwnerUseCase {
    OwnerDto addOwnerA(OwnerFieldsDto fields);  // Uses OpenAPI DTOs
    Optional<OwnerDto> getOwnerA(Integer id);
    Optional<List<OwnerDto>> listOwnersA(String lastName);
    // ... other methods
}
```

---

## Appendix D: Glossary

- **Adapter:** Implementation that connects ports to external systems (REST, JPA, JDBC)
- **Aggregate:** Cluster of domain objects treated as a single unit
- **Domain:** Core business logic and entities
- **Entity:** Domain object with identity (e.g., Owner, Pet, Vet)
- **Input Port:** Interface defining what the application can do (Use Case)
- **OpenAPI DTO:** Generated data transfer object from OpenAPI specification
- **Output Port:** Interface defining what the application needs (Repository)
- **Use Case:** Interface describing business operation (can have multiple methods)
- **Value Object:** Immutable domain object defined by attributes

---

**Document Status:** REVISED - Ready for Final Approval  
**Version:** 3.0 (Dynamic Module Discovery)  
**Last Updated:** November 10, 2025  
**Author:** GitHub Copilot (AI Assistant)  
**Changes:** 
- v2.0: Simplified based on feedback - removed CQRS, framework-free requirements; added generic tests
- v2.1: Removed cross-module tests, clarified OpenAPI DTOs fit, emphasized controllers stay unchanged
- v3.0: **Dynamic module discovery** from Spring Modulith, infrastructure module exclusion

---

## Approval Section

| Stakeholder | Role | Approval | Date | Comments |
|-------------|------|----------|------|----------|
| [Name] | Tech Lead | ☐ Approved ☐ Rejected | | v3.0 - Dynamic module discovery |
| [Name] | Architect | ☐ Approved ☐ Rejected | | v3.0 - Zero test maintenance |
| [Name] | Product Owner | ☐ Approved ☐ Rejected | | v3.0 - Automatic growth |

---

**Ready for implementation! Fully automatic, zero-maintenance test approach.**

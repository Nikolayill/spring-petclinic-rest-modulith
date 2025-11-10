# ADR-002: Spring Data JPA Pattern Recognition

**Status:** ✅ Accepted  
**Date:** November 10, 2025  
**Authors:** Development Team  
**Reviewers:** Architecture Team

---

## Context and Problem Statement

During the implementation of hexagonal architecture, we encountered an architectural testing challenge with Spring Data JPA repositories. Our ArchUnit tests were initially failing because Spring Data JPA repositories appeared to violate hexagonal architecture principles.

### The Challenge

Spring Data JPA repositories are **interfaces** that:
1. Extend domain port interfaces (e.g., `UserRepository`)
2. Use Spring Data JPA annotations and conventions
3. Are framework-specific (tied to Spring Data technology)
4. Generate implementation code at runtime

This created a classification dilemma:
- **Are they ports?** (interfaces defining contracts)
- **Are they adapters?** (technology-specific implementations)

### Initial Test Failures

```
ArchUnit Test Failure: 
Spring Data repositories in persistence package violate 
"ports should be pure interfaces" rule
```

## Decision

We have decided to classify **Spring Data JPA repositories as adapter-level interfaces** and implement a **specialized package structure** with **automatic test recognition**.

### Classification Decision

**Spring Data JPA repositories are ADAPTERS**, not pure ports, because:

1. **Technology-Specific**: Tied to Spring Data JPA framework
2. **Implementation Details**: Include method naming conventions, annotations
3. **Framework Magic**: Runtime implementation generation
4. **Adapter Concerns**: Contain persistence technology details

### Implementation Strategy

#### 1. Package Structure

```
adapter/out/persistence/
├── jdbc/                    # Pure JDBC implementations
├── jpa/                     # Pure JPA implementations  
└── springdatajpa/          # Spring Data JPA repositories ⭐
    ├── SpringDataUserRepository.java
    ├── SpringDataOwnerRepository.java
    └── SpringDataVetRepository.java
```

#### 2. Architectural Test Modification

```java
// HexagonalArchitectureTest.java line 158
classes().that()
    .resideInAPackage("..port.out..")
    .and().resideOutsideOfPackage("..springdatajpa..")  // ⭐ Exclusion
    .should().beInterfaces()
    .because("Repository ports should be pure interfaces without implementation details");
```

#### 3. Consistent Pattern Application

All three modules (User, Vet, Owner) follow the same pattern:
- ✅ Pure repository interfaces in `domain.port.out`
- ✅ Spring Data repositories in `adapter.out.persistence.springdatajpa`
- ✅ Automatic test exclusion based on package naming

## Rationale

### Why This Classification is Architecturally Correct

1. **Separation of Concerns**: 
   - **Pure ports** define business contracts (technology-agnostic)
   - **Spring Data repositories** contain implementation details (technology-specific)

2. **Dependency Direction Preserved**:
   ```
   Spring Data Repository (Adapter) → Repository Interface (Port) → Business Logic
   ```

3. **Technology Independence**: 
   - Business logic only knows about pure port interfaces
   - Spring Data details are isolated in adapter layer

4. **Substitutability**: 
   - Can replace Spring Data with pure JDBC/JPA implementations
   - Business logic remains unchanged

### Why Dedicated Package Structure

1. **Clear Intent**: `springdatajpa` package name makes framework dependency explicit
2. **Automated Testing**: ArchUnit can automatically exclude based on package naming
3. **Consistency**: All modules follow the same pattern
4. **Maintainability**: Easy to identify and manage framework-specific code

### Why Not Alternative Solutions

#### Alternative 1: Allow Framework Dependencies in Ports
- **Rejected**: Violates technology independence principle
- **Problem**: Business logic becomes tied to Spring Data

#### Alternative 2: Hide Spring Data Behind Pure Implementations
- **Rejected**: Unnecessary wrapper complexity
- **Problem**: Loses Spring Data benefits (automatic implementation)

#### Alternative 3: Separate Spring Data as Different Module
- **Rejected**: Over-engineering for current needs
- **Problem**: Breaks module cohesion

## Implementation Details

### Port Interface (Pure)

```java
// domain/port/out/UserRepository.java
package org.springframework.samples.petclinic.user.domain.port.out;

public interface UserRepository {
    User findByUsername(String username);
    User save(User user);
    // Pure business contract, no framework details
}
```

### Spring Data Adapter (Framework-Specific)

```java
// adapter/out/persistence/springdatajpa/SpringDataUserRepository.java  
package org.springframework.samples.petclinic.user.adapter.out.persistence.springdatajpa;

@Repository
public interface SpringDataUserRepository 
    extends UserRepository, JpaRepository<User, Integer> {
    
    // Spring Data method naming conventions
    Optional<User> findByUsernameIgnoreCase(String username);
    
    // Spring Data query annotations  
    @Query("SELECT u FROM User u WHERE u.enabled = true")
    List<User> findEnabledUsers();
}
```

### Business Logic Usage

```java
// application/UserUseCaseImpl.java
@Service
public class UserUseCaseImpl implements UserUseCase {
    
    private final UserRepository userRepository; // ← Pure port interface
    
    public UserUseCaseImpl(UserRepository userRepository) {
        this.userRepository = userRepository; // ← Spring will inject Spring Data impl
    }
}
```

## Consequences

### Positive

- ✅ **Hexagonal Compliance**: Clear port/adapter separation maintained
- ✅ **Spring Data Benefits**: Keeps automatic implementation generation
- ✅ **Technology Independence**: Business logic only depends on pure interfaces  
- ✅ **Automated Testing**: ArchUnit automatically validates pattern
- ✅ **Pattern Consistency**: All modules follow same structure
- ✅ **Clear Intent**: Package structure makes dependencies explicit

### Neutral

- 🔄 **Interface Duplication**: Spring Data interface extends pure port interface
- 🔄 **Package Proliferation**: More packages per module for organization

### Negative

- ❌ **Conceptual Complexity**: Requires understanding of adapter vs port classification
- ❌ **Documentation Overhead**: Need to explain the pattern to new developers

## Validation

### Test Results

```bash
# Before fix: 3 failures across all modules
./mvnw test -Dtest=HexagonalArchitectureTest
[FAIL] User module - Spring Data repository in wrong location
[FAIL] Vet module - Spring Data repository in wrong location  
[FAIL] Owner module - Spring Data repository in wrong location

# After implementation: All passing
./mvnw test -Dtest=HexagonalArchitectureTest
[PASS] 21/21 architectural tests passing ✅
```

### Module Verification

```bash
./mvnw test -Dtest=ModuleVerificationsTest

# Output shows clean separation:
# User module:
#   └── adapter.out.persistence.springdatajpa.SpringDataUserRepository
# Vet module:  
#   └── adapter.out.persistence.springdatajpa.SpringDataVetRepository
#   └── adapter.out.persistence.springdatajpa.SpringDataSpecialtyRepository
# Owner module:
#   └── adapter.out.persistence.springdatajpa.SpringDataOwnerRepository
#   └── (+ Pet, PetType, Visit Spring Data repositories)
```

### Functional Testing

```bash
./mvnw test
# Results: 194/194 tests passing ✅
# - All business logic working correctly
# - Spring Data repositories functional
# - Dependency injection working properly
```

## Team Guidelines

### For Developers

1. **Pure Repository Interfaces**: Place in `domain.port.out`
   ```java
   // ✅ Good: Business contract only
   public interface UserRepository {
       User findByUsername(String username);
   }
   ```

2. **Spring Data Repositories**: Place in `adapter.out.persistence.springdatajpa`
   ```java
   // ✅ Good: Framework-specific implementation
   @Repository
   public interface SpringDataUserRepository 
       extends UserRepository, JpaRepository<User, Integer> {
   }
   ```

3. **Business Logic**: Only depend on pure port interfaces
   ```java
   // ✅ Good: Depends on pure interface
   public UserUseCaseImpl(UserRepository userRepository) { ... }
   
   // ❌ Bad: Depends on Spring Data interface  
   public UserUseCaseImpl(SpringDataUserRepository repo) { ... }
   ```

### For Architects

1. **New Modules**: Follow the established pattern
2. **ArchUnit Tests**: Will automatically verify compliance
3. **Package Naming**: Use `springdatajpa` for framework-specific repositories
4. **Code Reviews**: Ensure pure ports vs adapter classification

## Future Considerations

### When Adding New Persistence Technologies

1. **MongoDB**: `adapter.out.persistence.mongodb`
2. **Redis**: `adapter.out.persistence.redis`  
3. **Custom**: `adapter.out.persistence.custom`

### When Extending Spring Data Features

1. **Custom Repositories**: Stay in `springdatajpa` package
2. **New Annotations**: Document framework coupling
3. **Query Methods**: Keep implementation details in adapters

## References

- [Spring Data JPA Documentation](https://docs.spring.io/spring-data/jpa/docs/current/reference/html/)
- [Hexagonal Architecture Ports vs Adapters](https://alistair.cockburn.us/hexagonal-architecture/)
- [ArchUnit Testing Patterns](https://www.archunit.org/userguide/html/000_Index.html)

## Revision History

| Version | Date | Changes | Author |
|---------|------|---------|--------|
| 1.0 | 2025-11-10 | Initial pattern decision | Development Team |
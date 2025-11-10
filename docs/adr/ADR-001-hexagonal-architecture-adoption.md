# ADR-001: Hexagonal Architecture Adoption

**Status:** ✅ Accepted  
**Date:** November 10, 2025  
**Authors:** Development Team  
**Reviewers:** Architecture Team

---

## Context and Problem Statement

The Spring PetClinic REST application initially used a traditional layered architecture with Spring Modulith for module organization. While this provided some separation of concerns, we identified several challenges:

1. **Tight Coupling**: Business logic was coupled to infrastructure concerns (REST, database)
2. **Testing Complexity**: Difficult to test business logic in isolation
3. **Technology Lock-in**: Hard to swap persistence or presentation technologies
4. **Unclear Boundaries**: Mixed responsibilities between layers
5. **Architecture Drift**: No automated verification of architectural rules

We needed an architecture that would:
- Provide clear separation between business logic and infrastructure
- Enable independent testing of business logic
- Support multiple adapter implementations (JDBC, JPA, Spring Data)
- Maintain testable and verifiable architectural boundaries
- Scale with future requirements

## Decision

We have decided to adopt **Hexagonal Architecture** (also known as Ports and Adapters) for all business modules while maintaining Spring Modulith for module organization.

### Key Architectural Elements

1. **Ports (Interfaces)**:
   - **Input Ports**: Use case interfaces in `application/port/in/`
   - **Output Ports**: Repository interfaces in `domain/port/out/`

2. **Adapters (Implementations)**:
   - **Input Adapters**: REST controllers in `adapter/in/web/`
   - **Output Adapters**: Persistence implementations in `adapter/out/persistence/`

3. **Business Logic (Core)**:
   - **Domain Models**: Entities in `domain/model/`
   - **Domain Services**: Business logic in `domain/service/`
   - **Application Services**: Use case implementations in `application/`

### Dependency Rules

```
Adapters → Ports → Business Logic
```

- ✅ Adapters depend on ports (interfaces)
- ✅ Business logic is independent of infrastructure
- ❌ Ports never depend on adapters
- ❌ Business logic never depends on REST/database specifics

## Rationale

### Why Hexagonal Architecture?

1. **Testability**: Business logic can be tested independently using mocked ports
2. **Flexibility**: Easy to swap adapters (JDBC ↔ JPA ↔ Spring Data)
3. **Maintainability**: Clear separation of concerns and responsibilities
4. **Technology Independence**: Core business logic is not tied to frameworks
5. **Future-Proofing**: Easy to add new adapters for different technologies

### Why Keep Spring Modulith?

- Excellent module discovery and boundary verification
- Integrates well with hexagonal architecture
- Provides automated documentation generation
- Enforces module-level separation

### Why ArchUnit for Testing?

- Automated verification of architectural rules
- Prevents architecture drift over time
- Scales automatically with new modules
- Provides fast feedback on violations

## Implementation Details

### Module Structure

Each business module follows this structure:

```
module/
├── application/
│   ├── port/
│   │   └── in/              # Use case interfaces
│   └── *UseCaseImpl.java    # Use case implementations
├── domain/
│   ├── model/               # Domain entities
│   ├── port/
│   │   └── out/             # Repository interfaces
│   └── service/             # Domain services
└── adapter/
    ├── in/
    │   └── web/             # REST controllers
    └── out/
        └── persistence/     # Repository implementations
```

### Technology Choices

| Component | Technology | Rationale |
|-----------|------------|-----------|
| **Module Discovery** | Spring Modulith | Automatic discovery, boundary verification |
| **Architecture Testing** | ArchUnit | Automated rule verification, prevent drift |
| **API Generation** | OpenAPI 3.1 | Contract-first, type-safe DTOs |
| **Persistence** | Multiple (JDBC/JPA/Spring Data) | Demonstrates adapter flexibility |

## Consequences

### Positive

- ✅ **Clear Boundaries**: Well-defined separation between layers
- ✅ **Testable Business Logic**: Domain and application layers testable in isolation
- ✅ **Flexible Infrastructure**: Multiple persistence implementations coexist
- ✅ **Automated Verification**: ArchUnit prevents architectural violations
- ✅ **Technology Independence**: Business logic not tied to Spring/database
- ✅ **Scalable Pattern**: Easy to add new modules following the same structure

### Negative

- ❌ **Initial Complexity**: More interfaces and abstractions than simple layered architecture
- ❌ **Learning Curve**: Team needs to understand hexagonal architecture principles
- ❌ **More Files**: Each module has more files due to port/adapter separation

### Neutral

- 🔄 **Same Functionality**: No change in external behavior
- 🔄 **Spring Integration**: Still uses Spring annotations (@Service, @Repository) where pragmatic
- 🔄 **Module Count**: Same number of business modules (user, vet, owner)

## Implementation Timeline

- **Phase 1** (✅ Complete): Foundation and architectural tests setup
- **Phase 2** (✅ Complete): User module refactoring  
- **Phase 3** (✅ Complete): Vet module refactoring
- **Phase 4** (✅ Complete): Owner module refactoring
- **Phase 5** (✅ Complete): Documentation and cleanup

**Total Duration**: 5 days  
**Test Results**: 21/21 architectural tests passing  
**Regression**: 0 business logic failures

## Alternatives Considered

### 1. Pure Layered Architecture
- **Pros**: Simple, familiar pattern
- **Cons**: Tight coupling, difficult testing, technology lock-in
- **Decision**: Rejected due to coupling issues

### 2. Clean Architecture
- **Pros**: Similar benefits to hexagonal
- **Cons**: More complex, additional abstraction layers
- **Decision**: Hexagonal chosen for simplicity

### 3. Modular Monolith Only
- **Pros**: Simple module boundaries
- **Cons**: No internal structure, coupling within modules
- **Decision**: Combined with hexagonal for best of both

## Validation

### Automated Testing

```bash
# Run architectural tests
./mvnw test -Dtest=HexagonalArchitectureTest

# Results: 21/21 tests passing
# - All use cases in application.port.in ✅
# - All repositories in domain.port.out ✅
# - All controllers in adapter.in.web ✅
# - No forbidden dependencies ✅
```

### Spring Modulith Verification

```bash
./mvnw test -Dtest=ModuleVerificationsTest

# Results: Module boundaries verified ✅
# - No circular dependencies
# - Clean module separation
# - Proper Spring bean organization
```

### Business Logic Regression

```bash
./mvnw test

# Results: 194/194 tests passing ✅
# - No business logic regressions
# - All REST endpoints working
# - All persistence layers functional
```

## References

- [Hexagonal Architecture (Alistair Cockburn)](https://alistair.cockburn.us/hexagonal-architecture/)
- [Spring Modulith Documentation](https://spring.io/projects/spring-modulith)
- [ArchUnit User Guide](https://www.archunit.org/userguide/html/000_Index.html)
- [Clean Architecture (Robert C. Martin)](https://blog.cleancoder.com/uncle-bob/2012/08/13/the-clean-architecture.html)

## Revision History

| Version | Date | Changes | Author |
|---------|------|---------|--------|
| 1.0 | 2025-11-10 | Initial adoption decision | Development Team |
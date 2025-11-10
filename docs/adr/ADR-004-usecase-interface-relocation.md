# ADR-004: UseCase Interface Relocation for Spring Modulith Compliance

**Status:** 🟡 Proposed  
**Date:** November 10, 2025  
**Authors:** Development Team  
**Reviewers:** Architecture Team  
**Supersedes:** None  
**Related:** ADR-001 (Hexagonal Architecture Adoption)

---

## Context and Problem Statement

During the implementation of hexagonal architecture with Spring Modulith, we identified a potential compliance issue with our current UseCase interface placement. Our analysis revealed that UseCase interfaces located in `application.port.in` packages are technically in **internal packages** according to Spring Modulith's module boundary rules.

### The Problem

**Spring Modulith Module Boundary Rules:**
- Only the **module root package** is considered the public API surface
- Sub-packages (like `application.port.in`) are considered **internal**
- External modules accessing internal packages violates module boundaries

**Current Structure Issues:**
```
owner/
├── application/
│   └── port/
│       └── in/              ❌ INTERNAL PACKAGE
│           └── OwnerUseCase.java  ← Technically not publicly accessible
```

**Impact:**
- UseCase interfaces (driving ports) are in internal packages
- External consumers accessing these interfaces violate Spring Modulith rules
- Potential for Spring Modulith verification failures as system grows
- Inconsistency with framework-intended module API design

### Evidence from Spring Modulith Documentation

> **"An application module's base package is considered the API package and thus is the only package to allow incoming dependencies from other modules"**

> **"Sub-packages are considered internal ones. Code within those must not be referred to from other modules."**

This confirms that our current placement violates Spring Modulith's intended module boundary design.

## Decision

We have decided to **relocate UseCase interfaces from `application.port.in` to module root packages** to ensure full Spring Modulith compliance while maintaining hexagonal architecture principles.

### New Structure

**Before (Current):**
```
owner/
├── application/
│   ├── port/
│   │   └── in/              ❌ Internal package
│   │       └── OwnerUseCase.java
│   └── OwnerUseCaseImpl.java
```

**After (Proposed):**
```
owner/
├── OwnerUseCase.java        ✅ Public API (module root)
├── PetUseCase.java          ✅ Public API (module root)
├── application/
│   ├── OwnerUseCaseImpl.java
│   └── PetUseCaseImpl.java
```

### Updated Architecture Rules

1. **Driving Ports Location**: UseCase interfaces at module root (public API)
2. **Implementation Location**: UseCase implementations in `application/` (internal)
3. **Driven Ports Location**: Repository interfaces remain in `domain.port.out/` (internal)
4. **Adapter Access**: Controllers access UseCase interfaces from module root

## Rationale

### Why This Change is Architecturally Sound

#### 1. Spring Modulith Compliance
- ✅ **Public API Surface**: UseCase interfaces become part of module's public contract
- ✅ **Framework Alignment**: Works with Spring Modulith's module boundary philosophy
- ✅ **Future-Proofing**: Prevents module boundary violations as system evolves

#### 2. Hexagonal Architecture Compatibility
- ✅ **Driving Ports Remain Contracts**: UseCase interfaces still define business contracts
- ✅ **Dependency Direction Preserved**: Adapters → UseCases → Domain (unchanged)
- ✅ **Business Logic Protection**: Domain and application layers remain internal

#### 3. Architectural Clarity
- ✅ **Explicit API Definition**: Module root clearly shows what's publicly accessible
- ✅ **Intentional Design**: Forces conscious decisions about public vs internal contracts
- ✅ **Industry Alignment**: Many successful hexagonal implementations use this pattern

### Comparison with Alternative Solutions

#### Alternative 1: Named Interfaces
```java
// application/port/in/package-info.java
@NamedInterface("api") 
package org.springframework.samples.petclinic.owner.application.port.in;
```

**Rejected Rationale:**
- More complex configuration
- Requires Spring Modulith-specific annotations
- Less intuitive for developers
- Proposed solution is cleaner and more explicit

#### Alternative 2: Open Modules
```java
@ApplicationModule(type = Type.OPEN)
package example.owner;
```

**Rejected Rationale:**
- Intended for legacy applications transitioning to modular structure
- Defeats the purpose of module boundary enforcement
- Not recommended for fully modularized applications

## Implementation Details

### Package Structure Changes

**All Three Modules (Owner, User, Vet):**

```
Before:                          After:
owner/                          owner/
├── application/                ├── OwnerUseCase.java        ← MOVED HERE
│   ├── port/                   ├── PetUseCase.java          ← MOVED HERE  
│   │   └── in/                 ├── PetTypeUseCase.java      ← MOVED HERE
│   │       ├── OwnerUseCase    ├── VisitUseCase.java        ← MOVED HERE
│   │       ├── PetUseCase      ├── application/
│   │       ├── PetTypeUseCase  │   ├── OwnerUseCaseImpl.java
│   │       └── VisitUseCase    │   ├── PetUseCaseImpl.java
│   ├── OwnerUseCaseImpl.java   │   ├── PetTypeUseCaseImpl.java
│   ├── PetUseCaseImpl.java     │   └── VisitUseCaseImpl.java
│   ├── PetTypeUseCaseImpl.java └── domain/
│   └── VisitUseCaseImpl.java       └── ... (unchanged)
└── domain/
    └── ... (unchanged)
```

### Import Statement Updates

**Controllers** (minimal changes):
```java
// Before
import org.springframework.samples.petclinic.owner.application.port.in.OwnerUseCase;

// After  
import org.springframework.samples.petclinic.owner.OwnerUseCase;
```

**Use Case Implementations** (minimal changes):
```java
// Before
import org.springframework.samples.petclinic.owner.application.port.in.OwnerUseCase;

// After
import org.springframework.samples.petclinic.owner.OwnerUseCase;
```

### Architectural Test Updates

**HexagonalArchitectureTest.java:**
```java
// Before
void shouldHaveUseCasesInApplicationPortIn(String moduleName) {
    classes()
        .that().haveSimpleNameEndingWith("UseCase")
        .and().areInterfaces()
        .should().resideInAPackage("..application.port.in")
        .check(getModuleClasses(moduleName));
}

// After
void shouldHaveUseCasesInModuleRoot(String moduleName) {
    classes()
        .that().haveSimpleNameEndingWith("UseCase")
        .and().areInterfaces() 
        .should().resideInAPackage("org.springframework.samples.petclinic." + moduleName)
        .check(getModuleClasses(moduleName));
}
```

## Consequences

### Positive Consequences

- ✅ **Full Spring Modulith Compliance**: UseCase interfaces properly exposed as public API
- ✅ **Framework Future-Proofing**: Aligns with Spring Modulith evolution and best practices
- ✅ **Clearer Module Contracts**: Module root explicitly shows public interface
- ✅ **Maintained Architecture**: All hexagonal architecture benefits preserved
- ✅ **Simplified Imports**: Shorter import paths for UseCase interfaces

### Neutral Consequences

- 🔄 **Package Structure Change**: Physical reorganization without logic changes
- 🔄 **Import Updates**: All controllers and implementations need import changes
- 🔄 **Test Updates**: Architectural tests need pattern adjustments
- 🔄 **Same Functionality**: No change in business logic or external behavior

### Potential Concerns & Mitigations

#### Concern: "Mixing Interface Types at Module Root"
**Mitigation**: Clear naming conventions (`*UseCase.java`) make driving ports easily identifiable

#### Concern: "Deviating from Traditional Hexagonal Structure"  
**Mitigation**: Change is framework-adaptation, not architectural compromise. Core principles preserved.

#### Concern: "Breaking ADR-001"
**Mitigation**: This ADR amends ADR-001 with Spring Modulith-specific refinements while maintaining original principles

## Implementation Timeline

- **Phase 1** (Day 1): Create ADR-004 and refactoring plan documentation
- **Phase 2** (Day 1): Update architecture diagrams and developer guide
- **Phase 3** (Day 2): Implement UseCase interface relocation for all modules
- **Phase 4** (Day 2): Update architectural tests and verify compliance
- **Phase 5** (Day 2): Final verification and documentation updates

**Total Duration**: 2 days  
**Risk Level**: Low (package reorganization, no logic changes)

## Verification Criteria

### Success Metrics

1. **✅ Spring Modulith Compliance**: `./mvnw test -Dtest=ModuleVerificationsTest` passes
2. **✅ Architecture Tests**: All 21 architectural tests pass with updated patterns  
3. **✅ Business Logic Integrity**: Full test suite (194 tests) continues passing
4. **✅ Import Resolution**: All controllers and implementations compile successfully

### Rollback Plan

If issues arise:
1. **Git Reset**: Use version control to revert to pre-refactoring state
2. **Import Restoration**: Automated find/replace to restore original import statements
3. **Test Restoration**: Revert architectural test patterns to original structure

## References

- [Spring Modulith Fundamentals - Application Modules](https://docs.spring.io/spring-modulith/reference/fundamentals.html#application-modules)
- [ADR-001: Hexagonal Architecture Adoption](./ADR-001-hexagonal-architecture-adoption.md)
- [Hexagonal Architecture (Alistair Cockburn)](https://alistair.cockburn.us/hexagonal-architecture/)
- [Spring Modulith Module Boundaries Documentation](https://docs.spring.io/spring-modulith/reference/fundamentals.html#application-modules.advanced)

## Revision History

| Version | Date | Changes | Author |
|---------|------|---------|--------|
| 1.0 | 2025-11-10 | Initial UseCase interface relocation decision | Development Team |
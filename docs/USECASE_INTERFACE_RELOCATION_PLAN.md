# Spring Modulith UseCase Interface Relocation Plan

**Project:** Spring PetClinic REST API with Hexagonal Architecture  
**Change:** UseCase Interface Relocation for Spring Modulith Compliance  
**Date:** November 10, 2025  
**Status:** 📋 **READY FOR REVIEW**  
**ADR Reference:** ADR-004: UseCase Interface Relocation for Spring Modulith Compliance

---

## 📝 Executive Summary

This refactoring plan implements **ADR-004** by relocating all UseCase interfaces from `application.port.in` packages to module root packages. This ensures **full Spring Modulith compliance** while preserving all hexagonal architecture benefits.

### 🎯 Objectives

- **Primary:** Achieve Spring Modulith compliance by exposing UseCase interfaces as public module API
- **Secondary:** Maintain all hexagonal architecture principles and benefits  
- **Tertiary:** Simplify import paths and improve module API clarity

### ⚡ Impact Summary

- **🔧 Code Changes:** Package reorganization and import updates only
- **🧪 Business Logic:** Zero changes to business logic or functionality
- **📊 Test Coverage:** All 194 tests continue passing
- **🏗️ Architecture:** Enhanced compliance without principle compromise

---

## 🔍 Current State Analysis

### Current UseCase Interface Locations

```
owner/application/port/in/
├── OwnerUseCase.java
├── PetUseCase.java  
├── PetTypeUseCase.java
└── VisitUseCase.java

user/application/port/in/
└── UserUseCase.java

vet/application/port/in/
├── VetUseCase.java
└── SpecialtyUseCase.java
```

### Current Import Patterns (Examples)

**Controllers accessing UseCase interfaces:**
```java
// OwnerRestController.java
import org.springframework.samples.petclinic.owner.application.port.in.OwnerUseCase;

// UserRestController.java  
import org.springframework.samples.petclinic.user.application.port.in.UserUseCase;

// VetRestController.java
import org.springframework.samples.petclinic.vet.application.port.in.VetUseCase;
```

**Implementations accessing UseCase interfaces:**
```java
// OwnerUseCaseImpl.java
import org.springframework.samples.petclinic.owner.application.port.in.OwnerUseCase;
```

---

## 🎯 Target Architecture

### New UseCase Interface Locations

```
owner/                           ← MODULE ROOT (PUBLIC API)
├── OwnerUseCase.java           ← MOVED HERE
├── PetUseCase.java             ← MOVED HERE
├── PetTypeUseCase.java         ← MOVED HERE
├── VisitUseCase.java           ← MOVED HERE
├── application/                 ← INTERNAL IMPLEMENTATION
│   ├── OwnerUseCaseImpl.java
│   ├── PetUseCaseImpl.java
│   ├── PetTypeUseCaseImpl.java
│   └── VisitUseCaseImpl.java
└── domain/                      ← INTERNAL DOMAIN
    └── ... (unchanged)

user/                            ← MODULE ROOT (PUBLIC API) 
├── UserUseCase.java            ← MOVED HERE
├── application/                 ← INTERNAL IMPLEMENTATION
│   └── UserUseCaseImpl.java
└── domain/                      ← INTERNAL DOMAIN
    └── ... (unchanged)

vet/                             ← MODULE ROOT (PUBLIC API)
├── VetUseCase.java             ← MOVED HERE  
├── SpecialtyUseCase.java       ← MOVED HERE
├── application/                 ← INTERNAL IMPLEMENTATION
│   ├── VetUseCaseImpl.java
│   └── SpecialityUseCasesImpl.java
└── domain/                      ← INTERNAL DOMAIN
    └── ... (unchanged)
```

### New Import Patterns (Simplified)

**Controllers accessing UseCase interfaces:**
```java
// OwnerRestController.java
import org.springframework.samples.petclinic.owner.OwnerUseCase;

// UserRestController.java
import org.springframework.samples.petclinic.user.UserUseCase;

// VetRestController.java
import org.springframework.samples.petclinic.vet.VetUseCase;
```

### Spring Modulith Module API Surfaces

**Clear Public Contracts:**
```
owner (module) → PUBLIC API:
├── OwnerUseCase.java          ✅ Publicly accessible
├── PetUseCase.java            ✅ Publicly accessible
├── PetTypeUseCase.java        ✅ Publicly accessible
└── VisitUseCase.java          ✅ Publicly accessible

user (module) → PUBLIC API:
└── UserUseCase.java           ✅ Publicly accessible

vet (module) → PUBLIC API:  
├── VetUseCase.java            ✅ Publicly accessible
└── SpecialtyUseCase.java      ✅ Publicly accessible
```

---

## 📋 Detailed Implementation Plan

### Phase 1: Preparation and Validation (30 minutes)

#### 1.1 📊 Current State Documentation
- [x] ✅ Document current UseCase interface locations
- [x] ✅ Identify all import statements that need updating
- [x] ✅ Create backup of current state via Git commit

#### 1.2 🧪 Pre-Refactoring Validation
- [ ] 🎯 Run full test suite: `./mvnw test`
- [ ] 🎯 Verify Spring Modulith compliance: `./mvnw test -Dtest=ModuleVerificationsTest`
- [ ] 🎯 Verify architectural tests: `./mvnw test -Dtest=HexagonalArchitectureTest`

**Success Criteria:**
- All 194 tests passing ✅
- Spring Modulith verification successful ✅
- All 21 architectural tests passing ✅

### Phase 2: Owner Module Refactoring (45 minutes)

#### 2.1 📦 Move UseCase Interfaces to Module Root
**Files to Move:**
```bash
# From: owner/application/port/in/
# To:   owner/

src/main/java/org/springframework/samples/petclinic/owner/application/port/in/OwnerUseCase.java
→ src/main/java/org/springframework/samples/petclinic/owner/OwnerUseCase.java

src/main/java/org/springframework/samples/petclinic/owner/application/port/in/PetUseCase.java  
→ src/main/java/org/springframework/samples/petclinic/owner/PetUseCase.java

src/main/java/org/springframework/samples/petclinic/owner/application/port/in/PetTypeUseCase.java
→ src/main/java/org/springframework/samples/petclinic/owner/PetTypeUseCase.java

src/main/java/org/springframework/samples/petclinic/owner/application/port/in/VisitUseCase.java
→ src/main/java/org/springframework/samples/petclinic/owner/VisitUseCase.java
```

#### 2.2 📝 Update Package Declarations
**In each moved UseCase interface:**
```java
// Before
package org.springframework.samples.petclinic.owner.application.port.in;

// After  
package org.springframework.samples.petclinic.owner;
```

#### 2.3 🔗 Update Import Statements

**Controller Files to Update:**
- `owner/adapter/in/web/OwnerRestController.java`
- `owner/adapter/in/web/PetRestController.java`
- `owner/adapter/in/web/PetTypeRestController.java`
- `owner/adapter/in/web/VisitRestController.java`

**Implementation Files to Update:**
- `owner/application/OwnerUseCaseImpl.java`
- `owner/application/PetUseCaseImpl.java`
- `owner/application/PetTypeUseCaseImpl.java` 
- `owner/application/VisitUseCaseImpl.java`

**Import Changes (Example):**
```java
// Before
import org.springframework.samples.petclinic.owner.application.port.in.OwnerUseCase;

// After
import org.springframework.samples.petclinic.owner.OwnerUseCase;
```

#### 2.4 🧹 Cleanup Empty Directories
```bash
# Remove empty directories after moving files
owner/application/port/in/  ← Should be empty, remove
owner/application/port/     ← Should be empty, remove (if no other contents)
```

#### 2.5 ✅ Owner Module Verification
- [ ] 🎯 Compile: `./mvnw compile`
- [ ] 🎯 Test Owner module specific tests
- [ ] 🎯 Verify no compilation errors

### Phase 3: User Module Refactoring (15 minutes)

#### 3.1 📦 Move UseCase Interface
```bash
src/main/java/org/springframework/samples/petclinic/user/application/port/in/UserUseCase.java
→ src/main/java/org/springframework/samples/petclinic/user/UserUseCase.java
```

#### 3.2 📝 Update Package Declaration
```java
// Before
package org.springframework.samples.petclinic.user.application.port.in;

// After
package org.springframework.samples.petclinic.user;
```

#### 3.3 🔗 Update Import Statements

**Files to Update:**
- `user/adapter/in/web/UserRestController.java`
- `user/application/UserUseCasesImpl.java`

#### 3.4 🧹 Cleanup Empty Directories
#### 3.5 ✅ User Module Verification

### Phase 4: Vet Module Refactoring (30 minutes)

#### 4.1 📦 Move UseCase Interfaces
```bash
src/main/java/org/springframework/samples/petclinic/vet/application/port/in/VetUseCase.java
→ src/main/java/org/springframework/samples/petclinic/vet/VetUseCase.java

src/main/java/org/springframework/samples/petclinic/vet/application/port/in/SpecialtyUseCase.java
→ src/main/java/org/springframework/samples/petclinic/vet/SpecialtyUseCase.java
```

#### 4.2 📝 Update Package Declarations
#### 4.3 🔗 Update Import Statements

**Files to Update:**
- `vet/adapter/in/web/VetRestController.java`
- `vet/adapter/in/web/SpecialtyRestController.java`
- `vet/application/VetUseCaseImpl.java`
- `vet/application/SpecialityUseCasesImpl.java`

#### 4.4 🧹 Cleanup Empty Directories
#### 4.5 ✅ Vet Module Verification

### Phase 5: Architecture Tests Update (30 minutes)

#### 5.1 🧪 Update HexagonalArchitectureTest.java

**Current Test Method:**
```java
@ParameterizedTest
@MethodSource("businessModules") 
void shouldHaveUseCasesInApplicationPortIn(String moduleName) {
    classes()
        .that().haveSimpleNameEndingWith("UseCase")
        .and().areInterfaces()
        .should().resideInAPackage("..application.port.in")
        .check(getModuleClasses(moduleName));
}
```

**Updated Test Method:**
```java
@ParameterizedTest
@MethodSource("businessModules")
void shouldHaveUseCasesInModuleRoot(String moduleName) {
    classes()
        .that().haveSimpleNameEndingWith("UseCase") 
        .and().areInterfaces()
        .should().resideInAPackage("org.springframework.samples.petclinic." + moduleName)
        .check(getModuleClasses(moduleName));
}
```

#### 5.2 🏗️ Update Test Names and Documentation

**Update Method Names:**
- `shouldHaveUseCasesInApplicationPortIn` → `shouldHaveUseCasesInModuleRoot`

**Update Test Documentation:**
```java
/**
 * Driving ports (UseCase interfaces) should be located at module root
 * to ensure they are part of the public API surface for Spring Modulith compliance.
 */
```

#### 5.3 ✅ Architecture Test Verification
- [ ] 🎯 Run architectural tests: `./mvnw test -Dtest=HexagonalArchitectureTest`
- [ ] 🎯 Verify all 21 tests pass with new structure

### Phase 6: Final Validation and Documentation (45 minutes)

#### 6.1 🧪 Complete Test Suite Validation
- [ ] 🎯 Run full test suite: `./mvnw test`
- [ ] 🎯 Verify Spring Modulith compliance: `./mvnw test -Dtest=ModuleVerificationsTest`
- [ ] 🎯 Ensure all 194 tests still pass
- [ ] 🎯 Check for any compilation warnings or errors

#### 6.2 📄 Update Architecture Documentation

**Files to Update:**
- [ ] 🎯 Update `HEXAGONAL_ARCHITECTURE_UML.md` diagrams
- [ ] 🎯 Update `DEVELOPER_GUIDE.md` with new patterns
- [ ] 🎯 Update main `README.md` architecture section

#### 6.3 🏗️ Git Commit Strategy

**Commit 1: UseCase Interface Relocation**
```bash
git add .
git commit -m "refactor: relocate UseCase interfaces to module root for Spring Modulith compliance

- Move all UseCase interfaces from application.port.in to module root packages
- Update import statements in controllers and implementations  
- Ensures UseCase interfaces are part of public module API surface
- Maintains hexagonal architecture principles while improving framework compliance

Related: ADR-004"
```

**Commit 2: Architecture Test Updates**
```bash
git add .
git commit -m "test: update architectural tests for UseCase interface relocation

- Update HexagonalArchitectureTest to expect UseCase interfaces at module root
- Rename test method to reflect new location expectation
- All 21 architectural tests continue passing

Related: ADR-004"
```

**Commit 3: Documentation Updates**
```bash
git add .
git commit -m "docs: update architecture documentation for UseCase interface relocation

- Update UML diagrams to show UseCase interfaces at module root
- Update developer guide with new patterns and examples
- Refresh architecture section in main README

Related: ADR-004"
```

---

## 🎯 Success Criteria & Verification

### ✅ Technical Validation

1. **Compilation Success**: No compilation errors after refactoring
2. **Full Test Suite**: All 194 tests pass (`./mvnw test`)
3. **Spring Modulith Compliance**: Module verification passes (`./mvnw test -Dtest=ModuleVerificationsTest`)
4. **Architecture Compliance**: All 21 architectural tests pass (`./mvnw test -Dtest=HexagonalArchitectureTest`)
5. **No Regression**: Same functionality and behavior as before

### 📊 Architecture Validation

1. **UseCase Interface Accessibility**: UseCase interfaces accessible as public module API
2. **Dependency Direction**: Adapter → UseCase → Domain (preserved)
3. **Module Boundaries**: Clean separation maintained
4. **Import Simplification**: Shorter, cleaner import paths

### 📋 Documentation Validation

1. **ADR-004 Compliance**: Implementation matches ADR-004 specifications
2. **Architecture Diagrams**: Updated to reflect new structure
3. **Developer Guide**: Clear examples of new patterns
4. **README Accuracy**: Architecture section reflects current implementation

---

## 🚨 Risk Assessment & Mitigation

### 🔴 High Priority Risks

| Risk | Probability | Impact | Mitigation Strategy |
|------|------------|--------|-------------------|
| **Import Resolution Failures** | Low | High | Systematic IDE refactoring, comprehensive compilation checks |
| **Test Failures** | Low | High | Incremental testing after each module, rollback plan available |

### 🟡 Medium Priority Risks  

| Risk | Probability | Impact | Mitigation Strategy |
|------|------------|--------|-------------------|
| **Architectural Test Misalignment** | Medium | Medium | Update tests incrementally, verify patterns match expectations |
| **Documentation Drift** | Medium | Low | Update all documentation in same refactoring cycle |

### 🟢 Low Priority Risks

| Risk | Probability | Impact | Mitigation Strategy |
|------|------------|--------|-------------------|
| **Developer Confusion** | Low | Low | Clear commit messages, updated developer guide |
| **Merge Conflicts** | Low | Low | Coordinate with team, complete refactoring in focused timeframe |

### 🔄 Rollback Plan

**If Critical Issues Arise:**

1. **Immediate Rollback**: `git reset --hard HEAD~3` (revert all commits)
2. **Selective Rollback**: Cherry-pick specific commits to revert
3. **Import Restoration**: Use IDE's automated refactoring to restore original imports
4. **Test Verification**: Ensure rollback restores all functionality

---

## ⏱️ Timeline & Resource Allocation

### Phase Timeline (Total: 3 hours 15 minutes)

| Phase | Duration | Description |
|-------|----------|-------------|
| **Phase 1** | 30 min | Preparation and validation |
| **Phase 2** | 45 min | Owner module refactoring |
| **Phase 3** | 15 min | User module refactoring |
| **Phase 4** | 30 min | Vet module refactoring |
| **Phase 5** | 30 min | Architecture tests update |
| **Phase 6** | 45 min | Final validation and documentation |

### Resource Requirements

- **Developer Time**: 1 developer, 3.25 hours focused work
- **Review Time**: 30 minutes for plan review + approval
- **Testing Time**: Included in phases (incremental testing)
- **Documentation Time**: Included in Phase 6

---

## 🎉 Expected Benefits

### Immediate Benefits

- ✅ **Full Spring Modulith Compliance**: UseCase interfaces properly exposed
- ✅ **Cleaner Imports**: Shorter import paths (`owner.OwnerUseCase` vs `owner.application.port.in.OwnerUseCase`)
- ✅ **Explicit Module Contracts**: Module root clearly shows public API
- ✅ **Framework Future-Proofing**: Aligns with Spring Modulith evolution

### Long-term Benefits

- ✅ **Architectural Clarity**: Clear separation between public API and internal implementation
- ✅ **Team Productivity**: Easier to understand what's publicly accessible
- ✅ **Maintenance Simplicity**: Fewer nested packages to navigate
- ✅ **Industry Alignment**: Follows common patterns in modular architectures

---

## 📞 Communication Plan

### Team Notification

**Before Refactoring:**
- Send plan review request with 24-hour review window
- Schedule brief team sync if questions arise
- Ensure no conflicting development work

**During Refactoring:**
- Post progress updates in team chat
- Notify when each phase completes
- Share any unexpected findings or issues

**After Refactoring:**
- Demo the changes and benefits to team
- Update team wiki/documentation with new patterns
- Schedule knowledge sharing session on Spring Modulith compliance

---

## 🏁 Ready for Review & Approval

This comprehensive refactoring plan is **ready for review**. Upon approval, implementation can begin immediately following the phased approach outlined above.

**Approval Required From:**
- [ ] 🎯 Tech Lead / Architecture Team
- [ ] 🎯 Product Owner (for timeline coordination)
- [ ] 🎯 QA Team (for testing strategy validation)

**Next Steps After Approval:**
1. Execute Phase 1: Preparation and Validation
2. Begin systematic implementation following the detailed plan
3. Provide progress updates as outlined in communication plan

---

*This plan implements ADR-004 and ensures the Spring PetClinic application maintains full compliance with both hexagonal architecture principles and Spring Modulith framework requirements.*
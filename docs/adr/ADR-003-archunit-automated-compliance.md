# ADR-003: ArchUnit Automated Architectural Compliance

**Status:** ✅ Accepted  
**Date:** November 10, 2025  
**Authors:** Development Team  
**Reviewers:** Architecture Team

---

## Context and Problem Statement

As we implemented hexagonal architecture across multiple modules (User, Vet, Owner), we needed a way to:

1. **Prevent Architecture Drift**: Ensure developers follow hexagonal architecture rules
2. **Scale Verification**: Automatically verify compliance as new modules are added
3. **Provide Fast Feedback**: Catch violations early in the development cycle
4. **Document Architecture**: Make architectural rules explicit and executable
5. **Enforce Consistency**: Ensure all modules follow the same patterns

Manual code reviews alone are insufficient because:
- ❌ Time-consuming and error-prone
- ❌ Subjective interpretation of rules
- ❌ Don't scale with project growth
- ❌ Easy to miss violations during refactoring

## Decision

We have decided to implement **automated architectural testing using ArchUnit** with **dynamic module discovery through Spring Modulith integration**.

### Core Components

1. **HexagonalArchitectureTest.java**: Parameterized test class that verifies hexagonal architecture rules
2. **Dynamic Module Discovery**: Automatically finds business modules using Spring Modulith
3. **Comprehensive Rule Set**: Tests all critical hexagonal architecture principles
4. **CI Integration**: Tests run as part of standard test suite

## Rationale

### Why ArchUnit?

1. **Java Native**: Written in Java, integrates seamlessly with Java projects
2. **JUnit Integration**: Works with existing test infrastructure
3. **Expressive API**: Rules are readable and maintainable
4. **Rich Assertions**: Covers classes, packages, dependencies, annotations
5. **Mature Library**: Well-established with comprehensive documentation

### Why Dynamic Module Discovery?

1. **Zero Maintenance**: New modules automatically tested
2. **Spring Modulith Integration**: Leverages existing module configuration
3. **Automatic Exclusions**: Infrastructure modules filtered out automatically
4. **Scalability**: Grows with the project without manual updates

### Why Parameterized Tests?

1. **Individual Results**: Each module tested separately with clear results
2. **Parallel Execution**: Tests can run in parallel for speed
3. **Focused Failures**: Pinpoint exactly which module violates which rule
4. **Better Reporting**: Clear test names and failure messages

## Implementation Details

### Test Structure

```java
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class HexagonalArchitectureTest {

    /**
     * Dynamically discover business modules from Spring Modulith.
     * Excludes infrastructure modules automatically.
     */
    private static Stream<String> businessModules() {
        ApplicationModules modules = ApplicationModules.of(PetClinicApplication.class);
        return modules.stream()
            .map(module -> module.getName())
            .filter(name -> !infrastructureModules.contains(name))
            .sorted();
    }

    @ParameterizedTest
    @MethodSource("businessModules")
    void shouldHaveLayeredArchitecture(String moduleName) {
        // Test implementation
    }
}
```

### Architectural Rules Verified

#### 1. Layered Architecture
```java
void shouldHaveLayeredArchitecture(String moduleName) {
    layeredArchitecture()
        .consideringOnlyDependenciesInLayers()
        .layer("Controller").definedBy("..adapter.in.web..")
        .layer("UseCase").definedBy("..application..")
        .layer("Domain").definedBy("..domain..")
        
        .whereLayer("Controller").mayNotBeAccessedByAnyLayer()
        .whereLayer("UseCase").mayOnlyBeAccessedByLayers("Controller")
        .whereLayer("Domain").mayNotAccessAnyLayer()
        
        .check(getModuleClasses(moduleName));
}
```

#### 2. Use Case Location  
```java
void shouldHaveUseCasesInApplicationPortIn(String moduleName) {
    classes()
        .that().haveSimpleNameEndingWith("UseCase")
        .and().areInterfaces()
        .should().resideInAPackage("..application.port.in")
        .check(getModuleClasses(moduleName));
}
```

#### 3. Repository Location
```java
void shouldHaveRepositoriesInDomainPortOut(String moduleName) {
    classes()
        .that().haveSimpleNameEndingWith("Repository")
        .and().areInterfaces() 
        .and().resideOutsideOfPackage("..springdatajpa..")
        .should().resideInAPackage("..domain.port.out")
        .check(getModuleClasses(moduleName));
}
```

#### 4. Controller Location
```java
void shouldHaveControllersInAdapterInWeb(String moduleName) {
    classes()
        .that().haveSimpleNameEndingWith("Controller")
        .should().resideInAPackage("..adapter.in.web")
        .check(getModuleClasses(moduleName));
}
```

#### 5. Dependency Direction
```java
void shouldNotHaveDomainDependingOnAdapters(String moduleName) {
    noClasses()
        .that().resideInAPackage("..domain..")
        .should().dependOnClassesThat()
        .resideInAPackage("..adapter..")
        .check(getModuleClasses(moduleName));
}
```

#### 6. Port Interface Verification
```java
void shouldHavePortsAsInterfaces(String moduleName) {
    classes()
        .that().resideInAPackage("..port.out..")
        .and().resideOutsideOfPackage("..springdatajpa..")
        .should().beInterfaces()
        .check(getModuleClasses(moduleName));
}
```

#### 7. Adapter Independence
```java
void shouldNotHaveAdaptersDependingOnEachOther(String moduleName) {
    noClasses()
        .that().resideInAPackage("..adapter.in..")
        .should().dependOnClassesThat()
        .resideInAPackage("..adapter.out..")
        .check(getModuleClasses(moduleName));
}
```

### Module Discovery Configuration

```java
private static final Set<String> infrastructureModules = Set.of(
    "shared",    // Cross-cutting concerns
    "util",      // Utility classes  
    "rest",      // REST infrastructure
    "rest.api",  // Generated API interfaces
    "rest.dto",  // Generated DTOs
    "config"     // Configuration classes
);
```

### Test Execution Results

```bash
./mvnw test -Dtest=HexagonalArchitectureTest

Running org.springframework.samples.petclinic.architecture.HexagonalArchitectureTest

✅ shouldHaveLayeredArchitecture(String)[1] owner
✅ shouldHaveLayeredArchitecture(String)[2] user  
✅ shouldHaveLayeredArchitecture(String)[3] vet

✅ shouldHaveUseCasesInApplicationPortIn(String)[1] owner
✅ shouldHaveUseCasesInApplicationPortIn(String)[2] user
✅ shouldHaveUseCasesInApplicationPortIn(String)[3] vet

... (21 total tests - 7 rules × 3 modules)

Tests run: 21, Failures: 0, Errors: 0, Skipped: 0
```

## Benefits Achieved

### 1. Automated Compliance Verification
- **Before**: Manual code review (error-prone, time-consuming)
- **After**: Automated testing (fast, reliable, comprehensive)

### 2. Early Violation Detection  
- **Before**: Violations discovered during code review or production
- **After**: Violations caught immediately during development

### 3. Living Documentation
- **Before**: Architecture decisions in documents (often outdated)
- **After**: Architecture rules expressed as executable tests

### 4. Scalable Governance
- **Before**: Manual verification doesn't scale with team/project growth
- **After**: Automatic verification scales seamlessly

### 5. Consistent Pattern Enforcement
- **Before**: Subjective interpretation of architectural guidelines
- **After**: Objective, automated rule enforcement

## CI/CD Integration

### Maven Integration
```xml
<!-- pom.xml -->
<dependencies>
    <dependency>
        <groupId>com.tngtech.archunit</groupId>
        <artifactId>archunit-junit5</artifactId>
        <version>1.3.0</version>
        <scope>test</scope>
    </dependency>
</dependencies>
```

### Test Execution
```bash
# Local development
./mvnw test

# CI/CD pipeline  
./mvnw clean verify
```

### Failure Reporting
```
[ERROR] HexagonalArchitectureTest.shouldHaveUseCasesInApplicationPortIn(owner) 
Architecture Violation [Priority: MEDIUM] - Rule 'classes that have simple name ending with 'UseCase' and are interfaces should reside in a package '..application.port.in'' was violated (1 times):
Class <org.springframework.samples.petclinic.owner.OwnerUseCase> does not reside in a package '..application.port.in' in (OwnerUseCase.java:0)
```

## Test Performance

| Metric | Value | Benefit |
|--------|--------|---------|
| **Execution Time** | ~2 seconds | Fast feedback |
| **Total Tests** | 21 | Comprehensive coverage |
| **Module Coverage** | 3 business modules | Complete verification |
| **Rules Verified** | 7 architectural rules | Thorough validation |
| **Maintenance** | Zero | Self-updating |

## Future Extensions

### 1. Additional Rules
```java
void shouldNotHaveCircularDependencies(String moduleName) {
    slices()
        .matching("..%s.(**)")
        .should().beFreeOfCycles()
        .check(getModuleClasses(moduleName));
}

void shouldFollowNamingConventions(String moduleName) {
    classes()
        .that().areAnnotatedWith(Service.class)
        .should().haveSimpleNameEndingWith("ServiceImpl")
        .check(getModuleClasses(moduleName));
}
```

### 2. Custom Rules
```java
void shouldNotUseDatabaseAnnotationsInDomain(String moduleName) {
    noClasses()
        .that().resideInAPackage("..domain.model..")
        .should().beAnnotatedWith(Table.class)
        .orShould().beAnnotatedWith(Column.class)
        .check(getModuleClasses(moduleName));
}
```

### 3. Performance Rules
```java
void shouldNotHaveDeepInheritanceHierarchies(String moduleName) {
    classes()
        .should().notHaveFullyQualifiedNameMatching(".*")
        .andShould().haveMaximumInheritanceDepth(3)
        .check(getModuleClasses(moduleName));
}
```

## Team Guidelines

### For Developers

1. **Run Tests Locally**: Always run `./mvnw test` before committing
2. **Understand Failures**: Read ArchUnit error messages carefully
3. **Follow Patterns**: Use existing modules as reference
4. **Ask Questions**: Consult team when architectural decisions are unclear

### For Architects

1. **Review Rules Regularly**: Ensure rules still align with goals
2. **Add New Rules**: Extend testing as architecture evolves
3. **Update Documentation**: Keep ADRs current with rule changes
4. **Monitor Trends**: Track violation patterns across releases

## Validation Results

### Quantitative Metrics
- ✅ **21/21 tests passing** (100% compliance)
- ✅ **3 modules verified** (complete coverage)
- ✅ **7 architectural rules** enforced
- ✅ **0 manual verification** required

### Qualitative Benefits
- ✅ **Developer confidence** in architectural decisions
- ✅ **Faster code reviews** (automated pre-screening)
- ✅ **Consistent quality** across all modules
- ✅ **Educational value** (tests teach architecture)

## References

- [ArchUnit User Guide](https://www.archunit.org/userguide/html/000_Index.html)
- [JUnit 5 Parameterized Tests](https://junit.org/junit5/docs/current/user-guide/#writing-tests-parameterized-tests)
- [Spring Modulith Testing](https://docs.spring.io/spring-modulith/docs/current/reference/html/#testing)
- [Hexagonal Architecture Testing Strategies](https://alistair.cockburn.us/hexagonal-architecture/)

## Revision History

| Version | Date | Changes | Author |
|---------|------|---------|--------|
| 1.0 | 2025-11-10 | Initial automated testing implementation | Development Team |
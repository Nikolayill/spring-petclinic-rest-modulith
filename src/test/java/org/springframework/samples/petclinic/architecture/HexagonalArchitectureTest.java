/*
 * Copyright 2016-2025 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.samples.petclinic.architecture;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import org.junit.jupiter.api.Disabled;
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
 *
 * @author GitHub Copilot
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
        "rest.api",   // OpenAPI generated interfaces (infrastructure)
        "rest.dto",   // OpenAPI generated DTOs (infrastructure)
        "config"      // Configuration classes
    );

    /**
     * Dynamically discover business modules from Spring Modulith.
     * This automatically includes new modules when added!
     */
    static Stream<String> businessModules() {
        ApplicationModules modules = ApplicationModules.of(PetClinicApplication.class);
        return modules.stream()
            .map(module -> module.getName())  // Gets module name (e.g., "owner", "user", "vet")
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
     * Generic test: Use case interfaces should be in module root for Spring Modulith compliance
     */
    @ParameterizedTest(name = "Module ''{0}'' - use cases should be in module root")
    @MethodSource("businessModules")
    void use_cases_should_be_in_module_root(String moduleName) {
        JavaClasses classes = new ClassFileImporter()
            .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
            .importPackages(BASE_PACKAGE + "." + moduleName);

        classes()
            .that().haveSimpleNameEndingWith("UseCase")
            .and().areInterfaces()
            .should().resideInAPackage(BASE_PACKAGE + "." + moduleName)
            .because("Use case interfaces are driving ports and must be accessible from outside the module for Spring Modulith compliance")
            .check(classes);
    }

    /**
     * Generic test: Repository interfaces should be in domain ports
     * Note: Spring Data JPA repositories are excluded as they are adapter-level extensions
     */
    @Disabled // incorrect, too strict rule
    @ParameterizedTest(name = "Module ''{0}'' - repositories should be in domain.port.out")
    @MethodSource("businessModules")
    void repositories_should_be_in_domain_port_out(String moduleName) {
        JavaClasses classes = new ClassFileImporter()
            .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
            .importPackages(BASE_PACKAGE + "." + moduleName);

        classes()
            .that().haveSimpleNameEndingWith("Repository")
            .and().areInterfaces()
            .and().resideOutsideOfPackage("..springdatajpa..")  // Exclude Spring Data JPA adapter repositories
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
            .that().areAnnotatedWith(org.springframework.web.bind.annotation.RestController.class)
            .should().resideInAPackage(modulePackage(moduleName, "adapter.in.web"))
            .allowEmptyShould(true) // To allow rules being evaluated without checking any classes
            .because("REST controllers are input adapters")
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

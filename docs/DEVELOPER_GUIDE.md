# Hexagonal Architecture Developer Guide

## 🎯 Overview

This guide provides step-by-step instructions for developing within the hexagonal architecture (ports and adapters pattern) implemented in this Spring Boot application. Follow this guide to maintain architectural consistency and ensure compliance with automated testing.

## 📚 Table of Contents

1. [Architecture Quick Reference](#architecture-quick-reference)
2. [Adding a New Business Module](#adding-a-new-business-module)
3. [Adding Use Cases to Existing Modules](#adding-use-cases-to-existing-modules)
4. [Adding New Adapters](#adding-new-adapters)
5. [Working with Spring Data JPA](#working-with-spring-data-jpa)
6. [Testing Your Changes](#testing-your-changes)
7. [Common Patterns and Examples](#common-patterns-and-examples)
8. [Troubleshooting](#troubleshooting)

---

## Architecture Quick Reference

### Module Structure
```
src/main/java/org/springframework/samples/petclinic/{module}/
├── {ModuleName}UseCase.java   # Use case interfaces (PUBLIC API at module root)
├── adapter/
│   ├── in/
│   │   └── web/           # REST controllers, command handlers
│   └── out/
│       ├── h2/           # H2-specific adapters
│       ├── hsqldb/       # HSQLDB-specific adapters  
│       ├── mysql/        # MySQL-specific adapters
│       ├── postgres/     # PostgreSQL-specific adapters
│       └── springdatajpa/ # Spring Data JPA repositories
├── application/
│   └── {ModuleName}UseCaseImpl.java  # Use case implementations
└── domain/
    ├── model/            # Domain entities and value objects
    └── port/
        └── out/          # Repository interfaces (driven ports)
```

**📌 Important**: UseCase interfaces are located at module root for Spring Modulith compliance (see ADR-004)

### Dependency Rules
- ✅ **Domain** → No dependencies (pure business logic)
- ✅ **Application** → Domain only
- ✅ **Adapters** → Application and Domain
- ❌ **Domain** should NOT depend on Application or Adapters
- ❌ **Application** should NOT depend on Adapters

---

## Adding a New Business Module

### Step 1: Create the Module Structure

```bash
# Example: Creating a "booking" module
mkdir -p src/main/java/org/springframework/samples/petclinic/booking/adapter/in/web
mkdir -p src/main/java/org/springframework/samples/petclinic/booking/adapter/out/springdatajpa
mkdir -p src/main/java/org/springframework/samples/petclinic/booking/application/port/in
mkdir -p src/main/java/org/springframework/samples/petclinic/booking/application/port/out
mkdir -p src/main/java/org/springframework/samples/petclinic/booking/application/service
mkdir -p src/main/java/org/springframework/samples/petclinic/booking/domain/model
mkdir -p src/main/java/org/springframework/samples/petclinic/booking/domain/port/out
```

### Step 2: Create the Domain Model

**File**: `src/main/java/org/springframework/samples/petclinic/booking/domain/model/Booking.java`
```java
package org.springframework.samples.petclinic.booking.domain.model;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Domain entity representing a booking in the business domain.
 * Contains only business logic, no persistence or framework concerns.
 */
public class Booking {
    private Long id;
    private Long petId;
    private Long vetId;
    private LocalDateTime bookingTime;
    private String purpose;
    private BookingStatus status;
    
    public Booking(Long petId, Long vetId, LocalDateTime bookingTime, String purpose) {
        this.petId = Objects.requireNonNull(petId, "Pet ID cannot be null");
        this.vetId = Objects.requireNonNull(vetId, "Vet ID cannot be null");
        this.bookingTime = Objects.requireNonNull(bookingTime, "Booking time cannot be null");
        this.purpose = Objects.requireNonNull(purpose, "Purpose cannot be null");
        this.status = BookingStatus.PENDING;
    }
    
    public void confirm() {
        if (this.status != BookingStatus.PENDING) {
            throw new IllegalStateException("Only pending bookings can be confirmed");
        }
        this.status = BookingStatus.CONFIRMED;
    }
    
    public void cancel() {
        if (this.status == BookingStatus.COMPLETED) {
            throw new IllegalStateException("Completed bookings cannot be cancelled");
        }
        this.status = BookingStatus.CANCELLED;
    }
    
    public boolean isInPast() {
        return bookingTime.isBefore(LocalDateTime.now());
    }
    
    // Getters and setters...
}
```

**File**: `src/main/java/org/springframework/samples/petclinic/booking/domain/model/BookingStatus.java`
```java
package org.springframework.samples.petclinic.booking.domain.model;

public enum BookingStatus {
    PENDING,
    CONFIRMED, 
    COMPLETED,
    CANCELLED
}
```

### Step 3: Define Domain Ports

**File**: `src/main/java/org/springframework/samples/petclinic/booking/domain/port/out/BookingRepository.java`
```java
package org.springframework.samples.petclinic.booking.domain.port.out;

import org.springframework.samples.petclinic.booking.domain.model.Booking;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Domain repository interface defining how the booking aggregate
 * can be persisted and retrieved. This belongs to the domain layer.
 */
public interface BookingRepository {
    
    Booking save(Booking booking);
    
    Optional<Booking> findById(Long id);
    
    List<Booking> findByPetId(Long petId);
    
    List<Booking> findByVetId(Long vetId);
    
    List<Booking> findBookingsBetween(LocalDateTime start, LocalDateTime end);
    
    void deleteById(Long id);
    
    boolean existsConflictingBooking(Long vetId, LocalDateTime startTime, LocalDateTime endTime);
}
```

### Step 4: Create Use Case Interfaces

**📌 Important**: UseCase interfaces are located at module root for Spring Modulith compliance

**File**: `src/main/java/org/springframework/samples/petclinic/booking/CreateBookingUseCase.java`
```java
package org.springframework.samples.petclinic.booking;

import org.springframework.samples.petclinic.booking.domain.model.Booking;
import java.time.LocalDateTime;

/**
 * Use case interface for creating a new booking.
 * This is a driving port (primary port).
 */
public interface CreateBookingUseCase {
    
    Booking createBooking(CreateBookingCommand command);
    
    record CreateBookingCommand(
        Long petId,
        Long vetId,
        LocalDateTime bookingTime,
        String purpose
    ) {}
}
```

**File**: `src/main/java/org/springframework/samples/petclinic/booking/ManageBookingUseCase.java`
```java
package org.springframework.samples.petclinic.booking;

import org.springframework.samples.petclinic.booking.domain.model.Booking;

/**
 * Use case interface for managing existing bookings.
 */
public interface ManageBookingUseCase {
    
    Booking confirmBooking(Long bookingId);
    
    void cancelBooking(Long bookingId);
    
    Booking findById(Long bookingId);
}
```

### Step 5: Implement Use Cases

**File**: `src/main/java/org/springframework/samples/petclinic/booking/application/BookingService.java`
```java
package org.springframework.samples.petclinic.booking.application;

import org.springframework.samples.petclinic.booking.CreateBookingUseCase;
import org.springframework.samples.petclinic.booking.ManageBookingUseCase;
import org.springframework.samples.petclinic.booking.domain.model.Booking;
import org.springframework.samples.petclinic.booking.domain.port.out.BookingRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * Application service implementing booking use cases.
 * This is where business workflows and validation logic reside.
 */
@Service
@Transactional
public class BookingService implements CreateBookingUseCase, ManageBookingUseCase {
    
    private final BookingRepository bookingRepository;
    
    public BookingService(BookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;
    }
    
    @Override
    public Booking createBooking(CreateBookingCommand command) {
        // Business rule: Cannot book in the past
        if (command.bookingTime().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Cannot book appointments in the past");
        }
        
        // Business rule: Check for conflicting bookings
        LocalDateTime startTime = command.bookingTime();
        LocalDateTime endTime = command.bookingTime().plusHours(1); // Assume 1-hour slots
        
        if (bookingRepository.existsConflictingBooking(command.vetId(), startTime, endTime)) {
            throw new IllegalArgumentException("Vet is not available at the requested time");
        }
        
        Booking booking = new Booking(
            command.petId(),
            command.vetId(), 
            command.bookingTime(),
            command.purpose()
        );
        
        return bookingRepository.save(booking);
    }
    
    @Override
    public Booking confirmBooking(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
            .orElseThrow(() -> new IllegalArgumentException("Booking not found: " + bookingId));
            
        booking.confirm();
        return bookingRepository.save(booking);
    }
    
    @Override
    public void cancelBooking(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
            .orElseThrow(() -> new IllegalArgumentException("Booking not found: " + bookingId));
            
        booking.cancel();
        bookingRepository.save(booking);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Booking findById(Long bookingId) {
        return bookingRepository.findById(bookingId)
            .orElseThrow(() -> new IllegalArgumentException("Booking not found: " + bookingId));
    }
}
```

### Step 6: Create Application Ports

**File**: `src/main/java/org/springframework/samples/petclinic/booking/application/port/out/BookingRepositoryPort.java`
```java
package org.springframework.samples.petclinic.booking.application.port.out;

import org.springframework.samples.petclinic.booking.domain.port.out.BookingRepository;

/**
 * Application-level repository port.
 * In this architecture, this extends the domain repository interface.
 */
public interface BookingRepositoryPort extends BookingRepository {
    // Additional application-specific methods can be added here if needed
}
```

### Step 7: Implement Adapters

**File**: `src/main/java/org/springframework/samples/petclinic/booking/adapter/out/springdatajpa/BookingRepositoryAdapter.java`
```java
package org.springframework.samples.petclinic.booking.adapter.out.springdatajpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.samples.petclinic.booking.domain.model.Booking;
import org.springframework.samples.petclinic.booking.domain.port.out.BookingRepository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Spring Data JPA adapter implementing the domain repository interface.
 * This adapter belongs to the infrastructure layer.
 */
public interface BookingRepositoryAdapter extends JpaRepository<Booking, Long>, BookingRepository {
    
    List<Booking> findByPetId(Long petId);
    
    List<Booking> findByVetId(Long vetId);
    
    @Query("SELECT b FROM Booking b WHERE b.bookingTime BETWEEN :start AND :end")
    List<Booking> findBookingsBetween(@Param("start") LocalDateTime start, 
                                     @Param("end") LocalDateTime end);
    
    @Query("SELECT COUNT(b) > 0 FROM Booking b WHERE b.vetId = :vetId " +
           "AND b.status IN ('PENDING', 'CONFIRMED') " +
           "AND ((b.bookingTime <= :start AND :start < b.bookingTime + INTERVAL '1' HOUR) " +
           "OR (:start <= b.bookingTime AND b.bookingTime < :end))")
    boolean existsConflictingBooking(@Param("vetId") Long vetId,
                                   @Param("start") LocalDateTime startTime,
                                   @Param("end") LocalDateTime endTime);
}
```

### Step 8: Create REST Controller

**File**: `src/main/java/org/springframework/samples/petclinic/booking/adapter/in/web/BookingController.java`
```java
package org.springframework.samples.petclinic.booking.adapter.in.web;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.samples.petclinic.booking.CreateBookingUseCase;
import org.springframework.samples.petclinic.booking.ManageBookingUseCase;
import org.springframework.samples.petclinic.booking.domain.model.Booking;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

/**
 * REST controller for booking endpoints.
 * This is an adapter that translates HTTP requests to use case calls.
 */
@RestController
@RequestMapping("/api/bookings")
public class BookingController {
    
    private final CreateBookingUseCase createBookingUseCase;
    private final ManageBookingUseCase manageBookingUseCase;
    
    public BookingController(CreateBookingUseCase createBookingUseCase,
                           ManageBookingUseCase manageBookingUseCase) {
        this.createBookingUseCase = createBookingUseCase;
        this.manageBookingUseCase = manageBookingUseCase;
    }
    
    @PostMapping
    public ResponseEntity<Booking> createBooking(@RequestBody CreateBookingRequest request) {
        var command = new CreateBookingUseCase.CreateBookingCommand(
            request.petId(),
            request.vetId(),
            request.bookingTime(),
            request.purpose()
        );
        
        Booking booking = createBookingUseCase.createBooking(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(booking);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Booking> getBooking(@PathVariable Long id) {
        Booking booking = manageBookingUseCase.findById(id);
        return ResponseEntity.ok(booking);
    }
    
    @PutMapping("/{id}/confirm")
    public ResponseEntity<Booking> confirmBooking(@PathVariable Long id) {
        Booking booking = manageBookingUseCase.confirmBooking(id);
        return ResponseEntity.ok(booking);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelBooking(@PathVariable Long id) {
        manageBookingUseCase.cancelBooking(id);
        return ResponseEntity.noContent().build();
    }
    
    record CreateBookingRequest(
        Long petId,
        Long vetId,
        LocalDateTime bookingTime,
        String purpose
    ) {}
}
```

### Step 9: Update Spring Modulith Configuration

**File**: `src/main/java/org/springframework/samples/petclinic/booking/package-info.java`
```java
/**
 * Booking module following hexagonal architecture.
 * 
 * This module handles all booking-related functionality including:
 * - Creating and managing bookings
 * - Validating booking conflicts
 * - Booking status management
 * 
 * @author Development Team
 */
package org.springframework.samples.petclinic.booking;
```

### Step 10: Add JPA Configuration

**File**: `src/main/java/org/springframework/samples/petclinic/booking/domain/model/Booking.java` (Add JPA annotations)
```java
package org.springframework.samples.petclinic.booking.domain.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Domain entity representing a booking.
 * JPA annotations are used for persistence mapping.
 */
@Entity
@Table(name = "bookings")
public class Booking {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "pet_id", nullable = false)
    private Long petId;
    
    @Column(name = "vet_id", nullable = false)
    private Long vetId;
    
    @Column(name = "booking_time", nullable = false)
    private LocalDateTime bookingTime;
    
    @Column(nullable = false)
    private String purpose;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BookingStatus status;
    
    // Default constructor for JPA
    protected Booking() {}
    
    // Domain constructor and methods...
}
```

---

## Adding Use Cases to Existing Modules

### Step 1: Define the Use Case Interface

**Example**: Adding a search capability to the Owner module

**File**: `src/main/java/org/springframework/samples/petclinic/owner/SearchOwnerUseCase.java`
```java
package org.springframework.samples.petclinic.owner;

import org.springframework.samples.petclinic.owner.domain.model.Owner;
import java.util.List;

public interface SearchOwnerUseCase {
    
    List<Owner> searchOwnersByName(String namePattern);
    
    List<Owner> findOwnersByCity(String city);
}
```

### Step 2: Extend Domain Repository (if needed)

**File**: `src/main/java/org/springframework/samples/petclinic/owner/domain/port/out/OwnerRepository.java` (add methods)
```java
// Add to existing interface:
List<Owner> findByLastNameContainingIgnoreCase(String namePattern);
List<Owner> findByAddressCityIgnoreCase(String city);
```

### Step 3: Update Service Implementation

**File**: `src/main/java/org/springframework/samples/petclinic/owner/application/service/OwnerService.java` (implement new interface)
```java
@Service
public class OwnerService implements CreateOwnerUseCase, UpdateOwnerUseCase, SearchOwnerUseCase {
    
    // Existing code...
    
    @Override
    @Transactional(readOnly = true)
    public List<Owner> searchOwnersByName(String namePattern) {
        if (namePattern == null || namePattern.trim().isEmpty()) {
            throw new IllegalArgumentException("Search pattern cannot be empty");
        }
        return ownerRepository.findByLastNameContainingIgnoreCase(namePattern.trim());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Owner> findOwnersByCity(String city) {
        if (city == null || city.trim().isEmpty()) {
            throw new IllegalArgumentException("City cannot be empty");
        }
        return ownerRepository.findByAddressCityIgnoreCase(city.trim());
    }
}
```

### Step 4: Update Controller

**File**: `src/main/java/org/springframework/samples/petclinic/owner/adapter/in/web/OwnerController.java` (add endpoints)
```java
@RestController
public class OwnerController {
    
    private final SearchOwnerUseCase searchOwnerUseCase;
    
    // Existing code...
    
    @GetMapping("/api/owners/search")
    public ResponseEntity<List<Owner>> searchOwners(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String city) {
        
        if (name != null) {
            return ResponseEntity.ok(searchOwnerUseCase.searchOwnersByName(name));
        } else if (city != null) {
            return ResponseEntity.ok(searchOwnerUseCase.findOwnersByCity(city));
        } else {
            return ResponseEntity.badRequest().build();
        }
    }
}
```

---

## Adding New Adapters

### Database Adapter Example

When adding a new database adapter (e.g., for MongoDB):

**File**: `src/main/java/org/springframework/samples/petclinic/owner/adapter/out/mongodb/OwnerMongoRepositoryAdapter.java`
```java
package org.springframework.samples.petclinic.owner.adapter.out.mongodb;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.samples.petclinic.owner.domain.model.Owner;
import org.springframework.samples.petclinic.owner.domain.port.out.OwnerRepository;
import java.util.List;

/**
 * MongoDB adapter implementing the domain repository interface.
 * This demonstrates how different persistence technologies can be plugged in.
 */
public interface OwnerMongoRepositoryAdapter extends MongoRepository<Owner, String>, OwnerRepository {
    
    List<Owner> findByLastNameContainingIgnoreCase(String namePattern);
    
    // Additional MongoDB-specific query methods can be added here
}
```

### External Service Adapter Example

**File**: `src/main/java/org/springframework/samples/petclinic/owner/adapter/out/email/EmailNotificationAdapter.java`
```java
package org.springframework.samples.petclinic.owner.adapter.out.email;

import org.springframework.samples.petclinic.owner.domain.port.out.NotificationService;
import org.springframework.stereotype.Component;

/**
 * Email adapter implementing the notification service port.
 */
@Component
public class EmailNotificationAdapter implements NotificationService {
    
    @Override
    public void sendWelcomeMessage(String ownerEmail, String ownerName) {
        // Implement email sending logic
        System.out.printf("Sending welcome email to %s (%s)%n", ownerName, ownerEmail);
    }
    
    @Override
    public void sendAppointmentReminder(String ownerEmail, String appointmentDetails) {
        // Implement appointment reminder logic
        System.out.printf("Sending appointment reminder to %s: %s%n", ownerEmail, appointmentDetails);
    }
}
```

---

## Working with Spring Data JPA

### Classification Guidelines

According to ADR-002, Spring Data JPA repositories are classified as **adapter-level interfaces** because they:
- Extend framework-specific interfaces (`JpaRepository`, `CrudRepository`)
- Include framework-specific annotations (`@Query`, `@Modifying`)
- Are automatically implemented by the Spring Data framework

### Repository Pattern

```java
// ✅ CORRECT: Domain repository (pure interface)
package org.springframework.samples.petclinic.owner.domain.port.out;
public interface OwnerRepository {
    Owner save(Owner owner);
    Optional<Owner> findById(Long id);
}

// ✅ CORRECT: Adapter repository (extends Spring Data)
package org.springframework.samples.petclinic.owner.adapter.out.springdatajpa;
public interface OwnerRepositoryAdapter extends JpaRepository<Owner, Long>, OwnerRepository {
    @Query("SELECT o FROM Owner o WHERE o.lastName LIKE %:name%")
    List<Owner> findByLastNameContaining(@Param("name") String name);
}
```

### Entity Configuration

```java
@Entity
@Table(name = "owners")
public class Owner {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    // Always use explicit column mapping
    @Column(name = "first_name", nullable = false)
    private String firstName;
    
    @Column(name = "last_name", nullable = false)  
    private String lastName;
    
    @Column(name = "address")
    private String address;
    
    @Column(name = "city")
    private String city;
    
    @Column(name = "telephone")
    private String telephone;
    
    // Domain logic methods...
}
```

---

## Testing Your Changes

### Run Architectural Tests

```bash
# Run the hexagonal architecture compliance tests
./mvnw test -Dtest=HexagonalArchitectureTest
```

Expected output for a new module:
```
[INFO] Running org.springframework.samples.petclinic.architecture.HexagonalArchitectureTest

✅ shouldHaveLayeredArchitecture(String)[1] booking
✅ shouldHaveLayeredArchitecture(String)[2] owner
✅ shouldHaveLayeredArchitecture(String)[3] user
✅ shouldHaveLayeredArchitecture(String)[4] vet

... (additional tests for all 4 modules)

Tests run: 28, Failures: 0, Errors: 0, Skipped: 0
```

### Run Spring Modulith Tests

```bash
./mvnw test -Dtest=ModuleVerificationsTest
```

### Run Full Test Suite

```bash
./mvnw test
```

---

## Common Patterns and Examples

### Domain Event Pattern

**File**: `src/main/java/org/springframework/samples/petclinic/booking/domain/model/BookingEvent.java`
```java
package org.springframework.samples.petclinic.booking.domain.model;

import java.time.LocalDateTime;

public record BookingEvent(
    Long bookingId,
    String eventType,
    LocalDateTime occurredAt
) {
    public static BookingEvent created(Long bookingId) {
        return new BookingEvent(bookingId, "BOOKING_CREATED", LocalDateTime.now());
    }
    
    public static BookingEvent confirmed(Long bookingId) {
        return new BookingEvent(bookingId, "BOOKING_CONFIRMED", LocalDateTime.now());
    }
}
```

### Value Object Pattern

**File**: `src/main/java/org/springframework/samples/petclinic/owner/domain/model/Address.java`
```java
package org.springframework.samples.petclinic.owner.domain.model;

import jakarta.persistence.Embeddable;
import jakarta.persistence.Column;
import java.util.Objects;

@Embeddable
public class Address {
    
    @Column(name = "street")
    private String street;
    
    @Column(name = "city")
    private String city;
    
    @Column(name = "state")
    private String state;
    
    @Column(name = "postal_code")
    private String postalCode;
    
    protected Address() {} // JPA constructor
    
    public Address(String street, String city, String state, String postalCode) {
        this.street = requireNonBlank(street, "Street cannot be blank");
        this.city = requireNonBlank(city, "City cannot be blank");
        this.state = state; // Optional
        this.postalCode = postalCode; // Optional
    }
    
    private String requireNonBlank(String value, String message) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(message);
        }
        return value.trim();
    }
    
    // Getters and equals/hashCode...
}
```

### Query Object Pattern

**File**: `src/main/java/org/springframework/samples/petclinic/booking/BookingQuery.java`
```java
package org.springframework.samples.petclinic.booking;

import java.time.LocalDateTime;

public record BookingQuery(
    Long petId,
    Long vetId,
    LocalDateTime startDate,
    LocalDateTime endDate,
    String status
) {
    public static BookingQuery forPet(Long petId) {
        return new BookingQuery(petId, null, null, null, null);
    }
    
    public static BookingQuery forVet(Long vetId) {
        return new BookingQuery(null, vetId, null, null, null);
    }
    
    public static BookingQuery forDateRange(LocalDateTime start, LocalDateTime end) {
        return new BookingQuery(null, null, start, end, null);
    }
}
```

---

## Troubleshooting

### Common ArchUnit Violations

#### ❌ Domain Depending on Adapters
```
Class <org.springframework.samples.petclinic.booking.domain.model.Booking> 
does depend on class <org.springframework.samples.petclinic.booking.adapter.out.springdatajpa.BookingRepositoryAdapter>
```

**Solution**: Remove direct adapter dependencies from domain classes.

#### ❌ Use Case in Wrong Package
```
Class <org.springframework.samples.petclinic.booking.CreateBookingUseCase> 
does not reside at module root
```

**Solution**: Move use case interfaces to module root package (e.g., `org.springframework.samples.petclinic.booking`) for Spring Modulith compliance.

#### ❌ Repository in Wrong Package
```
Class <org.springframework.samples.petclinic.booking.BookingRepository> 
does not reside in a package '..domain.port.out'  
```

**Solution**: Move domain repository interfaces to `domain.port.out` package.

### Spring Modulith Issues

#### ❌ Cyclic Dependencies
```
Module 'booking' depends on 'owner' which depends on 'booking'
```

**Solution**: Introduce events or shared interfaces to break cycles.

#### ❌ Module Not Discovered
```
No modules found for testing
```

**Solution**: Ensure `package-info.java` exists and module has proper package structure.

### Debugging Tips

1. **Use Module Boundary Visualization**:
   ```java
   @Test
   void writeModuleDocumentation() {
       ApplicationModules modules = ApplicationModules.of(PetClinicApplication.class);
       modules.forEach(System.out::println);
   }
   ```

2. **Check Package Structure**:
   ```bash
   find src/main/java -type d | grep -E "(domain|application|adapter)" | sort
   ```

3. **Verify Test Discovery**:
   ```bash
   ./mvnw test -Dtest=HexagonalArchitectureTest -X | grep "businessModules"
   ```

---

## Best Practices Summary

### ✅ DO
- Keep domain logic free of framework dependencies
- Use record classes for immutable data transfer objects
- Implement business rules in domain entities and services
- Write use case interfaces first, then implementations
- Use meaningful names that reflect business concepts
- Add comprehensive JavaDoc to public interfaces

### ❌ DON'T
- Mix business logic with persistence concerns
- Use Spring annotations in domain classes (except JPA for entities)
- Create direct dependencies between adapters
- Skip architectural tests when adding new modules
- Ignore Spring Modulith module boundary violations
- Create anemic domain models (getters/setters only)

---

## Further Reading

- [ADR-001: Hexagonal Architecture Adoption](docs/adr/ADR-001-hexagonal-architecture-adoption.md)
- [ADR-002: Spring Data JPA Pattern](docs/adr/ADR-002-spring-data-jpa-pattern.md)
- [ADR-003: ArchUnit Automated Compliance](docs/adr/ADR-003-archunit-automated-compliance.md)
- [Spring Modulith Documentation](https://docs.spring.io/spring-modulith/docs/current/reference/html/)
- [ArchUnit User Guide](https://www.archunit.org/userguide/html/000_Index.html)
- [Hexagonal Architecture: Alistair Cockburn](https://alistair.cockburn.us/hexagonal-architecture/)
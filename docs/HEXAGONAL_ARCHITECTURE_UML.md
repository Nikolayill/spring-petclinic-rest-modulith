# Hexagonal Architecture Module Organization

This document shows the essential structure and organization of hexagonal architecture modules.

## 📐 Hexagonal Architecture - Module Organization

```plantuml
@startuml
!theme plain
skinparam class {
    BackgroundColor<<domain>> lightblue
    BackgroundColor<<usecase>> lightyellow
    BackgroundColor<<service>> lightgreen
    BackgroundColor<<adapter>> lightcoral
    BorderColor black
}

package "module" <<module>> {
    package "domain" <<domain>> {
        package "model" {
            class Entity <<domain>>
            class ValueObject <<domain>>
        }

        package "port.out"  {
            interface Repository <<port>>
            interface Subsystem <<port>>
        }
    }
    package "application" { 
        package "port.in" <<usecase>> {
            interface UseCase <<usecase>>
        }

        package "service" <<service>> {
            class Service <<service>>
        }
    }

    package "adapter" <<adapter>> {
        package "in" {
            package "web" {
                class Controller <<adapter>>
            }
        }

        package "out" {
            package "springdata" {
                class JpaRepository <<adapter>>
            }
            package "feign" {
                class ExternalClient <<adapter>>
            }
        }
    }
}

' Dependencies (all point inward to domain)
Controller --> UseCase
Service ..|> UseCase
Service --> Repository
Service --> Subsystem
Service --> Entity
Repository --> Entity
JpaRepository ..|> Repository
JpaRepository --> Entity
ExternalClient ..|> Subsystem

@enduml
```

## 🎯 Dependency Rules

```plantuml
@startuml
!theme plain

package "Primary Adapters" #lightcoral {
    class "Driving Adapter\n(Controller, CLI)" as PrimaryAdapter
}

package "Application Layer" #lightyellow {
    interface "Driving Port\n(Use Case)" as InPort
    class "Application Service" as Service
    interface "Driven Port\n(Repository)" as OutPort
}

package "Secondary Adapters" #lightgray {
    class "Driven Adapter\n(JPA, JDBC)" as SecondaryAdapter
}

package "Domain Layer" #lightblue {
    class "Domain Entity" as Entity
}

PrimaryAdapter --> InPort : implements/calls
Service ..|> InPort : implements
Service --> OutPort : uses
Service --> Entity : uses
OutPort --> Entity : depends on
SecondaryAdapter ..|> OutPort : implements

note bottom of Entity
✅ No dependencies
Pure business logic
end note

note bottom of Service  
✅ Depends only on Domain + Driven Ports
Orchestrates business workflows
end note

note left of PrimaryAdapter
✅ Depends on Driving Ports
Handles external requests
end note

note right of SecondaryAdapter
✅ Implements Driven Ports
Infrastructure concerns
end note

@enduml
```

---

**Key Principles:**
- **🔵 Domain** (blue) - No external dependencies, pure business logic
- **🟡 Application** (yellow) - Depends only on domain, orchestrates use cases  
- **🔴 Adapters** (coral) - Depends on application ports, handles infrastructure

**Dependency Direction:** Always points inward → Domain is protected from external changes
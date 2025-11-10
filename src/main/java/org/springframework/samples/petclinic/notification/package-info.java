/**
 * Notification module for handling visit-related notifications.
 * 
 * This module follows hexagonal architecture principles and is designed to:
 * - Listen to VisitCreated domain events from the owner module
 * - Select a random vet from the vet module  
 * - Send notifications to external notification services
 * 
 * Architecture:
 * - Domain: Core business logic and models
 * - Application: Use case implementations and event handlers
 * - Adapters: Integration with external modules and services
 *
 * @author GitHub Copilot
 */
package org.springframework.samples.petclinic.notification;
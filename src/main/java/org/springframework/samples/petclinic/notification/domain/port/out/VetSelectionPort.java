package org.springframework.samples.petclinic.notification.domain.port.out;

/**
 * Outbound port for retrieving random vet information from vet module.
 * This is a driven port in the hexagonal architecture.
 *
 * @author GitHub Copilot
 */
public interface VetSelectionPort {
    
    /**
     * Select a random vet from available vets.
     *
     * @return VetInfo containing vet id and name, null if no vets available
     */
    VetInfo selectRandomVet();
    
    /**
     * Value object representing vet information
     */
    record VetInfo(Integer id, String name) {
    }
}
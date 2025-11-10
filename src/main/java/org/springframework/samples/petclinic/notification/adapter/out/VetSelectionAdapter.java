package org.springframework.samples.petclinic.notification.adapter.out;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.samples.petclinic.notification.domain.port.out.VetSelectionPort;
import org.springframework.samples.petclinic.rest.dto.VetDto;
import org.springframework.samples.petclinic.vet.VetUseCase;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.List;

/**
 * Adapter implementing VetSelectionPort to interact with the vet module.
 * This is an outbound adapter in the hexagonal architecture.
 *
 * @author GitHub Copilot
 */
@Component
public class VetSelectionAdapter implements VetSelectionPort {

    private static final Logger logger = LoggerFactory.getLogger(VetSelectionAdapter.class);
    private final VetUseCase vetUseCase;
    private final SecureRandom random;

    public VetSelectionAdapter(VetUseCase vetUseCase) {
        this.vetUseCase = vetUseCase;
        this.random = new SecureRandom();
    }

    @Override
    public VetInfo selectRandomVet() {
        logger.info("Selecting random vet from available vets");

        try {
            List<VetDto> availableVets = vetUseCase.listVets();

            if (availableVets == null || availableVets.isEmpty()) {
                logger.warn("No vets available in the system");
                return null;
            }

            // Select a random vet from the available list
            int randomIndex = random.nextInt(availableVets.size());
            VetDto selectedVet = availableVets.get(randomIndex);

            String fullName = selectedVet.getFirstName() + " " + selectedVet.getLastName();

            VetInfo vetInfo = new VetInfo(selectedVet.getId(), fullName);

            logger.info("Selected vet: {} (ID: {}) from {} available vets",
                        fullName, selectedVet.getId(), availableVets.size());

            return vetInfo;

        } catch (Exception e) {
            logger.error("Error selecting random vet: {}", e.getMessage(), e);
            return null;
        }
    }
}

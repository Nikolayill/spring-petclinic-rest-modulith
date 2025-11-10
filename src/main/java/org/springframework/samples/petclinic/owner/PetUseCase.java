package org.springframework.samples.petclinic.owner;

import org.springframework.samples.petclinic.rest.dto.PetDto;

import java.util.List;
import java.util.Optional;

public interface PetUseCase {
    Optional<PetDto> getPet(Integer petId);

    Optional<List<PetDto>> listPets();

    PetDto updatePet(Integer petId, PetDto petDto);

    Optional<Integer> deletePet(Integer petId);
}

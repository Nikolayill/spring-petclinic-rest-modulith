package org.springframework.samples.petclinic.owner;

import org.springframework.samples.petclinic.rest.dto.PetDto;

import java.util.List;
import java.util.Optional;

public interface PetUseCase {
    Optional<PetDto> getPetA(Integer petId);

    Optional<List<PetDto>> listPetsA();

    PetDto updatePetA(Integer petId, PetDto petDto);

    Optional<Integer> deletePetA(Integer petId);
}
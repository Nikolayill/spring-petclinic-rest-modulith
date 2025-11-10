package org.springframework.samples.petclinic.owner;

import org.springframework.samples.petclinic.rest.dto.PetTypeDto;
import org.springframework.samples.petclinic.rest.dto.PetTypeFieldsDto;

import java.util.List;
import java.util.Optional;

public interface PetTypeUseCase {
    Optional<List<PetTypeDto>> listPetTypes();

    Optional<PetTypeDto> getPetType(Integer petTypeId);

    PetTypeDto addPetType(PetTypeFieldsDto petTypeFieldsDto);

    PetTypeDto updatePetType(Integer petTypeId, PetTypeDto petTypeDto);

    Optional<Integer> deletePetType(Integer petTypeId);
}

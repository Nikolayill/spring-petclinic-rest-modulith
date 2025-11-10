package org.springframework.samples.petclinic.owner.application.port.in;

import org.springframework.samples.petclinic.rest.dto.PetTypeDto;
import org.springframework.samples.petclinic.rest.dto.PetTypeFieldsDto;

import java.util.List;
import java.util.Optional;

public interface PetTypeUseCase {
    Optional<List<PetTypeDto>> listPetTypesA();

    Optional<PetTypeDto> getPetTypeA(Integer petTypeId);

    PetTypeDto addPetTypeA(PetTypeFieldsDto petTypeFieldsDto);

    PetTypeDto updatePetTypeA(Integer petTypeId, PetTypeDto petTypeDto);

    Optional<Integer> deletePetTypeA(Integer petTypeId);
}

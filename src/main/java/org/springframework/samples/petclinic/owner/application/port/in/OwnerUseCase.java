package org.springframework.samples.petclinic.owner.application.port.in;

import org.springframework.samples.petclinic.rest.dto.OwnerDto;
import org.springframework.samples.petclinic.rest.dto.OwnerFieldsDto;
import org.springframework.samples.petclinic.rest.dto.PetDto;
import org.springframework.samples.petclinic.rest.dto.PetFieldsDto;
import org.springframework.samples.petclinic.rest.dto.VisitDto;
import org.springframework.samples.petclinic.rest.dto.VisitFieldsDto;

import java.util.List;
import java.util.Optional;

public interface OwnerUseCase {
    Optional<List<OwnerDto>> listOwnersA(String lastName);

    Optional<OwnerDto> getOwnerA(Integer ownerId);

    OwnerDto addOwnerA(OwnerFieldsDto ownerFieldsDto);

    OwnerDto updateOwnerA(Integer ownerId, OwnerFieldsDto ownerFieldsDto);

    Optional<Integer> deleteOwnerA(Integer ownerId);

    PetDto addPetToOwnerA(Integer ownerId, PetFieldsDto petFieldsDto);

    boolean updateOwnersPetA(Integer ownerId, Integer petId, PetFieldsDto petFieldsDto);

    VisitDto addVisitToOwnerA(Integer ownerId, Integer petId, VisitFieldsDto visitFieldsDto);

    Optional<PetDto> getOwnersPetA(Integer ownerId, Integer petId);
}

package org.springframework.samples.petclinic.owner;

import org.springframework.samples.petclinic.rest.dto.OwnerDto;
import org.springframework.samples.petclinic.rest.dto.OwnerFieldsDto;
import org.springframework.samples.petclinic.rest.dto.PetDto;
import org.springframework.samples.petclinic.rest.dto.PetFieldsDto;
import org.springframework.samples.petclinic.rest.dto.VisitDto;
import org.springframework.samples.petclinic.rest.dto.VisitFieldsDto;

import java.util.List;
import java.util.Optional;

public interface OwnerUseCase {
    Optional<List<OwnerDto>> listOwners(String lastName);

    Optional<OwnerDto> getOwner(Integer ownerId);

    OwnerDto addOwner(OwnerFieldsDto ownerFieldsDto);

    OwnerDto updateOwner(Integer ownerId, OwnerFieldsDto ownerFieldsDto);

    Optional<Integer> deleteOwner(Integer ownerId);

    PetDto addPetToOwner(Integer ownerId, PetFieldsDto petFieldsDto);

    boolean updateOwnersPet(Integer ownerId, Integer petId, PetFieldsDto petFieldsDto);

    VisitDto addVisitToOwner(Integer ownerId, Integer petId, VisitFieldsDto visitFieldsDto);

    Optional<PetDto> getOwnersPet(Integer ownerId, Integer petId);
}

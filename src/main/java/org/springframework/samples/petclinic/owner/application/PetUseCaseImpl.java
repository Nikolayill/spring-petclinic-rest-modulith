package org.springframework.samples.petclinic.owner.application;

import org.springframework.samples.petclinic.owner.PetUseCase;
import org.springframework.samples.petclinic.owner.domain.model.Pet;
import org.springframework.samples.petclinic.owner.domain.service.PetService;
import org.springframework.samples.petclinic.owner.domain.service.mapper.PetMapper;
import org.springframework.samples.petclinic.rest.dto.PetDto;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class PetUseCaseImpl implements PetUseCase {
    public final PetService petService;
    public final PetMapper petMapper;

    public PetUseCaseImpl(PetService petService, PetMapper petMapper) {
        this.petService = petService;
        this.petMapper = petMapper;
    }

    @Override
    public Optional<PetDto> getPetA(Integer petId) {
        return Optional.ofNullable(this.petService.findPetById(petId))
                       .map(petMapper::toPetDto);
    }

    @Override
    public Optional<List<PetDto>> listPetsA() {
        List<Pet> pets = new ArrayList<>(this.petService.findAllPets());
        if (pets.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(new ArrayList<>(petMapper.toPetsDto(pets)));
    }

    @Override
    public PetDto updatePetA(Integer petId, PetDto petDto) {
        Pet currentPet = this.petService.findPetById(petId);
        if (currentPet == null) {
            return null;
        }
        currentPet.setBirthDate(petDto.getBirthDate());
        currentPet.setName(petDto.getName());
        currentPet.setType(petMapper.toPetType(petDto.getType()));
        this.petService.savePet(currentPet);
        return petMapper.toPetDto(currentPet);
    }

    @Override
    public Optional<Integer> deletePetA(Integer petId) {
        Pet pet = this.petService.findPetById(petId);
        if (pet == null) {
            return Optional.empty();
        }
        this.petService.deletePet(pet);
        return Optional.ofNullable(pet.getId());
    }
}
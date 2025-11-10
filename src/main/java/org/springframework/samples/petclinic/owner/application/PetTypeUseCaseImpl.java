package org.springframework.samples.petclinic.owner.application;

import org.springframework.samples.petclinic.owner.PetTypeUseCase;
import org.springframework.samples.petclinic.owner.domain.model.PetType;
import org.springframework.samples.petclinic.owner.domain.service.PetTypeService;
import org.springframework.samples.petclinic.owner.domain.service.mapper.PetTypeMapper;
import org.springframework.samples.petclinic.rest.dto.PetTypeDto;
import org.springframework.samples.petclinic.rest.dto.PetTypeFieldsDto;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class PetTypeUseCaseImpl implements PetTypeUseCase {
    public final PetTypeService petTypeService;
    public final PetTypeMapper petTypeMapper;

    public PetTypeUseCaseImpl(PetTypeService petTypeService, PetTypeMapper petTypeMapper) {
        this.petTypeService = petTypeService;
        this.petTypeMapper = petTypeMapper;
    }

    @Override
    public Optional<List<PetTypeDto>> listPetTypesA() {
        List<PetType> petTypes = new ArrayList<>(this.petTypeService.findAllPetTypes());
        if (petTypes.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(petTypeMapper.toPetTypeDtos(petTypes));
    }

    @Override
    public Optional<PetTypeDto> getPetTypeA(Integer petTypeId) {
        return Optional.ofNullable(this.petTypeService.findPetTypeById(petTypeId))
                       .map(petTypeMapper::toPetTypeDto);
    }

    @Override
    public PetTypeDto addPetTypeA(PetTypeFieldsDto petTypeFieldsDto) {
        PetType petType = petTypeMapper.toPetType(petTypeFieldsDto);
        this.petTypeService.savePetType(petType);
        return petTypeMapper.toPetTypeDto(petType);
    }

    @Override
    public PetTypeDto updatePetTypeA(Integer petTypeId, PetTypeDto petTypeDto) {
        PetType currentPetType = this.petTypeService.findPetTypeById(petTypeId);
        if (currentPetType == null) {
            return null;
        }
        currentPetType.setName(petTypeDto.getName());
        this.petTypeService.savePetType(currentPetType);
        return petTypeMapper.toPetTypeDto(currentPetType);
    }

    @Override
    public Optional<Integer> deletePetTypeA(Integer petTypeId) {
        PetType petType = this.petTypeService.findPetTypeById(petTypeId);
        if (petType == null) {
            return Optional.empty();
        }
        this.petTypeService.deletePetType(petType);
        return Optional.ofNullable(petType.getId());
    }
}
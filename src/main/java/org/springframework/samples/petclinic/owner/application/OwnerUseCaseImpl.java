package org.springframework.samples.petclinic.owner.application;

import org.springframework.samples.petclinic.owner.OwnerUseCase;
import org.springframework.samples.petclinic.owner.domain.model.Owner;
import org.springframework.samples.petclinic.owner.domain.model.Pet;
import org.springframework.samples.petclinic.owner.domain.model.Visit;
import org.springframework.samples.petclinic.owner.domain.service.OwnerService;
import org.springframework.samples.petclinic.owner.domain.service.PetService;
import org.springframework.samples.petclinic.owner.domain.service.VisitService;
import org.springframework.samples.petclinic.owner.domain.service.mapper.OwnerMapper;
import org.springframework.samples.petclinic.owner.domain.service.mapper.PetMapper;
import org.springframework.samples.petclinic.owner.domain.service.mapper.VisitMapper;
import org.springframework.samples.petclinic.rest.dto.OwnerDto;
import org.springframework.samples.petclinic.rest.dto.OwnerFieldsDto;
import org.springframework.samples.petclinic.rest.dto.PetDto;
import org.springframework.samples.petclinic.rest.dto.PetFieldsDto;
import org.springframework.samples.petclinic.rest.dto.VisitDto;
import org.springframework.samples.petclinic.rest.dto.VisitFieldsDto;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Component
public class OwnerUseCaseImpl implements OwnerUseCase {
    private final OwnerService ownerService;
    private final PetService petService;
    private final VisitService visitService;
    private final OwnerMapper ownerMapper;
    private final PetMapper petMapper;
    private final VisitMapper visitMapper;

    public OwnerUseCaseImpl(OwnerService ownerService,
                           PetService petService,
                           VisitService visitService,
                           OwnerMapper ownerMapper,
                           PetMapper petMapper,
                           VisitMapper visitMapper) {
        this.ownerService = ownerService;
        this.petService = petService;
        this.visitService = visitService;
        this.ownerMapper = ownerMapper;
        this.petMapper = petMapper;
        this.visitMapper = visitMapper;
    }

    @Override
    public Optional<List<OwnerDto>> listOwnersA(String lastName) {
        Collection<Owner> owners;
        if (lastName != null) {
            owners = this.ownerService.findOwnerByLastName(lastName);
        } else {
            owners = this.ownerService.findAllOwners();
        }
        if (owners.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(ownerMapper.toOwnerDtoCollection(owners));
    }

    @Override
    public Optional<OwnerDto> getOwnerA(Integer ownerId) {
        return Optional.ofNullable(this.ownerService.findOwnerById(ownerId))
                       .map(ownerMapper::toOwnerDto);
    }

    @Override
    public OwnerDto addOwnerA(OwnerFieldsDto ownerFieldsDto) {
        Owner owner = ownerMapper.toOwner(ownerFieldsDto);
        this.ownerService.saveOwner(owner);
        return ownerMapper.toOwnerDto(owner);
    }

    @Override
    public OwnerDto updateOwnerA(Integer ownerId, OwnerFieldsDto ownerFieldsDto) {
        Owner currentOwner = this.ownerService.findOwnerById(ownerId);
        if (currentOwner == null) {
            return null;
        }
        currentOwner.setAddress(ownerFieldsDto.getAddress());
        currentOwner.setCity(ownerFieldsDto.getCity());
        currentOwner.setFirstName(ownerFieldsDto.getFirstName());
        currentOwner.setLastName(ownerFieldsDto.getLastName());
        currentOwner.setTelephone(ownerFieldsDto.getTelephone());
        this.ownerService.saveOwner(currentOwner);
        return ownerMapper.toOwnerDto(currentOwner);
    }

    @Override
    public Optional<Integer> deleteOwnerA(Integer ownerId) {
        Owner owner = this.ownerService.findOwnerById(ownerId);
        if (owner == null) {
            return Optional.empty();
        }
        this.ownerService.deleteOwner(owner);
        return Optional.ofNullable(owner.getId());
    }

    @Override
    public PetDto addPetToOwnerA(Integer ownerId, PetFieldsDto petFieldsDto) {
        Pet pet = petMapper.toPet(petFieldsDto);
        Owner owner = new Owner();
        owner.setId(ownerId);
        pet.setOwner(owner);
        pet.getType().setName(null);
        this.petService.savePet(pet);
        return petMapper.toPetDto(pet);
    }

    @Override
    public boolean updateOwnersPetA(Integer ownerId, Integer petId, PetFieldsDto petFieldsDto) {
        Owner currentOwner = this.ownerService.findOwnerById(ownerId);
        if (currentOwner != null) {
            Pet currentPet = this.petService.findPetById(petId);
            if (currentPet != null) {
                currentPet.setBirthDate(petFieldsDto.getBirthDate());
                currentPet.setName(petFieldsDto.getName());
                currentPet.setType(petMapper.toPetType(petFieldsDto.getType()));
                this.petService.savePet(currentPet);
                return true;
            }
        }
        return false;
    }

    @Override
    public VisitDto addVisitToOwnerA(Integer ownerId, Integer petId, VisitFieldsDto visitFieldsDto) {
        Visit visit = visitMapper.toVisit(visitFieldsDto);
        Pet pet = new Pet();
        pet.setId(petId);
        visit.setPet(pet);
        this.visitService.saveVisit(visit);
        return visitMapper.toVisitDto(visit);
    }

    @Override
    public Optional<PetDto> getOwnersPetA(Integer ownerId, Integer petId) {
        Owner owner = this.ownerService.findOwnerById(ownerId);
        if (owner != null) {
            Pet pet = owner.getPet(petId);
            if (pet != null) {
                return Optional.of(petMapper.toPetDto(pet));
            }
        }
        return Optional.empty();
    }
}
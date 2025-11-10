package org.springframework.samples.petclinic.owner.domain.service.impl;

import org.springframework.dao.DataAccessException;
import org.springframework.samples.petclinic.owner.domain.port.out.PetRepository;
import org.springframework.samples.petclinic.owner.domain.port.out.PetTypeRepository;
import org.springframework.samples.petclinic.owner.domain.model.Pet;
import org.springframework.samples.petclinic.owner.domain.model.PetType;
import org.springframework.samples.petclinic.owner.domain.service.PetService;
import org.springframework.samples.petclinic.owner.domain.service.PetTypeService;
import org.springframework.samples.petclinic.shared.util.FindEntityWrapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;

@Service
public class PetServiceImpl implements PetService {
    protected final PetRepository petRepository;
    private final PetTypeRepository petTypeRepository;

    public PetServiceImpl(PetRepository petRepository, PetTypeRepository petTypeRepository) {
        this.petRepository = petRepository;
        this.petTypeRepository = petTypeRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public Collection<Pet> findAllPets() throws DataAccessException {
        return petRepository.findAll();
    }

    @Override
    @Transactional
    public void deletePet(Pet pet) throws DataAccessException {
        petRepository.delete(pet);
    }

    @Override
    @Transactional(readOnly = true)
    public Pet findPetById(int id) throws DataAccessException {
        return FindEntityWrapper.findEntityById(() -> petRepository.findById(id));
    }

    // FIXME move method to usecase
    @Override
    @Transactional
    public void savePet(Pet pet) throws DataAccessException {
        Integer id = pet.getType().getId();
        pet.setType(FindEntityWrapper.findEntityById(() -> petTypeRepository.findById(id)));
        petRepository.save(pet);
    }
}

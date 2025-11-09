package org.springframework.samples.petclinic.owner.domain.service.impl;

import org.springframework.dao.DataAccessException;
import org.springframework.samples.petclinic.owner.adapter.repository.PetRepository;
import org.springframework.samples.petclinic.owner.adapter.repository.PetTypeRepository;
import org.springframework.samples.petclinic.owner.domain.model.PetType;
import org.springframework.samples.petclinic.owner.domain.service.PetTypeService;
import org.springframework.samples.petclinic.shared.util.FindEntityWrapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;

@Service
public class PetTypeServiceImpl implements PetTypeService {
    private final PetRepository petRepository;
    protected final PetTypeRepository petTypeRepository;

    public PetTypeServiceImpl(PetRepository petRepository, PetTypeRepository petTypeRepository) {
        this.petRepository = petRepository;
        this.petTypeRepository = petTypeRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public PetType findPetTypeById(int petTypeId) {
        return FindEntityWrapper.findEntityById(() -> petTypeRepository.findById(petTypeId));
    }

    @Override
    @Transactional(readOnly = true)
    public Collection<PetType> findAllPetTypes() throws DataAccessException {
        return petTypeRepository.findAll();
    }

    @Override
    @Transactional
    public void savePetType(PetType petType) throws DataAccessException {
        petTypeRepository.save(petType);
    }

    @Override
    @Transactional
    public void deletePetType(PetType petType) throws DataAccessException {
        petTypeRepository.delete(petType);
    }

    // FIXME move method to usecase
    @Override
    @Transactional(readOnly = true)
    public Collection<PetType> findPetTypes() throws DataAccessException {
        return petRepository.findPetTypes();
    }
}

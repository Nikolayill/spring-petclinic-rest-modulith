package org.springframework.samples.petclinic.owner.domain.service.impl;

import org.springframework.dao.DataAccessException;
import org.springframework.samples.petclinic.owner.domain.port.out.PetTypeRepository;
import org.springframework.samples.petclinic.owner.domain.model.PetType;
import org.springframework.samples.petclinic.owner.domain.service.PetTypeService;
import org.springframework.samples.petclinic.shared.util.FindEntityWrapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;

@Service
public class PetTypeServiceImpl implements PetTypeService {
    protected final PetTypeRepository petTypeRepository;

    public PetTypeServiceImpl(PetTypeRepository petTypeRepository) {
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

}

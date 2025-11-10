package org.springframework.samples.petclinic.owner.domain.service.impl;

import org.springframework.dao.DataAccessException;
import org.springframework.samples.petclinic.owner.domain.port.out.OwnerRepository;
import org.springframework.samples.petclinic.owner.domain.model.Owner;
import org.springframework.samples.petclinic.owner.domain.service.OwnerService;
import org.springframework.samples.petclinic.shared.util.FindEntityWrapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;

@Service
public class OwnerServiceImpl implements OwnerService {
    protected final OwnerRepository ownerRepository;

    public OwnerServiceImpl(OwnerRepository ownerRepository) {
        this.ownerRepository = ownerRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public Collection<Owner> findAllOwners() throws DataAccessException {
        return ownerRepository.findAll();
    }

    @Override
    @Transactional
    public void deleteOwner(Owner owner) throws DataAccessException {
        ownerRepository.delete(owner);
    }

    @Override
    @Transactional(readOnly = true)
    public Owner findOwnerById(int id) throws DataAccessException {
        return FindEntityWrapper.findEntityById(() -> ownerRepository.findById(id));
    }

    @Override
    @Transactional
    public void saveOwner(Owner owner) throws DataAccessException {
        ownerRepository.save(owner);

    }

    @Override
    @Transactional(readOnly = true)
    public Collection<Owner> findOwnerByLastName(String lastName) throws DataAccessException {
        return ownerRepository.findByLastName(lastName);
    }
}

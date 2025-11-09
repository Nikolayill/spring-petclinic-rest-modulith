package org.springframework.samples.petclinic.vet.domain.service.impl;

import org.springframework.dao.DataAccessException;
import org.springframework.samples.petclinic.shared.util.FindEntityWrapper;
import org.springframework.samples.petclinic.vet.adapter.repository.VetRepository;
import org.springframework.samples.petclinic.vet.domain.model.Vet;
import org.springframework.samples.petclinic.vet.domain.service.VetService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;

@Service
public class VetServiceImpl implements VetService {
    protected final VetRepository vetRepository;

    public VetServiceImpl(VetRepository vetRepository) {
        this.vetRepository = vetRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public Vet findVetById(int id) throws DataAccessException {
        return FindEntityWrapper.findEntityById(() -> vetRepository.findById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Collection<Vet> findAllVets() throws DataAccessException {
        return vetRepository.findAll();
    }

    @Override
    @Transactional
    public void saveVet(Vet vet) throws DataAccessException {
        vetRepository.save(vet);
    }

    @Override
    @Transactional
    public void deleteVet(Vet vet) throws DataAccessException {
        vetRepository.delete(vet);
    }

    @Override
    @Transactional(readOnly = true)
    public Collection<Vet> findVets() throws DataAccessException {
        return vetRepository.findAll();
    }
}

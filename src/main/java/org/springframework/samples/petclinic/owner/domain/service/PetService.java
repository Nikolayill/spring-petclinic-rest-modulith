package org.springframework.samples.petclinic.owner.domain.service;

import org.springframework.dao.DataAccessException;
import org.springframework.samples.petclinic.owner.domain.model.Pet;

import java.util.Collection;

public interface PetService {
    Collection<Pet> findAllPets() throws DataAccessException;

    void deletePet(Pet pet) throws DataAccessException;

    Pet findPetById(int petId);

    void savePet(Pet currentPet);

}

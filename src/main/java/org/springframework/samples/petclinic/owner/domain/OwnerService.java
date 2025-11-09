package org.springframework.samples.petclinic.owner.domain;

import org.springframework.dao.DataAccessException;
import org.springframework.samples.petclinic.model.Owner;
import org.springframework.samples.petclinic.model.Pet;
import org.springframework.samples.petclinic.model.Visit;

import java.util.Collection;

public interface OwnerService {
    Owner findOwnerById(int id) throws DataAccessException;

    Collection<Owner> findAllOwners() throws DataAccessException;

    void saveOwner(Owner owner) throws DataAccessException;

    void deleteOwner(Owner owner) throws DataAccessException;

    Collection<Owner> findOwnerByLastName(String lastName) throws DataAccessException;

    Pet findPetById(int id) throws DataAccessException;

    void savePet(Pet pet) throws DataAccessException;

    void saveVisit(Visit visit) throws DataAccessException;
}

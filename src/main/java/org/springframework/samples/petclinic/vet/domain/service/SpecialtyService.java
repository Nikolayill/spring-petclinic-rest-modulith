package org.springframework.samples.petclinic.vet.domain.service;

import org.springframework.dao.DataAccessException;
import org.springframework.samples.petclinic.vet.domain.model.Specialty;

import java.util.Collection;
import java.util.List;
import java.util.Set;

public interface SpecialtyService {
    Specialty findSpecialtyById(int specialtyId);

    Collection<Specialty> findAllSpecialties() throws DataAccessException;

    void saveSpecialty(Specialty specialty) throws DataAccessException;

    void deleteSpecialty(Specialty specialty) throws DataAccessException;

    List<Specialty> findSpecialtiesByNameIn(Set<String> names) throws DataAccessException;

}

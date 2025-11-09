package org.springframework.samples.petclinic.owner.domain.service;

import org.springframework.dao.DataAccessException;
import org.springframework.samples.petclinic.owner.domain.model.Visit;

import java.util.Collection;

public interface VisitService {
    Collection<Visit> findVisitsByPetId(int petId);

    Visit findVisitById(int visitId) throws DataAccessException;

    Collection<Visit> findAllVisits() throws DataAccessException;

    void deleteVisit(Visit visit) throws DataAccessException;

    void saveVisit(Visit visit) throws DataAccessException;
}

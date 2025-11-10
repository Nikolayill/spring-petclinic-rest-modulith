package org.springframework.samples.petclinic.owner.domain.service.impl;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.DataAccessException;
import org.springframework.samples.petclinic.owner.domain.event.VisitCreated;
import org.springframework.samples.petclinic.owner.domain.port.out.VisitRepository;
import org.springframework.samples.petclinic.owner.domain.model.Visit;
import org.springframework.samples.petclinic.owner.domain.service.VisitService;
import org.springframework.samples.petclinic.shared.util.FindEntityWrapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;

@Service
public class VisitServiceImpl implements VisitService {
    private final VisitRepository visitRepository;
    private final ApplicationEventPublisher events;

    public VisitServiceImpl(VisitRepository visitRepository, ApplicationEventPublisher events) {
        this.visitRepository = visitRepository;
        this.events = events;
    }

    @Override
    @Transactional(readOnly = true)
    public Visit findVisitById(int visitId) throws DataAccessException {
        return FindEntityWrapper.findEntityById(() -> visitRepository.findById(visitId));
    }

    @Override
    @Transactional(readOnly = true)
    public Collection<Visit> findAllVisits() throws DataAccessException {
        return visitRepository.findAll();
    }

    @Override
    @Transactional
    public void deleteVisit(Visit visit) throws DataAccessException {
        visitRepository.delete(visit);
    }

    @Override
    @Transactional
    public void saveVisit(Visit visit) throws DataAccessException {
        if (visit.getId() == null) {
            events.publishEvent(new VisitCreated(
                visit.getPet().getId(),
                visit.getDate(),
                visit.getDescription())
            );
        }
        visitRepository.save(visit);

    }

    @Override
    @Transactional(readOnly = true)
    public Collection<Visit> findVisitsByPetId(int petId) {
        return visitRepository.findByPetId(petId);
    }
}

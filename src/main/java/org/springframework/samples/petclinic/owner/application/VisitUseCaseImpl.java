package org.springframework.samples.petclinic.owner.application;

import org.springframework.samples.petclinic.owner.application.port.in.VisitUseCase;
import org.springframework.samples.petclinic.owner.domain.model.Visit;
import org.springframework.samples.petclinic.owner.domain.service.VisitService;
import org.springframework.samples.petclinic.owner.domain.service.mapper.VisitMapper;
import org.springframework.samples.petclinic.rest.dto.VisitDto;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class VisitUseCaseImpl implements VisitUseCase {
    public final VisitService visitService;
    public final VisitMapper visitMapper;

    public VisitUseCaseImpl(VisitService visitService, VisitMapper visitMapper) {
        this.visitService = visitService;
        this.visitMapper = visitMapper;
    }

    @Override
    public Optional<VisitDto> getVisitA(Integer visitId) {
        return Optional.ofNullable(this.visitService.findVisitById(visitId))
                       .map(visitMapper::toVisitDto);
    }

    @Override
    public Optional<List<VisitDto>> listVisitsA() {
        Optional<List<VisitDto>> result;
        List<Visit> visits = new ArrayList<>(this.visitService.findAllVisits());
        if (!visits.isEmpty()) {
            result = Optional.of(new ArrayList<>(visitMapper.toVisitsDto(visits)));
        } else {
            result = Optional.empty();
        }
        return result;
    }

    @Override
    public VisitDto addVisitA(VisitDto visitDto) {
        Visit visit = visitMapper.toVisit(visitDto);
        this.visitService.saveVisit(visit);
        return visitMapper.toVisitDto(visit);
    }

    @Override
    public VisitDto updateVisitA(Integer visitId, VisitDto visitDto) {
        VisitDto result;
        Visit currentVisit = this.visitService.findVisitById(visitId);
        if (currentVisit == null) {
            result = null;
        } else {
            currentVisit.setDate(visitDto.getDate());
            currentVisit.setDescription(visitDto.getDescription());
            this.visitService.saveVisit(currentVisit);
            result = visitMapper.toVisitDto(currentVisit);
        }
        return result;
    }

    @Override
    public Optional<Integer> deleteVisitA(Integer visitId) {
        Visit visit = this.visitService.findVisitById(visitId);
        if (visit == null) {
            return Optional.empty();
        }
        this.visitService.deleteVisit(visit);
        return Optional.ofNullable(visit.getId());
    }
}

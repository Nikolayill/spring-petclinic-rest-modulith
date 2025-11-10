package org.springframework.samples.petclinic.owner;

import org.springframework.samples.petclinic.rest.dto.VisitDto;

import java.util.List;
import java.util.Optional;

public interface VisitUseCase {
    Optional<VisitDto> getVisit(Integer visitId);

    Optional<List<VisitDto>> listVisits();

    VisitDto addVisit(VisitDto visitDto);

    VisitDto updateVisit(Integer visitId, VisitDto visitDto);

    Optional<Integer> deleteVisit(Integer visitId);
}

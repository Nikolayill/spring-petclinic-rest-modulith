package org.springframework.samples.petclinic.owner;

import org.springframework.samples.petclinic.rest.dto.VisitDto;

import java.util.List;
import java.util.Optional;

public interface VisitUseCase {
    Optional<VisitDto> getVisitA(Integer visitId);

    Optional<List<VisitDto>> listVisitsA();

    VisitDto addVisitA(VisitDto visitDto);

    VisitDto updateVisitA(Integer visitId, VisitDto visitDto);

    Optional<Integer> deleteVisitA(Integer visitId);
}
package org.springframework.samples.petclinic.vet;

import org.springframework.samples.petclinic.rest.dto.SpecialtyDto;

import java.util.List;
import java.util.Optional;

public interface SpecialityUseCases {
    List<SpecialtyDto> listSpecialtiesA();

    Optional<SpecialtyDto> getSpecialtyA(Integer specialtyId);

    SpecialtyDto getSpecialtyDtoA(SpecialtyDto specialtyDto);

    Optional<SpecialtyDto> updateSpecialityA(Integer specialtyId, SpecialtyDto specialtyDto);

    Optional<Integer> deleteSpecialityA(Integer specialtyId);
}

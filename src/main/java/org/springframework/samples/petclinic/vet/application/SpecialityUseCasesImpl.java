package org.springframework.samples.petclinic.vet.application;

import org.springframework.samples.petclinic.rest.dto.SpecialtyDto;
import org.springframework.samples.petclinic.vet.SpecialityUseCases;
import org.springframework.samples.petclinic.vet.domain.model.Specialty;
import org.springframework.samples.petclinic.vet.domain.service.SpecialtyService;
import org.springframework.samples.petclinic.vet.domain.service.mapper.SpecialtyMapper;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class SpecialityUseCasesImpl implements SpecialityUseCases {
    private final SpecialtyService specialtyService;
    private final SpecialtyMapper specialtyMapper;

    public SpecialityUseCasesImpl(SpecialtyService specialtyService, SpecialtyMapper specialtyMapper) {
        this.specialtyService = specialtyService;
        this.specialtyMapper = specialtyMapper;
    }

    @Override
    public List<SpecialtyDto> listSpecialtiesA() {
        List<SpecialtyDto> specialties = new ArrayList<SpecialtyDto>();
        specialties.addAll(specialtyMapper.toSpecialtyDtos(this.specialtyService.findAllSpecialties()));
        return specialties;
    }

    @Override
    public Optional<SpecialtyDto> getSpecialtyA(Integer specialtyId) {
        return Optional.ofNullable(this.specialtyService.findSpecialtyById(specialtyId))
                       .map(specialtyMapper::toSpecialtyDto);
    }

    @Override
    public SpecialtyDto getSpecialtyDtoA(SpecialtyDto specialtyDto) {
        Specialty specialty = specialtyMapper.toSpecialty(specialtyDto);
        this.specialtyService.saveSpecialty(specialty);
        return specialtyMapper.toSpecialtyDto(specialty);
    }

    @Override
    public Optional<SpecialtyDto> updateSpecialityA(Integer specialtyId, SpecialtyDto specialtyDto) {
        Optional<SpecialtyDto> response = Optional.empty();

        Specialty currentSpecialty = this.specialtyService.findSpecialtyById(specialtyId);
        if (currentSpecialty != null) {
            currentSpecialty.setName(specialtyDto.getName());
            this.specialtyService.saveSpecialty(currentSpecialty);
            response = Optional.ofNullable(specialtyMapper.toSpecialtyDto(currentSpecialty));
        }
        return response;
    }

    @Override
    public Optional<Integer> deleteSpecialityA(Integer specialtyId) {
        Specialty specialty = this.specialtyService.findSpecialtyById(specialtyId);
        Optional<Integer> r = Optional.ofNullable(specialty).map(Specialty::getId);

        this.specialtyService.deleteSpecialty(specialty);
        return r;
    }
}

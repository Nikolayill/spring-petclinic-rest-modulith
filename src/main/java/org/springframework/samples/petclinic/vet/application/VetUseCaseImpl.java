package org.springframework.samples.petclinic.vet.application;

import org.springframework.samples.petclinic.rest.dto.VetDto;
import org.springframework.samples.petclinic.vet.VetUseCase;
import org.springframework.samples.petclinic.vet.domain.model.Specialty;
import org.springframework.samples.petclinic.vet.domain.model.Vet;
import org.springframework.samples.petclinic.vet.domain.service.SpecialtyService;
import org.springframework.samples.petclinic.vet.domain.service.VetService;
import org.springframework.samples.petclinic.vet.domain.service.mapper.SpecialtyMapper;
import org.springframework.samples.petclinic.vet.domain.service.mapper.VetMapper;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class VetUseCaseImpl implements VetUseCase {
    private final VetService vetService;
    private final SpecialtyService specialtyService;
    private final VetMapper vetMapper;
    private final SpecialtyMapper specialtyMapper;

    public VetUseCaseImpl(VetService vetService, SpecialtyService specialtyService, VetMapper vetMapper, SpecialtyMapper specialtyMapper) {
        this.vetService = vetService;
        this.specialtyService = specialtyService;
        this.vetMapper = vetMapper;
        this.specialtyMapper = specialtyMapper;
    }

    @Override
    public ArrayList<VetDto> listVetsA() {
        return new ArrayList<VetDto>(vetMapper.toVetDtos(this.vetService.findAllVets()));
    }

    @Override
    public Optional<VetDto> getVetA(Integer vetId) {
        return Optional.ofNullable(this.vetService.findVetById(vetId))
                       .map(vetMapper::toVetDto);
    }

    @Override
    public VetDto addVetA(VetDto vetDto) {
        Vet vet = vetMapper.toVet(vetDto);
        if (vet.getNrOfSpecialties() > 0) {
            List<Specialty> vetSpecialities =
                    this.specialtyService.findSpecialtiesByNameIn(
                            vet.getSpecialties().stream().map(Specialty::getName).collect(Collectors.toSet()));
            vet.setSpecialties(vetSpecialities);
        }
        this.vetService.saveVet(vet);
        return vetMapper.toVetDto(vet);
    }

    @Override
    public VetDto updateVetA(Integer vetId, VetDto vetDto) {
        VetDto result = null;

        Vet currentVet = this.vetService.findVetById(vetId);
        if (currentVet == null) {
            return result;
        }

        currentVet.setFirstName(vetDto.getFirstName());
        currentVet.setLastName(vetDto.getLastName());
        currentVet.clearSpecialties();
        for (Specialty spec : specialtyMapper.toSpecialtys(vetDto.getSpecialties())) {
            currentVet.addSpecialty(spec);
        }
        if (currentVet.getNrOfSpecialties() > 0) {
            List<Specialty> vetSpecialities = this.specialtyService.findSpecialtiesByNameIn(currentVet.getSpecialties()
                                                                                                      .stream()
                                                                                                      .map(Specialty::getName)
                                                                                                      .collect(Collectors.toSet()));
            currentVet.setSpecialties(vetSpecialities);
        }
        this.vetService.saveVet(currentVet);
        result = vetMapper.toVetDto(currentVet);
        return result;
    }

    @Override
    public Optional<Integer> deleteVetA(Integer vetId) {
        Vet vet = this.vetService.findVetById(vetId);
        if (vet != null) {
            Integer id = vet.getId();
            this.vetService.deleteVet(vet);
            return Optional.of(id);
        }
        return Optional.empty();
    }
}

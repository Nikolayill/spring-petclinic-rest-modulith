package org.springframework.samples.petclinic.vet;

import org.springframework.samples.petclinic.rest.dto.VetDto;

import java.util.ArrayList;
import java.util.Optional;

public interface VetUseCase {
    ArrayList<VetDto> listVetsA();

    Optional<VetDto> getVetA(Integer vetId);

    VetDto addVetA(VetDto vetDto);

    VetDto updateVetA(Integer vetId, VetDto vetDto);

    Optional<Integer> deleteVetA(Integer vetId);
}

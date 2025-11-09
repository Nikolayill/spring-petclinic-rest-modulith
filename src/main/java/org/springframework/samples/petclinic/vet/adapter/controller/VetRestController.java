/*
 * Copyright 2016-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.samples.petclinic.vet.adapter.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.samples.petclinic.vet.domain.service.SpecialtyService;
import org.springframework.samples.petclinic.vet.domain.service.VetService;
import org.springframework.samples.petclinic.vet.domain.service.mapper.SpecialtyMapper;
import org.springframework.samples.petclinic.vet.domain.service.mapper.VetMapper;
import org.springframework.samples.petclinic.vet.domain.model.Specialty;
import org.springframework.samples.petclinic.vet.domain.model.Vet;
import org.springframework.samples.petclinic.rest.api.VetsApi;
import org.springframework.samples.petclinic.rest.dto.VetDto;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author Vitaliy Fedoriv
 */

@RestController
@CrossOrigin(exposedHeaders = "errors, content-type")
@RequestMapping("api")
public class VetRestController implements VetsApi {

    final VetService vetService;
    final SpecialtyService specialtyService;
    final VetMapper vetMapper;
    final SpecialtyMapper specialtyMapper;

    public VetRestController(VetService vetService,
                             SpecialtyService specialtyService,
                             VetMapper vetMapper,
                             SpecialtyMapper specialtyMapper) {
        this.vetService = vetService;
        this.specialtyService = specialtyService;
        this.vetMapper = vetMapper;
        this.specialtyMapper = specialtyMapper;
    }

    @PreAuthorize("hasRole(@roles.VET_ADMIN)")
    @Override
    public ResponseEntity<List<VetDto>> listVets() {
        List<VetDto> vets = listVetsA();
        if (vets.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(vets, HttpStatus.OK);
    }

    ArrayList<VetDto> listVetsA() {
        return new ArrayList<>(vetMapper.toVetDtos(this.vetService.findAllVets()));
    }

    @PreAuthorize("hasRole(@roles.VET_ADMIN)")
    @Override
    public ResponseEntity<VetDto> getVet(Integer vetId)  {
        VetDto vet = getVetA(vetId)
            .orElse(null);
        if (vet == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(vet, HttpStatus.OK);
    }

    Optional<VetDto> getVetA(Integer vetId) {
        return Optional.ofNullable(this.vetService.findVetById(vetId))
                       .map(vetMapper::toVetDto);
    }

    @PreAuthorize("hasRole(@roles.VET_ADMIN)")
    @Override
    public ResponseEntity<VetDto> addVet(VetDto vetDto) {
        HttpHeaders headers = new HttpHeaders();
        VetDto result = addVetA(vetDto);
        Integer id = result.getId();
        headers.setLocation(UriComponentsBuilder.newInstance().path("/api/vets/{id}").buildAndExpand(id).toUri());
        return new ResponseEntity<>(result, headers, HttpStatus.CREATED);
    }

    VetDto addVetA(VetDto vetDto) {
        Vet vet = vetMapper.toVet(vetDto);
        if(vet.getNrOfSpecialties() > 0){
            List<Specialty> vetSpecialities =
                this.specialtyService.findSpecialtiesByNameIn(
                    vet.getSpecialties().stream().map(Specialty::getName).collect(Collectors.toSet()));
            vet.setSpecialties(vetSpecialities);
        }
        this.vetService.saveVet(vet);
        return vetMapper.toVetDto(vet);
    }

    @PreAuthorize("hasRole(@roles.VET_ADMIN)")
    @Override
    public ResponseEntity<VetDto> updateVet(Integer vetId, VetDto vetDto)  {
        VetDto result = updateVetA(vetId, vetDto);

        if (result == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(result, HttpStatus.NO_CONTENT);
    }

    VetDto updateVetA(Integer vetId, VetDto vetDto) {
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

    @PreAuthorize("hasRole(@roles.VET_ADMIN)")
    @Transactional
    @Override
    public ResponseEntity<VetDto> deleteVet(Integer vetId) {
        if (deleteVetA(vetId).isPresent()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    Optional<Integer> deleteVetA(Integer vetId) {
        Vet vet = this.vetService.findVetById(vetId);
        if (vet != null) {
            Integer id = vet.getId();
            this.vetService.deleteVet(vet);
            return Optional.of(id);
        }
        return Optional.empty();
    }
}

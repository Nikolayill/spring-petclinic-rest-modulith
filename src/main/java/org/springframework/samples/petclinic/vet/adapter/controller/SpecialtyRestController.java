/*
 * Copyright 2016-2017 the original author or authors.
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

import jakarta.transaction.Transactional;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.samples.petclinic.rest.api.SpecialtiesApi;
import org.springframework.samples.petclinic.rest.dto.SpecialtyDto;
import org.springframework.samples.petclinic.vet.domain.model.Specialty;
import org.springframework.samples.petclinic.vet.domain.service.SpecialtyService;
import org.springframework.samples.petclinic.vet.domain.service.mapper.SpecialtyMapper;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author Vitaliy Fedoriv
 */

@RestController
@CrossOrigin(exposedHeaders = "errors, content-type")
@RequestMapping("api")
public class SpecialtyRestController implements SpecialtiesApi {

    final SpecialtyService specialtyService;

    final SpecialtyMapper specialtyMapper;

    public SpecialtyRestController(SpecialtyService specialtyService, SpecialtyMapper specialtyMapper) {
        this.specialtyService = specialtyService;
        this.specialtyMapper = specialtyMapper;
    }

    @PreAuthorize("hasRole(@roles.VET_ADMIN)")
    @Override
    public ResponseEntity<List<SpecialtyDto>> listSpecialties() {
        List<SpecialtyDto> specialties = listSpecialtiesA();
        if (specialties.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(specialties, HttpStatus.OK);
    }

    List<SpecialtyDto> listSpecialtiesA() {
        List<SpecialtyDto> specialties = new ArrayList<>();
        specialties.addAll(specialtyMapper.toSpecialtyDtos(this.specialtyService.findAllSpecialties()));
        return specialties;
    }

    @PreAuthorize("hasRole(@roles.VET_ADMIN)")
    @Override
    public ResponseEntity<SpecialtyDto> getSpecialty(Integer specialtyId) {
        Optional<SpecialtyDto> specialtyDto = getSpecialtyA(specialtyId);
        SpecialtyDto specialty = specialtyDto.orElse(null);
        if (specialty == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(specialty, HttpStatus.OK);
    }

    Optional<SpecialtyDto> getSpecialtyA(Integer specialtyId) {
        return Optional.ofNullable(this.specialtyService.findSpecialtyById(specialtyId))
                       .map(specialtyMapper::toSpecialtyDto);
    }

    @PreAuthorize("hasRole(@roles.VET_ADMIN)")
    @Override
    public ResponseEntity<SpecialtyDto> addSpecialty(SpecialtyDto specialtyDto) {
        HttpHeaders headers = new HttpHeaders();
        SpecialtyDto result = getSpecialtyDtoA(specialtyDto);
        headers.setLocation(UriComponentsBuilder.newInstance().path("/api/specialties/{id}").buildAndExpand(result.getId()).toUri());
        return new ResponseEntity<>(result, headers, HttpStatus.CREATED);
    }

    SpecialtyDto getSpecialtyDtoA(SpecialtyDto specialtyDto) {
        Specialty specialty = specialtyMapper.toSpecialty(specialtyDto);
        this.specialtyService.saveSpecialty(specialty);
        return specialtyMapper.toSpecialtyDto(specialty);
    }

    @PreAuthorize("hasRole(@roles.VET_ADMIN)")
    @Override
    public ResponseEntity<SpecialtyDto> updateSpecialty(Integer specialtyId, SpecialtyDto specialtyDto) {
        Optional<SpecialtyDto> response = updateSpecialityA(specialtyId, specialtyDto);


        return response.map(r -> new ResponseEntity<>(r, HttpStatus.NO_CONTENT))
                       .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    Optional<SpecialtyDto> updateSpecialityA(Integer specialtyId, SpecialtyDto specialtyDto) {
        Optional<SpecialtyDto> response = Optional.empty();

        Specialty currentSpecialty = this.specialtyService.findSpecialtyById(specialtyId);
        if (currentSpecialty != null) {
            currentSpecialty.setName(specialtyDto.getName());
            this.specialtyService.saveSpecialty(currentSpecialty);
            response = Optional.ofNullable(specialtyMapper.toSpecialtyDto(currentSpecialty));
        }
        return response;
    }

    @PreAuthorize("hasRole(@roles.VET_ADMIN)")
    @Transactional
    @Override
    public ResponseEntity<SpecialtyDto> deleteSpecialty(Integer specialtyId) {
        Optional<Integer> r = deleteSpecialityA(specialtyId);

        if (r.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    Optional<Integer> deleteSpecialityA(Integer specialtyId) {
        Specialty specialty = this.specialtyService.findSpecialtyById(specialtyId);
        Optional<Integer> r = Optional.ofNullable(specialty).map(Specialty::getId);

        this.specialtyService.deleteSpecialty(specialty);
        return r;
    }

}

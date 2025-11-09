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

package org.springframework.samples.petclinic.rest.controller;

import jakarta.transaction.Transactional;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.samples.petclinic.rest.api.SpecialtiesApi;
import org.springframework.samples.petclinic.rest.dto.SpecialtyDto;
import org.springframework.samples.petclinic.vet.SpecialityUseCases;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Optional;

/**
 * @author Vitaliy Fedoriv
 */

@RestController
@CrossOrigin(exposedHeaders = "errors, content-type")
@RequestMapping("api")
public class SpecialtyRestController implements SpecialtiesApi {

    private final SpecialityUseCases specialityUseCases;

    public SpecialtyRestController(SpecialityUseCases specialityUseCases) {
        this.specialityUseCases = specialityUseCases;
    }

    @PreAuthorize("hasRole(@roles.VET_ADMIN)")
    @Override
    public ResponseEntity<List<SpecialtyDto>> listSpecialties() {
        List<SpecialtyDto> specialties = specialityUseCases.listSpecialtiesA();
        if (specialties.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(specialties, HttpStatus.OK);
    }

    @PreAuthorize("hasRole(@roles.VET_ADMIN)")
    @Override
    public ResponseEntity<SpecialtyDto> getSpecialty(Integer specialtyId) {
        Optional<SpecialtyDto> specialtyDto = specialityUseCases.getSpecialtyA(specialtyId);
        SpecialtyDto specialty = specialtyDto.orElse(null);
        if (specialty == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(specialty, HttpStatus.OK);
    }

    @PreAuthorize("hasRole(@roles.VET_ADMIN)")
    @Override
    public ResponseEntity<SpecialtyDto> addSpecialty(SpecialtyDto specialtyDto) {
        HttpHeaders headers = new HttpHeaders();
        SpecialtyDto result = specialityUseCases.getSpecialtyDtoA(specialtyDto);
        headers.setLocation(UriComponentsBuilder.newInstance().path("/api/specialties/{id}")
                                                .buildAndExpand(result.getId()).toUri());
        return new ResponseEntity<>(result, headers, HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole(@roles.VET_ADMIN)")
    @Override
    public ResponseEntity<SpecialtyDto> updateSpecialty(Integer specialtyId, SpecialtyDto specialtyDto) {
        Optional<SpecialtyDto> response = specialityUseCases.updateSpecialityA(specialtyId, specialtyDto);


        return response.map(r -> new ResponseEntity<>(r, HttpStatus.NO_CONTENT))
                       .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PreAuthorize("hasRole(@roles.VET_ADMIN)")
    @Transactional
    @Override
    public ResponseEntity<SpecialtyDto> deleteSpecialty(Integer specialtyId) {
        Optional<Integer> r = specialityUseCases.deleteSpecialityA(specialtyId);

        if (r.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}

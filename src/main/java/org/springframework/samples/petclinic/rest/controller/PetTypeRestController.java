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
import org.springframework.samples.petclinic.owner.PetTypeUseCase;
import org.springframework.samples.petclinic.rest.api.PettypesApi;
import org.springframework.samples.petclinic.rest.dto.PetTypeDto;
import org.springframework.samples.petclinic.rest.dto.PetTypeFieldsDto;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@RestController
@CrossOrigin(exposedHeaders = "errors, content-type")
@RequestMapping("api")
public class PetTypeRestController implements PettypesApi {

    private final PetTypeUseCase petTypeUseCase;

    public PetTypeRestController(PetTypeUseCase petTypeUseCase) {
        this.petTypeUseCase = petTypeUseCase;
    }

    @PreAuthorize("hasAnyRole(@roles.OWNER_ADMIN, @roles.VET_ADMIN)")
    @Override
    public ResponseEntity<List<PetTypeDto>> listPetTypes() {
        return petTypeUseCase.listPetTypesA().map(r -> new ResponseEntity<>(r, HttpStatus.OK))
                           .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PreAuthorize("hasAnyRole(@roles.OWNER_ADMIN, @roles.VET_ADMIN)")
    @Override
    public ResponseEntity<PetTypeDto> getPetType(Integer petTypeId) {
        return petTypeUseCase.getPetTypeA(petTypeId)
                           .map(r -> new ResponseEntity<>(r, HttpStatus.OK))
                           .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PreAuthorize("hasRole(@roles.VET_ADMIN)")
    @Override
    public ResponseEntity<PetTypeDto> addPetType(PetTypeFieldsDto petTypeFieldsDto) {
        HttpHeaders headers = new HttpHeaders();
        PetTypeDto petTypeDto = petTypeUseCase.addPetTypeA(petTypeFieldsDto);
        headers.setLocation(UriComponentsBuilder.newInstance().path("/api/pettypes/{id}").buildAndExpand(petTypeDto.getId()).toUri());
        return new ResponseEntity<>(petTypeDto, headers, HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole(@roles.VET_ADMIN)")
    @Override
    public ResponseEntity<PetTypeDto> updatePetType(Integer petTypeId, PetTypeDto petTypeDto) {
        PetTypeDto result = petTypeUseCase.updatePetTypeA(petTypeId, petTypeDto);
        if (result == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(result, HttpStatus.NO_CONTENT);
    }

    @PreAuthorize("hasRole(@roles.VET_ADMIN)")
    @Transactional
    @Override
    public ResponseEntity<PetTypeDto> deletePetType(Integer petTypeId) {
        var result = petTypeUseCase.deletePetTypeA(petTypeId);
        if (result.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}

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

package org.springframework.samples.petclinic.owner.adapter.in.web;

import jakarta.transaction.Transactional;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.samples.petclinic.owner.application.port.in.VisitUseCase;
import org.springframework.samples.petclinic.rest.api.VisitsApi;
import org.springframework.samples.petclinic.rest.dto.VisitDto;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

/**
 * @author Vitaliy Fedoriv
 */

@RestController
@CrossOrigin(exposedHeaders = "errors, content-type")
@RequestMapping("api")
public class VisitRestController implements VisitsApi {

    private final VisitUseCase visitUseCase;

    public VisitRestController(VisitUseCase visitUseCaser) {
        this.visitUseCase = visitUseCaser;
    }


    @PreAuthorize("hasRole(@roles.OWNER_ADMIN)")
    @Override
    public ResponseEntity<List<VisitDto>> listVisits() {
        return visitUseCase.listVisitsA().map(r -> new ResponseEntity<>(r, HttpStatus.OK))
                           .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PreAuthorize("hasRole(@roles.OWNER_ADMIN)")
    @Override
    public ResponseEntity<VisitDto> getVisit( Integer visitId) {
        return visitUseCase.getVisitA(visitId)
                           .map(r -> new ResponseEntity<>(r, HttpStatus.OK))
                           .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PreAuthorize("hasRole(@roles.OWNER_ADMIN)")
    @Override
    public ResponseEntity<VisitDto> addVisit(VisitDto visitDto) {
        HttpHeaders headers = new HttpHeaders();
        visitDto = visitUseCase.addVisitA(visitDto);
        headers.setLocation(UriComponentsBuilder.newInstance().path("/api/visits/{id}").buildAndExpand(visitDto.getId()).toUri());
        return new ResponseEntity<>(visitDto, headers, HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole(@roles.OWNER_ADMIN)")
    @Override
    public ResponseEntity<VisitDto> updateVisit(Integer visitId, VisitDto visitDto) {
        VisitDto result = visitUseCase.updateVisitA(visitId, visitDto);

        if(result==null){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        }
        return new ResponseEntity<>(result, HttpStatus.NO_CONTENT);
    }

    @PreAuthorize("hasRole(@roles.OWNER_ADMIN)")
    @Transactional
    @Override
    public ResponseEntity<VisitDto> deleteVisit(Integer visitId) {
        var result = visitUseCase.deleteVisitA(visitId);

        if (result.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}

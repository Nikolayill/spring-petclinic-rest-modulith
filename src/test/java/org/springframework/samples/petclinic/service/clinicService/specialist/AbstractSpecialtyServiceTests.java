/*
 * Copyright 2002-2017 the original author or authors.
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
package org.springframework.samples.petclinic.service.clinicService.specialist;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.samples.petclinic.shared.util.EntityUtils;
import org.springframework.samples.petclinic.vet.domain.model.Specialty;
import org.springframework.samples.petclinic.vet.domain.service.impl.SpecialtyServiceImpl;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;


abstract class AbstractSpecialtyServiceTests {

    @Autowired
    protected SpecialtyServiceImpl specialtyService;

    @Test
    void shouldFindSpecialtyById(){
    	Specialty specialty = this.specialtyService.findSpecialtyById(1);
    	assertThat(specialty.getName()).isEqualTo("radiology");
    }

    @Test
    void shouldFindAllSpecialtys(){
        Collection<Specialty> specialties = this.specialtyService.findAllSpecialties();
        Specialty specialty1 = EntityUtils.getById(specialties, Specialty.class, 1);
        assertThat(specialty1.getName()).isEqualTo("radiology");
        Specialty specialty3 = EntityUtils.getById(specialties, Specialty.class, 3);
        assertThat(specialty3.getName()).isEqualTo("dentistry");
    }

    @Test
    @Transactional
    void shouldInsertSpecialty() {
        Collection<Specialty> specialties = this.specialtyService.findAllSpecialties();
        int found = specialties.size();

        Specialty specialty = new Specialty();
        specialty.setName("dermatologist");

        this.specialtyService.saveSpecialty(specialty);
        assertThat(specialty.getId().longValue()).isNotEqualTo(0);

        specialties = this.specialtyService.findAllSpecialties();
        assertThat(specialties.size()).isEqualTo(found + 1);
    }

    @Test
    @Transactional
    void shouldUpdateSpecialty(){
    	Specialty specialty = this.specialtyService.findSpecialtyById(1);
    	String oldLastName = specialty.getName();
        String newLastName = oldLastName + "X";
        specialty.setName(newLastName);
        this.specialtyService.saveSpecialty(specialty);
        specialty = this.specialtyService.findSpecialtyById(1);
        assertThat(specialty.getName()).isEqualTo(newLastName);
    }

    @Test
    @Transactional
    void shouldDeleteSpecialty(){
        Specialty specialty = new Specialty();
        specialty.setName("test");
        this.specialtyService.saveSpecialty(specialty);
        Integer specialtyId = specialty.getId();
        assertThat(specialtyId).isNotNull();
    	specialty = this.specialtyService.findSpecialtyById(specialtyId);
        assertThat(specialty).isNotNull();
        this.specialtyService.deleteSpecialty(specialty);
        try {
        	specialty = this.specialtyService.findSpecialtyById(specialtyId);
		} catch (Exception e) {
			specialty = null;
		}
        assertThat(specialty).isNull();
    }

    @Test
    @Transactional
    void shouldFindSpecialtiesByNameIn() {
        Specialty specialty1 = new Specialty();
        specialty1.setName("radiology");
        specialty1.setId(1);
        Specialty specialty2 = new Specialty();
        specialty2.setName("surgery");
        specialty2.setId(2);
        Specialty specialty3 = new Specialty();
        specialty3.setName("dentistry");
        specialty3.setId(3);
        List<Specialty> expectedSpecialties = List.of(specialty1, specialty2, specialty3);
        Set<String> specialtyNames = expectedSpecialties.stream()
            .map(Specialty::getName)
            .collect(Collectors.toSet());
        Collection<Specialty> actualSpecialties = this.specialtyService.findSpecialtiesByNameIn(specialtyNames);
        assertThat(actualSpecialties).isNotNull();
        assertThat(actualSpecialties.size()).isEqualTo(expectedSpecialties.size());
        for (Specialty expected : expectedSpecialties) {
            assertThat(actualSpecialties.stream()
                .anyMatch(
                    actual -> actual.getName().equals(expected.getName())
                    && actual.getId().equals(expected.getId()))).isTrue();
        }
    }

    void clearCache() {}
}

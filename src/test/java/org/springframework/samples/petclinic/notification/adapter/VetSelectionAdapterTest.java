package org.springframework.samples.petclinic.notification.adapter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.samples.petclinic.notification.adapter.out.VetSelectionAdapter;
import org.springframework.samples.petclinic.notification.domain.port.out.VetSelectionPort;
import org.springframework.samples.petclinic.rest.dto.VetDto;
import org.springframework.samples.petclinic.vet.VetUseCase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for VetSelectionAdapter.
 * Tests the integration with the vet module.
 *
 * @author GitHub Copilot
 */
@ExtendWith(MockitoExtension.class)
class VetSelectionAdapterTest {
    
    @Mock
    private VetUseCase vetUseCase;
    
    private VetSelectionAdapter vetSelectionAdapter;
    
    @BeforeEach
    void setUp() {
        vetSelectionAdapter = new VetSelectionAdapter(vetUseCase);
    }
    
    @Test
    void shouldSelectRandomVetFromAvailableVets() {
        // Given
        List<VetDto> vets = Arrays.asList(
            createVetDto(1, "John", "Smith"),
            createVetDto(2, "Jane", "Doe"),
            createVetDto(3, "Mike", "Wilson")
        );
        when(vetUseCase.listVets()).thenReturn(new ArrayList<>(vets));
        
        // When
        VetSelectionPort.VetInfo selectedVet = vetSelectionAdapter.selectRandomVet();
        
        // Then
        assertNotNull(selectedVet);
        assertTrue(selectedVet.id() >= 1 && selectedVet.id() <= 3);
        assertTrue(selectedVet.name().equals("John Smith") || 
                  selectedVet.name().equals("Jane Doe") || 
                  selectedVet.name().equals("Mike Wilson"));
        verify(vetUseCase).listVets();
    }
    
    @Test
    void shouldReturnNullWhenNoVetsAvailable() {
        // Given
        when(vetUseCase.listVets()).thenReturn(new ArrayList<>());
        
        // When
        VetSelectionPort.VetInfo selectedVet = vetSelectionAdapter.selectRandomVet();
        
        // Then
        assertNull(selectedVet);
        verify(vetUseCase).listVets();
    }
    
    @Test
    void shouldReturnNullWhenVetListIsNull() {
        // Given
        when(vetUseCase.listVets()).thenReturn(null);
        
        // When
        VetSelectionPort.VetInfo selectedVet = vetSelectionAdapter.selectRandomVet();
        
        // Then
        assertNull(selectedVet);
        verify(vetUseCase).listVets();
    }
    
    @Test
    void shouldHandleVetUseCaseExceptionGracefully() {
        // Given
        when(vetUseCase.listVets()).thenThrow(new RuntimeException("Database error"));
        
        // When
        VetSelectionPort.VetInfo selectedVet = vetSelectionAdapter.selectRandomVet();
        
        // Then
        assertNull(selectedVet);
        verify(vetUseCase).listVets();
    }
    
    @Test
    void shouldSelectSingleVetWhenOnlyOneAvailable() {
        // Given
        List<VetDto> vets = List.of(createVetDto(1, "John", "Smith"));
        when(vetUseCase.listVets()).thenReturn(new ArrayList<>(vets));
        
        // When
        VetSelectionPort.VetInfo selectedVet = vetSelectionAdapter.selectRandomVet();
        
        // Then
        assertNotNull(selectedVet);
        assertEquals(1, selectedVet.id());
        assertEquals("John Smith", selectedVet.name());
        verify(vetUseCase).listVets();
    }
    
    private VetDto createVetDto(Integer id, String firstName, String lastName) {
        VetDto vet = new VetDto();
        vet.setId(id);
        vet.setFirstName(firstName);
        vet.setLastName(lastName);
        return vet;
    }
}
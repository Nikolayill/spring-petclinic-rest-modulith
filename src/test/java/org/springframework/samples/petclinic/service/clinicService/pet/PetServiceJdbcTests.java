package org.springframework.samples.petclinic.service.clinicService.pet;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles({"jdbc", "hsqldb"})
public class PetServiceJdbcTests extends AbstractPetServiceTests {
}

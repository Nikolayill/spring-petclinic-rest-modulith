package org.springframework.samples.petclinic.service.clinicService.pettype;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles({"jdbc", "hsqldb"})
public class PetTypeServiceJdbcTests extends AbstractPetTypeServiceTests {
}

package org.springframework.samples.petclinic.service.clinicService.visit;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles({"jdbc", "hsqldb"})
public class VisitServiceJdbcTests extends AbstractVisitServiceTests {
}

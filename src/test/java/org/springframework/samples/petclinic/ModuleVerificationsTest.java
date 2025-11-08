package org.springframework.samples.petclinic;

import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;

public class ModuleVerificationsTest {
    @Test
    void testModules() {
        final ApplicationModules applicationModules = ApplicationModules.of(PetClinicApplication.class);
        System.out.println(applicationModules);
        applicationModules.verify();
    }
}

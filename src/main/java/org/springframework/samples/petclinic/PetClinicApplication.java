package org.springframework.samples.petclinic;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.modulith.Modulith;

@Modulith
public class PetClinicApplication extends SpringBootServletInitializer {

	public static void main(String[] args) {
		SpringApplication.run(PetClinicApplication.class, args);
	}
}

package org.springframework.samples.petclinic;

import org.springframework.modulith.core.ApplicationModuleDetectionStrategy;
import org.springframework.modulith.core.JavaPackage;

import java.util.Set;
import java.util.stream.Stream;

public class CustomModuleDetectionStrategy implements ApplicationModuleDetectionStrategy {
    private static final Set<String> LEGACY_MODULES = Set.of(
        "config"
        ,"mapper"
        ,"model"
        ,"repository"
        ,"rest"
        ,"security"
        ,"service"
        ,"util"
    );

    @Override
    public Stream<JavaPackage> getModuleBasePackages(JavaPackage basePackage) {
        return ApplicationModuleDetectionStrategy.directSubPackage().getModuleBasePackages(basePackage)
                                                 .filter(jp -> !LEGACY_MODULES.contains(jp.getLocalName()));
    }
}

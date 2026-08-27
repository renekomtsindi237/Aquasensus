package org.aquasensus;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import org.junit.jupiter.api.Test;

class ArchitectureCouchesTest {

    private static final JavaClasses CLASSES = new ClassFileImporter().importPackages("org.aquasensus");

    @Test
    void domaineSansTechnique() {
        noClasses()
                .that()
                .resideInAPackage("..domain..")
                .should()
                .dependOnClassesThat()
                .resideInAnyPackage("org.springframework..", "jakarta.persistence..", "jakarta.servlet..")
                .check(CLASSES);
    }

    @Test
    void webSansInfrastructure() {
        noClasses()
                .that()
                .resideInAPackage("..web..")
                .should()
                .dependOnClassesThat()
                .resideInAPackage("..infrastructure..")
                .check(CLASSES);
    }
}

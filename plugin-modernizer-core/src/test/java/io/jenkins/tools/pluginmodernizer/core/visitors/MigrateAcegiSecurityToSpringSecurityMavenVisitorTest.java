package io.jenkins.tools.pluginmodernizer.core.visitors;

import static org.openrewrite.maven.Assertions.pomXml;
import static org.openrewrite.test.RewriteTest.toRecipe;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.openrewrite.ExecutionContext;
import org.openrewrite.maven.MavenIsoVisitor;
import org.openrewrite.test.RewriteTest;
import org.openrewrite.xml.tree.Xml;

/**
 * Tests for {@link MigrateAcegiSecurityToSpringSecurityMavenVisitor}.
 */
@Execution(ExecutionMode.CONCURRENT)
public class MigrateAcegiSecurityToSpringSecurityMavenVisitorTest implements RewriteTest {

    @Test
    void migrateAcegiToSpringSecurity() {
        rewriteRun(
                spec -> spec.recipe(toRecipe(() -> new MavenIsoVisitor<>() {
                    @Override
                    public Xml.Document visitDocument(Xml.Document x, ExecutionContext ctx) {
                        doAfterVisit(new MigrateAcegiSecurityToSpringSecurityMavenVisitor());
                        return super.visitDocument(x, ctx);
                    }
                })),
                // language=xml
                pomXml(
                        """
                        <?xml version="1.0" encoding="UTF-8"?>
                        <project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
                          <modelVersion>4.0.0</modelVersion>
                          <groupId>io.jenkins.plugins</groupId>
                          <artifactId>empty</artifactId>
                          <version>1.0.0-SNAPSHOT</version>
                          <packaging>hpi</packaging>
                          <name>Empty Plugin</name>
                          <dependencies>
                            <dependency>
                              <groupId>org.acegisecurity</groupId>
                              <artifactId>acegi-security</artifactId>
                              <version>1.0.7</version>
                            </dependency>
                          </dependencies>
                        </project>
                        """,
                        """
                        <?xml version="1.0" encoding="UTF-8"?>
                        <project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
                          <modelVersion>4.0.0</modelVersion>
                          <groupId>io.jenkins.plugins</groupId>
                          <artifactId>empty</artifactId>
                          <version>1.0.0-SNAPSHOT</version>
                          <packaging>hpi</packaging>
                          <name>Empty Plugin</name>
                          <dependencies>
                            <dependency>
                              <groupId>org.springframework.security</groupId>
                              <artifactId>spring-security-config</artifactId>
                              <version>5.8.10</version>
                            </dependency>
                            <dependency>
                              <groupId>org.springframework.security</groupId>
                              <artifactId>spring-security-core</artifactId>
                              <version>5.8.10</version>
                            </dependency>
                          </dependencies>
                        </project>
                        """));
    }
}

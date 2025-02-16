package io.jenkins.tools.pluginmodernizer.core.recipes;

import static io.jenkins.tools.pluginmodernizer.core.recipes.DeclarativeRecipesTest.collectRewriteTestDependencies;
import static org.openrewrite.java.Assertions.java;
import static org.openrewrite.maven.Assertions.pomXml;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.openrewrite.java.JavaParser;
import org.openrewrite.test.RewriteTest;

/**
 * Test for {@link MigrateAcegiSecurityToSpringSecurity}.
 */
@Execution(ExecutionMode.CONCURRENT)
public class MigrateAcegiSecurityToSpringSecurityTest implements RewriteTest {

    @Test
    void migrateAcegiToSpringSecurity() {
        rewriteRun(
                spec -> {
                    var parser = JavaParser.fromJavaVersion().logCompilationWarningsAndErrors(true);
                    collectRewriteTestDependencies().forEach(parser::addClasspathEntry);
                    spec.recipe(new MigrateAcegiSecurityToSpringSecurity()).parser(parser);
                },
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
                        """),
                java(
                        """
                        import org.acegisecurity.Authentication;
                        import org.acegisecurity.GrantedAuthority;
                        import org.acegisecurity.GrantedAuthorityImpl;
                        import org.acegisecurity.providers.AbstractAuthenticationToken;
                        import org.acegisecurity.context.SecurityContextHolder;
                        import org.acegisecurity.AuthenticationException;
                        import org.acegisecurity.AuthenticationManager;
                        import org.acegisecurity.BadCredentialsException;
                        import org.acegisecurity.userdetails.UserDetails;
                        import org.acegisecurity.userdetails.UserDetailsService;
                        import org.acegisecurity.userdetails.UsernameNotFoundException;
                        import jenkins.model.Jenkins;

                        public class Foo {
                            public void foo() {
                                Authentication auth = Jenkins.getAuthentication();
                            }
                        }
                        """,
                        """
                        import org.springframework.security.core.Authentication;
                        import org.springframework.security.core.GrantedAuthority;
                        import org.springframework.security.core.GrantedAuthorityImpl;
                        import org.springframework.security.core.providers.AbstractAuthenticationToken;
                        import org.springframework.security.core.context.SecurityContextHolder;
                        import org.springframework.security.core.AuthenticationException;
                        import org.springframework.security.core.AuthenticationManager;
                        import org.springframework.security.core.BadCredentialsException;
                        import org.springframework.security.core.userdetails.UserDetails;
                        import org.springframework.security.core.userdetails.UserDetailsService;
                        import org.springframework.security.core.userdetails.UsernameNotFoundException;
                        import jenkins.model.Jenkins;

                        public class Foo {
                            public void foo() {
                                Authentication auth = Jenkins.getAuthentication2();
                            }
                        }
                        """));
    }
}

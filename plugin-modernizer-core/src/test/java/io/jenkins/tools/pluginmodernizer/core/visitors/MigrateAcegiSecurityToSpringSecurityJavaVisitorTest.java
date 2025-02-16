package io.jenkins.tools.pluginmodernizer.core.visitors;

import static io.jenkins.tools.pluginmodernizer.core.recipes.DeclarativeRecipesTest.collectRewriteTestDependencies;
import static org.openrewrite.java.Assertions.*;
import static org.openrewrite.test.RewriteTest.toRecipe;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.openrewrite.ExecutionContext;
import org.openrewrite.java.JavaIsoVisitor;
import org.openrewrite.java.JavaParser;
import org.openrewrite.java.tree.J;
import org.openrewrite.test.RewriteTest;

/**
 * Tests for {@link MigrateAcegiSecurityToSpringSecurityJavaVisitor}.
 */
@Execution(ExecutionMode.CONCURRENT)
public class MigrateAcegiSecurityToSpringSecurityJavaVisitorTest implements RewriteTest {

    @Test
    void migrateAcegiToSpringSecurity() {
        rewriteRun(
                spec -> {
                    var parser = JavaParser.fromJavaVersion().logCompilationWarningsAndErrors(true);
                    collectRewriteTestDependencies().forEach(parser::addClasspathEntry);
                    spec.recipe(toRecipe(() -> new JavaIsoVisitor<>() {
                                @Override
                                public J.CompilationUnit visitCompilationUnit(
                                        J.CompilationUnit cu, ExecutionContext ctx) {
                                    doAfterVisit(new MigrateAcegiSecurityToSpringSecurityJavaVisitor());
                                    return super.visitCompilationUnit(cu, ctx);
                                }
                            }))
                            .parser(parser);
                },
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

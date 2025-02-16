package io.jenkins.tools.pluginmodernizer.core.visitors;

import org.openrewrite.ExecutionContext;
import org.openrewrite.java.ChangeMethodName;
import org.openrewrite.java.ChangePackage;
import org.openrewrite.java.ChangeType;
import org.openrewrite.java.JavaIsoVisitor;
import org.openrewrite.java.tree.J;

/**
 * A Java visitor that performs the migration
 * of Acegi Security to Spring Security
 */
public class MigrateAcegiSecurityToSpringSecurityJavaVisitor extends JavaIsoVisitor<ExecutionContext> {

    @Override
    public J.CompilationUnit visitCompilationUnit(J.CompilationUnit cu, ExecutionContext ctx) {
        cu = (J.CompilationUnit) new ChangePackage("org.acegisecurity", "org.springframework.security.core", false)
                .getVisitor()
                .visitNonNull(cu, ctx);
        return super.visitCompilationUnit(cu, ctx);
    }

    @Override
    public J.Identifier visitIdentifier(J.Identifier ident, ExecutionContext ctx) {
        // Core security classes
        ident = (J.Identifier) new ChangeType(
                        "org.acegisecurity.Authentication", "org.springframework.security.core.Authentication", null)
                .getVisitor()
                .visitNonNull(ident, ctx);
        ident = (J.Identifier) new ChangeType(
                        "org.acegisecurity.GrantedAuthority",
                        "org.springframework.security.core.authority.GrantedAuthority",
                        null)
                .getVisitor()
                .visitNonNull(ident, ctx);
        ident = (J.Identifier) new ChangeType(
                        "org.acegisecurity.GrantedAuthorityImpl",
                        "org.springframework.security.core.authority.SimpleGrantedAuthority",
                        null)
                .getVisitor()
                .visitNonNull(ident, ctx);
        // Context classes
        ident = (J.Identifier) new ChangeType(
                        "org.acegisecurity.context.SecurityContextHolder",
                        "org.springframework.security.core.context.SecurityContextHolder",
                        null)
                .getVisitor()
                .visitNonNull(ident, ctx);
        // Authentication classes
        ident = (J.Identifier) new ChangeType(
                        "org.acegisecurity.providers.AbstractAuthenticationToken",
                        "org.springframework.security.authentication.AbstractAuthenticationToken",
                        null)
                .getVisitor()
                .visitNonNull(ident, ctx);
        ident = (J.Identifier) new ChangeType(
                        "org.acegisecurity.AuthenticationException",
                        "org.springframework.security.core.AuthenticationException",
                        null)
                .getVisitor()
                .visitNonNull(ident, ctx);
        ident = (J.Identifier) new ChangeType(
                        "org.acegisecurity.AuthenticationManager",
                        "org.springframework.security.authentication.AuthenticationManager",
                        null)
                .getVisitor()
                .visitNonNull(ident, ctx);
        ident = (J.Identifier) new ChangeType(
                        "org.acegisecurity.BadCredentialsException",
                        "org.springframework.security.authentication.BadCredentialsException",
                        null)
                .getVisitor()
                .visitNonNull(ident, ctx);
        // User Details classes
        ident = (J.Identifier) new ChangeType(
                        "org.acegisecurity.userdetails.UserDetails",
                        "org.springframework.security.core.userdetails.UserDetails",
                        null)
                .getVisitor()
                .visitNonNull(ident, ctx);
        ident = (J.Identifier) new ChangeType(
                        "org.acegisecurity.userdetails.UserDetailsService",
                        "org.springframework.security.core.userdetails.UserDetailsService",
                        null)
                .getVisitor()
                .visitNonNull(ident, ctx);
        ident = (J.Identifier) new ChangeType(
                        "org.acegisecurity.userdetails.UsernameNotFoundException",
                        "org.springframework.security.core.userdetails.UsernameNotFoundException",
                        null)
                .getVisitor()
                .visitNonNull(ident, ctx);
        return super.visitIdentifier(ident, ctx);
    }

    @Override
    public J.MethodInvocation visitMethodInvocation(J.MethodInvocation method, ExecutionContext ctx) {
        method = (J.MethodInvocation)
                new ChangeMethodName("jenkins.model.Jenkins getAuthentication()", "getAuthentication2", null, null)
                        .getVisitor()
                        .visitNonNull(method, ctx);
        return super.visitMethodInvocation(method, ctx);
    }
}

package io.jenkins.tools.pluginmodernizer.core.visitors;

import org.openrewrite.ExecutionContext;
import org.openrewrite.maven.AddDependency;
import org.openrewrite.maven.MavenIsoVisitor;
import org.openrewrite.maven.RemoveDependency;
import org.openrewrite.xml.tree.Xml;

/**
 * A Maven visitor that performs the migration
 * of Acegi Security to Spring Security
 */
public class MigrateAcegiSecurityToSpringSecurityMavenVisitor extends MavenIsoVisitor<ExecutionContext> {

    @Override
    public Xml.Document visitDocument(Xml.Document document, ExecutionContext ctx) {
        document = (Xml.Document) new RemoveDependency("org.acegisecurity", "acegi-security", null)
                .getVisitor()
                .visitNonNull(document, ctx);

        document = (Xml.Document) new AddDependency(
                        "org.springframework.security",
                        "spring-security-core",
                        "5.8.10",
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null)
                .getVisitor()
                .visitNonNull(document, ctx);

        document = (Xml.Document) new AddDependency(
                        "org.springframework.security",
                        "spring-security-config",
                        "5.8.10",
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null)
                .getVisitor()
                .visitNonNull(document, ctx);

        return super.visitDocument(document, ctx);
    }
}

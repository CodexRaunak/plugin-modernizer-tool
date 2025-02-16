package io.jenkins.tools.pluginmodernizer.core.recipes;

import io.jenkins.tools.pluginmodernizer.core.visitors.MigrateAcegiSecurityToSpringSecurityJavaVisitor;
import io.jenkins.tools.pluginmodernizer.core.visitors.MigrateAcegiSecurityToSpringSecurityMavenVisitor;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import org.openrewrite.ExecutionContext;
import org.openrewrite.ScanningRecipe;
import org.openrewrite.Tree;
import org.openrewrite.TreeVisitor;
import org.openrewrite.java.tree.J;
import org.openrewrite.maven.MavenIsoVisitor;
import org.openrewrite.xml.tree.Xml;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MigrateAcegiSecurityToSpringSecurity extends ScanningRecipe<Set<String>> {
    /**
     * Logger
     */
    private static final Logger LOG = LoggerFactory.getLogger(MigrateAcegiSecurityToSpringSecurity.class);

    @Override
    public String getDisplayName() {
        return "Migrate Acegi Security to Spring Security";
    }

    @Override
    public String getDescription() {
        return "Migrate acegi security to spring security.";
    }

    @Override
    public Set<String> getInitialValue(ExecutionContext ctx) {
        return new HashSet<>();
    }

    @Override
    public TreeVisitor<?, ExecutionContext> getScanner(Set<String> acc) {
        return new MavenIsoVisitor<ExecutionContext>() {
            @Override
            public Xml.Tag visitTag(Xml.Tag tag, ExecutionContext ctx) {
                if ("dependency".equals(tag.getName())) {
                    Optional<Xml.Tag> groupIdTag = tag.getChild("groupId");
                    Optional<Xml.Tag> artifactIdTag = tag.getChild("artifactId");

                    if (groupIdTag.isPresent() && artifactIdTag.isPresent()) {
                        String groupId = groupIdTag.get().getValue().orElse("");
                        String artifactId = artifactIdTag.get().getValue().orElse("");

                        // Add acegi-security to the accumulator if found.
                        if ("org.acegisecurity".equals(groupId) && "acegi-security".equals(artifactId)) {
                            acc.add("acegi-security");
                            LOG.info("Found acegi-security dependency performing migration.");
                        }
                    }
                }
                return super.visitTag(tag, ctx);
            }
        };
    }

    @Override
    public TreeVisitor<?, ExecutionContext> getVisitor(Set<String> acc) {
        return new TreeVisitor<Tree, ExecutionContext>() {
            @Override
            public Tree visit(Tree tree, ExecutionContext executionContext) {
                // Check accumulator, if we found acegi-security, we will perform the migration.
                if (!acc.isEmpty()) {
                    if (tree instanceof J.CompilationUnit) {
                        return new MigrateAcegiSecurityToSpringSecurityJavaVisitor().visit(tree, executionContext);
                    } else if (tree instanceof Xml.Document) {
                        return new MigrateAcegiSecurityToSpringSecurityMavenVisitor().visit(tree, executionContext);
                    }
                }
                return tree;
            }
        };
    }
}
